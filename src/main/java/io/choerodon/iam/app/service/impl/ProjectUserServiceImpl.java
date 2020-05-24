package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ProjectUserVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.dto.*;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.ProjectUserMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author zmf
 * @since 20-4-21
 */
@Service
public class ProjectUserServiceImpl implements ProjectUserService {

    private static final String ERROR_SAVE_PROJECTUSER_FAILED = "error.save.projectUser.failed";
    private ProjectUserMapper projectUserMapper;
    private DevopsFeignClient devopsFeignClient;
    private ProjectC7nService projectC7nService;
    private ProjectAssertHelper projectAssertHelper;
    private MemberRoleRepository memberRoleRepository;
    private RoleC7nMapper roleC7nMapper;
    private ProjectMapper projectMapper;
    private OrganizationResourceLimitService organizationResourceLimitService;

    private RoleMapper roleMapper;
    private LabelC7nMapper labelC7nMapper;
    private RoleMemberService roleMemberService;
    private MemberRoleService memberRoleService;

    public ProjectUserServiceImpl(ProjectUserMapper projectUserMapper,
                                  DevopsFeignClient devopsFeignClient,
                                  RoleC7nMapper roleC7nMapper,
                                  MemberRoleRepository memberRoleRepository,
                                  ProjectAssertHelper projectAssertHelper,
                                  @Lazy ProjectC7nService projectC7nService,
                                  ProjectMapper projectMapper,
                                  OrganizationResourceLimitService organizationResourceLimitService,
                                  RoleMapper roleMapper,
                                  LabelC7nMapper labelC7nMapper,
                                  MemberRoleService memberRoleService,
                                  RoleMemberService roleMemberService) {
        this.projectUserMapper = projectUserMapper;
        this.devopsFeignClient = devopsFeignClient;
        this.projectC7nService = projectC7nService;
        this.roleC7nMapper = roleC7nMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.projectMapper = projectMapper;
        this.organizationResourceLimitService = organizationResourceLimitService;
        this.roleMapper = roleMapper;
        this.labelC7nMapper = labelC7nMapper;
        this.memberRoleService = memberRoleService;
        this.memberRoleRepository = memberRoleRepository;
        this.roleMemberService = roleMemberService;
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, PageRequest pageRequest, String loginName, String realName, String roleName, Boolean enabled, String params) {
        return PageHelper.doPage(pageRequest, () -> projectUserMapper.selectUserWithRolesOnProjectLevel(
                ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params));

    }

