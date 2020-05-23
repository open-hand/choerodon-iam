package io.choerodon.iam.api.eventhandler;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;
import org.hzero.iam.app.service.LdapService;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;
import static io.choerodon.iam.infra.utils.SagaTopic.Organization.TASK_CREATE_LDAP_AUTO;


/**
 * @author scp
 */
@Component
public class OrganizationListener {
    private final ObjectMapper mapper = new ObjectMapper();

    private LdapC7nService ldapC7nService;

    public OrganizationListener(LdapC7nService ldapC7nService) {
        this.ldapC7nService = ldapC7nService;
    }

    @SagaTask(code = TASK_CREATE_LDAP_AUTO, sagaCode = CREATE_LDAP_AUTO, seq = 10, description = "ldap自动同步创建/删除quartzTask")
    public void producerQuartzTask(String message) throws IOException {
        LdapAutoTaskEventPayload autoTaskEventPayload =
                mapper.readValue(message, LdapAutoTaskEventPayload.class);
        ldapC7nService.handleLdapAutoTask(autoTaskEventPayload);
    }
}
