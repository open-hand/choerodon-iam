package io.choerodon.base.api.controller.v1

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.CommonCheckResultVO
import io.choerodon.base.app.service.RemoteTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author AndersIes
 * @date 2019/8/14 10 03
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RemoteTokenPublicControllerSpec extends Specification {

    private static String BASE_PATH = "/v1/public/remote_tokens"

    @Autowired
    private TestRestTemplate restTemplate

    private RemoteTokenService remoteTokenService = Mock(RemoteTokenService)

    @Autowired
    private RemoteTokenPublicController remoteTokenPublicController

    def setup() {
        remoteTokenPublicController.setRemoteTokenService(remoteTokenService)
    }

    def "token校验（校验是否存在，是否启用）"() {
        given: "参数准备"
        def token = "token"
        def entity

        when: "调用方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/check?remote_token={token}", CommonCheckResultVO.class, token)
        then: "结果比对"
        1 * remoteTokenService.checkToken(_)
        entity.statusCode.is2xxSuccessful()
    }
}
