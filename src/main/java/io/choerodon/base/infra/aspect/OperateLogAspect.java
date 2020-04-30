package io.choerodon.base.infra.aspect;

import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class OperateLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperateLogAspect.class);

    //组织层解锁用户
    private static final String UNLOCK_USER = "unlockUser";
    //组织层创建用户
    private static final String CREATE_USERORG = "createUserOrg";
    //组织层启用用户
    private static final String ENABLE_USER = "enableUser";
    //组织层禁用用户
    private static final String DISABLE_USER = "disableUser";
    //删除组织管理员角色
    private static final String DELETE_ORGADMINISTRATOR = "deleteOrgAdministrator";
    //添加管理员角色
    private static final String CREATE_ORGADMINISTRATOR = "createOrgAdministrator";
    //平台层修改组织的信息
    private static final String UPDATE_ORGANIZATION = "updateOrganization";
    //平台层启用组织
    private static final String ENABLE_ORGANIZATION = "enableOrganization";
    //平台层停用组织
    private static final String DISABLE_ORGANIZATION = "disableOrganization";
    //创建项目
    private static final String CREATE_PROJECT = "createProject";
    //启用项目
    private static final String ENABLE_PROJECT = "enableProject";
    //禁用项目
    private static final String DISABLE_PROJECT = "disableProject";
    //分配Root权限
    private static final String ADDADMIN_USERS = "addAdminUsers";
    //平台/组织层角色分配
    private static final String ASSIGN_USERS_ROLES = "assignUsersRoles";
    //平台层重试事务实例
    private static final String SITE_RETRY = "siteRetry";
    //组织层重试是实例
    private static final String ORG_RETRY = "orgRetry";
    //组织层重置用户密码
    private static final String RESET_USERPASSWORD = "resetUserPassword";


    @Autowired
    private OperateLogMapper operateLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AsgardFeignClient asgardFeignClient;


    @Pointcut("@annotation(io.choerodon.base.infra.annotation.OperateLog)")
    public void updateMethodPointcut() {
        throw new UnsupportedOperationException();
    }


    @Around("updateMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        Long operatorId = DetailsHelper.getUserDetails().getUserId();
        OperateLogDTO operateLogDTO = new OperateLogDTO();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        Map<Object, Object> parmMap = new HashMap<>();
        parmMap = processParameters(parameterNames, args);
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        String type = operateLog.type();
        String content = operateLog.content();
        EnumSet<ResourceType> level = EnumSet.noneOf(ResourceType.class);
        ResourceType[] resourceTypes = operateLog.level();
        for (int i = 0; i < resourceTypes.length; i++) {
            level.add(resourceTypes[i]);
        }
        List<String> contentList = new ArrayList<>();
        Long organizationId = null;
        if (null != operateLog && null != method && null != type) {
            switch (type) {
                case UNLOCK_USER:
                    contentList.add(handleUnlockUserOperateLog(content, operatorId, parmMap));
                    organizationId = getOrganizationId(parmMap);
                    break;
                case ENABLE_USER:
                    contentList.add(handleCommonOperateLog(content, operatorId, parmMap));
                    organizationId = getOrganizationId(parmMap);
                    break;
                case DISABLE_USER:
                    contentList.add(handleCommonOperateLog(content, operatorId, parmMap));
                    organizationId = getOrganizationId(parmMap);
                    break;
                case DELETE_ORGADMINISTRATOR:
                    contentList.add(handleCommonOperateLog(content, operatorId, parmMap));
                    organizationId = getOrganizationId(parmMap);
                    break;
                case CREATE_ORGADMINISTRATOR:
                    contentList = handleCreateOrgAdministratorOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                    break;
                case UPDATE_ORGANIZATION:
                    contentList = handleOrganizationOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                    break;
                case ENABLE_ORGANIZATION:
                    contentList = handleOrganizationOperateLog(content, operatorId, parmMap);
                    break;
                case DISABLE_ORGANIZATION:
                    contentList = handleOrganizationOperateLog(content, operatorId, parmMap);
                    break;
                case CREATE_PROJECT:
                    contentList = handleCreateProjectOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationIdByProject(parmMap);
                    break;
                case ENABLE_PROJECT:
                    contentList = handleProjectOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                    break;
                case DISABLE_PROJECT:
                    contentList = handleProjectOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                    break;
                case ADDADMIN_USERS:
                    contentList = handleAddAdminUsersOperateLog(content, operatorId, parmMap);
                    break;
                case CREATE_USERORG:
                    contentList = handleCreateUserOrgOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                    break;
                case ASSIGN_USERS_ROLES:
                    if (ResourceType.ORGANIZATION.value().equals((String) parmMap.get("sourceType"))) {
                        level.remove(ResourceType.SITE);
                        contentList = handleAssignUsersRolesOnSiteLevelOperateLog(content, operatorId, parmMap);
                        organizationId = (Long) parmMap.get("sourceId");
                    }
                    if (ResourceType.SITE.value().equals((String) parmMap.get("sourceType"))) {
                        level.remove(ResourceType.ORGANIZATION);
                        contentList = handleAssignUsersRolesOnSiteLevelOperateLog(content, operatorId, parmMap);
                    }
                    break;
                case SITE_RETRY:
                    contentList = handleRetryOperateLog(content, operatorId, parmMap);
                    break;
                case ORG_RETRY:
                    contentList = handleRetryOperateLog(content, operatorId, parmMap);
                    organizationId = (Long) parmMap.get("sourceId");
                    break;
                case RESET_USERPASSWORD:
                    contentList = handleResetUserpasswordOperateLog(content, operatorId, parmMap);
                    organizationId = getOrganizationId(parmMap);
                default:
                    break;
            }
        }


        Object object = null;
        operateLogDTO.setOperatorId(operatorId);
        operateLogDTO.setSuccess(true);
        operateLogDTO.setMethod(method.getName());
        operateLogDTO.setType(type);
        operateLogDTO.setSourceId(organizationId);
        try {
            object = pjp.proceed();
        } catch (Throwable e) {
            LOGGER.info("error.log:{}", e.getMessage());
            throw new CommonException(e.getMessage());
        }

        contentList.forEach(s -> {
            operateLogDTO.setContent(s);
            level.forEach(v -> {
                if (ResourceType.SITE.value().equals(v.value())) {
                    operateLogDTO.setSourceId(0L);
                }
                operateLogDTO.setSourceType(v.value());
                operateLogDTO.setId(null);
                if (operateLogMapper.insert(operateLogDTO) != 1) {
                    LOGGER.info("error.insert.operate.success.log");
                }
            });
        });
        return object;
    }

    private List<String> handleResetUserpasswordOperateLog(String content, Long operatorId, Map<Object, Object> parmMap) {
        Long userId = (Long) parmMap.get("userId");
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        UserDTO userDTO = userMapper.selectByPrimaryKey(userId);
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator) || Objects.isNull(userDTO)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), getParms(userId));
        contentList.add(format);
        return contentList;
    }


    private List<String> handleRetryOperateLog(String content, Long operatorId, Map<Object, Object> parmMap) {
        long id = (long) parmMap.get("id");
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator)) {
            return contentList;
        }
        SagaTaskInstanceDTO body = asgardFeignClient.query(id).getBody();
        if (Objects.isNull(body)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), body.getSagaCode());
        contentList.add(format);
        return contentList;
    }


    private List<String> handleCreateUserOrgOperateLog(String content, Long operatorId, Map<Object, Object> parmMap) {
        UserDTO userDTO = (UserDTO) parmMap.get("userDTO");
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(userDTO)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), getEmailParms(userDTO));
        contentList.add(format);
        return contentList;
    }

    private List<String> handleAssignUsersRolesOnSiteLevelOperateLog(String content, Long operatorId, Map<Object, Object> parmMap) {
        List<MemberRoleDTO> memberRoleDTOList = (List<MemberRoleDTO>) parmMap.get("memberRoleDTOList");
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator)) {
            return contentList;
        }
        memberRoleDTOList.forEach(memberRoleDTO -> {
            RoleDTO roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
            if (!Objects.isNull(roleDTO)) {
                String format = String.format(content, getParms(memberRoleDTO.getMemberId()), getParms(operatorId), roleDTO.getName());
                contentList.add(format);
            }
        });
        return contentList;
    }

    private List<String> handleAddAdminUsersOperateLog(String content, Long operatorId, Map<Object, Object> parmMap) {
        long[] ids = (long[]) parmMap.get("ids");
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator)) {
            return contentList;
        }
        for (long id : ids) {
            String format = String.format(content, getParms(id), getParms(operatorId));
            contentList.add(format);
        }
        return contentList;
    }

    private Long getOrganizationIdByProject(Map<Object, Object> parmMap) {
        ProjectDTO projectDTO = (ProjectDTO) parmMap.get("projectDTO");
        return projectDTO.getOrganizationId();
    }

    private Long getOrganizationId(Map<Object, Object> parmMap) {
        return (Long) parmMap.get("organizationId");
    }

    private Map<Object, Object> processParameters(String[] parameterNames, Object[] args) {
        Map<Object, Object> objectMap = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            objectMap.put(parameterNames[i], args[i]);
        }
        return objectMap;
    }

    private List<String> handleProjectOperateLog(String content, Long operatorId, Map map) {
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        Long projectId = (Long) map.get("projectId");
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator) || Objects.isNull(projectDTO)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), projectDTO.getName());
        contentList.add(format);
        return contentList;
    }

    private List<String> handleCreateProjectOperateLog(String content, Long operatorId, Map map) {
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        ProjectDTO projectDTO = (ProjectDTO) map.get("projectDTO");
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), projectDTO.getName());
        contentList.add(format);
        return contentList;
    }

    private List<String> handleOrganizationOperateLog(String content, Long operatorId, Map map) {
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(map.get("organizationId"));
        List<String> contentList = new ArrayList<>();
        if (Objects.isNull(operator) || Objects.isNull(organizationDTO)) {
            return contentList;
        }
        String format = String.format(content, getParms(operatorId), organizationDTO.getName());
        contentList.add(format);
        return contentList;
    }


    private List<String> handleCreateOrgAdministratorOperateLog(String content, Long operatorId, Map map) {
        List<Long> userIds = (List<Long>) map.get("userIds");
        List<String> contentList = new ArrayList<>();
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        if (Objects.isNull(operator)) {
            return contentList;
        }
        userIds.forEach(userId -> {
            contentList.add(String.format(content, getParms(userId), getParms(operatorId)));
        });
        return contentList;
    }


    private String handleCommonOperateLog(String content, Long operatorId, Map map) {
        return String.format(content, getParms(operatorId), getParms((Long) map.get("userId")));
    }

    private String handleUnlockUserOperateLog(String content, Long operatorId, Map map) {
        return String.format(content, getParms(operatorId), getParms((Long) map.get("userId")));
    }

    private String getParms(Long userId) {
        UserDTO targeter = userMapper.selectByPrimaryKey(userId);
        if (!Objects.isNull(targeter)) {
            if (targeter.getLdap()) {
                return targeter.getRealName() + "(" + targeter.getLoginName() + ")";
            } else {
                return targeter.getRealName() + "(" + targeter.getEmail() + ")";
            }
        }
        throw new CommonException("error.query.user");
    }

    private String getEmailParms(UserDTO userDTO) {
        return userDTO.getRealName() + "(" + userDTO.getEmail() + ")";
    }
}
