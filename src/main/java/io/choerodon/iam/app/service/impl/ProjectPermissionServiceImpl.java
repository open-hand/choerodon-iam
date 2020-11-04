package io.choerodon.iam.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.OnlineUserStatistics;
import io.choerodon.iam.api.vo.ProjectUserVO;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.api.vo.UserVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.app.service.MessageSendService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.ProjectPermissionService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.dto.*;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.feign.MessageFeignClient;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;
import io.choerodon.iam.infra.mapper.ProjectPermissionMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.utils.ConvertUtils;
import io.choerodon.iam.infra.utils.PageUtils;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.iam.infra.utils.SagaTopic.User.PROJECT_IMPORT_USER;

/**
 * @author zmf
 * @since 20-4-21
 */
@Service
public class ProjectPermissionServiceImpl implements ProjectPermissionService {

    private static final String ERROR_SAVE_PROJECTUSER_FAILED = "error.save.projectUser.failed";
    private ProjectPermissionMapper projectPermissionMapper;
    private DevopsFeignClient devopsFeignClient;
    private ProjectC7nService projectC7nService;
    private ProjectAssertHelper projectAssertHelper;
    private MemberRoleRepository memberRoleRepository;
    private RoleC7nMapper roleC7nMapper;
    private RoleMapper roleMapper;
    private LabelC7nMapper labelC7nMapper;
    private RoleMemberService roleMemberService;
    private MemberRoleService memberRoleService;
    private TransactionalProducer producer;
    private MessageSendService messageSendService;
    private UserMapper userMapper;
    private MessageFeignClient messageFeignClient;

