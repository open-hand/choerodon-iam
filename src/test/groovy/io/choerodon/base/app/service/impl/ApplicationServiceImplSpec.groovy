package io.choerodon.base.app.service.impl

import com.github.pagehelper.PageInfo
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.ApplicationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import io.choerodon.base.infra.dto.ApplicationDTO
import io.choerodon.base.infra.dto.ProjectDTO
import io.choerodon.base.infra.dto.devops.AppServiceUploadPayload
import io.choerodon.base.infra.feign.DevopsFeignClient
import io.choerodon.base.infra.mapper.ApplicationMapper
import io.choerodon.base.infra.mapper.ProjectMapper
import io.choerodon.core.exception.CommonException
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author wanghao* @Date 2019/8/15 15:44
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ApplicationServiceImplSpec extends Specification {
    @Autowired
    private ApplicationService applicationService
    @Autowired
    private ApplicationMapper realApplicationMapper
    @Shared
    def organizationId = 1
    private ApplicationMapper applicationMapper = Mock(ApplicationMapper)
    private TransactionalProducer producer = Mock(TransactionalProducer)
    private ProjectMapper projectMapper = Mock(ProjectMapper)
    private DevopsFeignClient devopsFeignClient = Mock(DevopsFeignClient)

    void setup() {
        DetailsHelper.setCustomUserDetails(Mock(CustomUserDetails))
        DependencyInjectUtil.setAttribute(applicationService, "applicationMapper", applicationMapper)
        DependencyInjectUtil.setAttribute(applicationService, "producer", producer)
        DependencyInjectUtil.setAttribute(applicationService, "projectMapper", projectMapper)
        DependencyInjectUtil.setAttribute(applicationService, "devopsFeignClient", devopsFeignClient)
    }

    void init() {
        def applicationDTO = null
        for (int i = 0; i < 5; i++) {
            applicationDTO = new ApplicationDTO()
            applicationDTO.setName("apim总前端" + i)
            applicationDTO.setCode("chot-c7napim" + i)
            applicationDTO.setOrganizationId(organizationId)
            applicationDTO.setImageUrl(i + "qeweweas.png")
            applicationDTO.setSourceId(1)
            applicationDTO.setType("custom")
            realApplicationMapper.insert(applicationDTO)
        }
    }

    def "updateNameAndLogoById"() {
        given: "构建参数"
        def applicationDTO = Mock(ApplicationDTO)
        when: "根据主键更新应用"
        applicationService.updateNameAndLogoById(applicationDTO)
        then: "校验结果"
        1 * producer.applyAndReturn(_, _)
    }

    def "verifyCode"() {
        given: "构建参数"
        def organizationId = 1L
        def code = "api-service"
        when: "校验code的唯一性"
        applicationService.verifyCode(organizationId, code)
        then: "校验结果"
        1 * applicationMapper.select(_) >> Mock(ArrayList)
    }

    def "listAppsByUserIdAndOrgId"() {
        given: "构建参数"
        def organizationId = 1L
        def asTemplate = true
        when: "查询用户在组织下的应用"
        applicationService.listAppsByUserIdAndOrgId(organizationId, asTemplate)
        then: "校验结果"
        1 * applicationMapper.selectAppsByUserIdAndOrgId(_, _, _)
    }

    def "pagingByOptions"() {
        given: "构建参数"
        init()
        DependencyInjectUtil.setAttribute(applicationService, "applicationMapper", realApplicationMapper)

        def organizationId = 1L
        def doPage = true
        def pageRequest = new PageRequest(1, 7)
        pageRequest.setSort(new Sort(new Sort.Order("id")))
        def name = "apim总前端"
        def code = "chot-c7napim1"
        def type = "custom"
        def params = new String[2]
        params[0] = "ts"
        params[1] = "ch"

        when: "查询应用下的版本，doPage == true"
        def res = applicationService.pagingByOptions(organizationId, doPage, pageRequest, name, code, type, params)
        then: "校验结果"
        res.list.size() > 0

        when: "查询应用下的版本，doPage == false"
        doPage = false
        res = applicationService.pagingByOptions(organizationId, doPage, pageRequest, name, code, type, params)
        then: "校验结果"
        res.list.size() > 0

        cleanup:
        DependencyInjectUtil.setAttribute(applicationService, "applicationMapper", applicationMapper)


    }

    def "queryProjectByApp"() {
        given: "构建参数"
        def applicationId = 1L
        when: "查询应用关联的项目"
        applicationService.queryProjectByApp(applicationId)
        then: "校验结果"
        1 * projectMapper.selectOne(_)
    }

    def "getAppByProject"() {
        given: "构建参数"
        def projectId = 1L

        when: "查询应用关联的项目,项目不存在"
        applicationService.getAppByProject(projectId)
        then: "校验结果"
        def e = thrown(CommonException)
        e.message == "error.project.not.exist"
        1 * projectMapper.selectByPrimaryKey(_) >> null

        when: "查询应用关联的项目"
        applicationService.getAppByProject(projectId)
        then: "校验结果"
        1 * projectMapper.selectByPrimaryKey(_) >> Mock(ProjectDTO)
        1 * applicationMapper.selectByPrimaryKey(_)

    }

    def "getAppServiceVersionInfo"() {
        given: "构建参数"
        def id = 1L
        def appServiceMarketVOList = new ArrayList<AppServiceUploadPayload>()
        appServiceMarketVOList.add(new AppServiceUploadPayload())
        appServiceMarketVOList.add(new AppServiceUploadPayload())
        def respose = ResponseEntity.ok(PageInfo.of(appServiceMarketVOList))

        when: "获取应用服务版本信息,devopsFeignClient调用失败"
        def res = applicationService.getAppServiceVersionInfo(id)
        then: "校验结果"
        res.isEmpty()
        1 * devopsFeignClient.pageByAppId(id, 0, 0) >> null

        when: "获取应用服务版本信息"
        res = applicationService.getAppServiceVersionInfo(id)
        then: "校验结果"
        !res.isEmpty()
        1 * devopsFeignClient.pageByAppId(id, 0, 0) >> respose

    }

    def "getAppById"() {
        given: "构建参数"
        def id = 1L
        when: "根据Id查应用"
        applicationService.getAppById(id)
        then: "校验结果"
        1 * applicationMapper.selectByPrimaryKey(_)
    }

}
