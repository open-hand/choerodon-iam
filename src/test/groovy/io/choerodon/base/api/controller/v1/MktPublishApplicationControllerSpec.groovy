package io.choerodon.base.api.controller.v1

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.AppCategoryDTO
import io.choerodon.base.api.dto.CommonCheckResultVO
import io.choerodon.base.api.vo.MarketPublishApplicationVO
import io.choerodon.base.api.vo.MktPublishAppVersionVO
import io.choerodon.base.api.vo.PublishAppPageVO
import io.choerodon.base.app.service.MktApplyAndPublishService
import io.choerodon.base.app.service.MktPublishApplicationService
import io.choerodon.base.app.service.MktPublishVersionInfoService
import io.choerodon.base.infra.dto.MktPublishApplicationDTO
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.*

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * MktPublishApplicationControllerSpec
 *
 * @author pengyuhua* @date 2019/9/25
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
@Subject(MktPublishApplicationController)
class MktPublishApplicationControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/projects/{project_id}/publish_applications"

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private MktPublishApplicationController mktPublishApplicationController

    private MktPublishApplicationService mktPublishApplicationService = Mock()
    private MktPublishVersionInfoService mktPublishVersionInfoService = Mock()
    private MktApplyAndPublishService mktApplyAndPublishService = Mock()

    @Shared Long projectId = 1L
    @Shared Long applicationId = 2L
    @Shared def dSVaData = new StringBuffer()

    def "setupSpec"() {
        for ( def i = 0; i< 20; i++) {
            dSVaData.append( i + "adsasdadwsasdxq")
        }
    }

    def "setup"() {
        DependencyInjectUtil.setAttribute(mktPublishApplicationController, "mktPublishApplicationService", mktPublishApplicationService)
        DependencyInjectUtil.setAttribute(mktPublishApplicationController, "mktPublishVersionInfoService", mktPublishVersionInfoService)
        DependencyInjectUtil.setAttribute(mktPublishApplicationController, "mktApplyAndPublishService", mktApplyAndPublishService)
    }

    def "List"() {
        given: "数据构建"
        def variables = new HashMap()
        variables.put("project_id", projectId)
        variables.put("version", "0.0.1")
        variables.put("status", "published")
        variables.put("free", "true")
        variables.put("publish_type", "all")
        variables.put("name", "运营")
        variables.put("source_app_name", "测试")
        variables.put("description", "description")
        variables.put("params", ["sss", "ssss"])

        when: "方法调用(不传过滤参数)"
        restTemplate.getForEntity(BASE_PATH, PublishAppPageVO, projectId)

        then: "结果检查"
        1 * mktPublishApplicationService.pageSearchPublishApps(_, _, _, _, _, _)

        when: "方法调用(传过滤参数)"
        restTemplate.getForEntity(BASE_PATH + "?version={version}&status={status}&free={free}&publish_type={publish_type}&" +
                "name={name}&source_app_name={source_app_name}&description={description}&params={params}", PublishAppPageVO, variables)

        then: "结果检查"
        1 * mktPublishApplicationService.pageSearchPublishApps(_, _, _, _, _, _)
    }

    def "ListMktAppVersions"() {
        given: "数据构建"

        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/versions?application_id={applicationId}", MktPublishAppVersionVO, projectId, applicationId)

        then: "结果检查"
        1 * mktPublishVersionInfoService.listMktAppVersions(_)
    }

    def "QueryMktPublishAppDetail"() {
        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/{application_id}/detail", MarketPublishApplicationVO, projectId, applicationId)

        then: "结果检查"
        1 * mktPublishApplicationService.queryMktPublishAppDetail(_, _)
    }

    @Unroll
    def "UpdateMktUnPublishApp Validate1"() {
        given: "数据构建"
        def updateVO = new MktPublishApplicationDTO()
                .setName(name)  // 两种校验情况 空值与格式
                .setDescription(description) // 两种校验情况 空值与长度校验
                .setImageUrl(imageUrl)
                .setNotificationEmail(notificationEmail)
                .setPublishType(publishType)
                .setFree(free)
                .setOverview(overview)
                .setCategoryName(categoryName)

        when: "方法调用(校验检查)"
        println(dSVaData.size())
        def result = restTemplate.exchange(BASE_PATH + "/unpublish_apps/{id}", HttpMethod.PUT, new HttpEntity<MktPublishApplicationDTO>(updateVO), ExceptionResponse, projectId, applicationId)

        then: "异常比对"
        result.getBody().getCode() == errorCode

        where: "测试数据表"
        name | description | imageUrl | notificationEmail | publishType | free | overview | categoryName || errorCode
        null | "dse"       | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.name.can.not.be.empty"
        ""   | "dse"       | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.name.can.not.be.empty"
        "#a" | "dse"       | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.name.invalid"
        "a1" | null        | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.description.can.not.be.empty"
        "a1" | ""          | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.description.can.not.be.empty"
        "a1" | dSVaData.toString()    | "ds.com" | "aaa@qq.com"      | "all"       | true | "overvi" || "categoryN"  | "error.mkt.publish.application.description.size"
        "a1" | "descri"    | null     | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.image.url.can.not.be.empty"
        "a1" | "descri"    | ""       | "aaa@qq.com"      | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.image.url.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | null              | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.notification.email.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | ""                | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.notification.email.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | "all"       | true | "overvi" | "categoryN"  || "error.mkt.publish.application.notification.email.invalid"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | null        | true | "overvi" | "categoryN"  || "error.mkt.publish.application.publish.type.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | ""          | true | "overvi" | "categoryN"  || "error.mkt.publish.application.publish.type.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | "all"       | null | "overvi" | "categoryN"  || "error.mkt.publish.application.free.can.not.be.null"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | "all"       | true | "overvi" | null         || "error.mkt.publish.application.category.name.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | "all"       | true | "overvi" | ""           || "error.mkt.publish.application.category.name.can.not.be.empty"
        "a1" | "descri"    | "ds.com" | "dasdaqq.com"     | "all"       | true | "overvi" | "a@1"        || "error.mkt.publish.application.category.name.invalid"
    }

    def "UpdateMktUnPublishApp Normal"() {
        given: "数据构建"
        def updateVO = new MktPublishApplicationDTO()
                .setName("name")
                .setDescription("description")
                .setImageUrl("imageurl.com")
                .setNotificationEmail("notification.email@qq.com")
                .setPublishType("all")
                .setFree(true)
                .setOverview("overview")
                .setCategoryName("categoryName")

        when: "方法调用"
        restTemplate.exchange(BASE_PATH + "/unpublish_apps/{id}", HttpMethod.PUT, new HttpEntity<MktPublishApplicationDTO>(updateVO), MktPublishApplicationDTO, projectId, applicationId)

        then: "结果校验"
        1 * mktPublishApplicationService.updateMktPublishAppInfo(_, _, _)
    }

    @Unroll
    def "UpdateMktPublishedApp validate"() {
        given: "数据构建"
        def updateVO = new MktPublishApplicationDTO()
                .setDescription(description)
                .setImageUrl(imageUrl)
                .setOverview(overview)

        when: "方法调用(校验检查)"
        def result = restTemplate.exchange(BASE_PATH + "/published_apps/{id}", HttpMethod.PUT, new HttpEntity<MktPublishApplicationDTO>(updateVO), ExceptionResponse, projectId, applicationId)

        then: "异常比对"
        result.getBody().getCode() == errorCode

        where: "测试数据表"
        description  |  imageUrl  |  overview  |   errorCode
        null         |  "dsd.com" |  "dsdsdsa" | "error.mkt.publish.application.description.can.not.be.empty"
        ""           |  "dsd.com" |  "dsdsdsa" | "error.mkt.publish.application.description.can.not.be.empty"
        dSVaData.toString() |  "dsd.com" |  "dsdsdsa" | "error.mkt.publish.application.description.size"
        "desaad"     |  null      |  "dsdsdsa" | "error.mkt.publish.application.image.url.can.not.be.empty"
        "desaad"     |  ""        |  "dsdsdsa" | "error.mkt.publish.application.image.url.can.not.be.empty"
        "desaad"     |  "dsd.com" |  null      | "error.mkt.publish.application.update.overview.cannot.be.empty"
        "desaad"     |  "dsd.com" |  ""        | "error.mkt.publish.application.update.overview.cannot.be.empty"
    }

    def "UpdateMktPublishedApp Normal"() {
        given: "数据构建"
        def updateVO = new MktPublishApplicationDTO()
                .setDescription("desaad")
                .setImageUrl("dsd.com")
                .setOverview("dsdsdsa")

        when: "方法调用[正常调用]"
        restTemplate.exchange(BASE_PATH + "/published_apps/{id}", HttpMethod.PUT, new HttpEntity<MktPublishApplicationDTO>(updateVO), MktPublishApplicationDTO, projectId, applicationId)

        then: "结果比对"
        1 * mktPublishApplicationService.updateMktPublishAppInfo(_, _, _)
    }

    def "QueryMktPublishAppVersionDetail"() {
        given: "数据构建"
        def versionId = 1L

        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/versions/{version_id}/detail", MktPublishAppVersionVO, projectId, versionId)

        then: "结果比对"
        1 * mktPublishVersionInfoService.queryMktPublishAppVersionDetail(_)
    }

    def "ListApplicationInfoByAppIds"() {
        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/list_by_ids?ids=1,2", MktPublishApplicationDTO, projectId)

        then: "结果比对"
        1 * mktPublishApplicationService.listApplicationInfoByIds(_)
    }

    def "GetEnableCategoryList"() {
        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/app_categories/list/enable", AppCategoryDTO, projectId)

        then: "结果比对"
        1 * mktPublishApplicationService.getEnableCategoryList(_)
    }

    def "CategoriesCheck"() {
        given: "数据构建"
        def versionId = 1L
        def category_name = "category_name"

        when: "方法调用"
        restTemplate.getForEntity(BASE_PATH + "/app_categories/check", CommonCheckResultVO, projectId, category_name)

        then: "结果比对"
        1 * mktPublishApplicationService.categoriesCheck(_)
    }
}
