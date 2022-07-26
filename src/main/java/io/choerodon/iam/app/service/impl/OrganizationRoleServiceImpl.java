package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.service.role.RoleCreateService;
import org.hzero.iam.domain.service.role.validator.InternalRoleCreateValidator;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.iam.infra.mapper.LabelRelMapper;
import org.hzero.iam.infra.mapper.MemberRoleMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.constant.MisConstants;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.enums.MenuLabelEnum;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.utils.CommonExAssertUtil;
import io.choerodon.iam.infra.utils.ConvertUtils;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/22 10:10
 */
@Service
public class OrganizationRoleServiceImpl implements OrganizationRoleC7nService {

    private static final String ERROR_BUILT_IN_ROLE_NOT_BE_EDIT = "error.built.in.role.not.be.edit";
    private static final String ERROR_ROLE_ID_NOT_BE_NULL = "error.role.id.not.be.null";
    private static final String DELETE_ENABLED_ROLE_FAILED = "delete.enabled.role.failed";
    private static final String ADMINISTRATOR = "administrator";
    private static final String ORG_LEVEL = "organization";
    private static final String PROJECT_ADMIN = "project-admin";
    @Autowired
    private RoleService roleService;
    @Autowired
    private LabelC7nService labelC7nService;
    @Autowired
    private RolePermissionC7nService rolePermissionC7nService;
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private RoleC7nService roleC7nService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuC7nService menuC7nService;
    @Autowired
    private MenuC7nMapper menuC7nMapper;
    @Autowired
    private MemberRoleMapper memberRoleMapper;
    @Autowired
    private LabelRelMapper labelRelMapper;
    @Autowired
    private RoleCreateService roleCreateService;
    @Autowired
    private InternalRoleCreateValidator internalRoleCreateValidator;

    @Override
    @Transactional
    public void create(Long organizationId, RoleVO roleVO) {
        Role tenantAdmin = roleC7nService.getTenantAdminRole(organizationId);
        roleVO.setParentRoleId(tenantAdmin.getId());

        //  给角色添加层级标签
        if (ResourceLevel.PROJECT.value().equals(roleVO.getRoleLevel())) {
            Label label = labelC7nService.selectByName(RoleLabelEnum.PROJECT_ROLE.value());
            roleVO.getRoleLabels().add(label);
        } else if (ResourceLevel.ORGANIZATION.value().equals(roleVO.getRoleLevel())) {
            Label label = labelC7nService.selectByName(RoleLabelEnum.TENANT_ROLE.value());
            roleVO.getRoleLabels().add(label);

        }
        // 创建角色
        CustomUserDetails details = UserUtils.getUserDetails();
        User adminUser = new User();
        adminUser.setId(details.getUserId());
        roleVO.setTenantId(organizationId);
        Role role = roleCreateService.createRole(roleVO, adminUser, false, false, internalRoleCreateValidator);


        // 分配权限集
        // 默认分配个人信息权限集

        Set<Long> psIds = listUserInfoPsIds();

        roleVO.getMenuIdList().addAll(psIds);
        assignRolePermission(role.getId(), roleVO.getMenuIdList());
    }

    private Set<Long> listUserInfoPsIds() {
        // 查询个人信息权限集
        // 查询个人信息菜单
        List<Menu> menus = menuC7nService.listUserInfoMenuOnlyTypeMenu();
        Set<Long> ids = menus.stream().map(Menu::getId).collect(Collectors.toSet());
        List<Menu> menuList = menuC7nMapper.listPermissionSetByParentIds(ids);
        return menuList.stream().map(Menu::getId).collect(Collectors.toSet());

    }

