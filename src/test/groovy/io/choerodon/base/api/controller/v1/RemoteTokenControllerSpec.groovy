package io.choerodon.base.api.controller.v1

import com.github.pagehelper.PageInfo
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.RemoteTokenBase64VO
import io.choerodon.base.app.service.RemoteTokenService
import io.choerodon.base.infra.dto.RemoteTokenDTO
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
 * @author PENGYUHUA* @author AndersIes
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class RemoteTokenControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/organizations/{organization_id}/remote_tokens"
    @Autowired
    private RemoteTokenController remoteTokenController
    @Autowired
    TestRestTemplate testRestTemplate

    private RemoteTokenService remoteTokenService = Mock(RemoteTokenService)

    def "setup"() {
        remoteTokenController.setRemoteTokenService(remoteTokenService)
    }


    def "test create"() {
        given: "构造请求参数"
        def organizationId = 1
        RemoteTokenDTO remoteTokenDTO = new RemoteTokenDTO()

        when: "调用创建组织的远程连接令牌方法[令牌名称为空]"
        remoteTokenDTO.setEmail("relaxingchu@qq.com")
        def entry = testRestTemplate.postForEntity(BASE_PATH, remoteTokenDTO, ExceptionResponse, organizationId)

        then: "结果比对"
        entry.statusCode.is2xxSuccessful()
        entry.getBody().getCode() == "error.remote.token.create.name.cannot.be.empty"

        when: "调用创建组织的远程连接令牌方法[邮箱为空]"
        remoteTokenDTO.setEmail(null)
        remoteTokenDTO.setName("remoteToken")
        entry = testRestTemplate.postForEntity(BASE_PATH, remoteTokenDTO, ExceptionResponse, organizationId)

        then: "结果比对"
        entry.statusCode.is2xxSuccessful()
        entry.getBody().getCode() == "error.remote.token.create.email.cannot.be.empty"

        when: "调用创建组织的远程连接令牌方法[邮箱不为空但格式错误]"
        remoteTokenDTO.setEmail("relaxingchuqq.com")
        entry = testRestTemplate.postForEntity(BASE_PATH, remoteTokenDTO, ExceptionResponse, organizationId)

        then: "结果比对"
        entry.statusCode.is2xxSuccessful()
        entry.getBody().getCode() == "error.remote.token.create.email.invalid"

        when: "调用创建组织的远程连接令牌方法[remoteTokenDTO数据初始化完整]"
        remoteTokenDTO.setEmail("relaxingchu@qq.com")
        entry = testRestTemplate.postForEntity(BASE_PATH, remoteTokenDTO, RemoteTokenBase64VO, organizationId)

        then: "结果比对"
        1 * remoteTokenService.createNewOne(_, _) >> {
            RemoteTokenBase64VO remoteTokenBase64VO = new RemoteTokenBase64VO()
            remoteTokenBase64VO.setEmail("relaxingchu@qq.com")
            remoteTokenBase64VO.setName("remoteToken")
            remoteTokenBase64VO.setExpired(false)
            remoteTokenBase64VO.setId(256L)
            return remoteTokenBase64VO
        }
        entry.statusCode.is2xxSuccessful()
        entry.getBody().getId() == 256L


    }

    def "test expired"() {
        given: "构建测试数据"
        def organization_id = 1
        def id = 1

        when: "调用将远程应用令牌置为失效方法"
        def entry = testRestTemplate.exchange(BASE_PATH + "/{id}/expired", HttpMethod.PUT, null, RemoteTokenBase64VO, organization_id, id)

        then: "结果比对"
        1 * remoteTokenService.expired(_, _) >> {
            RemoteTokenBase64VO remoteTokenBase64VO = new RemoteTokenBase64VO()
            remoteTokenBase64VO.setEmail("relaxingchu@qq.com")
            remoteTokenBase64VO.setName("remoteToken")
            remoteTokenBase64VO.setExpired(false)
            remoteTokenBase64VO.setId(256L)
            return remoteTokenBase64VO
        }
        entry.statusCode.is2xxSuccessful()
        entry.getBody().getId() == 256L
    }

    def "获取组织最新的远程连接令牌"() {
        given: "参数准备"
        def organization_id = 1
        def entity

        when: "调用方法"
        entity = testRestTemplate.getForEntity(BASE_PATH + "/latest", RemoteTokenBase64VO.class, organization_id)

        then: "结果比对"
        1 * remoteTokenService.getTheLatest(_)
        entity.statusCode.is2xxSuccessful()
    }

    def "分页查询组织远程连接TOKEN的历史记录（不包括最新一条记录，不论最新记录的状态）"() {
        given: "参数准备"
        def organization_id = 123
        def filterDTOName = "filterDTOName"
        def entity

        when: "调用方法"
        entity = testRestTemplate.getForEntity(BASE_PATH, PageInfo.class, organization_id)
        then: "结果比对"
        1 * remoteTokenService.pagingTheHistoryList(_, _, _, _)
        entity.statusCode.is2xxSuccessful()

        when: "调用方法"
        entity = testRestTemplate.getForEntity(BASE_PATH + "?name={filterDTOName}", PageInfo.class, organization_id, filterDTOName)
        then: "结果比对"
        1 * remoteTokenService.pagingTheHistoryList(_, _, _, _)
        entity.statusCode.is2xxSuccessful()
    }
}
