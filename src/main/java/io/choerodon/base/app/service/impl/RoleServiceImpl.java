package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.base.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.base.api.dto.payload.UserMemberEventPayload;
import io.choerodon.base.api.query.ClientRoleQuery;
import io.choerodon.base.api.validator.ResourceLevelValidator;
import io.choerodon.base.app.service.RoleService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.asserts.*;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.base.infra.utils.ParamUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.web.util.PageableHelper;

/**
 * @author superlee
 * @since 2018/3/27
 */
@Component
public class RoleServiceImpl implements RoleService {
    private static final String ERROR_ROLE_CODE_EMPTY = "error.role.code.empty";
    private static final String GITLAB_ROLE = "gitlab";

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    private ClientMapper clientMapper;

    private RoleMapper roleMapper;

    private RoleAssertHelper roleAssertHelper;

    private LabelAssertHelper labelAssertHelper;

    private RoleLabelMapper roleLabelMapper;

    private LabelMapper labelMapper;

    private UserMapper userMapper;

    private RolePermissionMapper rolePermissionMapper;

    private PermissionAssertHelper permissionAssertHelper;

    private OrganizationAssertHelper organizationAssertHelper;

    private ProjectAssertHelper projectAssertHelper;

    private SagaClient sagaClient;

    private final ObjectMapper mapper = new ObjectMapper();


    public RoleServiceImpl(ClientMapper clientMapper,
                           RoleMapper roleMapper,
                           RoleAssertHelper roleAssertHelper,
                           LabelAssertHelper labelAssertHelper,
                           RoleLabelMapper roleLabelMapper,
                           PermissionAssertHelper permissionAssertHelper,
                           LabelMapper labelMapper,
                           SagaClient sagaClient, UserMapper userMapper,
                           RolePermissionMapper rolePermissionMapper,
                           OrganizationAssertHelper organizationAssertHelper,
                           ProjectAssertHelper projectAssertHelper) {
        this.clientMapper = clientMapper;
        this.roleMapper = roleMapper;
        this.roleAssertHelper = roleAssertHelper;
        this.labelAssertHelper = labelAssertHelper;
        this.roleLabelMapper = roleLabelMapper;
        this.permissionAssertHelper = permissionAssertHelper;
        this.labelMapper = labelMapper;
        this.sagaClient = sagaClient;
        this.userMapper = userMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectAssertHelper = projectAssertHelper;
    }