    @Override
    @Transactional
    public RoleVO update(Long organizationId, Long roleId, RoleVO roleVO) {
        Role role = roleMapper.selectByPrimaryKey(roleId);
        if (role.getTenantId() != null) {
            CommonExAssertUtil.assertTrue(organizationId.equals(role.getTenantId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        }

        roleVO.setId(roleId);

        // 预定义角色无法修改
        checkEnableEdit(roleId);


        // 修改角色
        roleMapper.updateByPrimaryKeySelective(roleVO);

        // 更新角色权限
        List<RolePermission> rolePermissions = rolePermissionC7nService.listRolePermissionByRoleId(roleId);
        Set<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());
        Set<Long> psIds = listUserInfoPsIds();
        permissionIds.addAll(psIds);
        // 要新增的权限
        Set<Long> newPermissionIds = roleVO.getMenuIdList().stream().filter(permissionId -> !permissionIds.contains(permissionId)).collect(Collectors.toSet());
        // 要删除的权限
        Set<Long> deletePermissionIds = permissionIds.stream().filter(permissionId -> !roleVO.getMenuIdList().contains(permissionId)).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(deletePermissionIds)) {
            // 删除权限
            rolePermissionC7nService.batchDelete(roleId, deletePermissionIds);
        }
        if (!CollectionUtils.isEmpty(newPermissionIds)) {
            // 新增权限
            assignRolePermission(roleId, newPermissionIds);
        }


        return roleVO;
    }


    @Override
    public List<Role> getByTenantIdAndLabel(Long tenantId, String labelName) {
        return roleC7nMapper.getByTenantIdAndLabel(tenantId, labelName);
    }

    @Override
    public RoleVO queryById(Long organizationId, Long roleId) {
        Role role = roleMapper.selectByPrimaryKey(roleId);
        List<Label> labels = roleC7nMapper.listRoleLabels(role.getId());

        RoleVO roleVO = ConvertUtils.convertObject(role, RoleVO.class);
        roleVO.setRoleLabels(new ArrayList<>());
        Set<String> labelNames = new HashSet<>();
        for (Label label : labels) {
            if (RoleLabelEnum.TENANT_ROLE.value().equals(label.getName())) {
                labelNames.add(MenuLabelEnum.TENANT_MENU.value());
                labelNames.add(MenuLabelEnum.TENANT_GENERAL.value());
            }
            if (RoleLabelEnum.PROJECT_ROLE.value().equals(label.getName())) {
                labelNames.add(MenuLabelEnum.N_GENERAL_PROJECT_MENU.value());
                labelNames.add(MenuLabelEnum.N_AGILE_MENU.value());
                labelNames.add(MenuLabelEnum.N_REQUIREMENT_MENU.value());
                labelNames.add(MenuLabelEnum.N_PROGRAM_PROJECT_MENU.value());
                labelNames.add(MenuLabelEnum.N_TEST_MENU.value());
                labelNames.add(MenuLabelEnum.N_DEVOPS_MENU.value());
                labelNames.add(MenuLabelEnum.N_OPERATIONS_MENU.value());
                labelNames.add(MenuLabelEnum.N_PROGRAM_MENU.value());
                labelNames.add(MenuLabelEnum.N_WATERFALL_MENU.value());
                labelNames.add(MenuLabelEnum.N_WATERFALL_AGILE_MENU.value());
            }
            if (RoleLabelEnum.GITLAB_OWNER.value().equals(label.getName())
                    || RoleLabelEnum.GITLAB_DEVELOPER.value().equals(label.getName())) {
                roleVO.getRoleLabels().add(label);
            }
        }
        SecurityTokenHelper.close();
        Set<String> typeNames = new HashSet<>();
        typeNames.add(HiamMenuType.ROOT.value());
        typeNames.add(HiamMenuType.DIR.value());
        typeNames.add(HiamMenuType.MENU.value());
        List<Menu> menus = menuC7nMapper.listMenuByLabelAndType(labelNames, typeNames);
        // 组织管理员展示项目层的菜单
        List<Menu> projectMenus = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase(role.getCode(), ADMINISTRATOR)
                && StringUtils.equalsIgnoreCase(role.getLevel(), ORG_LEVEL)) {
            projectMenus = getProjectMenus(typeNames);
        }
        SecurityTokenHelper.clear();


        List<Menu> rolePermissions = rolePermissionC7nService.listRolePermissionByRoleIdAndLabels(roleId, null);
        Set<Long> psIds = rolePermissions.stream().map(Menu::getId).collect(Collectors.toSet());
        // 查询权限集
        Set<Long> ids = menus.stream().map(Menu::getId).collect(Collectors.toSet());
        List<Menu> permissionSetList = menuC7nMapper.listPermissionSetByParentIds(ids);
        permissionSetList.forEach(ps -> {
            if (psIds.contains(ps.getId())) {
                ps.setCheckedFlag("Y");
            } else {
                ps.setCheckedFlag("N");
            }
        });
        menus.addAll(permissionSetList);
        //填充permission
        if (StringUtils.equalsIgnoreCase(role.getCode(), ADMINISTRATOR)
                && StringUtils.equalsIgnoreCase(role.getLevel(), ORG_LEVEL)) {
            fillRolePermissions(projectMenus, roleId, organizationId);
        }

