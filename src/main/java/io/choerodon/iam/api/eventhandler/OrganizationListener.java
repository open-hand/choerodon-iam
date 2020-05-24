package io.choerodon.iam.api.eventhandler;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.infra.dto.ProjectUserDTO;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hzero.iam.app.service.LdapService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;
import static io.choerodon.iam.infra.utils.SagaTopic.Organization.TASK_CREATE_LDAP_AUTO;
import static io.choerodon.iam.infra.utils.SagaTopic.Organization.TASK_PROJECT_IMPORT_USER;
import static io.choerodon.iam.infra.utils.SagaTopic.User.PROJECT_IMPORT_USER;


/**
 * @author scp
 */
@Component
public class OrganizationListener {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();

    private LdapC7nService ldapC7nService;
    private ProjectUserService projectUserService;

    public OrganizationListener(LdapC7nService ldapC7nService,ProjectUserService projectUserService) {
        this.ldapC7nService = ldapC7nService;
        this.projectUserService = projectUserService;
    }

    @SagaTask(code = TASK_CREATE_LDAP_AUTO, sagaCode = CREATE_LDAP_AUTO, seq = 10, description = "ldap自动同步创建/删除quartzTask")
    public void producerQuartzTask(String message) throws IOException {
        LdapAutoTaskEventPayload autoTaskEventPayload =
                mapper.readValue(message, LdapAutoTaskEventPayload.class);
        ldapC7nService.handleLdapAutoTask(autoTaskEventPayload);
    }

    @SagaTask(code = TASK_PROJECT_IMPORT_USER, sagaCode = PROJECT_IMPORT_USER, seq = 10, description = "项目层导入用户")
    public void projectImportUser(String message) {
        List<ProjectUserDTO> userDTOList = gson.fromJson(message,
                new TypeToken<List<ProjectUserDTO>>() {
                }.getType());
        projectUserService.assignUsersProjectRoles(userDTOList.get(0).getMemberId(), userDTOList);
    }
}
