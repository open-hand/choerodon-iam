package io.choerodon.iam.app.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.role.impl.RoleCreateInternalService;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.app.service.LabelC7nService;
import io.choerodon.iam.app.service.OrganizationRoleC7nService;
import io.choerodon.iam.app.service.RolePermissionC7nService;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.enums.RoleLevelEnum;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/4/22 10:10
 */
@Service
public class OrganizationRoleServiceImpl implements OrganizationRoleC7nService {

    private static final String ERROR_BUILT_IN_ROLE_NOT_BE_EDIT = "error.built.in.role.not.be.edit";
    private static final String ERROR_ROLE_ID_NOT_BE_NULL = "error.role.id.not.be.null";

    private RoleCreateInternalService roleCreateInternalService;
    private RoleService roleService;
    private LabelC7nService labelC7nService;
    private RolePermissionC7nService rolePermissionC7nService;
    private RoleC7nMapper roleC7nMapper;


    public OrganizationRoleServiceImpl(RoleCreateInternalService roleCreateInternalService,
                                       RoleService roleService,
                                       LabelC7nService labelC7nService,
                                       RolePermissionC7nService rolePermissionC7nService,
                                       RoleC7nMapper roleC7nMapper) {
        this.roleCreateInternalService = roleCreateInternalService;
        this.roleService = roleService;
        this.labelC7nService = labelC7nService;
        this.rolePermissionC7nService = rolePermissionC7nService;
        this.roleC7nMapper = roleC7nMapper;
    }

    @Override
    @Transactional
    public void create(Long organizationId, RoleVO roleVO) {
        // todo 查询组织管理员,设置parent_id
        Long orgAdminId = 1L;
        roleVO.setParentRoleId(orgAdminId);

        //  如果是项目层角色，添加角色标签
        if (RoleLevelEnum.PROJECT.value().equals(roleVO.getRoleLevel())) {
            Label label = labelC7nService.selectByName(RoleLabelEnum.PROJECT_ROLE.value());
            roleVO.getRoleLabels().add(new org.hzero.iam.domain.entity.RoleLabel().setLabelId(label.getId()));
        }
        // 创建角色
        CustomUserDetails details = UserUtils.getUserDetails();
        User adminUser = new User();
        adminUser.setId(details.getUserId());
        Role role = roleCreateInternalService.createRole(roleVO, adminUser, false, false);

        // 分配权限集
        assignRolePermission(role.getId(), roleVO.getMenuIdList());
    }

    @Override
    @Transactional
    public void update(Long organizationId, Long roleId, RoleVO roleVO) {
        roleVO.setId(roleId);

        // 预定义角色无法修改
        checkEnableEdit(roleId);

        if (Boolean.TRUE.equals(roleVO.getUpdateRoleFlag())) {
            roleService.updateRole(roleVO);
        }

        if (Boolean.TRUE.equals(roleVO.getUpdatePermissionFlag())) {
            List<RolePermission> rolePermissions = rolePermissionC7nService.listRolePermissionByRoleId(roleId);
            Set<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());
            // 要新增的权限
            Set<Long> newPermissionIds = roleVO.getMenuIdList().stream().filter(permissionId -> !permissionIds.contains(permissionId)).collect(Collectors.toSet());
            // 要删除的权限
            Set<Long> deletePermissionIds = permissionIds.stream().filter(permissionId -> !roleVO.getMenuIdList().contains(permissionId)).collect(Collectors.toSet());

            // 删除权限
            rolePermissionC7nService.batchDelete(roleId, deletePermissionIds);
            // 新增权限
            assignRolePermission(roleId, newPermissionIds);
        }
    }

    @Override
    public List<RoleVO> list(Long organizationId) {

        return null;
    }

    @Override
    public Role getByTenantIdAndLabel(Long tenantId, String labelName) {
        return roleC7nMapper.getByTenantIdAndLabel(tenantId, labelName);
    }

    /**
     * 校验是否时预定义角色，预定义角色无法编辑
     * @param roleId
     */
    private void checkEnableEdit(Long roleId) {
        org.hzero.iam.domain.vo.RoleVO role = roleService.selectRoleDetails(roleId);
        if (Boolean.TRUE.equals(role.getBuiltIn())) {
            throw new CommonException(ERROR_BUILT_IN_ROLE_NOT_BE_EDIT);
        }
    }

    /**
     * 分配角色权限
     * @param roleId
     * @param permissionIds
     */
    private void assignRolePermission(Long roleId, Set<Long> permissionIds) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionSetIds(permissionIds);
        rolePermission.setType(RolePermissionType.PS.name());
        roleService.directAssignRolePermission(rolePermission);
    }
}
