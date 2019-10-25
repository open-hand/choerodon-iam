package io.choerodon.base.api.controller.v1

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.ApplicationVersionService
import io.choerodon.base.infra.dto.ApplicationVersionDTO
import io.choerodon.core.exception.ExceptionResponse
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
 * @author wanghao
 * @Date 2019/8/16 16:44
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ProjectAppVersionControllerSpec extends Specification {
    @Autowired
    TestRestTemplate testRestTemplate
    @Autowired
    private ProjectAppVersionController projectAppVersionController
    @Shared
    def testUrl = "/v1/projects/{project_id}/applications/versions"
    @Shared
    def applicationId = 1
    @Shared
    def projectId = 1

    ApplicationVersionService applicationVersionService = Mock(ApplicationVersionService)

    void setup() {
        DependencyInjectUtil.setAttribute(projectAppVersionController, "applicationVersionService", applicationVersionService);
    }

    def "CreateAppVersion"() {
        given: "构建参数"
        def applicationVersionDTO = new ApplicationVersionDTO()
        applicationVersionDTO.setName("version-test-1")
        applicationVersionDTO.setStatusCode("released")

        when: "新建应用版本,不设置应用ID"
        def res = testRestTemplate.postForEntity(testUrl, applicationVersionDTO, ExceptionResponse, projectId)
        then: "校验结果"
        res.body.failed

        and:
        applicationVersionDTO.setApplicationId(1)
        when: "新建应用版本,设置应用ID"
        testRestTemplate.postForEntity(testUrl, applicationVersionDTO, ApplicationVersionDTO, projectId)
        then: "校验结果"
        1 * applicationVersionService.create(_)
    }

    def "UpdateAppVersion"() {
        given: "构建参数"
        def applicationVersionDTO = new ApplicationVersionDTO()
        applicationVersionDTO.setDescription("添加描述")
        def entity = new HttpEntity<>(applicationVersionDTO)
        when: "根据主键更新应用版本"
        testRestTemplate.exchange(testUrl + "/{id}", HttpMethod.PUT, entity, ApplicationVersionDTO, projectId, 1)
        then: "校验结果"
        1 * applicationVersionService.update(applicationId, _)
    }
    def "publishAppVersion"() {
        given: "构建参数"
        def entity = new HttpEntity<>()
        when: "根据主键发布应用版本"
        testRestTemplate.exchange(testUrl + "/publish/{id}", HttpMethod.PUT, entity, ApplicationVersionDTO, projectId, 1)
        then: "校验结果"
        1 * applicationVersionService.publishAppVersion(_)
    }
    def "archiveAppVersion"() {
        given: "构建参数"
        def entity = new HttpEntity<>()
        when: "根据主键归档应用版本"
        testRestTemplate.exchange(testUrl + "/archive/{id}", HttpMethod.PUT, entity, ApplicationVersionDTO, projectId, 1)
        then: "校验结果"
        1 * applicationVersionService.archiveAppVersion(_)
    }

    def "CheckAppVersionName"() {
        when: "name唯一性校验"
        testRestTemplate.getForEntity(testUrl + "/{application_id}/check/{name}", Boolean.class, projectId, applicationId, "version-test-1")
        then: "校验结果"
        1 * applicationVersionService.checkName(_, _)
    }

    def "ListByOptions"() {
        given: "设置参数"
        def params = new String[2]
        params[0] = "ts"
        params[1] = "co"
        when: "name唯一性校验"
        testRestTemplate.getForEntity(testUrl + "/{application_id}?&page={page}&size={size}&name={name}&description={description}&status={status}&params={params}",
                List.class, projectId, applicationId, 1, 2, "version-test-1", "描述", "released", params)
        then: "校验结果"
        1 * applicationVersionService.listByOptions(_, _, _, _, _, _)
    }

    def "DeleteAppVersion"() {
        given: "构建参数"
        def entity = new HttpEntity<>()
        when: "根据主键删除应用版本"
        testRestTemplate.exchange(testUrl + "/{id}", HttpMethod.DELETE, entity, Void, projectId, 1)
        then: "校验结果"
        1 * applicationVersionService.delete(_)
    }
}