    @Override
    public List<UserDTO> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params) {
        List<UserDTO> users = projectUserMapper.selectUserWithRolesOnProjectLevel(ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, null, params);
        return users.size() == 0 ? null : users.stream().filter(t -> !t.getId().equals(DetailsHelper.getUserDetails().getUserId())).collect(Collectors.toList());
    }

    @Override
    public List<UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIdsInProject(Long projectId, Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<UserDTO> userDTOS = projectUserMapper.listUserWithRolesOnProjectLevelByIds(projectId, userIds);
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
        return projectUserMapper.listProjectUsersByProjectIdAndRoleLabel(projectId, roleLabel);
    }

    @Override
    public List<UserDTO> listUsersByName(Long projectId, String param) {
        return projectUserMapper.listUsersByName(projectId, param);
    }

    @Override
    public List<UserDTO> listProjectOwnerById(Long projectId) {
        return projectUserMapper.listProjectOwnerById(projectId);
    }

    @Override
    public List<UserDTO> listUsersByNameWithLimit(Long projectId, String param) {
        return projectUserMapper.listUsersByNameWithLimit(projectId, param);
    }


    @Override
    public Page<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(PageRequest pageRequest, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long projectId, boolean doPage) {
        String param = Optional.ofNullable(roleAssignmentSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        if (Boolean.TRUE.equals(doPage)) {
            return PageHelper.doPageAndSort(pageRequest, () -> projectUserMapper.listProjectUsersByRoleIdAndOptions(projectId, roleId, roleAssignmentSearchDTO, param));
        } else {
            Page<UserDTO> page = new Page<>();
            page.setContent(projectUserMapper.listProjectUsersByRoleIdAndOptions(projectId, roleId, roleAssignmentSearchDTO, param));
            return page;
        }
    }

    @Override
    public Page<UserDTO> agileUsers(Long projectId, PageRequest pageable, Set<Long> userIds, String param) {
        return PageHelper.doPage(pageable, () -> projectUserMapper.selectAgileUsersByProjectId(projectId, userIds, param));
    }

    @Override
    public List<RoleDTO> listRolesByProjectIdAndUserId(Long projectId, Long userId) {
        return projectUserMapper.listRolesByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRoles(PageRequest pageRequest, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long projectId) {
        Page<UserDTO> userList = PageHelper.doPage(pageRequest, () -> projectUserMapper.listProjectUser(projectId, roleAssignmentSearchDTO));
        if (userList == null && userList.size() < 1) {
            return userList;
        }
        Set<Long> userIds = userList.stream().map(User::getId).collect(Collectors.toSet());
        List<ProjectUserVO> projectUserVOS = projectUserMapper.listByProjectIdAndUserIds(projectId, userIds);
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
    public List<RoleDTO> listRolesByName(Long sourceId, String roleName, Boolean onlySelectEnable) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
        return roleC7nMapper.fuzzySearchRolesByName(roleName, projectDTO.getOrganizationId(), ResourceLevel.ORGANIZATION.value(), RoleLabelEnum.PROJECT_ROLE.value(), onlySelectEnable);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignProjectUserRolesInternal(Long projectId, List<ProjectUserDTO> projectUsers) {
        ProjectDTO project = projectC7nService.queryProjectById(projectId);
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
            // 直接插入，如果已经有了，会将id回写到dto
            memberRoleService.batchAssignMemberRole(Arrays.asList(memberRole));
            // 插入fd_project_user表数据
            ProjectUserDTO projectUserDTO = new ProjectUserDTO();
            projectUserDTO.setProjectId(projectId);
            projectUserDTO.setMemberRoleId(Objects.requireNonNull(memberRole.getId()));
            if (projectUserMapper.insertSelective(projectUserDTO) != 1) {
                throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
            }
        });
    }

    @Override
    public void assignUsersProjectRoles(Long projectId, List<ProjectUserDTO> projectUserDTOList) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
        Map<Long, Set<String>> userRoleLabelsMap = new HashMap<>();
        projectUserDTOList.forEach(projectUserDTO -> {
            if (projectUserDTO.getMemberId() == null || projectUserDTO.getRoleId() == null) {
                throw new EmptyParamException("error.projectUser.insert.empty");
            }
            projectUserDTO.setProjectId(projectId);
            // 1. set memberRoleId
            projectUserDTO.setMemberRoleId(getMemberRoleId(projectUserDTO.getMemberId(), projectUserDTO.getRoleId(), projectDTO.getOrganizationId()));
            if (projectUserMapper.insertSelective(projectUserDTO) != 1) {
                throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
            }
            // 2.构建saga对象
            Role role = roleMapper.selectByPrimaryKey(projectUserDTO.getRoleId());
            List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(role.getId());
            if (!CollectionUtils.isEmpty(labelDTOS)) {
                Set<String> labelNames = labelDTOS.stream().map(Label::getName).collect(Collectors.toSet());
                Set<String> roleLabels = userRoleLabelsMap.get(projectUserDTO.getMemberId());
                if (!CollectionUtils.isEmpty(roleLabels)) {
                    roleLabels.addAll(labelNames);
                } else {
                    userRoleLabelsMap.put(projectUserDTO.getMemberId(), labelNames);
                }
            }
        });
        // 3.发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        userRoleLabelsMap.forEach((k, v) -> {
            UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
            userMemberEventPayload.setUserId(k);
            userMemberEventPayload.setRoleLabels(v);
            userMemberEventPayload.setResourceId(projectId);
            userMemberEventPayload.setResourceType(ResourceLevel.PROJECT.value());
            userMemberEventPayloads.add(userMemberEventPayload);
            roleMemberService.updateMemberRole(userMemberEventPayloads, ResourceLevel.PROJECT, projectId);
        });
        // 4.todo 发送notice
    }

    @Override
    public void updateUserRoles(Long userId, Long projectId, List<Long> roleIdList, Boolean syncAll) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
        // 1. 获取原memberRoleIds
        List<Long> oldMemberRoleIds = projectUserMapper.listMemberRoleByProjectIdAndUserId(projectId, userId).stream().map(MemberRole::getId).collect(Collectors.toList());
        List<Long> newMemberRoleIds = new ArrayList<>();
        Set<String> labelNames = new HashSet<>();

        // 2. 获取新memberRoleIds
        for (Long roleId : roleIdList) {
            ProjectUserDTO projectUserDTO = new ProjectUserDTO();
            projectUserDTO.setProjectId(projectId);
            projectUserDTO.setMemberRoleId(getMemberRoleId(userId, roleId, projectDTO.getOrganizationId()));
            if (projectUserMapper.selectOne(projectUserDTO) == null) {
                if (projectUserMapper.insertSelective(projectUserDTO) != 1) {
                    throw new CommonException(ERROR_SAVE_PROJECTUSER_FAILED);
                }
            }
            List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(roleMapper.selectByPrimaryKey(roleId).getId());
            if (!CollectionUtils.isEmpty(labelDTOS)) {
                labelNames.addAll(labelDTOS.stream().map(Label::getName).collect(Collectors.toSet()));
            }
            newMemberRoleIds.add(projectUserDTO.getMemberRoleId());
        }

        // 3. 删除MemberRole
        oldMemberRoleIds.forEach(aLong -> {
            if (!newMemberRoleIds.contains(aLong)) {
                List<ProjectUserDTO> userDTOList = projectUserMapper.select(new ProjectUserDTO().setMemberRoleId(aLong));
                if (CollectionUtils.isEmpty(userDTOList)) {
                    memberRoleService.batchDeleteMemberRole(projectDTO.getOrganizationId(), Arrays.asList(memberRoleRepository.selectByPrimaryKey(aLong)));
                }
            }
        });

        // 4. 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(projectId);
        userMemberEventPayload.setResourceType(ResourceLevel.PROJECT.value());
        userMemberEventPayload.setSyncAll(syncAll);
        userMemberEventPayloads.add(userMemberEventPayload);
        roleMemberService.updateMemberRole(userMemberEventPayloads, ResourceLevel.PROJECT, projectId);

    }

    private Long getMemberRoleId(Long userId, Long roleId, Long organizationId) {
        MemberRole memberRole = new MemberRole();
        memberRole.setMemberId(userId);
        memberRole.setRoleId(roleId);
        memberRole.setSourceId(organizationId);
        memberRole.setMemberType(MemberType.USER.value());
        MemberRole queryMemberRole = memberRoleRepository.selectOne(memberRole);
        if (ObjectUtils.isEmpty(queryMemberRole) || ObjectUtils.isEmpty(queryMemberRole.getId())) {
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(organizationId);
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRoleService.batchAssignMemberRole(Arrays.asList(memberRole));
            return memberRole.getId();
        } else {
            return queryMemberRole.getId();
        }
    }
}
