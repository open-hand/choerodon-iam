package io.choerodon.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.WorkGroupUserRelDTO;
import io.choerodon.iam.infra.mapper.WorkGroupMapper;
import io.choerodon.iam.infra.mapper.WorkGroupUserRelMapper;
import io.choerodon.iam.infra.utils.PageUtils;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaotianxin
 * @date 2021-11-08 20:51
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkGroupUserRelServiceImpl implements WorkGroupUserRelService {

    @Autowired
    private WorkGroupUserRelMapper workGroupUserRelMapper;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WorkGroupMapper workGroupMapper;
    @Autowired
    private OrganizationUserService organizationUserService;
    @Autowired
    private UserC7nService userC7nService;
    @Autowired
    private ProjectC7nService projectC7nService;

    @Override
    public void batchInsertRel(Long organizationId, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        List<Long> userIds = workGroupUserRelParamVO.getUserIds();
        if (!CollectionUtils.isEmpty(userIds)) {
            Set<Long> existUserIds = workGroupUserRelMapper.queryByWorkGroupId(organizationId, workGroupUserRelParamVO.getWorkGroupId());
            userIds.stream()
                    .filter(v -> !existUserIds.contains(v))
                    .forEach(v -> {
                        WorkGroupUserRelDTO workGroupUserRelDTO = new WorkGroupUserRelDTO();
                        workGroupUserRelDTO.setWorkGroupId(workGroupUserRelParamVO.getWorkGroupId());
                        workGroupUserRelDTO.setUserId(v);
                        workGroupUserRelDTO.setOrganizationId(organizationId);
                        baseInsert(workGroupUserRelDTO);
                    });
        }
    }

    private void baseInsert(WorkGroupUserRelDTO workGroupUserRelDTO) {
        if (workGroupUserRelMapper.insertSelective(workGroupUserRelDTO) != 1) {
            throw new CommonException("error.work.group.user.rel.insert");
        }
    }

    @Override
    public void batchDeleteRel(Long organizationId, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        List<Long> userIds = workGroupUserRelParamVO.getUserIds();
        if (!CollectionUtils.isEmpty(userIds)) {
            List<Long> workGroupIds = workGroupService.listChildrenWorkGroup(organizationId, Collections.singletonList(workGroupUserRelParamVO.getWorkGroupId()));
            workGroupUserRelMapper.batchDelete(organizationId, workGroupIds, workGroupUserRelParamVO.getUserIds());
        }
    }

    @Override
    public Page<WorkGroupUserRelVO> pageByQuery(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        if (ObjectUtils.isEmpty(workGroupUserRelParamVO.getWorkGroupId())) {
            throw new CommonException("error.work.group.id.null");
        }
        List<Long> workGroupIds = Collections.singletonList(workGroupUserRelParamVO.getWorkGroupId());
        Set<Long> userIds = workGroupUserRelMapper.listUserIdsByWorkGroupIds(organizationId, workGroupIds);
        if (CollectionUtils.isEmpty(userIds)) {
            return new Page<>();
        }
        AgileUserVO agileUserVO = modelMapper.map(workGroupUserRelParamVO, AgileUserVO.class);
        agileUserVO.setUserIds(userIds);
        Page<UserDTO> userPage = organizationUserService.pagingUsersOnOrganizationLevel(organizationId, pageRequest, agileUserVO);
        List<UserDTO> content = userPage.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return new Page<>();
        }
        List<Long> users = content.stream().map(UserDTO::getId).collect(Collectors.toList());
        List<WorkGroupVO> workGroupVOS = workGroupUserRelMapper.selectWorkGroupByUserId(organizationId, users);
        Map<Long, List<WorkGroupVO>> workGroupMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(workGroupVOS)) {
            workGroupMap.putAll(workGroupVOS.stream().collect(Collectors.groupingBy(WorkGroupVO::getUserId)));
        }
        List<WorkGroupUserRelVO> list = new ArrayList<>();
        content.forEach(v -> {
            WorkGroupUserRelVO workGroupUserRelVO = new WorkGroupUserRelVO();
            workGroupUserRelVO.setUserId(v.getId());
            workGroupUserRelVO.setUserVO(modelMapper.map(v, UserVO.class));
            workGroupUserRelVO.setWorkGroupVOS(workGroupMap.get(v.getId()));
            list.add(workGroupUserRelVO);
        });
        return PageUtils.copyPropertiesAndResetContent(userPage, list);
    }

    @Override
    public Page<WorkGroupUserRelVO> pageUnAssignee(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        AgileUserVO agileUserVO = modelMapper.map(workGroupUserRelParamVO, AgileUserVO.class);
        // 查询已分配工作的用户
        Set<Long> existUserIds = workGroupUserRelMapper.queryByWorkGroupId(organizationId, null);
        if (ObjectUtils.isEmpty(agileUserVO)) {
            agileUserVO = new AgileUserVO();
        }
        agileUserVO.setIgnoredUserIds(existUserIds);
        Page<UserDTO> userPage = organizationUserService.pagingUsersOnOrganizationLevel(organizationId, pageRequest, agileUserVO);
        List<UserDTO> content = userPage.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return new Page<>();
        }
        List<WorkGroupUserRelVO> list = new ArrayList<>();
        content.forEach(v -> {
            WorkGroupUserRelVO workGroupUserRelVO = new WorkGroupUserRelVO();
            workGroupUserRelVO.setUserId(v.getId());
            workGroupUserRelVO.setUserVO(modelMapper.map(v, UserVO.class));
            list.add(workGroupUserRelVO);
        });
        return PageUtils.copyPropertiesAndResetContent(userPage, list);
    }

    @Override
    public Map<Long, Set<Long>> getWorkGroupMap(Long organizationId) {
        WorkGroupUserRelDTO workGroupUserRelDTO = new WorkGroupUserRelDTO();
        workGroupUserRelDTO.setOrganizationId(organizationId);
        List<WorkGroupUserRelDTO> workGroupUserRelDTOS = workGroupUserRelMapper.select(workGroupUserRelDTO);
        Map<Long, Set<Long>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(workGroupUserRelDTOS)) {
            map.putAll(workGroupUserRelDTOS.stream().collect(Collectors.groupingBy(WorkGroupUserRelDTO::getWorkGroupId, Collectors.mapping(WorkGroupUserRelDTO::getUserId, Collectors.toSet()))));
        }
        return map;
    }

    @Override
    public Page<WorkGroupUserRelVO> pageUnlinkUser(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        AgileUserVO agileUserVO = modelMapper.map(workGroupUserRelParamVO, AgileUserVO.class);
        if (!ObjectUtils.isEmpty(workGroupUserRelParamVO.getWorkGroupId())) {
            // 传了工作组id就要忽略工作组已关联的成员
            Set<Long> userIds = workGroupUserRelMapper.listUserIdsByWorkGroupIds(organizationId, Arrays.asList(workGroupUserRelParamVO.getWorkGroupId()));
            agileUserVO.setIgnoredUserIds(userIds);
        }
        Page<UserDTO> userPage = organizationUserService.pagingUsersOnOrganizationLevel(organizationId, pageRequest, agileUserVO);
        List<UserDTO> content = userPage.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return new Page<>();
        }
        List<Long> userIds = userPage.stream().map(UserDTO::getId).collect(Collectors.toList());
        List<WorkGroupVO> workGroupVOS = workGroupUserRelMapper.selectWorkGroupByUserId(organizationId, userIds);
        Map<Long, List<WorkGroupVO>> workGroupMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(workGroupVOS)) {
            workGroupMap.putAll(workGroupVOS.stream().collect(Collectors.groupingBy(WorkGroupVO::getUserId)));
        }
        List<WorkGroupUserRelVO> list = new ArrayList<>();
        content.forEach(v -> {
            WorkGroupUserRelVO workGroupUserRelVO = new WorkGroupUserRelVO();
            workGroupUserRelVO.setUserId(v.getId());
            workGroupUserRelVO.setUserVO(modelMapper.map(v, UserVO.class));
            workGroupUserRelVO.setWorkGroupVOS(workGroupMap.get(v.getId()));
            list.add(workGroupUserRelVO);
        });
        return PageUtils.copyPropertiesAndResetContent(userPage, list);
    }

    @Override
    public Page<UserDTO> pageByGroups(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        // 查询当前组织权限项目
        ProjectDTO search = new ProjectDTO();
        search.setEnabled(true);
        List<ProjectDTO> projectVOS = userC7nService.listProjectsByUserIdForSimple(organizationId, userId, search, null);
        if (ObjectUtils.isEmpty(projectVOS)) {
            return new Page<>();
        }
        List<Long> projectIds = projectVOS.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        List<Long> selectedUserIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(workGroupUserRelParamVO.getUserIds())) {
            selectedUserIds.addAll(workGroupUserRelParamVO.getUserIds());
        }
        List<Long> selectedWorkGroupIds = workGroupUserRelParamVO.getWorkGroupIds();
        Page<UserDTO> userPage = new Page<>();
        AgileUserVO agileUserVO = new AgileUserVO(null, new HashSet<>(projectIds), workGroupUserRelParamVO.getRealName(), organizationId, null);
        Set<Long> ignoredUserIds = new HashSet<>();
        // 处理分组筛选
        Boolean doPage = handlerWorkGroupIds(organizationId, selectedWorkGroupIds, ignoredUserIds, agileUserVO, selectedUserIds);
        // 过滤选中的用户
        ignoredUserIds.addAll(selectedUserIds);
        agileUserVO.setIgnoredUserIds(ignoredUserIds);
        if (Boolean.TRUE.equals(doPage)) {
            userPage = projectC7nService.agileUsersByProjects(pageRequest, agileUserVO);
        }
        // 处理选中的用户
        appendSelectedUsers(userPage, selectedUserIds, pageRequest);
        return userPage;
    }

    @Override
    public Set<Long> listUserIdsByWorkGroupIds(Long organizationId, WorkHoursSearchVO workHoursSearchVO) {
        List<Long> workGroupIds = workHoursSearchVO.getWorkGroupIds();
        List<Long> projectIds = workHoursSearchVO.getProjectIds();
        List<Long> userIds = workHoursSearchVO.getUserIds();
        Set<Long> userIdSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(workGroupIds) && ObjectUtils.isEmpty(userIds)) {
            List<Long> workGroupIdList = workGroupService.listChildrenWorkGroup(organizationId, workGroupIds);
            if (!ObjectUtils.isEmpty(workGroupIdList)) {
                userIdSet.addAll(workGroupUserRelMapper.listUserIdsByWorkGroupIds(organizationId, workGroupIdList));
            }
            if (!ObjectUtils.isEmpty(projectIds) && workGroupIds.contains(0L)) {
                // 查询登记过工时但未分配工作组的人员
                Set<Long> noGroupUserIds = workGroupUserRelMapper.selectNoGroupUsers(organizationId, projectIds, workHoursSearchVO.getStartTime(), workHoursSearchVO.getEndTime());
                if (!noGroupUserIds.isEmpty()) {
                    List<User> users = userC7nService.listUsersByIds(noGroupUserIds.toArray(new Long[noGroupUserIds.size()]), true);
                    if (!ObjectUtils.isEmpty(users)) {
                        userIdSet.addAll(users.stream().map(User::getId).collect(Collectors.toList()));
                    }
                }
            }
            if (userIdSet.isEmpty()) {
                userIdSet.add(0L);
            }
        }
        return userIdSet;
    }

    private Boolean handlerWorkGroupIds(Long organizationId, List<Long> selectedWorkGroupIds, Set<Long> ignoredUserIds, AgileUserVO agileUserVO, List<Long> selectedUserIds) {
        boolean doPage = true;
        if (!CollectionUtils.isEmpty(selectedWorkGroupIds)) {
            // 查询选中的工作组及子级
            List<Long> workGroupIds = workGroupService.listChildrenWorkGroup(organizationId, selectedWorkGroupIds);
            Set<Long> workGroupUserIds = new HashSet<>();
            if (!CollectionUtils.isEmpty(workGroupIds)) {
                workGroupUserIds = workGroupUserRelMapper.listUserIdsByWorkGroupIds(organizationId, workGroupIds);
            }
            // 是否包含未分配工作组
            boolean containsNoGroup = selectedWorkGroupIds.contains(0L);
            if (containsNoGroup) {
                // 包含未分配，分页查询时忽略其他未选中工作组的用户但保留已选中工作组的用户
                List<Long> unSelectedWorkGroupIds = workGroupMapper.selectIdsByOrganizationId(organizationId, workGroupIds);
                ignoredUserIds.addAll(workGroupUserRelMapper.listUserIdsByWorkGroupIds(organizationId, unSelectedWorkGroupIds));
                ignoredUserIds.removeIf(workGroupUserIds::contains);
                selectedUserIds.removeIf(ignoredUserIds::contains);
                agileUserVO.setUserIds(null);
            } else {
                // 不包含未分配，分页查询选中工作组的用户
                agileUserVO.setUserIds(workGroupUserIds);
                selectedUserIds.removeIf(v -> CollectionUtils.isEmpty(agileUserVO.getUserIds()) || !agileUserVO.getUserIds().contains(v));
                if (CollectionUtils.isEmpty(workGroupUserIds)) {
                    // 选中工作组无用户时，不分页查询用户
                    doPage = false;
                }
            }
        }
        return doPage;
    }

    private void appendSelectedUsers(Page<UserDTO> userPage, List<Long> selectedUserIds, PageRequest pageRequest) {
        boolean append = !ObjectUtils.isEmpty(selectedUserIds) && pageRequest.getPage() == 0;
        if (append) {
            // 拼接选中的用户
            List<User> users = userC7nService.listUsersByIds(selectedUserIds.toArray(new Long[selectedUserIds.size()]), true);
            List<UserDTO> list = modelMapper.map(users, new TypeToken<List<UserDTO>>() {
            }.getType());
            if (!CollectionUtils.isEmpty(userPage.getContent())) {
                list.addAll(userPage.getContent());
            }
            userPage.setContent(list);
        }
    }
}
