package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ProjectUserVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.UserWithGitlabIdDTO;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.ProjectUserMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.utils.IamPageUtils;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author zmf
 * @since 20-4-21
 */
@Service
public class ProjectUserServiceImpl implements ProjectUserService {
    private ProjectUserMapper projectUserMapper;
    private DevopsFeignClient devopsFeignClient;
    private ProjectC7nService projectC7nService;
    private ProjectAssertHelper projectAssertHelper;
    private RoleC7nMapper roleC7nMapper;

    public ProjectUserServiceImpl(ProjectUserMapper projectUserMapper,
                                  DevopsFeignClient devopsFeignClient,
                                  RoleC7nMapper roleC7nMapper,
                                  ProjectAssertHelper projectAssertHelper,
                                  ProjectC7nService projectC7nService) {
        this.projectUserMapper = projectUserMapper;
        this.devopsFeignClient = devopsFeignClient;
        this.projectC7nService = projectC7nService;
        this.roleC7nMapper = roleC7nMapper;
        this.projectAssertHelper = projectAssertHelper;
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, PageRequest pageRequest, String loginName, String realName, String roleName, Boolean enabled, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result = IamPageUtils.createEmptyPage(page, size);
        if (doPage) {
            int start = IamPageUtils.getBegin(page, size);
            int count = projectUserMapper.selectCountUsersOnProjectLevel(ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            List<UserDTO> users = projectUserMapper.selectUserWithRolesOnProjectLevel(
                    start, size, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result.setTotalElements(count);
            result.getContent().addAll(users);
        } else {
            List<UserDTO> users = projectUserMapper.selectUserWithRolesOnProjectLevel(
                    null, null, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result.setTotalElements(users.size());
            result.getContent().addAll(users);
        }
        return result;
    }

    @Override
    public List<UserDTO> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params) {
        List<UserDTO> users = projectUserMapper.selectUserWithRolesOnProjectLevel(
                null, null, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, null, params);
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

}
