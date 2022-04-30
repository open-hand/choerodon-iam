package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.exception.NotLoginException;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.api.vo.agile.RoleUserCountVO;
import io.choerodon.iam.app.service.FixService;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.constant.LabelC7nConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.AdminFeignClient;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.utils.C7nCollectionUtils;
import io.choerodon.iam.infra.utils.ConvertUtils;
import io.choerodon.iam.infra.utils.PageUtils;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/12 17:00
 */
@Service
public class RoleC7nServiceImpl implements RoleC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleC7nServiceImpl.class);
    private static final String DEFAULT_HZERO_PLATFORM_CODE = "HZERO-PLATFORM";
    private static final String NULL_VERSION = "null_version";

    @Value("${choerodon.fix.data.page.size:200}")
    private Integer pageSize;

    @Value("${choerodon.fix.data.page.sleep.time: 500}")
    private Integer sleepTime;

    private RoleC7nMapper roleC7nMapper;
    private ProjectPermissionMapper projectPermissionMapper;
    private ProjectMapper projectMapper;
    private RoleMapper roleMapper;
    private UserC7nMapper userC7nMapper;
    private ClientC7nMapper clientC7nMapper;
    @Autowired
    private FixService fixService;
    @Autowired
    private IDocumentService documentService;
    @Autowired
    private RolePermissionC7nMapper rolePermissionC7nMapper;
    @Autowired
    private AdminFeignClient adminFeignClient;

    public RoleC7nServiceImpl(RoleC7nMapper roleC7nMapper, UserC7nMapper userC7nMapper, ProjectPermissionMapper projectPermissionMapper, ProjectMapper projectMapper, ClientC7nMapper clientC7nMapper, RoleMapper roleMapper) {
        this.roleC7nMapper = roleC7nMapper;
        this.projectPermissionMapper = projectPermissionMapper;
        this.projectMapper = projectMapper;
        this.roleMapper = roleMapper;
        this.userC7nMapper = userC7nMapper;
        this.clientC7nMapper = clientC7nMapper;
    }

    @Override
    public List<io.choerodon.iam.api.vo.agile.RoleVO> listRolesWithUserCountOnProjectLevel(Long projectId, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        RoleVO param = new RoleVO();
        if (roleAssignmentSearchDTO != null) {
            param.setName(roleAssignmentSearchDTO.getRoleName());
        }
        List<Role> roles = roleC7nMapper.listRolesByTenantIdAndLableWithOptions(projectDTO.getOrganizationId(), LabelC7nConstants.PROJECT_ROLE, param);
        List<io.choerodon.iam.api.vo.agile.RoleVO> roleVOList = ConvertUtils.convertList(roles, io.choerodon.iam.api.vo.agile.RoleVO.class);
        List<RoleUserCountVO> roleUserCountVOS = projectPermissionMapper.countProjectRoleUser(projectId);
        Map<Long, io.choerodon.iam.api.vo.agile.RoleVO> roleMap = roleVOList.stream().collect(Collectors.toMap(RoleDTO::getId, v -> v));
        // 给角色添加用户数
        roleUserCountVOS.forEach(v -> {
            io.choerodon.iam.api.vo.agile.RoleVO roleVO = roleMap.get(v.getRoleId());
            if (roleVO != null) {
                roleVO.setUserCount(v.getUserNumber());
            }
        });
        return roleVOList;
    }

    @Override
    public Page<UserPermissionVO> listRole(PageRequest pageRequest, Long tenantId, String name, String level, String params) {
        Long userId = Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new).getUserId();
        List<UserPermissionVO> roleDTOList = new ArrayList<>();

        Page<UserRoleVO> result = PageHelper.doPage(pageRequest, () -> roleC7nMapper.selectRoles(userId, name, level, params));
        if (!CollectionUtils.isEmpty(result.getContent())) {
            result.setContent(result.getContent().stream().filter(v -> !v.getCode().equals(DEFAULT_HZERO_PLATFORM_CODE)).collect(Collectors.toList()));
        }
        result.getContent().forEach(i -> {
            String[] roles = i.getRoleNames().split(",");
            List<RoleNameAndEnabledVO> list = new ArrayList<>(roles.length);
            for (String role : roles) {
                String[] nameAndEnabled = role.split("\\|");
                boolean roleEnabled = true;
                if (nameAndEnabled[2].equals("0")) {
                    roleEnabled = false;
                }
                list.add(new RoleNameAndEnabledVO(nameAndEnabled[0], nameAndEnabled[1], roleEnabled));
            }
            UserPermissionVO roleC7nDTO = ConvertUtils.convertObject(i, UserPermissionVO.class);
            roleC7nDTO.setRoles(list);
            if (ResourceLevel.PROJECT.value().equals(i.getLevel())) {
                roleC7nDTO.setTenantId(projectMapper.selectByPrimaryKey(i.getId()).getOrganizationId());
            }
            roleDTOList.add(roleC7nDTO);
        });
        return PageUtils.copyPropertiesAndResetContent(result, roleDTOList);
    }

    @Override
    public Page<io.choerodon.iam.api.vo.RoleVO> pagingSearch(PageRequest pageRequest, Long tenantId, String name, String code, String roleLevel, Boolean builtIn, Boolean enabled, String params) {
        String labelName = null;

        if (ResourceLevel.ORGANIZATION.value().equals(roleLevel)) {
            labelName = RoleLabelEnum.TENANT_ROLE.value();
        }
        if (ResourceLevel.PROJECT.value().equals(roleLevel)) {
            labelName = RoleLabelEnum.PROJECT_ROLE.value();
        }
        String finalLabelName = labelName;
        Page<io.choerodon.iam.api.vo.RoleVO> page = PageHelper.doPage(pageRequest, () -> roleC7nMapper.fulltextSearch(tenantId, name, code, ResourceLevel.ORGANIZATION.value(), builtIn, enabled, finalLabelName, params));

        if (!CollectionUtils.isEmpty(page.getContent())) {
            page.getContent().stream().forEach(roleVO -> {
                List<Label> labels = roleC7nMapper.listRoleLabels(roleVO.getId());
                if (!CollectionUtils.isEmpty(labels)) {
                    labels.forEach(label -> {
                        if (RoleLabelEnum.TENANT_ROLE.value().equals(label.getName())) {
                            roleVO.setRoleLevel(ResourceLevel.ORGANIZATION.value());
                        }
                        if (RoleLabelEnum.PROJECT_ROLE.value().equals(label.getName())) {
                            roleVO.setRoleLevel(ResourceLevel.PROJECT.value());
                        }
                    });
                }
            });
        }
        return page;
    }

    @Override
    public Role getTenantAdminRole(Long organizationId) {
        return roleC7nMapper.getTenantAdminRole(organizationId);
    }

    @Override
    public List<RoleDTO> listRolesByName(Long organizationId, String roleName, String roleCode, String labelName, Boolean onlySelectEnable) {
        return roleC7nMapper.listRolesByName(organizationId, roleName, roleCode, labelName, onlySelectEnable);
    }

    @Override
    public List<RoleC7nDTO> listRolesWithUserCountOnOrganizationLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId) {
        List<RoleC7nDTO> roles = ConvertUtils.convertList(
                roleC7nMapper.fuzzySearchRolesByName(roleAssignmentSearchDTO.getRoleName(), sourceId, ResourceLevel.ORGANIZATION.value(), RoleLabelEnum.TENANT_ROLE.value(), false),
                RoleC7nDTO.class);
        String param = ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = userC7nMapper.selectUserCountFromMemberRoleByOptions(r.getId(),
                    "user", sourceId, ResourceLevel.ORGANIZATION.value(), roleAssignmentSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleC7nDTO> listRolesWithClientCountOnProjectLevel(ClientRoleQueryVO clientRoleQueryVO, Long sourceId) {
        List<RoleC7nDTO> roles = ConvertUtils.convertList(
                roleC7nMapper.fuzzySearchRolesByName(clientRoleQueryVO.getRoleName(), sourceId, ResourceLevel.PROJECT.value(), RoleLabelEnum.PROJECT_ROLE.value(), false),
                RoleC7nDTO.class);
        String param = ParamUtils.arrToStr(clientRoleQueryVO.getParam());
        roles.forEach(r -> {
            Integer count = clientC7nMapper.selectClientCountFromMemberRoleByOptions(
                    r.getId(), ResourceLevel.PROJECT.value(), sourceId, clientRoleQueryVO, param);
            r.setUserCount(count);
        });
        return roles;
    }


    @Override
    public List<RoleC7nDTO> listRolesWithClientCountOnOrganizationLevel(ClientRoleQueryVO clientRoleQueryVO, Long sourceId, Boolean enable) {
        List<RoleC7nDTO> roles = ConvertUtils.convertList(
                roleC7nMapper.fuzzySearchRolesByName(clientRoleQueryVO.getRoleName(), sourceId, ResourceLevel.ORGANIZATION.value(), RoleLabelEnum.TENANT_ROLE.value(), enable),
                RoleC7nDTO.class);
        String param = ParamUtils.arrToStr(clientRoleQueryVO.getParam());
        roles.forEach(r -> {
            Integer count = clientC7nMapper.selectClientCountFromMemberRoleByOptions(
                    r.getId(), ResourceLevel.ORGANIZATION.value(), sourceId, clientRoleQueryVO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<Long> queryIdsByLabelNameAndLabelType(String labelName, String labelType) {
        List<Role> roles = roleC7nMapper.selectRolesByLabelNameAndType(labelName, labelType, null);
        return roles.stream().map(Role::getId).collect(Collectors.toList());
    }

    @Override
    public List<Role> listByLabelNames(Long tenantId, String labelName) {
        return roleC7nMapper.listByLabelNames(tenantId, labelName);
    }

    @Override
    public Role getSiteRoleByCode(String code) {
        Role role = new Role();
        role.setCode(code);
        role.setBuiltIn(true);
        role.setLevel(ResourceLevel.SITE.value());
        return roleMapper.selectOne(role);
    }

    @Override
    public List<User> listVindicators() {
        return roleC7nMapper.listVindicators();
    }

    @Override
    public List<SimpleRoleVO> listRolesByIds(List<Long> roleIds, Long tenantId) {
        return !CollectionUtils.isEmpty(roleIds) ? roleC7nMapper.listRolesByIds(roleIds, tenantId) : null;
    }

    @Override
    public void syncRolesAndPermission() {
        try {
            List<String> serviceCodes = adminFeignClient.listServiceCodes().getBody();
            assert serviceCodes != null;
            serviceCodes.forEach(serviceName -> {
                try {
                    documentService.refreshPermissionAsync(serviceName, NULL_VERSION, true);
                } catch (Exception e) {
                    LOGGER.error("error.sync.permission.service:{}", serviceName);
                }
            });
        } catch (Exception e) {
            LOGGER.error("error.sync.permission.service", e);
        }
        fixService.fixMenuLevelPath(true);
        fixChildPermission();
    }

    @Override
    public void fixChildPermission() {
        // 查询模板角色
        List<Role> tplRoles = roleC7nMapper.listByLabelNames(0L, RoleLabelEnum.TENANT_ROLE_TPL.value());

        tplRoles.forEach(tplRole -> {
            // 查询模板角色拥有的权限
            List<RolePermission> tplPs = rolePermissionC7nMapper.listRolePermissionIds(tplRole.getId());
            Set<Long> tplPsIds = tplPs.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());
            Map<Long, RolePermission> tplPsMap = tplPs.stream().collect(Collectors.toMap(RolePermission::getPermissionSetId, v -> v));
            // 查询模板子角色
            List<Role> childRoles = roleC7nMapper.listChildRoleByTplRoleId(tplRole.getId());

            // 修复子角色权限
            childRoles.forEach(childRole -> {
                List<RolePermission> childPs = rolePermissionC7nMapper.listRolePermissionIds(childRole.getId());
                Map<Long, RolePermission> childPsMap = childPs.stream().collect(Collectors.toMap(RolePermission::getPermissionSetId, v -> v));
                Set<Long> childPsIds = childPs.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());

                Set<Long> addPsIds = new HashSet<>();
                Set<Long> delPsIds = new HashSet<>();

                if (CollectionUtils.isEmpty(tplPsIds)) {
                    delPsIds = childPsIds;
                } else {
                    addPsIds = tplPsIds.stream().filter(id -> !childPsIds.contains(id)
                                    && StringUtils.equals(Constants.YesNoFlag.YES, tplPsMap.get(id).getCreateFlag()))
                            .collect(Collectors.toSet());
                    delPsIds = childPsIds.stream().filter(id -> !tplPsIds.contains(id)
                                    || StringUtils.equals(Constants.YesNoFlag.DELETE, tplPsMap.get(id).getCreateFlag()))
                            .collect(Collectors.toSet());
                }

                // 删除子角色权限
                if (!CollectionUtils.isEmpty(delPsIds)) {
                    // 要删除的role-permission-id
                    Set<Long> delRpIds = new HashSet<>();
                    delPsIds.forEach(id -> {
                        RolePermission rolePermission = childPsMap.get(id);
                        delRpIds.add(rolePermission.getId());
                    });
                    rolePermissionC7nMapper.batchDeleteById(delRpIds);
                }
                // 新增子角色权限
                List<RolePermission> rolePermissionList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(addPsIds)) {
                    addPsIds.forEach(id -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setCreateFlag(Constants.YesNoFlag.NO);
                        rolePermission.setInheritFlag(Constants.YesNoFlag.YES);
                        rolePermission.setRoleId(childRole.getId());
                        rolePermission.setPermissionSetId(id);
                        rolePermission.setType(RolePermissionType.PS.name());
                        rolePermission.setCreationDate(new Date());
                        rolePermission.setCreatedBy(0L);
                        rolePermission.setLastUpdateDate(new Date());
                        rolePermission.setLastUpdatedBy(0L);
                        rolePermissionList.add(rolePermission);
                    });

                    if (rolePermissionList.size() > pageSize) {

                        List<List<RolePermission>> fragmentList = C7nCollectionUtils.fragmentList(rolePermissionList, pageSize);
                        for (List<RolePermission> permissions : fragmentList) {
                            rolePermissionC7nMapper.batchInsert(permissions);
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    } else {
                        rolePermissionC7nMapper.batchInsert(rolePermissionList);
                    }
                }
            });
        });
    }
}