        roleVO.setMenuList(menus);
        // 组织管理员展示项目层的菜单
        roleVO.setProjectList(projectMenus);
        return roleVO;
    }

    private void fillRolePermissions(List<Menu> projectMenus, Long roleId, Long organizationId) {
        //查询当前组织下的项目所有者角色
        Role role = new Role();
        role.setTenantId(organizationId);
        role.setCode(PROJECT_ADMIN);
        role.setBuiltIn(true);
        Role roleDb = roleMapper.selectOne(role);

        if (Objects.isNull(roleDb)) {
            return;
        }
        List<Menu> rolePermissions = rolePermissionC7nService.listRolePermissionByRoleIdAndLabels(roleId, null);
        Set<Long> psIds = rolePermissions.stream().map(Menu::getId).collect(Collectors.toSet());
        // 查询权限集
        Set<Long> ids = projectMenus.stream().map(Menu::getId).collect(Collectors.toSet());
        List<Menu> permissionSetList = menuC7nMapper.listPermissionSetByParentIds(ids);
        permissionSetList.forEach(ps -> {
            if (psIds.contains(ps.getId())) {
                ps.setCheckedFlag("Y");
            } else {
                ps.setCheckedFlag("N");
            }
        });
        projectMenus.addAll(permissionSetList);
    }

    private List<Menu> getProjectMenus(Set<String> typeNames) {
        Set<String> labelNames = new HashSet<>();
        labelNames.add(MenuLabelEnum.N_GENERAL_PROJECT_MENU.value());
        labelNames.add(MenuLabelEnum.N_AGILE_MENU.value());
        labelNames.add(MenuLabelEnum.N_REQUIREMENT_MENU.value());
        labelNames.add(MenuLabelEnum.N_PROGRAM_PROJECT_MENU.value());
        labelNames.add(MenuLabelEnum.N_TEST_MENU.value());
        labelNames.add(MenuLabelEnum.N_DEVOPS_MENU.value());
        labelNames.add(MenuLabelEnum.N_OPERATIONS_MENU.value());
        labelNames.add(MenuLabelEnum.N_PROGRAM_MENU.value());
        List<Menu> projectList = menuC7nMapper.listMenuByLabelAndType(labelNames, typeNames);
        return projectList;
    }

    @Override
    @Transactional
    public void delete(Long organizationId, Long roleId) {
        Assert.notNull(roleId, ERROR_ROLE_ID_NOT_BE_NULL);
        Assert.notNull(organizationId, ResourceCheckConstants.ERROR_ORGANIZATION_ID_IS_NULL);
        // 数据权限校验
        Role role = roleMapper.selectByPrimaryKey(roleId);
        CommonExAssertUtil.assertTrue(organizationId.equals(role.getTenantId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        // 启用状态角色不可删除
        if (Boolean.TRUE.equals(role.getEnabled())) {
            throw new CommonException(DELETE_ENABLED_ROLE_FAILED);
        }
        // 1. 删除用户角色关系
        MemberRole memberRole = new MemberRole();
        memberRole.setRoleId(roleId);
        memberRoleMapper.delete(memberRole);
        // 2. 删除角色标签关系
        LabelRel labelRel = new LabelRel();
        labelRel.setDataType("ROLE");
        labelRel.setDataId(roleId);
        labelRelMapper.delete(labelRel);
        // 3. 删除角色权限
        rolePermissionC7nService.deleteByRoleId(roleId);
        // 4. 删除角色
        roleMapper.deleteByPrimaryKey(roleId);

    }

    @Override
    public Boolean checkCodeExist(Long organizationId, String code) {
        Role role = new Role();
        role.setTenantId(organizationId);
        role.setCode(code);
        return !CollectionUtils.isEmpty(roleMapper.select(role));
    }

    /**
     * 校验是否时预定义角色，预定义角色无法编辑
     *
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
     *
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
