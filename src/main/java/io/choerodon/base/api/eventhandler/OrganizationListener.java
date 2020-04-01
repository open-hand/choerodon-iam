package io.choerodon.base.api.eventhandler;

import static io.choerodon.base.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;
import static io.choerodon.base.infra.utils.SagaTopic.Organization.TASK_CREATE_LDAP_AUTO;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.base.api.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.base.app.service.LdapService;


/**
 * @author scp
 */
@Component
public class OrganizationListener {
    private final ObjectMapper mapper = new ObjectMapper();

    private LdapService ldapService;

    public OrganizationListener(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    @SagaTask(code = TASK_CREATE_LDAP_AUTO, sagaCode = CREATE_LDAP_AUTO, seq = 10, description = "ldap自动同步创建/删除quartzTask")
    public void producerQuartzTask(String message) throws IOException {
        LdapAutoTaskEventPayload autoTaskEventPayload =
                mapper.readValue(message, LdapAutoTaskEventPayload.class);
        ldapService.handleLdapAutoTask(autoTaskEventPayload);
    }
}
