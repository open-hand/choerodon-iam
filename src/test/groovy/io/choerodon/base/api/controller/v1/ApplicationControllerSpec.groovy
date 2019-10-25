package io.choerodon.base.api.controller.v1

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.vo.ProjectAndAppVO
import io.choerodon.base.app.service.AppDownloadService
import io.choerodon.base.app.service.ApplicationService
import io.choerodon.base.infra.dto.ApplicationDTO
import io.choerodon.base.infra.dto.ProjectDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/1
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class ApplicationControllerSpec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    ApplicationController applicationController

    @Shared
    def testUrl = "/v1/applications"

    ApplicationService applicationService = Mock(ApplicationService)
    AppDownloadService appDownloadService = Mock(AppDownloadService)

    void setup() {
        DependencyInjectUtil.setAttribute(applicationController, "applicationService", applicationService)
        DependencyInjectUtil.setAttribute(applicationController, "appDownloadService", appDownloadService)
    }


    def "queryProjectByApp"() {
        when: "查询应用关联的项目"
        testRestTemplate.getForEntity(testUrl + "/{id}/project", ProjectDTO, 1)
        then: "验证结果"
        1 * applicationService.queryProjectByApp(_)
    }

    def "getAppById"() {
        when: "根据Id查询应用"
        testRestTemplate.getForEntity(testUrl + "/{id}", ApplicationDTO.class, 1)
        then: "验证结果"
        1 * applicationService.getAppById(_)
    }

    def "getProjectAndAppByToken"() {
        given:
        def token = "12321"
        when: "通过应用token查询项目"
        testRestTemplate.getForEntity(testUrl + "?token={token}", ProjectAndAppVO.class, token)
        then: "验证结果"
        1 * applicationService.getProjectAndAppByToken(_)
    }

    def "downloadApplication"() {
        given:
        def publishAppVersionId = 1L
        def organizationId = 1L
        def entity = new HttpEntity<>()
        when: "下载SaaS应用"
        testRestTemplate.postForEntity(testUrl + "/{publish_app_version_id}/download?organization_id={organizationId}", entity, ApplicationDTO.class, publishAppVersionId, organizationId)
        then: "验证结果"
        1 * appDownloadService.downloadApplication(_, _)
    }

    def "completeDownloadApplication"() {
        given:
        def appDownloadRecordId = 1L
        def appVersionId = 1L
        Set<Long> serviceVersionIds = new HashSet<>()
        serviceVersionIds.add(1L)
        when: "完成SaaS应用下载"
        testRestTemplate.postForEntity(testUrl + "/{app_download_record_id}/complete_downloading?app_version_id={appVersionId}", serviceVersionIds, Void.class, appDownloadRecordId, appVersionId)
        then: "验证结果"
        1 * appDownloadService.completeDownloadApplication(_, _, _)
    }

    def "failToDownloadApplication"() {
        given:
        def appDownloadRecordId = 1L
        def entity = new HttpEntity<>()
        when: "SaaS应用下载失败"
        testRestTemplate.exchange(testUrl + "/{app_download_record_id}/fail_downloading", HttpMethod.PUT, entity, Void.class, appDownloadRecordId)
        then: "验证结果"
        1 * appDownloadService.failToDownloadApplication(_)
    }
}
