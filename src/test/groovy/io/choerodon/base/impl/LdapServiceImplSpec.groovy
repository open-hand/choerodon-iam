package io.choerodon.base.app.service.impl

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.LdapService
import io.choerodon.base.infra.asserts.LdapAssertHelper
import io.choerodon.base.infra.asserts.OrganizationAssertHelper
import io.choerodon.base.infra.utils.ldap.LdapSyncUserTask
import io.choerodon.base.infra.dto.LdapDTO
import io.choerodon.base.infra.dto.OrganizationDTO
import io.choerodon.base.infra.mapper.LdapErrorUserMapper
import io.choerodon.base.infra.mapper.LdapHistoryMapper
import io.choerodon.base.infra.mapper.LdapMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan*    */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class LdapServiceImplSpec extends Specification {
    @Autowired
    OrganizationAssertHelper organizationAssertHelper
    LdapAssertHelper ldapAssertHelper = Mock(LdapAssertHelper)
    @Autowired
    LdapMapper ldapMapper
    @Autowired
    LdapSyncUserTask ldapSyncUserTask
    @Autowired
    LdapSyncUserTask.FinishFallback finishFallback
    @Autowired
    LdapErrorUserMapper ldapErrorUserMapper
    @Autowired
    LdapHistoryMapper ldapHistoryMapper
    LdapService ldapService


    def setup() {
        ldapService = new LdapServiceImpl(organizationAssertHelper, ldapAssertHelper,
                ldapMapper, ldapSyncUserTask, finishFallback, ldapErrorUserMapper, ldapHistoryMapper)
        LdapDTO ldapDTO = new LdapDTO()
        ldapDTO.setServerAddress("ldap://acfun.hand.com")
        ldapDTO.setPort("389")
        ldapDTO.setUseSSL(false)
        ldapDTO.setDirectoryType("OpenLDAP")
        ldapDTO.setObjectClass("person")
        ldapDTO.setRealNameField("displayName")
        ldapDTO.setLoginNameField("employeeNumber")
        ldapDTO.setEmailField("mail")
        ldapDTO.setPhoneField("mobile")
        ldapDTO.setBaseDn("ou=emp,dc=hand,dc=com")
        ldapDTO.setConnectionTimeout(1000)

        ldapAssertHelper.ldapNotExisted(_, _) >> ldapDTO
    }

    def "SyncLdapUser"() {
//        given: "构造请求参数"
//        Long organizationId = 1L
//        Long id = 1L
//
//        when: "调用方法"
//        ldapService.syncLdapUser(organizationId, id)
//
//        then: "校验结果"
//        true
    }
}