    public ProjectPermissionServiceImpl(ProjectPermissionMapper projectPermissionMapper,
                                        DevopsFeignClient devopsFeignClient,
                                        RoleC7nMapper roleC7nMapper,
                                        MemberRoleRepository memberRoleRepository,
                                        ProjectAssertHelper projectAssertHelper,
                                        @Lazy ProjectC7nService projectC7nService,
                                        RoleMapper roleMapper,
                                        TransactionalProducer producer,
                                        LabelC7nMapper labelC7nMapper,
                                        MemberRoleService memberRoleService,
                                        @Lazy RoleMemberService roleMemberService,
                                        MessageSendService messageSendService,
                                        MessageFeignClient messageFeignClient,
                                        UserMapper userMapper) {
        this.projectPermissionMapper = projectPermissionMapper;
        this.devopsFeignClient = devopsFeignClient;
        this.projectC7nService = projectC7nService;
        this.roleC7nMapper = roleC7nMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.roleMapper = roleMapper;
        this.labelC7nMapper = labelC7nMapper;
        this.memberRoleService = memberRoleService;
        this.producer = producer;
        this.memberRoleRepository = memberRoleRepository;
        this.roleMemberService = roleMemberService;
        this.messageFeignClient = messageFeignClient;
        this.messageSendService = messageSendService;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, PageRequest pageRequest, String loginName, String realName, String roleName, Boolean enabled, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result;

        // 因为PageHelper和Mybatis的级联映射,这里只能手写分页
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = projectPermissionMapper.selectCountUsersOnProjectLevel(ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            List<UserDTO> users = projectPermissionMapper.selectUserWithRolesOnProjectLevel(
                    start, size, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result = PageUtils.buildPage(page, size, count, users);
        } else {
            List<UserDTO> users = projectPermissionMapper.selectUserWithRolesOnProjectLevel(
                    null, null, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result = PageUtils.buildPage(page, size, users.size(), users);
        }

        setRoleIsMember(result);
        return result;
    }

    /**
     * 设置角色是否是项目成员
     *
     * @param result 用户结果
     */
    private void setRoleIsMember(Page<UserDTO> result) {
        List<UserDTO> userDTOList = result.getContent();
        if (!CollectionUtils.isEmpty(userDTOList)) {
            Set<Long> roleIds = userDTOList.stream().flatMap(v -> v.getRoles().stream()).map(Role::getId).collect(Collectors.toSet());
            Map<Long, Boolean> roleIsMember = new HashMap<>();
            roleIds.forEach(id -> {
                List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(id);
                roleIsMember.put(id, labelDTOS.stream().anyMatch(l -> RoleLabelEnum.PROJECT_MEMBER.value().equals(l.getName())));
            });

            userDTOList.forEach(user -> {
                List<Role> userRoles = user.getRoles();
                List<Role> newResult = userRoles.stream().map(u -> {
                    RoleVO roleVO = ConvertUtils.convertObject(u, RoleVO.class);
                    roleVO.setProjectAdminFlag(null);
                    roleVO.setProjectMemberFlag(roleIsMember.get(roleVO.getId()));
                    return roleVO;
                }).collect(Collectors.toList());
                user.setRoles(newResult);
            });
        }
    }


    @Override
    public List<UserDTO> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params) {
        List<UserDTO> users = projectPermissionMapper.selectUserWithRolesOnProjectLevel(null, null, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, null, params);
        return users.size() == 0 ? null : users.stream().filter(t -> !t.getId().equals(DetailsHelper.getUserDetails().getUserId())).collect(Collectors.toList());
    }

    @Override
    public List<UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIdsInProject(Long projectId, Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<UserDTO> userDTOS = projectPermissionMapper.listUserWithRolesOnProjectLevelByIds(projectId, userIds);
        List<UserAttrVO> userAttrVOS = devopsFeignClient.listByUserIds(userIds).getBody();
        if (userAttrVOS == null) {
            userAttrVOS = new ArrayList<>();
        }
        Map<Long, Long> userIdMap = userAttrVOS.stream().collect(Collectors.toMap(UserAttrVO::getIamUserId, UserAttrVO::getGitlabUserId));
        // 填充gitlabUserId
        return userDTOS.stream().map(user -> toUserWithGitlabIdDTO(user, userIdMap.get(user.getId()))).collect(Collectors.toList());
    }

    private UserWithGitlabIdDTO toUserWithGitlabIdDTO(UserDTO userDTO, @Nullable Long gitlabUserId) {
        if (userDTO == null) {
            return null;
        }
        UserWithGitlabIdDTO userWithGitlabIdDTO = new UserWithGitlabIdDTO();
        BeanUtils.copyProperties(userDTO, userWithGitlabIdDTO);
        userWithGitlabIdDTO.setGitlabUserId(gitlabUserId);
        return userWithGitlabIdDTO;
    }

    @Override
    public List<UserDTO> listProjectUsersByProjectIdAndRoleLabel(Long projectId, String roleLabel) {
        return projectPermissionMapper.listProjectUsersByProjectIdAndRoleLabel(projectId, roleLabel);
    }

    @Override
    public List<UserDTO> listUsersByName(Long projectId, String param) {
        return projectPermissionMapper.listUsersByName(projectId, param);
    }

    @Override
    public List<UserDTO> listProjectOwnerById(Long projectId) {
        return projectPermissionMapper.listProjectUsersByProjectIdAndRoleLabel(projectId, RoleLabelEnum.PROJECT_ADMIN.value());
    }

    @Override
    public List<UserDTO> listUsersByNameWithLimit(Long projectId, String param) {
        return projectPermissionMapper.listUsersByNameWithLimit(projectId, param);
    }


    @Override
    public Page<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(PageRequest pageRequest, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long projectId, boolean doPage) {
        String param = Optional.ofNullable(roleAssignmentSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        if (Boolean.TRUE.equals(doPage)) {
            return PageHelper.doPageAndSort(pageRequest, () -> projectPermissionMapper.listProjectUsersByRoleIdAndOptions(projectId, roleId, roleAssignmentSearchDTO, param));
        } else {
            Page<UserDTO> page = new Page<>();
            page.setContent(projectPermissionMapper.listProjectUsersByRoleIdAndOptions(projectId, roleId, roleAssignmentSearchDTO, param));
            return page;
        }
    }

    @Override
    public List<RoleDTO> listRolesByProjectIdAndUserId(Long projectId, Long userId) {
        return projectPermissionMapper.listRolesByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRoles(PageRequest pageRequest, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long projectId) {
        Page<UserDTO> userList = PageHelper.doPage(pageRequest, () -> projectPermissionMapper.listProjectUser(projectId, roleAssignmentSearchDTO));
        if (userList == null || userList.size() < 1) {
            return userList;
        }
        Set<Long> userIds = userList.stream().map(User::getId).collect(Collectors.toSet());
        List<ProjectUserVO> projectUserVOS = projectPermissionMapper.listByProjectIdAndUserIds(projectId, userIds);
        Map<Long, List<ProjectUserVO>> map = projectUserVOS.stream().collect(Collectors.groupingBy(ProjectUserVO::getMemberId));


        userList.forEach(userDTO -> {
            List<ProjectUserVO> proejctUserList = map.get(userDTO.getId());
            if (!CollectionUtils.isEmpty(proejctUserList)) {
                userDTO.setRoles(proejctUserList.stream().map(ProjectUserVO::getRole).collect(Collectors.toList()));
            }
        });

        return userList;
    }


    @Override
    public List<RoleVO> listRolesByName(Long sourceId, String roleName, Boolean onlySelectEnable) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
        List<RoleVO> roleVOS = roleC7nMapper.fuzzySearchRolesByName(roleName, projectDTO.getOrganizationId(), ResourceLevel.ORGANIZATION.value(), RoleLabelEnum.PROJECT_ROLE.value(), onlySelectEnable);
        Set<Long> roleIds = roleVOS.stream().map(Role::getId).collect(Collectors.toSet());
        Map<Long, List<LabelDTO>> labelMap = new HashMap<>();
        roleIds.forEach(id -> {
            List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(id);
            labelMap.put(id, labelDTOS);
        });
        roleVOS.forEach(role -> {
            List<LabelDTO> labelDTOS = labelMap.get(role.getId());
            if (labelDTOS.stream().anyMatch(labelDTO -> RoleLabelEnum.PROJECT_ADMIN.value().equals(labelDTO.getName()))) {
                role.setProjectAdminFlag(true);
            }
            if (labelDTOS.stream().anyMatch(labelDTO -> RoleLabelEnum.PROJECT_MEMBER.value().equals(labelDTO.getName()))) {
                role.setProjectMemberFlag(true);
            }
        });

        return roleVOS;
    }

    @Override
    public void assignUsersProjectRoles(Long projectId, List<ProjectPermissionDTO> projectUserDTOList) {
        Map<Long, List<ProjectPermissionDTO>> map = projectUserDTOList.stream().collect(Collectors.groupingBy(ProjectPermissionDTO::getMemberId));
        map.forEach((k, v) -> addProjectRolesForUser(projectId, k, v.stream().map(ProjectPermissionDTO::getRoleId).collect(Collectors.toSet())));

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignProjectUserRolesInternal(Long projectId, List<ProjectPermissionDTO> projectUsers) {
        ProjectDTO project = projectC7nService.queryProjectById(projectId, true, true, true);
        projectUsers.forEach(u -> {
            // 要先在组织层插入一条角色
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(u.getMemberId());
            memberRole.setRoleId(u.getRoleId());
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setSourceId(project.getOrganizationId());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(project.getOrganizationId());
            memberRole.setMemberType(HiamMemberType.USER.value());
            Map<String, Object> additionalParams = new HashMap<>();
            additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
            memberRole.setAdditionalParams(additionalParams);
            // 直接插入，如果已经有了，会将id回写到dto
            memberRoleService.batchAssignMemberRoleInternal(Arrays.asList(memberRole));
            // 插入fd_project_user表数据
            ProjectPermissionDTO projectPermissionDTO = new ProjectPermissionDTO();
            projectPermissionDTO.setProjectId(projectId);
            projectPermissionDTO.setMemberRoleId(Objects.requireNonNull(memberRole.getId()));
            if (projectPermissionMapper.insertSelective(projectPermissionDTO) != 1) {
                throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
            }
        });
    }

    @Override
    public void addProjectRolesForUser(Long projectId, Long userId, Set<Long> roleIds) {
        Assert.notNull(projectId, "error.projectId.is.null");
        Assert.notNull(userId, "error.userId.is.null");
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);


        roleIds.forEach(roleId -> {
            User user = userMapper.selectByPrimaryKey(userId);
            Role role = roleMapper.selectByPrimaryKey(roleId);

            ProjectPermissionDTO projectPermissionDTO = new ProjectPermissionDTO();
            projectPermissionDTO.setProjectId(projectId);
            projectPermissionDTO.setMemberRoleId(getMemberRoleId(userId, MemberType.USER.value(), roleId, projectDTO.getOrganizationId()));
            // 1. set memberRoleId
            // 判断用户角色关系是否已经存在，存在则跳过
            if (projectPermissionMapper.selectOne(projectPermissionDTO) != null) {
                return;
            }
            if (projectPermissionMapper.insertSelective(projectPermissionDTO) != 1) {
                throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
            }

            // 发送通知
            List<User> userList = new ArrayList<>();
            userList.add(user);
            messageSendService.sendProjectAddUserMsg(projectDTO, role.getName(), userList);
        });


        // 2.构建saga对象
        Map<Long, Set<String>> userRoleLabelsMap = new HashMap<>();
        List<Role> roleList = roleC7nMapper.listProjectRoleByProjectIdAndUserId(projectId, userId);
        Set<Long> ownedRoleIds = roleList.stream().map(Role::getId).collect(Collectors.toSet());
        ownedRoleIds.forEach(id -> {
            List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(id);
            if (!CollectionUtils.isEmpty(labelDTOS)) {
                Set<String> labelNames = labelDTOS.stream().map(Label::getName).collect(Collectors.toSet());
                Set<String> roleLabels = userRoleLabelsMap.get(userId);
                if (!CollectionUtils.isEmpty(roleLabels)) {
                    roleLabels.addAll(labelNames);
                } else {
                    userRoleLabelsMap.put(userId, labelNames);
                }
            }
        });
        // 3.发送saga
        assignUsersProjectRolesEvent(projectId, ResourceLevel.PROJECT, userRoleLabelsMap);
    }

    @Override
    public void assignUsersProjectRolesEvent(Long sourceId, ResourceLevel level, Map<Long, Set<String>> userRoleLabelsMap) {
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        userRoleLabelsMap.forEach((k, v) -> {
            UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
            userMemberEventPayload.setUserId(k);
            userMemberEventPayload.setRoleLabels(v);
            userMemberEventPayload.setResourceId(sourceId);
            userMemberEventPayload.setResourceType(level.value());
            userMemberEventPayloads.add(userMemberEventPayload);
            roleMemberService.updateMemberRole(k, userMemberEventPayloads, level, sourceId);
        });
    }

    @Override
    @Saga(code = PROJECT_IMPORT_USER, description = "项目层导入用户", inputSchemaClass = List.class)
    public void importProjectUser(Long projectId, List<ProjectPermissionDTO> projectUserDTOList) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefId(projectId + "")
                        .withRefType(ResourceLevel.PROJECT.value())
                        .withSagaCode(PROJECT_IMPORT_USER)
                        .withSourceId(projectId),
                builder -> {
                    builder
                            .withPayloadAndSerialize(projectUserDTOList)
                            .withSourceId(projectId);
                    return projectUserDTOList;
                });
    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, Long projectId, Set<Long> roleIdList, Boolean syncAll) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);

        List<MemberRole> oldMemberRoleList = projectPermissionMapper.listMemberRoleByProjectIdAndUserId(projectId, userId, null);
        Map<Long, Long> oldMemberRoleMap = oldMemberRoleList.stream().collect(Collectors.toMap(MemberRole::getRoleId, MemberRole::getId));
        Set<Long> oldRoleIds = oldMemberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());


        // 要删除的角色
        Set<Long> deleteRoleIds = oldMemberRoleList.stream().map(MemberRole::getRoleId).filter(v -> !roleIdList.contains(v)).collect(Collectors.toSet());
        // 要新增的角色
        Set<Long> insertRoleIds = roleIdList.stream().filter(v -> !oldRoleIds.contains(v)).collect(Collectors.toSet());

        Set<Long> deleteMemberRoleIds = new HashSet<>();
        // 删除角色，不删除member-role表中的角色（可能会有并发问题）
        if (!CollectionUtils.isEmpty(deleteRoleIds)) {
            deleteRoleIds.forEach(v -> {
                Long memberRoleId = oldMemberRoleMap.get(v);
                if (memberRoleId != null) {
                    deleteMemberRoleIds.add(memberRoleId);
                }
            });
            projectPermissionMapper.deleteByIds(projectId, deleteMemberRoleIds);
        }
        // 新增角色
        if (!CollectionUtils.isEmpty(insertRoleIds)) {
            insertRoleIds.forEach(v -> {
                ProjectPermissionDTO projectPermissionDTO = new ProjectPermissionDTO();
                projectPermissionDTO.setProjectId(projectId);
                projectPermissionDTO.setMemberRoleId(getMemberRoleId(userId, MemberType.USER.value(), v, projectDTO.getOrganizationId()));
                if (projectPermissionMapper.selectOne(projectPermissionDTO) == null) {
                    if (projectPermissionMapper.insertSelective(projectPermissionDTO) != 1) {
                        throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
                    }
                }
            });
        }
        Set<String> labelNames = new HashSet<>();
        if (!CollectionUtils.isEmpty(roleIdList)) {
            labelNames = labelC7nMapper.selectLabelNamesInRoleIds(roleIdList);
        }

        // 4. 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(projectId);
        userMemberEventPayload.setResourceType(ResourceLevel.PROJECT.value());
        userMemberEventPayload.setSyncAll(syncAll);
        userMemberEventPayloads.add(userMemberEventPayload);
        roleMemberService.updateMemberRole(DetailsHelper.getUserDetails().getUserId(), userMemberEventPayloads, ResourceLevel.PROJECT, projectId);

    }

    @Override
    public OnlineUserStatistics getUserCount(Long projectId, PageRequest pageRequest) {
        OnlineUserStatistics onlineUserStatistics = new OnlineUserStatistics();

        // 获取该项目下的所有用户id(包括在线与不在线)
        List<Long> userIdBelongToCurrentProject = projectPermissionMapper.selectUsersByOptions(projectId, null, null, null)
                .stream()
                .map(UserDTO::getId)
                .collect(Collectors.toList());

        // 过滤不属于该项目的用户id
        List<Long> onlineUserIds = Objects.requireNonNull(messageFeignClient.getOnlineUserIds().getBody())
                .stream()
                .filter(userIdBelongToCurrentProject::contains)
                .sorted()
                .collect(Collectors.toList());

        if (onlineUserIds.size() == 0) {
            onlineUserStatistics.setTotalOnlineUser(0);
            onlineUserStatistics.setOnlineUserList(new Page<>());
            return onlineUserStatistics;
        }

        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int total = onlineUserIds.size();

        List<Long> onlineUserIdsToGetInfo = onlineUserIds.subList(page * size, Math.min(size * (page + 1), total));
        List<UserVO> userVOS = projectPermissionMapper.listRolesByProjectIdAndUserIds(projectId, onlineUserIdsToGetInfo);

        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setContent(userVOS);
        userVOPage.setSize(size);
        userVOPage.setNumber(page);
        userVOPage.setTotalElements(total);

        int remain = total % size;
        userVOPage.setTotalPages(remain == 0 ? total / size : total / size + 1);

        onlineUserStatistics.setTotalOnlineUser(total);
        onlineUserStatistics.setOnlineUserList(userVOPage);

        return onlineUserStatistics;

    }

    @Override
    public Long getMemberRoleId(Long userId, String memberType, Long roleId, Long organizationId) {
        MemberRole memberRole = new MemberRole();
        memberRole.setMemberId(userId);
        memberRole.setRoleId(roleId);
        memberRole.setSourceId(organizationId);
        memberRole.setMemberType(memberType);
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
        memberRole.setAdditionalParams(additionalParams);
        MemberRole queryMemberRole = memberRoleRepository.selectOne(memberRole);
        if (ObjectUtils.isEmpty(queryMemberRole) || ObjectUtils.isEmpty(queryMemberRole.getId())) {
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(organizationId);
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRoleService.batchAssignMemberRoleInternal(Arrays.asList(memberRole));
            return memberRole.getId();
        } else {
            return queryMemberRole.getId();
        }
    }
}
