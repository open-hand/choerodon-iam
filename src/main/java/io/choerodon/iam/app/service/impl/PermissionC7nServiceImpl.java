package io.choerodon.iam.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.PermissionCheckDTO;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;
import io.choerodon.iam.infra.mapper.PermissionC7nMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

/**
 * @author scp
 * @since 2020/4/1
 *
 */
@Service
public class PermissionC7nServiceImpl implements PermissionC7nService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionC7nServiceImpl.class);
    @Autowired
    private PermissionC7nMapper permissionC7nMapper;
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuC7nMapper menuC7nMapper;
    @Autowired
    private ProjectAssertHelper projectAssertHelper;
    @Autowired
    private UserC7nService userC7nService;

    @Autowired
    private PermissionRepository permissionRepository;


    @Override
    public Set<Permission> queryByRoleIds(List<Long> roleIds) {
        Set<Permission> permissions = new HashSet<>();
        roleIds.forEach(roleId -> {
            List<Permission> permissionList = permissionC7nMapper.selectByRoleId(roleId, null);
            permissions.addAll(permissionList);
        });
        return permissions;
    }

    @Override
    public List<PermissionCheckDTO> checkPermissionSets(List<String> codes, Long projectId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        Boolean isOrgRoot = false;
        if (projectId != null) {
            ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
            isOrgRoot = userC7nService.checkIsOrgRoot(projectDTO.getOrganizationId(), self.getUserId());
        }
        Boolean finalIsOrgRoot = isOrgRoot;
        LOGGER.info(">>>>>>>>>>>> check permission >>>>>>>>>>>>>");
        LOGGER.info("CustomUserDetails is {}.ProjectId id is {}.", self);
        return menuRepository.checkPermissionSets(codes, (c) -> menuC7nMapper.checkPermissionSets(self.roleMergeIds(), projectId, self.getUserId(), finalIsOrgRoot, c));
    }

    @Override
    public List<Permission> getPermission(String[] codes) {
        List<Permission> permissions = permissionRepository.selectByCodes(codes);
        return permissions;
    }

}
