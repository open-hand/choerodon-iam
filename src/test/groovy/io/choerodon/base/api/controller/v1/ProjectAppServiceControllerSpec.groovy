package io.choerodon.base.api.controller.v1


import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.ApplicationServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author wanghao
 * @Date 2019/8/15 14:15
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ProjectAppServiceControllerSpec extends Specification {
    @Autowired
    TestRestTemplate testRestTemplate
    @Autowired
    private ProjectAppServiceController projectAppServiceController
    @Shared
    def testUrl = "/v1/projects/{project_id}/applications/{application_id}/services"
    @Shared
    def applicationId = 1L
    @Shared
    def projectId = 1L
    private ApplicationServiceService applicationServiceService = Mock(ApplicationServiceService)

    void setup() {
        DependencyInjectUtil.setAttribute(projectAppServiceController, "applicationServiceService", applicationServiceService);
    }

    def "pagingAppVersionByOptions"() {
        given: "分页查询应用的所有服务"
        def params = new String[2]
        params[0] = "ts"
        params[1] = "co"
        when: "分页查询应用的所有服务"
        testRestTemplate.getForEntity(testUrl + "?&page={page}&size={size}&name={name}&code={code}&active={active}&type={type}&params={params}",
                String.class, projectId, applicationId, 1, 2, "service", "abc", true, "custom", params)
        then: "验证结果"
        1 * applicationServiceService.pagingByOptions(_, _, _, _, _, _, _)
    }

    def "listVersionsByAppServiceId"() {
        given: "根据应用服务ID查询所对应的版本列表"
        def app_service_id = 1

        when: "调用listVersionsByAppServiceId接口"
        testRestTemplate.getForEntity(testUrl + "/{id}/versions", List.class, projectId, applicationId, app_service_id)

        then: "结果校验"
        1 * applicationServiceService.listVersionsByAppServiceId(_)
    }
}
