package io.choerodon.base.api.controller.v1

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.vo.ApplicationVO
import io.choerodon.base.app.service.ApplicationService
import io.choerodon.base.infra.dto.ApplicationDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author wanghao*
 * @Date 2019/8/16 16:44
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ProjectApplicationControllerSpec extends Specification {
    @Autowired
    TestRestTemplate testRestTemplate
    @Autowired
    private ProjectApplicationController projectApplicationController
    @Shared
    def testUrl = "/v1/projects/{project_id}/applications"
    @Shared
    def projectId = 1L
    ApplicationService applicationService = Mock(ApplicationService)

    void setup() {
        DependencyInjectUtil.setAttribute(projectApplicationController, "applicationService", applicationService);
    }

    def "getAppsByProject"() {
        when: "查询项目关联的所有应用"
        testRestTemplate.getForEntity(testUrl, List.class, projectId)
        then: "验证结果"
        1 * applicationService.getAppByProject(_)
    }
    def "getSingleAppByProject"() {
        when: "查询项目直接关联的应用"
        testRestTemplate.getForEntity(testUrl + "/singleton", ApplicationVO.class, projectId)
        then: "验证结果"
        1 * applicationService.getSingleAppByProject(_)
    }

    def "getAppServiceVersionInfo"() {
        given: "设置参数"
        def id = 1L
        when: "查询应用下服务和服务版本信息"
        testRestTemplate.getForEntity(testUrl + "/{id}/services_info", List.class, projectId, id)
        then: "验证结果"
        1 * applicationService.getAppServiceVersionInfo(_)

    }

    def "updateApplication"() {
        given: "设置参数"
        def id = 1L
        def applicationDTO = new ApplicationDTO()
        applicationDTO.setName("123")
        def httpEntity = new HttpEntity<>(applicationDTO)
        when: "更新应用"
        testRestTemplate.exchange(testUrl + "/{id}", HttpMethod.PUT, httpEntity, ApplicationDTO.class, projectId, id)
        then: "验证结果"
        1 * applicationService.updateNameAndLogoById(_)
    }
}
