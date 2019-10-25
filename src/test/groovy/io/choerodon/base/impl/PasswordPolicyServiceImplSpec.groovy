package io.choerodon.base.app.service.impl

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.PasswordPolicyService
import io.choerodon.base.infra.dto.OrganizationDTO
import io.choerodon.base.infra.dto.PasswordPolicyDTO
import io.choerodon.base.infra.mapper.OrganizationMapper
import io.choerodon.base.infra.mapper.PasswordPolicyMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PasswordPolicyServiceImplSpec extends Specification {
    @Autowired
    private PasswordPolicyService passwordPolicyService
    @Autowired
    private PasswordPolicyMapper policyMapper
    @Autowired
    private OrganizationMapper organizationMapper

    //测试后删除数据
    @Transactional
    def "Create"() {
        given: "构造请求参数"
        OrganizationDTO organizationDO = new OrganizationDTO()
        organizationDO.setName("password_policy")
        organizationDO.setCode("password_policy")
        organizationMapper.insert(organizationDO)
        PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO()
        passwordPolicyDTO.setCode("code")
        passwordPolicyDTO.setName("passwordPolicy")
        passwordPolicyDTO.setOrganizationId(organizationDO.getId())

        when: "调用方法"
        PasswordPolicyDTO passwordPolicyDTO1 =
                passwordPolicyService.create(organizationDO.getId(), passwordPolicyDTO)

        then: "校验结果"
        noExceptionThrown()
        passwordPolicyDTO1.getCode().equals(passwordPolicyDTO.getCode())
        passwordPolicyDTO1.getName().equals(passwordPolicyDTO.getName())
        passwordPolicyDTO1.getOrganizationId().equals(passwordPolicyDTO.getOrganizationId())
    }
}
