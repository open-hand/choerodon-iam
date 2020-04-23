package io.choerodon.iam.app.service.impl;

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

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.app.service.LabelC7nService;
import io.choerodon.iam.app.service.OrganizationRoleService;
import io.choerodon.iam.infra.enums.RoleLabel;
import io.choerodon.iam.infra.enums.RoleLevelEnum;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/4/22 10:10
 */
@Service
public class OrganizationRoleServiceImpl implements OrganizationRoleService {

    private RoleCreateInternalService roleCreateInternalService;
    private RoleService roleService;
    private LabelC7nService labelC7nService;

    public OrganizationRoleServiceImpl(RoleCreateInternalService roleCreateInternalService,
                                       RoleService roleService,
                                       LabelC7nService labelC7nService) {
        this.roleCreateInternalService = roleCreateInternalService;
        this.roleService = roleService;
        this.labelC7nService = labelC7nService;
    }

    @Override
    @Transactional
    public void create(Long organizationId, RoleVO roleVO) {
        // todo 查询组织管理员,设置parent_id
        Long orgAdminId = 1L;
        roleVO.setParentRoleId(orgAdminId);

        //  如果是项目层角色，添加角色标签
        if (RoleLevelEnum.PROJECT.value().equals(roleVO.getRoleLevel())) {
            Label label = labelC7nService.selectByName(RoleLabel.PROJECT_ROLE.value());
            roleVO.getRoleLabels().add(new org.hzero.iam.domain.entity.RoleLabel().setLabelId(label.getId()));
        }
        // 创建角色
        CustomUserDetails details = UserUtils.getUserDetails();
        User adminUser = new User();
        adminUser.setId(details.getUserId());
        Role role = roleCreateInternalService.createRole(roleVO, adminUser, false, false);

        // 分配权限集
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(role.getId());
        rolePermission.setPermissionSetIds(roleVO.getMenuIdList());
        rolePermission.setType(RolePermissionType.PS.name());
        roleService.directAssignRolePermission(rolePermission);
    }
}
