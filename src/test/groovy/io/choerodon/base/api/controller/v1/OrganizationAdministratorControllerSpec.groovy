package io.choerodon.base.api.controller.v1

import io.choerodon.base.IAMServiceApplication
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.UserSearchDTO
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author bgzyy
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = IAMServiceApplication.class)
@Import(IntegrationTestConfiguration)
@Stepwise
class OrganizationAdministratorControllerSpec extends Specification {

    private static final String BASE_PATH = "/v1/organizations/{organization_id}/org_administrator"

    @Autowired
    private TestRestTemplate restTemplate

    def "pagingQueryOrgAdministrator"() {
        given: "构造请求参数"
        def organizationId = 1
        def userSearchDTO = new UserSearchDTO()

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH + "/list", userSearchDTO, ExceptionResponse, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "createOrgAdministrator"() {
        given: "构造请求参数"
        def organizationId = 1
        def userIds = 1L

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH + "?id=" + userIds, null, Boolean, organizationId)

        then: "校验结果"
        entity.getBody().is(true)
    }

    def "deleteOrgAdministrator"() {
        given: "构造请求参数"
        def organizationId = 1
        def id = 1

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.DELETE, null, Boolean, organizationId, id)

        then: "校验结果"
        entity.getBody().is(true)
    }
}
