package io.choerodon.base.app.service.impl

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.vo.PublishAppPageVO
import io.choerodon.base.app.service.*
import io.choerodon.base.infra.dto.ApplicationDTO
import io.choerodon.base.infra.dto.MktPublishApplicationDTO
import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum
import io.choerodon.base.infra.feign.MarketFeignClient
import io.choerodon.base.infra.mapper.ApplicationMapper
import io.choerodon.base.infra.mapper.MktPublishApplicationMapper
import io.choerodon.base.infra.mapper.OrganizationMapper
import io.choerodon.base.infra.retrofit.PublishAppRetrofitCalls
import io.choerodon.core.exception.CommonException
import io.choerodon.core.exception.ext.UpdateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * MktPublishApplicationServiceImplSpec
 *
 * @author pengyuhua* @date 2019/10/8
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class MktPublishApplicationServiceImplSpec extends Specification {
    @Autowired
    private MktPublishApplicationService mktPublishApplicationService

    private ApplicationSvcVersionRefService applicationSvcVersionRefService = Mock()
    private MktPublishVersionInfoService mktPublishVersionInfoService = Mock()
    private ApplicationVersionService applicationVersionService = Mock()
    private MktApplyAndPublishService mktApplyAndPublishService = Mock()
    private MktPublishApplicationMapper mktPublishApplicationMapper = Mock()
    private OrganizationMapper organizationMapper = Mock()
    private ApplicationMapper applicationMapper = Mock()
    private PublishAppRetrofitCalls publishAppRetrofitCalls = Mock()
    private MarketFeignClient marketFeignClient = Mock()

    @Shared def queryDTO = new MktPublishApplicationDTO().setId(2L).setReleased(false)
    @Shared def queryApplicationDTO = new ApplicationDTO().setProjectId(2L)
    @Shared MktPublishApplicationDTO upVO = new MktPublishApplicationDTO()

    def "setup"() {
        DependencyInjectUtil.setAttribute(mktPublishApplicationService, "mktPublishApplicationMapper", mktPublishApplicationMapper)
        DependencyInjectUtil.setAttribute(mktPublishApplicationService, "applicationMapper", applicationMapper)
        DependencyInjectUtil.setAttribute(mktPublishApplicationService, "mktApplyAndPublishService", mktApplyAndPublishService)
    }


    /**
     * 分页接口 分页插件返回值拿不到暂不测
     */
    @Ignore
    def "PageSearchPublishApps"() {
        given: "数据构建"
        def projectId = 11L
        List<ApplicationDTO> selectAppDTOs = new ArrayList<>()
        def applicationDTO = new ApplicationDTO().setProjectId(projectId)
        applicationDTO.setId(21L)
        selectAppDTOs.add(applicationDTO)
        List<MktPublishApplicationDTO> selectMktAppDTOs = new ArrayList<>()
        selectMktAppDTOs.add(new MktPublishApplicationDTO().setId(31L).setRefAppId(21L).setReleased(false))
        selectMktAppDTOs.add(new MktPublishApplicationDTO().setId(32L).setRefAppId(21L).setReleased(true))
        List<PublishAppPageVO> finalList = new ArrayList<>()
        def pubvo = new PublishAppPageVO()
        pubvo.setId(31L)
        pubvo.setStatus(PublishAppVersionStatusEnum.UNPUBLISHED.value())
        finalList.add(pubvo)


        and: "mock"
        mktPublishApplicationMapper.countProjectApps(_) >> 1
        applicationMapper.select(_) >> selectAppDTOs
        mktPublishApplicationMapper.select(_) >> selectMktAppDTOs
        mktPublishApplicationMapper.pageSearchPublishApps(_, _, _, _, _) >> finalList

        when: ""
        mktPublishApplicationService.pageSearchPublishApps(projectId, new PageRequest(1,0), null, null, null, null)

        then: ""
    }

    @Unroll
    def "queryMktPublishAppDetail exception test"() {
        given: "数据构建"
        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> mktPublishDto
        mktApplyAndPublishService.checkAppExist(_) >> applicationDto

        when: "方法调用"
        mktPublishApplicationService.queryMktPublishAppDetail(1L, 2L)

        then: "结果比对"
        def excp = thrown(CommonException)
        excp.code == exception

        where: "测试数据"
        mktPublishDto | applicationDto          | exception
        null          | queryApplicationDTO     | "error.mkt.publish.application.does.not.exist"
        queryDTO      | queryApplicationDTO     | "error.publish.app.project.id.not.equaled"
    }

    def "queryMktPublishAppDetail"() {
        given: "数据构建"
        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> queryDTO
        mktApplyAndPublishService.checkAppExist(_) >> queryApplicationDTO

        when: "方法调用"
        def result = mktPublishApplicationService.queryMktPublishAppDetail(2L, 2L)

        then: "结果比对"
        !result.getCategoryDefault()
        result.getId() == 2L
    }

    @Unroll
    def "updateMktPublishAppInfo exception"() {
        given: "数据构建"
        upVO.setId(1L).setName("name").setNotificationEmail("dsdsd@qq.com")
        upVO.setPublishType("deploy").setFree(true).setCategoryName("yingyong").setCategoryCode("yingyong01").setRemark("remark").setReleased(false)
        upVO.setObjectVersionNumber(1L)

        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> selectAppDto
        mktPublishApplicationMapper.updateByPrimaryKey(_) >> upResult

        when: "方法调用"
        mktPublishApplicationService.updateMktPublishAppInfo(1L, selectAppDto, released)

        then: "异常比对"
        def excp = thrown(exceptionType)
        excp.code == exception

        where: "数据表"
        selectAppDto  |  upResult | released | exceptionType   |  exception
        null          |  1        | false    | CommonException |  "error.mkt.publish.application.does.not.exist"
        upVO          |  1        | true     | CommonException |  "error.mkt.publish.application.release.not.equaled"
        upVO          |  -1       | false    | UpdateException |  "error.mkt.publish.application.update"
    }

    def "updateMktPublishAppInfo normal"() {
        given: "数据构建"
        upVO.setId(1L).setName("name").setNotificationEmail("dsdsd@qq.com")
        upVO.setPublishType("deploy").setFree(true).setCategoryName("yingyong").setCategoryCode("yingyong01").setRemark("remark").setReleased(false)
        upVO.setObjectVersionNumber(1L)

        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> selectAppDto
        mktPublishApplicationMapper.updateByPrimaryKey(_) >> upResult

        when: "方法调用"
        def result = mktPublishApplicationService.updateMktPublishAppInfo(1L, selectAppDto, released)

        then: "异常比对"
        result.getId() == 1L
        result.getRemark() == "remark"
        result.getNotificationEmail() == "dsdsd@qq.com"

        where: "数据表"
        selectAppDto  |  upResult | released | exceptionType   |  exception
        upVO          |  1        | false    | _               |  _
    }
}
