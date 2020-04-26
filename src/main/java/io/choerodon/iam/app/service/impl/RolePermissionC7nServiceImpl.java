package io.choerodon.iam.app.service.impl;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.repository.RolePermissionRepository;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.iam.app.service.RolePermissionC7nService;
import io.choerodon.iam.infra.mapper.RolePermissionC7nMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 11:51
 */
@Service
public class RolePermissionC7nServiceImpl implements RolePermissionC7nService {

    private static final String ERROR_ROLE_ID_NOT_BE_NULL = "error.role.id.not.be.null";
    private RolePermissionRepository rolePermissionRepository;
    private RolePermissionC7nMapper rolePermissionC7nMapper;

    public RolePermissionC7nServiceImpl(RolePermissionRepository rolePermissionRepository,
                                        RolePermissionC7nMapper rolePermissionC7nMapper) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.rolePermissionC7nMapper = rolePermissionC7nMapper;
    }

    @Override
    public List<RolePermission> listRolePermissionByRoleId(Long roleId) {
        Assert.notNull(roleId, ERROR_ROLE_ID_NOT_BE_NULL);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setType(RolePermissionType.PS.name());
        return rolePermissionRepository.select(rolePermission);
    }

    @Override
    @Transactional
    public void batchDelete(Long roleId, Set<Long> deletePermissionIds) {
        Assert.notNull(roleId, ERROR_ROLE_ID_NOT_BE_NULL);
        rolePermissionC7nMapper.batchDelete(roleId, deletePermissionIds);
    }
}
