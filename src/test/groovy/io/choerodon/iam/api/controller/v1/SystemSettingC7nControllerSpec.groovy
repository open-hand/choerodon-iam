package io.choerodon.iam.api.controller.v1


import io.choerodon.core.exception.ExceptionResponse
import io.choerodon.iam.IntegrationTestConfiguration
import io.choerodon.iam.api.vo.SysSettingVO
import io.choerodon.iam.app.service.SystemSettingC7nService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Subject(SystemSettingC7nController)
class SystemSettingC7nControllerSpec extends Specification {

    def BASE_URL = "/choerodon/v1/system/setting"
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    SystemSettingC7nController systemSettingC7nController

    SystemSettingC7nService systemSettingC7nService = Mock()

    void setup() {
        ReflectionTestUtils.setField(systemSettingC7nController, "systemSettingService", systemSettingC7nService)
    }

    def "updateGeneralInfo"() {
        given:
        def sysSettingVO = new SysSettingVO()

        def result = new SysSettingVO()
        result.setSystemName("choerodon1")

        when: "参数错误"
        def entity1 = testRestTemplate.postForEntity(BASE_URL, sysSettingVO, ExceptionResponse.class)
        then: "校验结果"
        entity1.body.failed
        0 * systemSettingC7nService.updateGeneralInfo(_)

        when: "参数正确"
        sysSettingVO.setSystemName("choerodon")
        sysSettingVO.setDefaultLanguage("chinese")
        def entity2 = testRestTemplate.postForEntity(BASE_URL, sysSettingVO, SysSettingVO.class)
        then: "校验结果"
        entity2.body.getSystemName() == "choerodon1"
        1 * systemSettingC7nService.updateGeneralInfo(_) >> result

    }

    def "getSetting"() {
        given:
        def settingVO = new SysSettingVO()
        settingVO.setSystemName("choerodon")

        when: "查询平台配置"
        def entity2 = testRestTemplate.getForEntity(BASE_URL, SysSettingVO.class)
        then: "校验结果"
        entity2.body.getSystemName() == "choerodon"
        1 * systemSettingC7nService.getSetting() >> settingVO

    }

}
