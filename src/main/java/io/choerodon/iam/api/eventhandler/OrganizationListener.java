package io.choerodon.iam.api.eventhandler;


import static io.choerodon.iam.infra.utils.SagaTopic.Organization.*;
import static io.choerodon.iam.infra.utils.SagaTopic.Project.*;
import static io.choerodon.iam.infra.utils.SagaTopic.User.ORG_USER_CREAT;
import static io.choerodon.iam.infra.utils.SagaTopic.User.PROJECT_IMPORT_USER;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.iam.api.eventhandler.payload.ClientPayload;
import io.choerodon.iam.api.vo.ProjectCategoryVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.dto.ProjectPermissionDTO;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;


/**
 * @author scp
 */
@Component
public class OrganizationListener {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();

    private LdapC7nService ldapC7nService;
    private ProjectPermissionService projectPermissionService;
    private ClientC7nService clientC7nService;
    @Autowired
    @Lazy
    private RolePermissionC7nService memberRoleAssignService;

    @Autowired
    private ProjectC7nService projectC7nService;

    public OrganizationListener(LdapC7nService ldapC7nService, ProjectPermissionService projectPermissionService, ClientC7nService clientC7nService) {
        this.ldapC7nService = ldapC7nService;
        this.projectPermissionService = projectPermissionService;
        this.clientC7nService = clientC7nService;
    }

    @SagaTask(code = TASK_CREATE_LDAP_AUTO, sagaCode = CREATE_LDAP_AUTO, seq = 10, description = "ldap自动同步创建/删除quartzTask")
    public void producerQuartzTask(String message) throws IOException {
        LdapAutoTaskEventPayload autoTaskEventPayload =
                mapper.readValue(message, LdapAutoTaskEventPayload.class);
        ldapC7nService.handleLdapAutoTask(autoTaskEventPayload);
    }

    @SagaTask(code = TASK_PROJECT_IMPORT_USER, sagaCode = PROJECT_IMPORT_USER, seq = 10, description = "项目层导入用户")
    public void projectImportUser(String message) {
        List<ProjectPermissionDTO> userDTOList = gson.fromJson(message,
                new TypeToken<List<ProjectPermissionDTO>>() {
                }.getType());
        memberRoleAssignService.assignUsersProjectRoles(userDTOList.get(0).getMemberId(), userDTOList);
    }

    @SagaTask(code = DELETE_CLIENT, sagaCode = DELETE_CLIENT, seq = 10, description = "删除client角色")
    public void deleteClientRole(String message) throws IOException {
        ClientPayload clientPayload = mapper.readValue(message, ClientPayload.class);
        clientC7nService.deleteClientRole(clientPayload.getClientId(), clientPayload.getTenantId());
    }

    /**
     * 修改项目类型  更改项目的标签放到最后一个步骤
     *
     * @param message
     * @throws IOException
     */
    @SagaTask(code = ADD_PROJECT_CATEGORY, sagaCode = PROJECT_UPDATE, seq = 3, description = "修改项目类型")
    public void addProjectCategory(String message) throws IOException {
        ProjectEventPayload projectEventPayload = mapper.readValue(message, ProjectEventPayload.class);
        if (CollectionUtils.isEmpty(projectEventPayload.getProjectCategoryVOS())){
            return;
        }
        projectC7nService.addProjectCategory(projectEventPayload.getProjectId(), projectEventPayload.getProjectCategoryVOS().stream().map(ProjectCategoryVO::getId).collect(Collectors.toList()));
    }
}
