package io.choerodon.iam.api.controller.v1

import io.choerodon.core.domain.Page
import io.choerodon.iam.IntegrationTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Subject(OrganizationAdminC7nController)
class OrganizationAdminC7nControllerSpec extends Specification {
    def BASE_URL = "/choerodon/v1/organizations/1"
    @Autowired
    TestRestTemplate testRestTemplate;


    void setup() {

    }

    def "PagingQueryOrgAdministrator"() {
        given: "构造参数"
        when: '应用查询ci脚本文件 - 应用服务不存在'
        def entity = testRestTemplate.getForEntity("/choerodon/v1/organizations/1/org_administrator", Page.class)
        then: '校验返回结果'
        // 校验响应码
        entity.statusCode.is4xxClientError()
        // 校验响应体
        entity.body == null
    }

    def "CreateOrgAdministrator"() {
        given: "构造参数"
        Long[] userIds = [1]

        when: '应用查询ci脚本文件 - 应用服务不存在'
        def entity = testRestTemplate.postForEntity(BASE_URL + "/org_administrator?id={userIds}", null, Void, userIds)
        then: '校验返回结果'
        // 校验响应码
        entity.statusCode.is2xxSuccessful()
    }

}
