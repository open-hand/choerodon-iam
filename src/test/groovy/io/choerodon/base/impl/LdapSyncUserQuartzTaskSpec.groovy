package io.choerodon.base.app.service.impl

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.LdapService
import io.choerodon.base.app.task.LdapSyncUserQuartzTask
import io.choerodon.base.infra.utils.ldap.LdapSyncUserTask
import io.choerodon.base.infra.mapper.OrganizationMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class LdapSyncUserQuartzTaskSpec extends Specification {

    private LdapService ldapService = Mock(LdapService)
    @Autowired
    private OrganizationMapper organizationMapper
    @Autowired
    private LdapSyncUserTask ldapSyncUserTask
//    @Autowired
//    private ILdapService iLdapService
//    private LdapHistoryRepository ldapHistoryRepository = Mock(LdapHistoryRepository)
    private LdapSyncUserQuartzTask ldapSyncUserQuartzTask

    def setup() {
        ldapSyncUserQuartzTask = new LdapSyncUserQuartzTask(ldapService,
                organizationMapper, ldapSyncUserTask, ldapHistoryRepository, iLdapService)
    }

//    def "SyncLdapUser"() {
//        given: "构造请求参数"
//        Map<String, Object> map = new HashMap<>()
//        map.put("organizationCode", "error")
//        map.put("countLimit", 500)
//        LdapDTO ldapDTO = new LdapDTO()
//        ldapDTO.setId(1L)
//        ldapDTO.setObjectClass("person")
//        ldapDTO.setOrganizationId(1L)
//
//        when: "调用方法"
//        ldapSyncUserQuartzTask.syncLdapUser(map)
//
//        then: "校验结果"
//        def exception = thrown(CommonException)
//        exception.message.equals("error.ldapSyncUserTask.organizationNotNull")
//
//        when: "调用方法"
//        map.put("organizationCode", "operation")
//        map.put("countLimit", 500)
//        ldapSyncUserQuartzTask.syncLdapUser(map)
//
//        then: "校验结果"
////        exception = thrown(CommonException)
//        exception.message.equals("error.ldapSyncUserTask.organizationNotNull")
//        1 * ldapService.queryByOrganizationId(_ as Long) >> { ldapDTO }
//        1 * ldapService.validateLdap(_ as Long, _ as Long) >> { ConvertHelper.convert(ldapDTO, LdapDO) }
//    }
}
