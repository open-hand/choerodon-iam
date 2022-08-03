package io.choerodon.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.utils.ConvertUtils;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.WorkGroupService;
import io.choerodon.iam.app.service.WorkGroupUserRelService;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.WorkGroupDTO;
import io.choerodon.iam.infra.dto.WorkGroupTreeClosureDTO;
import io.choerodon.iam.infra.mapper.WorkGroupMapper;
import io.choerodon.iam.infra.mapper.WorkGroupTreeClosureMapper;
import io.choerodon.iam.infra.mapper.WorkGroupUserRelMapper;
import io.choerodon.iam.infra.utils.rank.RankUtil;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author zhaotianxin
 * @date 2021-11-08 15:37
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkGroupServiceImpl implements WorkGroupService {
    private static final Logger LOGGER = getLogger(WorkGroupServiceImpl.class);

//    public static final String TYPE_DING_TALK = "ding_talk";

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WorkGroupUserRelMapper workGroupUserRelMapper;

    @Autowired
    private WorkGroupUserRelService workGroupUserRelService;
    @Autowired
    private OrganizationUserService organizationUserService;
    @Autowired
    private TenantC7nService tenantC7nService;

    @Autowired
    private WorkGroupTreeClosureMapper workGroupTreeClosureMapper;

    @Override
    public WorkGroupTreeVO queryWorkGroupTree(Long organizationId, boolean withExtraItems) {
        List<WorkGroupDTO> workGroupDTOS = workGroupMapper.selectByOrganiztionId(organizationId);
        WorkGroupTreeVO workGroupTreeVO = new WorkGroupTreeVO();
        List<WorkGroupVO> workGroupVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workGroupDTOS)) {
            List<WorkGroupVO> workGroupVOList = modelMapper.map(workGroupDTOS, new TypeToken<List<WorkGroupVO>>() {
            }.getType());
            Map<Long, List<WorkGroupVO>> workGroupMap = workGroupVOList.stream().collect(Collectors.groupingBy(WorkGroupVO::getParentId));
            // 构造树形结构
            List<WorkGroupVO> rootWorkGroups = workGroupMap.get(0L);
            if (CollectionUtils.isNotEmpty(rootWorkGroups)) {
                workGroupTreeVO.setRootIds(rootWorkGroups.stream().map(WorkGroupVO::getId).collect(Collectors.toList()));
                Map<Long, Set<Long>> workGroupUserMap = workGroupUserRelService.getWorkGroupMap(organizationId);
                handlerChildren(rootWorkGroups, workGroupMap, workGroupUserMap, workGroupVOS);
                Collections.sort(workGroupVOS, Comparator.comparing(WorkGroupVO::getRank));
            }
        }
        if (withExtraItems) {
            int orgUserCount = 0;
            Page<UserDTO> userPage = organizationUserService.pagingUsersOnOrganizationLevel(organizationId, new PageRequest(0, 10), new AgileUserVO());
            orgUserCount = Long.valueOf(userPage.getTotalElements()).intValue();
            // 构建未分配工作组
            buildUnAssignee(organizationId, orgUserCount, workGroupVOS);
            // 构建组织信息
            workGroupVOS.add(buildOrganizationInfo(organizationId, orgUserCount));
            workGroupTreeVO.setWorkGroupVOS(workGroupVOS);
        }
        return workGroupTreeVO;
    }

    private WorkGroupVO buildOrganizationInfo(Long organizationId, int orgUserCount) {
        TenantVO tenant = tenantC7nService.queryTenantById(organizationId, true);
        WorkGroupVO workGroupVO = new WorkGroupVO();
        workGroupVO.setUserCount(orgUserCount);
        workGroupVO.setName(tenant.getTenantName());
        return workGroupVO;
    }

    private void buildUnAssignee(Long organizationId, int orgUserCount, List<WorkGroupVO> rootWorkGroups) {
        Set<Long> userIds = workGroupUserRelMapper.queryByWorkGroupId(organizationId, null);
        WorkGroupVO workGroupVO = new WorkGroupVO();
        workGroupVO.setName("未分配工作组");
        workGroupVO.setParentId(0L);
        int userCount = orgUserCount;
        if (CollectionUtils.isNotEmpty(userIds)) {
            int count = orgUserCount - userIds.size();
            userCount = count >= 0 ? count : 0;
        }
        workGroupVO.setUserCount(userCount);
        rootWorkGroups.add(workGroupVO);
    }

    private void handlerChildren(List<WorkGroupVO> rootWorkGroups,
                                 Map<Long, List<WorkGroupVO>> workGroupMap,
                                 Map<Long, Set<Long>> workGroupUserMap,
                                 List<WorkGroupVO> workGroupVOS) {
        rootWorkGroups.forEach(v -> {
            List<WorkGroupVO> workGroupVOList = workGroupMap.get(v.getId());
            Set<Long> userIds = workGroupUserMap.getOrDefault(v.getId(), new HashSet<>());
            if (!CollectionUtils.isEmpty(workGroupVOList)) {
                handlerChildren(workGroupVOList, workGroupMap, workGroupUserMap, workGroupVOS);
                workGroupVOList.forEach(workGroupVO -> userIds.addAll(workGroupVO.getUserIds()));
                v.setChildren(workGroupVOList.stream().map(WorkGroupVO::getId).collect(Collectors.toList()));
            }
            v.setUserCount(userIds.size());
            v.setUserIds(userIds);
            workGroupVOS.add(v);
        });
    }

    @Override
    public WorkGroupVO create(Long organizationId, WorkGroupVO workGroupVO) {
        validate(workGroupVO);
        WorkGroupDTO groupDTO = modelMapper.map(workGroupVO, WorkGroupDTO.class);
        // 查询最大的rank值
        if (workGroupVO.getRank() == null) {
            String minRank = workGroupMapper.queryMinRank(organizationId, workGroupVO.getParentId());
            groupDTO.setRank(ObjectUtils.isEmpty(minRank) ? RankUtil.mid() : RankUtil.genPre(minRank));
        }
        groupDTO.setOrganizationId(organizationId);
        baseCreate(groupDTO);
        WorkGroupVO map = modelMapper.map(groupDTO, WorkGroupVO.class);
        // 保存树形结构
        buildTreeClosure(map, organizationId);
        return queryById(organizationId, groupDTO.getId());
    }

    private void buildTreeClosure(WorkGroupVO workGroupVO, Long organizationId) {
        // 查询祖先
        List<Long> ancestorIds = workGroupTreeClosureMapper.queryAncestor(organizationId, workGroupVO.getParentId());
        if (CollectionUtils.isEmpty(ancestorIds)) {
            ancestorIds = new ArrayList<>();
        }
        ancestorIds.add(workGroupVO.getId());
        List<WorkGroupTreeClosureDTO> workGroupTreeClosureDTOS = new ArrayList<>();
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Long userId;
        if (!ObjectUtils.isEmpty(customUserDetails)) {
            userId = customUserDetails.getUserId();
        } else {
            userId = 0L;
        }
        for (Long ancestorId : ancestorIds) {
            WorkGroupTreeClosureDTO workGroupTreeClosureDTO = new WorkGroupTreeClosureDTO();
            workGroupTreeClosureDTO.setAncestorId(ancestorId);
            workGroupTreeClosureDTO.setDescendantId(workGroupVO.getId());
            workGroupTreeClosureDTOS.add(workGroupTreeClosureDTO);
        }
        workGroupTreeClosureMapper.batchInsert(organizationId, userId, workGroupTreeClosureDTOS);
    }

    private WorkGroupDTO baseCreate(WorkGroupDTO workGroupDTO) {
        if (workGroupMapper.insertSelective(workGroupDTO) != 1) {
            throw new CommonException("error.work.group.insert");
        }
        return workGroupDTO;
    }

    void validate(WorkGroupVO workGroupVO) {
        if (ObjectUtils.isEmpty(workGroupVO.getParentId())) {
            throw new CommonException("error.work.group.parent.null");
        }
        if (ObjectUtils.isEmpty(workGroupVO.getName())) {
            throw new CommonException("error.work.group.name.null");
        }
    }

    @Override
    public WorkGroupVO update(Long organizationId, WorkGroupVO workGroupVO) {
        WorkGroupDTO workGroupDTO = workGroupMapper.selectByPrimaryKey(workGroupVO.getId());
        // 是否更改层级
        boolean changeLevel = !Objects.equals(workGroupDTO.getParentId(), workGroupVO.getParentId());
        if (changeLevel) {
            changeLevel(organizationId, workGroupDTO, workGroupVO);
        }
        WorkGroupDTO groupDTO = modelMapper.map(workGroupVO, WorkGroupDTO.class);
        baseUpdate(groupDTO);
        return queryById(organizationId, workGroupVO.getId());
    }

    private void changeLevel(Long organizationId, WorkGroupDTO workGroupDTO, WorkGroupVO workGroupVO) {
        List<Long> oldAncestors = workGroupTreeClosureMapper.queryAncestor(organizationId, workGroupDTO.getParentId());
        List<Long> descendants = workGroupTreeClosureMapper.queryDescendant(organizationId, Collections.singletonList(workGroupDTO.getId()));
        // 删除当前节点以及子节点和原层级的关系
        if (CollectionUtils.isNotEmpty(oldAncestors)) {
            workGroupTreeClosureMapper.deleteByAncestorsAndDescendants(organizationId, oldAncestors, descendants);
        }
        List<Long> newAncestors = workGroupTreeClosureMapper.queryAncestor(organizationId, workGroupVO.getParentId());
        if (CollectionUtils.isNotEmpty(newAncestors)) {
            Long userId = DetailsHelper.getUserDetails().getUserId();
            List<WorkGroupTreeClosureDTO> workGroupTreeClosureDTOS = new ArrayList<>();
            for (Long newAncestor : newAncestors) {
                for (Long descendant : descendants) {
                    WorkGroupTreeClosureDTO workGroupTreeClosureDTO = new WorkGroupTreeClosureDTO();
                    workGroupTreeClosureDTO.setAncestorId(newAncestor);
                    workGroupTreeClosureDTO.setDescendantId(descendant);
                    workGroupTreeClosureDTOS.add(workGroupTreeClosureDTO);
                }
            }
            workGroupTreeClosureMapper.batchInsert(organizationId, userId, workGroupTreeClosureDTOS);
        }
    }

    private WorkGroupDTO baseUpdate(WorkGroupDTO workGroupDTO) {
        if (workGroupMapper.updateByPrimaryKeySelective(workGroupDTO) != 1) {
            throw new CommonException("error.work.group.update");
        }
        return workGroupDTO;
    }

    @Override
    public void delete(Long organizationId, Long workGroupId) {
        // 查询当前工作组下面的所有子工作组
        WorkGroupDTO workGroupDTO = new WorkGroupDTO();
        workGroupDTO.setOrganizationId(organizationId);
        List<Long> children = workGroupTreeClosureMapper.queryDescendant(organizationId, Collections.singletonList(workGroupId));
        if (CollectionUtils.isNotEmpty(children)) {
            // 删除工作组下关联的团队成员
            workGroupUserRelMapper.deleteByWorkGroupIds(organizationId, children);
            // 刪除树形关系
            workGroupTreeClosureMapper.deleteDescendant(organizationId, children);
            // 删除出工作组
            workGroupMapper.deleteByWorkGroupIds(organizationId, children);
        }
    }

    @Override
    public WorkGroupVO queryById(Long organizationId, Long workGroupId) {
        WorkGroupDTO workGroupDTO = workGroupMapper.selectByPrimaryKey(workGroupId);
        return modelMapper.map(workGroupDTO, WorkGroupVO.class);
    }

    @Override
    public Boolean checkName(Long organizationId, Long parentId, String name) {
        WorkGroupDTO workGroupDTO = new WorkGroupDTO();
        workGroupDTO.setOrganizationId(organizationId);
        workGroupDTO.setName(name);
        workGroupDTO.setParentId(parentId);
        List<WorkGroupDTO> workGroupDTOS = workGroupMapper.select(workGroupDTO);
        return CollectionUtils.isNotEmpty(workGroupDTOS);
    }

    @Override
    public WorkGroupVO moveWorkGroup(Long organizationId, Long parentId, MoveWorkGroupVO moveWorkGroupVO) {
        String rank = null;
        if (Boolean.TRUE.equals(moveWorkGroupVO.getBefore())) {
            rank = getBeforeRank(organizationId, parentId, moveWorkGroupVO);
        } else {
            rank = getAfterRank(organizationId, parentId, moveWorkGroupVO);
        }
        WorkGroupDTO workGroupDTO = workGroupMapper.selectByPrimaryKey(moveWorkGroupVO.getWorkGroupId());
        // 是否更改层级
        boolean changeLevel = !Objects.equals(parentId, workGroupDTO.getParentId());
        if (changeLevel) {
            WorkGroupVO workGroupVO = modelMapper.map(workGroupDTO, WorkGroupVO.class);
            workGroupVO.setParentId(parentId);
            changeLevel(organizationId, workGroupDTO, workGroupVO);
        }
        workGroupDTO.setRank(rank);
        workGroupDTO.setParentId(parentId);
        baseUpdate(workGroupDTO);
        return queryById(organizationId, moveWorkGroupVO.getWorkGroupId());
    }

    @Override
    public List<Long> listChildrenWorkGroup(Long organizationId, List<Long> workGroupIds) {
        return workGroupTreeClosureMapper.queryDescendant(organizationId, workGroupIds);
    }

    @Override
    public Map<Long, List<String>> listGroupNameByUserIds(Long organizationId, List<Long> userIds) {
        Map<Long, List<String>> userWorkGroupMap = new HashMap<>();
        // 查出组织下的所有工作组
        List<WorkGroupDTO> allWorkGroupDTO = workGroupMapper.selectByOrganiztionId(organizationId);
        Map<Long, WorkGroupDTO> workGroupDTOMappedById = allWorkGroupDTO.stream().collect(Collectors.toMap(WorkGroupDTO::getId, Function.identity()));

        // 查询用户所属工作组
        List<WorkGroupVO> workGroupVOSByUserIds = workGroupUserRelMapper.selectWorkGroupByUserId(organizationId, userIds);
        Map<Long, List<WorkGroupVO>> workGroupVOSGroupingByUserId = workGroupVOSByUserIds.stream().collect(Collectors.groupingBy(WorkGroupVO::getUserId));

        Map<Long, String> workGroupNameMap = new HashMap<>();
        TenantVO tenant = tenantC7nService.queryTenantById(organizationId, true);
        if (tenant == null) {
            throw new CommonException("error.tenant.not.exists");
        }

        userIds.forEach(userId -> {
            List<WorkGroupVO> workGroupVOList = workGroupVOSGroupingByUserId.get(userId);
            if (!org.apache.commons.lang3.ObjectUtils.isEmpty(workGroupVOList)) {
                List<String> userWorkGroupNames = userWorkGroupMap.get(userId);
                if (org.apache.commons.lang3.ObjectUtils.isEmpty(userWorkGroupNames)) {
                    userWorkGroupNames = new ArrayList<>();
                    userWorkGroupMap.put(userId, userWorkGroupNames);
                }
                for (WorkGroupVO workGroupVO : workGroupVOList) {
                    String workGroupName = workGroupNameMap.get(workGroupVO.getId());
                    if (org.apache.commons.lang3.ObjectUtils.isEmpty(workGroupName)) {
                        workGroupName = WorkGroupServiceImpl.calculateWorkGroup(workGroupVO, workGroupDTOMappedById, "-");
                        workGroupNameMap.put(workGroupVO.getId(), workGroupName);
                    }
                    userWorkGroupNames.add(workGroupName);
                }
            }
        });
        return userWorkGroupMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkGroupInternal(Long organizationId, WorkGroupVO workGroupVO) {
        Long parentId = 0L;
        if (!ObjectUtils.isEmpty(workGroupVO.getParentOpenObjectId()) && !Objects.equals(workGroupVO.getParentOpenObjectId(), "0")) {
            // 查询parent
            WorkGroupDTO parentGroupDTO = workGroupMapper.queryByOpenObjectId(organizationId, workGroupVO.getParentOpenObjectId(), workGroupVO.getOpenType());
            if (parentGroupDTO == null) {
                LOGGER.info("error.get.parent.group,parentDeptId:{}", workGroupVO.getParentOpenObjectId());
            } else {
                parentId = parentGroupDTO.getId();
            }
        }
        // 创建工作组
        workGroupVO.setParentId(parentId);
        WorkGroupDTO groupDTO = workGroupMapper.queryByOpenObjectId(organizationId, workGroupVO.getOpenObjectId(), workGroupVO.getOpenType());
        Long groupId;
        if (groupDTO == null) {
            // 不是跟部门 倒叙排列
            if (parentId != 0) {
                String maxRank = workGroupMapper.queryMaxRank(organizationId, workGroupVO.getParentId());
                workGroupVO.setRank(ObjectUtils.isEmpty(maxRank) ? RankUtil.mid() : RankUtil.genNext(maxRank));
            }
            groupId = create(organizationId, workGroupVO).getId();
            workGroupMapper.updateOpenObjectIdById(groupId, workGroupVO.getOpenObjectId(), workGroupVO.getOpenType());
        } else {
            groupDTO.setParentId(parentId);
            groupDTO.setName(workGroupVO.getName());
            WorkGroupVO groupVO1 = modelMapper.map(groupDTO, WorkGroupVO.class);
            groupId = update(organizationId, groupVO1).getId();
        }
        return groupId;
    }

    @Override
    public List<WorkGroupVO> listWorkGroupByUserIds(Long organizationId, List<Long> userIds) {
        return workGroupUserRelMapper.selectWorkGroupByUserId(organizationId, userIds);
    }

    @Override
    public List<WorkGroupVO> listWorkGroups(Long organizationId) {
        return modelMapper.map(workGroupMapper.selectByOrganiztionId(organizationId), new TypeToken<List<WorkGroupVO>>() {
        }.getType());
    }

    private String getAfterRank(Long organizationId, Long parentId, MoveWorkGroupVO moveWorkGroupVO) {
        String leftRank = workGroupMapper.queryRank(organizationId, parentId, moveWorkGroupVO.getOutSetId());
        if (ObjectUtils.isEmpty(leftRank)) {
            leftRank = RankUtil.mid();
        }
        String rightRank = workGroupMapper.queryRightRank(organizationId, parentId, leftRank);
        return ObjectUtils.isEmpty(rightRank) ? RankUtil.genNext(leftRank) : RankUtil.between(leftRank, rightRank);
    }

    private String getBeforeRank(Long organizationId, Long parentId, MoveWorkGroupVO moveWorkGroupVO) {
        if (Objects.equals(0L, moveWorkGroupVO.getOutSetId())) {
            String minRank = workGroupMapper.queryMinRank(organizationId, parentId);
            return ObjectUtils.isEmpty(minRank) ? RankUtil.mid() : RankUtil.genPre(minRank);
        } else {
            String rightRank = workGroupMapper.queryRank(organizationId, parentId, moveWorkGroupVO.getOutSetId());
            String leftRank = workGroupMapper.queryLeftRank(organizationId, parentId, rightRank);
            if (ObjectUtils.isEmpty(leftRank)) {
                return RankUtil.genPre(rightRank);
            }
            return RankUtil.between(leftRank, rightRank);
        }
    }

    public static String calculateWorkGroup(WorkGroupVO workGroupVO, Map<Long, WorkGroupDTO> workGroupDTOGroupById, String separator) {
        WorkGroupDTO workGroupDTO = ConvertUtils.convertObject(workGroupVO, WorkGroupDTO.class);
        return recursiveWorkGroup(workGroupDTO, workGroupDTOGroupById, separator);
    }

    private static String recursiveWorkGroup(WorkGroupDTO workGroupDTO, Map<Long, WorkGroupDTO> workGroupDTOMappedById, String separator) {
        if (workGroupDTO.getParentId() != 0) {
            return recursiveWorkGroup(workGroupDTOMappedById.get(workGroupDTO.getParentId()), workGroupDTOMappedById, separator) + separator + workGroupDTO.getName();
        }
        return workGroupDTO.getName();
    }
}