    @Override
    public PageInfo<RoleDTO> pagingSearch(Pageable pageable, String name, String code, String level,
                                          Boolean builtIn, Boolean enabled, String params) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> roleMapper.fulltextSearch(name, code, level, builtIn, enabled, params));
    }

    @Override
    public PageInfo<RoleDTO> pagingQueryOrgRoles(Long orgId, Pageable pageable, String name, String code, String level,
                                                 Boolean builtIn, Boolean enabled, String params) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> roleMapper.pagingQueryOrgRoles(orgId, name, code, level, builtIn, enabled, params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO create(RoleDTO roleDTO) {
        insertCheck(roleDTO);
        roleDTO.setBuiltIn(false);
        roleDTO.setEnabled(true);
        roleDTO.setEnableForbidden(true);
        roleDTO.setModified(true);
        roleAssertHelper.codeExisted(roleDTO.getCode());
        if (roleMapper.insertSelective(roleDTO) != 1) {
            throw new InsertException("error.role.insert");
        }
        //维护role_permission表
        insertRolePermission(roleDTO);
        //维护role_label表
        insertRoleLabel(roleDTO);
        return roleDTO;
    }

    private void insertRoleLabel(RoleDTO role) {
        List<LabelDTO> labels = role.getLabels();
        if (labels != null) {
            labels.forEach(label -> {
                RoleLabelDTO roleLabelDTO = new RoleLabelDTO();
                roleLabelDTO.setLabelId(label.getId());
                roleLabelDTO.setRoleId(role.getId());
                roleLabelMapper.insertSelective(roleLabelDTO);
            });
        }
    }

    private void insertRolePermission(RoleDTO role) {
        List<PermissionDTO> permissions = role.getPermissions();
        if (!ObjectUtils.isEmpty(permissions)) {
            permissions.forEach(p -> {
                String permissionCode = p.getCode();
                validateRolePermission(role, permissionCode);
                RolePermissionDTO dto = new RolePermissionDTO();
                dto.setPermissionCode(permissionCode);
                dto.setRoleCode(role.getCode());
                rolePermissionMapper.insertSelective(dto);
            });
        }
    }

    private void insertCheck(RoleDTO roleDTO) {
        validateGitlabLabel(roleDTO);
        validateRoleLabels(roleDTO);
        validateResourceLevel(roleDTO);
        validateCode(roleDTO.getCode());
        validatePermissions(roleDTO.getPermissions());
    }

    /**
     * 校验gitlab角色标签
     * @param roleDTO
     */
    private void validateGitlabLabel(RoleDTO roleDTO) {
        if (roleDTO.getResourceLevel().equals(ResourceType.PROJECT.value())) {
            if (roleDTO.getGitlabLabel() == null) {
                throw new CommonException("error.project.role.gitlab.label");
            } else {
                List<LabelDTO> labels = roleDTO.getLabels() == null ? new ArrayList<>() : roleDTO.getLabels();
                labels.add(roleDTO.getGitlabLabel());
                roleDTO.setLabels(labels);
            }
        }
    }

    private void validateRoleLabels(RoleDTO roleDTO) {
        String level = roleDTO.getResourceLevel();
        List<LabelDTO> roleLabels = roleDTO.getLabels();
        if (!CollectionUtils.isEmpty(roleLabels)) {
            roleLabels.forEach(label -> {
                Long labelId = label.getId();
                if (labelId == null) {
                    throw new CommonException("error.label.id.null");
                }
                label = labelAssertHelper.labelNotExisted(labelId);
                if (!label.getLevel().equals(level)) {
                    throw new CommonException("error.role.label.not.same.level");
                }
            });
        }
    }

    private void validateResourceLevel(RoleDTO roleDTO) {
        String level = roleDTO.getResourceLevel();
        if (!ResourceType.contains(level)) {
            throw new IllegalArgumentException("error.role.illegal.level");
        }
        List<Long> roleIds = roleDTO.getRoleIds();
        if (roleIds != null) {
            roleIds.forEach(roleId -> {
                RoleDTO dto = roleAssertHelper.roleNotExisted(roleId);
                if (!dto.getResourceLevel().equals(level)) {
                    throw new CommonException("error.roles.in.same.level");
                }
            });
        }
    }

    private void validatePermissions(List<PermissionDTO> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new CommonException("error.role_permission.empty");
        }
    }

    private void validateCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new CommonException(ERROR_ROLE_CODE_EMPTY);
        }
        if (code.length() > 128) {
            throw new CommonException("error.role.code.length");
        }
        String[] codes = code.split("/");
        String lastCode = codes[codes.length - 1];
        Pattern p = Pattern.compile("^[a-z]([-a-z0-9]*[a-z0-9])?$");
        Matcher m = p.matcher(lastCode);
        boolean isCheck = m.matches();
        if (!isCheck) {
            throw new CommonException("error.role.code.regular.illegal");
        }
    }

    @Override
    public RoleDTO createBaseOnRoles(RoleDTO roleDTO) {
        insertCheck(roleDTO);
        List<PermissionDTO> permissionDTOS = new ArrayList<>();
        List<Long> roleIds = roleDTO.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Long> permissionIds = rolePermissionMapper.queryExistingPermissionIdsByRoleIds(roleIds);
            permissionDTOS = permissionIds.parallelStream().map(id -> {
                PermissionDTO permissionDTO = new PermissionDTO();
                permissionDTO.setId(id);
                return permissionDTO;
            }).collect(Collectors.toList());
        }
        roleDTO.setPermissions(permissionDTOS);
        return ((RoleServiceImpl) AopContext.currentProxy()).create(roleDTO);
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public RoleDTO update(RoleDTO roleDTO) {
        updateCheck(roleDTO);
        //更新操作不能改level
        roleDTO.setResourceLevel(null);
        roleDTO.setCode(null);
        Long id = roleDTO.getId();
        RoleDTO role1 = roleAssertHelper.roleNotExisted(id);
        //内置的角色不允许更新字段，只能更新label
        boolean isBuiltIn = role1.getBuiltIn();
        if (isBuiltIn) {
            updateRoleLabel(roleDTO);
            return roleDTO;
        } else {
            RoleDTO dto = updateRole(roleDTO);
            //维护role_permission关系
            updateRolePermission(dto);
            //维护role_label表
            updateRoleLabel(dto);
            return dto;
        }
    }

    private RoleDTO updateRole(RoleDTO roleDTO) {
        if (roleMapper.updateByPrimaryKeySelective(roleDTO) != 1) {
            throw new UpdateException("error.role.update");
        }
        RoleDTO dto = roleMapper.selectByPrimaryKey(roleDTO);
        roleDTO.setResourceLevel(dto.getResourceLevel());
        roleDTO.setCode(dto.getCode());
        return roleDTO;
    }

    private void updateRolePermission(RoleDTO role) {
        List<PermissionDTO> permissions = role.getPermissions();
        RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
        rolePermissionDTO.setRoleCode(role.getCode());
        List<RolePermissionDTO> existingRolePermissions = rolePermissionMapper.select(rolePermissionDTO);
        List<String> existingPermissionCode =
                existingRolePermissions.stream().map(RolePermissionDTO::getPermissionCode).collect(Collectors.toList());
        List<String> newPermissionCode =
                permissions.stream().map(PermissionDTO::getCode).collect(Collectors.toList());
        //permissionCode交集
        List<String> intersection = existingPermissionCode.stream().filter(newPermissionCode::contains).collect(Collectors.toList());
        //删除的permissionCode集合
        List<String> deleteList = existingPermissionCode.stream().filter(item -> !intersection.contains(item)).collect(Collectors.toList());
        //新增的permissionCode集合
        List<String> insertList = newPermissionCode.stream().filter(item -> !intersection.contains(item)).collect(Collectors.toList());
        insertList.forEach(permissionCode -> {
            validateRolePermission(role, permissionCode);
            RolePermissionDTO rp = new RolePermissionDTO();
            rp.setRoleCode(role.getCode());
            rp.setPermissionCode(permissionCode);
            if (rolePermissionMapper.insertSelective(rp) != 1) {
                throw new InsertException("error.rolePermission.insert");
            }
        });
        deleteList.forEach(permissionCode -> {
            validateRolePermission(role, permissionCode);
            RolePermissionDTO rp = new RolePermissionDTO();
            rp.setRoleCode(role.getCode());
            rp.setPermissionCode(permissionCode);
            rolePermissionMapper.delete(rp);
        });
    }

    private void validateRolePermission(RoleDTO role, String permissionCode) {
        String roleCode = role.getCode();
        if (permissionCode == null) {
            throw new EmptyParamException("error.permission.code.empty");
        }
        if (roleCode == null) {
            throw new EmptyParamException(ERROR_ROLE_CODE_EMPTY);
        }
        PermissionDTO permission = permissionAssertHelper.permissionNotExisted(permissionCode);
        if (!permission.getResourceLevel().equals(role.getResourceLevel())) {
            throw new CommonException("error.role.level.not.equals.to.permission.level", roleCode, permission.getCode());
        }
    }

    private void updateRoleLabel(RoleDTO roleDTO) {
        RoleLabelDTO roleLabelDTO = new RoleLabelDTO();
        roleLabelDTO.setRoleId(roleDTO.getId());
        List<RoleLabelDTO> roleLabels = roleLabelMapper.select(roleLabelDTO);
        List<Long> existingLabelIds = roleLabels.stream()
                .map(RoleLabelDTO::getLabelId).collect(Collectors.toList());
        List<LabelDTO> labels = roleDTO.getLabels();
        final List<Long> newLabelIds = new ArrayList<>();
        if (!ObjectUtils.isEmpty(labels)) {
            newLabelIds.addAll(labels.stream().map(LabelDTO::getId).collect(Collectors.toList()));
        }
        //labelId交集
        List<Long> intersection = existingLabelIds.stream().filter(newLabelIds::contains).collect(Collectors.toList());
        //删除的labelId集合
        List<Long> deleteList = existingLabelIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        //新增的labelId集合
        List<Long> insertList = newLabelIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        List<UserDTO> users = userMapper.selectUsersFromMemberRoleByOptions(roleDTO.getId(), "user", null, ResourceLevel.PROJECT.value(), null, null);
        boolean sendSagaEvent = !ObjectUtils.isEmpty(users) && devopsMessage;
        doUpdateAndDelete(roleDTO, insertList, deleteList);
        if (sendSagaEvent) {
            users.forEach(user -> {
                List<LabelDTO> labelList = labelMapper.selectByUserId(user.getId());
                UserMemberEventPayload payload = new UserMemberEventPayload();
                payload.setResourceId(user.getSourceId());
                payload.setUserId(user.getId());
                payload.setResourceType(ResourceLevel.PROJECT.value());
                payload.setUsername(user.getLoginName());
                Set<String> nameSet = new HashSet<>(labelList.stream().map(LabelDTO::getName).collect(Collectors.toSet()));
                payload.setRoleLabels(nameSet);
                userMemberEventPayloads.add(payload);
            });
            try {
                String input = mapper.writeValueAsString(userMemberEventPayloads);
                String refIds = userMemberEventPayloads.stream().map(t -> String.valueOf(t.getUserId())).collect(Collectors.joining(","));
                sagaClient.startSaga(MEMBER_ROLE_UPDATE, new StartInstanceDTO(input, "users", refIds));
            } catch (Exception e) {
                throw new CommonException("error.IRoleServiceImpl.update.event", e);
            }
        }
    }

    private void doUpdateAndDelete(RoleDTO roleDTO, List<Long> insertList, List<Long> deleteList) {
        insertList.forEach(labelId -> {
            checkLabelId(labelId);
            RoleLabelDTO rl = new RoleLabelDTO();
            rl.setRoleId(roleDTO.getId());
            rl.setLabelId(labelId);
            if (roleLabelMapper.insertSelective(rl) != 1) {
                throw new CommonException("error.roleLabel.create");
            }
        });
        deleteList.forEach(labelId -> {
            checkLabelId(labelId);
            RoleLabelDTO rl = new RoleLabelDTO();
            rl.setRoleId(roleDTO.getId());
            rl.setLabelId(labelId);
            roleLabelMapper.delete(rl);
        });
    }

    private void checkLabelId(Long labelId) {
        if (labelId == null) {
            throw new CommonException("error.labelId.empty");
        }
        labelAssertHelper.labelNotExisted(labelId);
    }

    @Override
    public RoleDTO orgUpdate(RoleDTO roleDTO, Long orgId) {
        checkRoleCanBeModified(roleDTO.getId(), orgId);
        return ((RoleServiceImpl) AopContext.currentProxy()).update(roleDTO);
    }

    private void updateCheck(RoleDTO roleDTO) {
        validateRoleLabels(roleDTO);
        if (StringUtils.isEmpty(roleDTO.getName())) {
            throw new CommonException("error.role.name.empty");
        }
        if (roleDTO.getName().length() > 64) {
            throw new CommonException("error.role.name.size");
        }
        ResourceLevelValidator.validate(roleDTO.getResourceLevel());
        validateCode(roleDTO.getCode());
        if (roleDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.role.objectVersionNumber.empty");
        }
        validatePermissions(roleDTO.getPermissions());
    }

    @Override
    public void delete(Long id) {
        RoleDTO roleDTO = roleAssertHelper.roleNotExisted(id);
        boolean isBuiltIn = roleDTO.getBuiltIn();
        if (!isBuiltIn) {
            roleMapper.deleteByPrimaryKey(id);
        } else {
            throw new CommonException("error.role.not.allow.to.be.delete");
        }
        RolePermissionDTO rolePermission = new RolePermissionDTO();
        rolePermission.setRoleCode(roleDTO.getCode());
        rolePermissionMapper.delete(rolePermission);
        RoleLabelDTO roleLabelDTO = new RoleLabelDTO();
        roleLabelDTO.setRoleId(id);
        roleLabelMapper.delete(roleLabelDTO);
    }

    @Override
    public RoleDTO queryById(Long id) {
        return roleAssertHelper.roleNotExisted(id);
    }

    @Override
    public RoleDTO enableRole(Long id) {
        return updateStatus(id, true);
    }

    private RoleDTO updateStatus(Long id, boolean enabled) {
        RoleDTO dto = roleAssertHelper.roleNotExisted(id);
        dto.setEnabled(enabled);
        if (roleMapper.updateByPrimaryKeySelective(dto) != 1) {
            throw new UpdateException("error.role.update.status");
        }
        return dto;
    }

    @Override
    public RoleDTO disableRole(Long id) {
        return updateStatus(id, false);
    }

    @Override
    public RoleDTO orgEnableRole(Long roleId, Long orgId) {
        checkRoleCanBeModified(roleId, orgId);
        return enableRole(roleId);
    }

    @Override
    public RoleDTO orgDisableRole(Long roleId, Long orgId) {
        checkRoleCanBeModified(roleId, orgId);
        return disableRole(roleId);
    }

    @Override
    public RoleDTO queryWithPermissionsAndLabels(Long id) {
        RoleDTO roleDTO = roleMapper.selectRoleWithPermissionsAndLabels(id);
        if (roleDTO.getResourceLevel().equals(ResourceType.PROJECT.value())) {
            List<LabelDTO> labelDTOS = new ArrayList<>(roleDTO.getLabels());
            for (LabelDTO labelDTO : roleDTO.getLabels()) {
                if (labelDTO.getName().contains(GITLAB_ROLE)) {
                    labelDTOS.remove(labelDTO);
                    roleDTO.setGitlabLabel(labelDTO);
                    break;
                }
            }
            roleDTO.setLabels(labelDTOS);
        }
        return roleDTO;
    }

    @Override
    public List<RoleDTO> listRolesWithUserCountOnSiteLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (roleAssignmentSearchDTO.getRoleName(), ResourceLevel.SITE.value(), null, false);
        String param = ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = userMapper.selectUserCountFromMemberRoleByOptions(r.getId(),
                    "user", 0L, ResourceLevel.SITE.value(), roleAssignmentSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleDTO> listRolesWithClientCountOnSiteLevel(ClientRoleQuery clientRoleSearchDTO) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (clientRoleSearchDTO.getRoleName(), ResourceLevel.SITE.value(), null, false);
        String param = ParamUtils.arrToStr(clientRoleSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = clientMapper.selectClientCountFromMemberRoleByOptions(
                    r.getId(), ResourceLevel.SITE.value(), 0L, clientRoleSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleDTO> listRolesWithUserCountOnOrganizationLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (roleAssignmentSearchDTO.getRoleName(), ResourceLevel.ORGANIZATION.value(), sourceId, false);
        String param = ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = userMapper.selectUserCountFromMemberRoleByOptions(r.getId(),
                    "user", sourceId, ResourceLevel.ORGANIZATION.value(), roleAssignmentSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleDTO> listRolesWithClientCountOnOrganizationLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (clientRoleSearchDTO.getRoleName(), ResourceLevel.ORGANIZATION.value(), sourceId, false);
        String param = ParamUtils.arrToStr(clientRoleSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = clientMapper.selectClientCountFromMemberRoleByOptions(
                    r.getId(), ResourceLevel.ORGANIZATION.value(), sourceId, clientRoleSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleDTO> listRolesWithUserCountOnProjectLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (roleAssignmentSearchDTO.getRoleName(), ResourceLevel.PROJECT.value(), null, false);
        String param = ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = userMapper.selectUserCountFromMemberRoleByOptions(
                    r.getId(), "user", sourceId, ResourceLevel.PROJECT.value(), roleAssignmentSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public List<RoleDTO> listRolesWithClientCountOnProjectLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId) {
        List<RoleDTO> roles = roleMapper.fuzzySearchRolesByName
                (clientRoleSearchDTO.getRoleName(), ResourceLevel.PROJECT.value(), null, false);
        String param = ParamUtils.arrToStr(clientRoleSearchDTO.getParam());
        roles.forEach(r -> {
            Integer count = clientMapper.selectClientCountFromMemberRoleByOptions(
                    r.getId(), ResourceLevel.PROJECT.value(), sourceId, clientRoleSearchDTO, param);
            r.setUserCount(count);
        });
        return roles;
    }

    @Override
    public void check(RoleDTO role) {
        boolean checkCode = !StringUtils.isEmpty(role.getCode());
        if (!checkCode) {
            throw new CommonException(ERROR_ROLE_CODE_EMPTY);
        }
        checkCode(role);
    }

    private void checkCode(RoleDTO role) {
        boolean createCheck = StringUtils.isEmpty(role.getId());
        if (createCheck) {
            roleAssertHelper.codeExisted(role.getCode());
        } else {
            Long id = role.getId();
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setCode(role.getCode());
            RoleDTO roleDTO1 = roleMapper.selectOne(roleDTO);
            boolean existed = roleDTO1 != null && !id.equals(roleDTO1.getId());
            if (existed) {
                throw new CommonException("error.role.code.existed");
            }
        }
    }

    @Override
    public List<Long> queryIdsByLabelNameAndLabelType(String labelName, String labelType) {
        List<RoleDTO> roles = roleMapper.selectRolesByLabelNameAndType(labelName, labelType, null);
        return roles.stream().map(RoleDTO::getId).collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> selectByLabel(String label, Long organizationId) {
        return roleMapper.selectRolesByLabelNameAndType(label, "role", organizationId);
    }

    @Override
    public List<RoleDTO> listRolesBySourceIdAndTypeAndUserId(String sourceType, Long sourceId, Long userId) {
        return roleMapper.queryRolesInfoByUser(sourceType, sourceId, userId);
    }

    @Override
    public RoleDTO queryByCode(String code) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setCode(code);
        return roleMapper.selectOne(roleDTO);
    }

    @Override
    public List<RoleDTO> listRolesByName(String sourceType, Long sourceId, String roleName, Boolean onlySelectEnable) {
        validateSourceNotExisted(sourceType, sourceId);
        return roleMapper.fuzzySearchRolesByName(roleName, sourceType, sourceId, onlySelectEnable);
    }

    private void validateSourceNotExisted(String sourceType, Long sourceId) {
        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
            organizationAssertHelper.notExisted(sourceId);
        }
        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
            projectAssertHelper.projectNotExisted(sourceId);
        }
    }

    /**
     * 校验是否可修改该角色
     * 时机：组织层修改/启停用角色时校验
     * 内容：1.角色必须存在，2.不可修改平台层创建的组织层角色，3.不可修改其他组织创建的角色
     *
     * @param roleId 角色Id
     * @param orgId  组织Id
     */
    private void checkRoleCanBeModified(Long roleId, Long orgId) {
        RoleDTO roleDTO = roleAssertHelper.roleNotExisted(roleId);
        if (roleDTO.getOrganizationId() == null || !Objects.equals(roleDTO.getOrganizationId(), orgId)) {
            throw new CommonException("error.role.modify.check");
        }
    }
}
