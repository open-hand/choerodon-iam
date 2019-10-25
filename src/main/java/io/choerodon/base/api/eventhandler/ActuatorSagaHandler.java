package io.choerodon.base.api.eventhandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.actuator.util.MicroServiceInitData;
import io.choerodon.annotation.entity.PermissionDescription;
import io.choerodon.annotation.entity.PermissionEntity;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.base.app.service.UploadHistoryService;
import io.choerodon.base.infra.dto.PermissionDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.RolePermissionDTO;
import io.choerodon.base.infra.mapper.PermissionMapper;
import io.choerodon.base.infra.mapper.RoleMapper;
import io.choerodon.base.infra.mapper.RolePermissionMapper;
import io.choerodon.core.swagger.PermissionData;

@Component
public class ActuatorSagaHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorSagaHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ACTUATOR_REFRESH_SAGA_CODE = "mgmt-actuator-refresh";
    private static final String PERMISSION_REFRESH_TASK_SAGA_CODE = "iam-permission-task-refresh";
    private static final String INIT_DATA_REFRESH_TASK_SAGA_CODE = "iam-init-data-task-refresh";
    private static final String BASE_ROLE_PERMISSION_FIX_REFRESH_TASK_SAGA_CODE = "base-role-permission-fix-task-refresh";
    private static final String ORG_INIT_DATA_MENU_CATEGORY_TASK_REFRESH = "org-init-data-task-menu-category-refresh";
    private static final String ORG_INIT_DATA_CATEGORY_TASK_REFRESH = "org-init-data-task-category-refresh";
    private static final String ORG_INIT_DATA_REPORT_TASK_REFRESH = "org-init-data-task-report-refresh";
    private static final String ORG_INIT_DATA_LOV_TASK_REFRESH = "org-init-data-task-lov-refresh";

    private UploadHistoryService.ParsePermissionService parsePermissionService;
    private RoleMapper roleMapper;
    private PermissionMapper permissionMapper;
    private RolePermissionMapper rolePermissionMapper;
    private DataSource dataSource;


    public ActuatorSagaHandler(UploadHistoryService.ParsePermissionService parsePermissionService,
                               RoleMapper roleMapper, PermissionMapper permissionMapper,
                               RolePermissionMapper rolePermissionMapper, DataSource dataSource) {
        this.parsePermissionService = parsePermissionService;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.dataSource = dataSource;
    }

    @SagaTask(code = PERMISSION_REFRESH_TASK_SAGA_CODE, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新权限表数据")
    public String refreshPermission(String actuatorJson) throws IOException {
        Map actuator = OBJECT_MAPPER.readValue(actuatorJson, Map.class);
        String service = (String) actuator.get("service");
        Map permissionNode = (Map) actuator.get("permission");
        LOGGER.info("start to refresh permission, service: {}", service);
        String permissionJson = OBJECT_MAPPER.writeValueAsString(permissionNode);
        Map<String, PermissionDescription> descriptions = OBJECT_MAPPER.readValue(permissionJson, OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, PermissionDescription.class));
        parsePermissionService.processDescriptions(service, descriptions);
        return actuatorJson;
    }



    @SagaTask(code = INIT_DATA_REFRESH_TASK_SAGA_CODE, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新菜单表数据")
    public String refreshInitData(String actuatorJson) throws IOException, SQLException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to refresh init data, service: {}", service);
        JsonNode data = root.get("init-data");
        if (data == null || data.size() == 0) {
            LOGGER.info("actuator init-data is empty skip base-init-data-task-refresh.");
            return actuatorJson;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            MicroServiceInitData.processInitData(data, connection, new HashSet<>(Arrays.asList("IAM_PERMISSION", "IAM_MENU_B", "IAM_MENU_PERMISSION", "IAM_DASHBOARD", "IAM_DASHBOARD_ROLE", "IAM_ROLE_PERMISSION")));
            connection.commit();
        }
        return actuatorJson;
    }

    @SagaTask(code = ORG_INIT_DATA_CATEGORY_TASK_REFRESH, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新组织/项目类别数据")
    public String refreshCategory(String actuatorJson) throws IOException, SQLException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to refresh category init data, service: {}", service);
        JsonNode data = root.get("init-data");
        if (data == null || data.size() == 0) {
            LOGGER.info("actuator init-data is empty skip org-init-data-task-category-refresh.");
            return actuatorJson;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            MicroServiceInitData.processInitData(data, connection, new HashSet<>(Arrays.asList("FD_PROJECT_CATEGORY", "FD_ORGANIZATION_CATEGORY")));
            connection.commit();
        }
        return actuatorJson;
    }

    @SagaTask(code = ORG_INIT_DATA_REPORT_TASK_REFRESH, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新报表数据")
    public String refreshReport(String actuatorJson) throws IOException, SQLException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to refresh report init data, service: {}", service);
        JsonNode data = root.get("init-data");
        if (data == null || data.size() == 0) {
            LOGGER.info("actuator init-data is empty skip report refresh.");
            return actuatorJson;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            MicroServiceInitData.processInitData(data, connection, Collections.singleton("FD_REPORT"));
            connection.commit();
        }
        return actuatorJson;
    }

    @SagaTask(code = ORG_INIT_DATA_LOV_TASK_REFRESH, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 1, description = "刷新 Lov, Lockup, Prompt 数据")
    public String refreshLov(String actuatorJson) throws IOException, SQLException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to refresh lov init data, service: {}", service);
        JsonNode data = root.get("init-data");
        if (data == null || data.size() == 0) {
            LOGGER.info("actuator init-data is empty skip org-init-data-task-category-refresh.");
            return actuatorJson;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            MicroServiceInitData.processInitData(data, connection, new HashSet<>(Arrays.asList("FD_LOV", "FD_LOV_GRID_FIELD", "FD_LOV_QUERY_FIELD", "FD_LOOKUP", "FD_LOOKUP_VALUE", "FD_PROMPT")));
            connection.commit();
        }
        return actuatorJson;
    }

    @SagaTask(code = ORG_INIT_DATA_MENU_CATEGORY_TASK_REFRESH, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 2, description = "刷新组织/项目类别下菜单数据")
    public String refreshCategoryMenu(String actuatorJson) throws IOException, SQLException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to refresh menu category init data, service: {}", service);
        JsonNode data = root.get("init-data");
        if (data == null || data.size() == 0) {
            LOGGER.info("actuator init-data is empty skip org-init-data-task-menu-category-refresh.");
            return actuatorJson;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            MicroServiceInitData.processInitData(data, connection, new HashSet<>(Collections.singletonList("FD_CATEGORY_MENU")));
            connection.commit();
        }
        return actuatorJson;
    }

    @SagaTask(code = BASE_ROLE_PERMISSION_FIX_REFRESH_TASK_SAGA_CODE, sagaCode = ACTUATOR_REFRESH_SAGA_CODE, seq = 2, description = "修复角色权限关联为代码关联")
    public String rolePermissionDataFix(String actuatorJson) throws IOException {
        JsonNode root = OBJECT_MAPPER.readTree(actuatorJson);
        String service = root.get("service").asText();
        LOGGER.info("start to fix role permission relation data, service: {}", service);
        Map<Long, RoleDTO> roles = roleMapper.selectAll().stream().collect(Collectors.toMap(RoleDTO::getId, r -> r));
        Map<Long, PermissionDTO> permissions = permissionMapper.selectAll().stream().collect(Collectors.toMap(PermissionDTO::getId, r -> r));
        List<RolePermissionDTO> rolePermissions = rolePermissionMapper.selectAll();
        for (RolePermissionDTO rolePermission : rolePermissions) {
            try {
                Long roleId = Long.valueOf(rolePermission.getRoleCode());
                Long permissionId = Long.valueOf(rolePermission.getRoleCode());
                RoleDTO role = roles.get(roleId);
                PermissionDTO permission = permissions.get(permissionId);
                if (role != null && permission != null) {
                    RolePermissionDTO example = new RolePermissionDTO();
                    example.setRoleCode(role.getCode());
                    example.setPermissionCode(permission.getCode());
                    if (rolePermissionMapper.selectOne(example) != null) {
                        rolePermissionMapper.deleteByPrimaryKey(rolePermission.getId());
                    } else {
                        rolePermission.setRoleCode(role.getCode());
                        rolePermission.setPermissionCode(permission.getCode());
                        rolePermissionMapper.updateByPrimaryKey(rolePermission);
                    }
                } else {
                    rolePermissionMapper.deleteByPrimaryKey(rolePermission.getId());
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return actuatorJson;
    }
}
