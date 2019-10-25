package io.choerodon.base.app.service.impl

import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.vo.AppServiceDetailsVO
import io.choerodon.base.api.vo.MktUnPublishVersionInfoVO
import io.choerodon.base.app.service.ApplicationSvcVersionRefService
import io.choerodon.base.app.service.MktPublishVersionInfoService
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO
import io.choerodon.base.infra.dto.ApplicationVersionDTO
import io.choerodon.base.infra.dto.MktPublishApplicationDTO
import io.choerodon.base.infra.dto.MktPublishVersionInfoDTO
import io.choerodon.base.infra.enums.ApplicationSvcVersionStatusEnum
import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum
import io.choerodon.base.infra.mapper.ApplicationSvcVersionRefMapper
import io.choerodon.base.infra.mapper.ApplicationVersionMapper
import io.choerodon.base.infra.mapper.MktPublishApplicationMapper
import io.choerodon.base.infra.mapper.MktPublishVersionInfoMapper
import io.choerodon.core.exception.CommonException
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * MktPublishVersionInfoServiceImplSpec
 *
 * @author pengyuhua* @date 2019/9/27
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class MktPublishVersionInfoServiceImplSpec extends Specification {

    @Autowired
    private MktPublishVersionInfoService mktPublishVersionInfoService

    private MktPublishApplicationMapper mktPublishApplicationMapper = Mock()
    private MktPublishVersionInfoMapper mktPublishVersionInfoMapper = Mock()
    private ApplicationVersionMapper applicationVersionMapper = Mock()
    private ApplicationSvcVersionRefService applicationSvcVersionRefService = Mock()
    private ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper = Mock()

    @Shared private def appId = 1L

    def "setup"() {
        DependencyInjectUtil.setAttribute(mktPublishVersionInfoService, "mktPublishApplicationMapper", mktPublishApplicationMapper)
        DependencyInjectUtil.setAttribute(mktPublishVersionInfoService, "mktPublishVersionInfoMapper", mktPublishVersionInfoMapper)
        DependencyInjectUtil.setAttribute(mktPublishVersionInfoService, "applicationVersionMapper", applicationVersionMapper)
        DependencyInjectUtil.setAttribute(mktPublishVersionInfoService, "applicationSvcVersionRefService", applicationSvcVersionRefService)
        DependencyInjectUtil.setAttribute(mktPublishVersionInfoService, "applicationSvcVersionRefMapper", applicationSvcVersionRefMapper)
    }

    @Ignore
    def "listMktAppVersions test exception" () {
        given: ""

        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> null

        when: "方法调用（市场应用信息不存在）"
        mktPublishVersionInfoService.listMktAppVersions(appId)

        then: "结果比对"
        def excp = thrown(CommonException)
        excp.code == "error.mkt.publish.application.does.not.exist"
    }

    def "listMktAppVersions"() {
        given: ""
        List<MktPublishApplicationDTO> selectAllAppDTOs = new ArrayList<>()
        List<MktPublishApplicationDTO> selectAllVersionInfoDTOs = new ArrayList<>()
        List<MktPublishApplicationDTO> selectAllSvcVersionRefDTOs = new ArrayList<>()
        selectAllAppDTOs.add(new MktPublishApplicationDTO().setId(appId).setRefAppId(2L))
        selectAllVersionInfoDTOs.add(new MktPublishVersionInfoDTO().setId(1L).setPublishApplicationId(appId).setStatus(PublishAppVersionStatusEnum.PUBLISHED.value()))
        selectAllSvcVersionRefDTOs.add(new ApplicationSvcVersionRefDTO().setApplicationVersionId(1L).setStatus(ApplicationSvcVersionStatusEnum.PROCESSING.value()))

        and: "mock"
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> new MktPublishApplicationDTO().setRefAppId(2L)
        mktPublishApplicationMapper.select(_) >> selectAllAppDTOs
        mktPublishVersionInfoMapper.select(_) >> selectAllVersionInfoDTOs
        applicationSvcVersionRefService.getSvcVersions(_) >> new ArrayList<AppServiceDetailsVO>()
        applicationVersionMapper.selectByPrimaryKey(_) >> new ApplicationVersionDTO().setId(1L).setVersion("0.0.1")
        applicationSvcVersionRefMapper.select(_) >> selectAllSvcVersionRefDTOs

        when: "方法调用"
        def result = mktPublishVersionInfoService.listMktAppVersions(appId)

        then: "结果比对"
        result.size() == 1
        result.get(0).getId() == 1L
        result.get(0).getVersion() == "0.0.1"
        result.get(0).getPublishing()
    }

    def "updateUnPublished test exception status.not.allow"() {
        given: "数据构建"
        def checkRe = new MktPublishVersionInfoDTO().setId(1L).setDocument("unpublished").setChangelog("changeLog")
        checkRe.setStatus(PublishAppVersionStatusEnum.UNDER_APPROVAL.value())
        def upVo = new MktUnPublishVersionInfoVO().setOverview("overview").setObjectVersionNumber(1L)

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> checkRe

        when: "方法调用"
        mktPublishVersionInfoService.updateUnPublished(1L, 1L, upVo, false)

        then: "异常比对"
        def excp = thrown(CommonException)
        excp.code == "error.mkt.publish.version.info.update.status.not.allow"
    }

    def "updateUnPublished test exception versionInfo update"() {
        given: "数据构建"
        def checkRe = new MktPublishVersionInfoDTO().setId(1L).setDocument("unpublished").setChangelog("changeLog")
        checkRe.setStatus(PublishAppVersionStatusEnum.UNPUBLISHED.value())
        def upVo = new MktUnPublishVersionInfoVO().setOverview("overview").setObjectVersionNumber(1L)

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> checkRe
        mktPublishVersionInfoMapper.updateByPrimaryKeySelective(_) >> -1

        when: "方法调用"
        mktPublishVersionInfoService.updateUnPublished(1L, 1L, upVo, false)

        then: "异常比对"
        def excp = thrown(CommonException)
        excp.code == "error.mkt.publish.version.info.update"
    }

    def "updateUnPublished test exception app update"() {
        given: "数据构建"
        def checkRe = new MktPublishVersionInfoDTO().setId(1L).setDocument("unpublished").setChangelog("changeLog").setPublishApplicationId(1L)
        checkRe.setStatus(PublishAppVersionStatusEnum.UNPUBLISHED.value())
        def upVo = new MktUnPublishVersionInfoVO().setOverview("overview").setObjectVersionNumber(1L)

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> checkRe
        mktPublishVersionInfoMapper.updateByPrimaryKeySelective(_) >> 1
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> new MktPublishApplicationDTO().setId(1L)
        mktPublishApplicationMapper.updateByPrimaryKeySelective(_) >> -1

        when: "方法调用"
        mktPublishVersionInfoService.updateUnPublished(1L, 1L, upVo, false)

        then: "异常比对"
        def excp = thrown(CommonException)
        excp.code == "error.mkt.publish.application.update"
    }

    def "updateUnPublished test normal update"() {
        given: "数据构建"
        def checkRe = new MktPublishVersionInfoDTO().setId(1L).setDocument("unpublished").setChangelog("changeLog").setPublishApplicationId(1L)
        checkRe.setStatus(PublishAppVersionStatusEnum.UNPUBLISHED.value())
        def upVo = new MktUnPublishVersionInfoVO().setOverview("overview").setObjectVersionNumber(1L)
        def versions = new ArrayList<AppServiceDetailsVO>()
        versions.add(new AppServiceDetailsVO().setId(31L))

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> checkRe
        mktPublishVersionInfoMapper.updateByPrimaryKeySelective(_) >> 1
        mktPublishApplicationMapper.selectByPrimaryKey(_) >> new MktPublishApplicationDTO().setId(1L)
        mktPublishApplicationMapper.updateByPrimaryKeySelective(_) >> 1
        applicationVersionMapper.selectByPrimaryKey(_) >> new ApplicationVersionDTO().setId(21L)
        applicationSvcVersionRefService.getSvcVersions(_) >> versions

        when: "方法调用"
        def result = mktPublishVersionInfoService.updateUnPublished(1L, 1L, upVo, false)

        then: "异常比对"
        result.getId() == 1L
        result.getContainServices().size() == 1
    }


    def "checkMktPublishVersionInfoExit test exception"() {
        given: "数据构建"
        def versionId = 1L

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> null

        when: "方法调用"
        mktPublishVersionInfoService.checkMktPublishVersionInfoExit(versionId)

        then: "结果比对"
        def excp = thrown(CommonException)
        excp.code == "error.mkt.publish.version.info.not.exit"
    }

    def "checkMktPublishVersionInfoExit"() {
        given: "数据构建"
        def versionId = 1L

        and: "mock"
        mktPublishVersionInfoMapper.selectByPrimaryKey(_) >> new MktPublishVersionInfoDTO().setId(1L)

        when: "方法调用"
        def result =  mktPublishVersionInfoService.checkMktPublishVersionInfoExit(versionId)

        then: "结果比对"
        result.getId() == 1L
    }
}
