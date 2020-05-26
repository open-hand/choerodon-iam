package io.choerodon.iam.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.PermissionCheckDTO;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;
import io.choerodon.iam.infra.mapper.PermissionC7nMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class PermissionC7nServiceImpl implements PermissionC7nService {

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
        return menuRepository.checkPermissionSets(codes, (c) -> menuC7nMapper.checkPermissionSets(self.roleMergeIds(), projectId, self.getUserId(), c));
    }
}
