package io.choerodon.iam.app.service.impl


import io.choerodon.iam.IntegrationTestConfiguration
import io.choerodon.iam.api.vo.SysSettingVO
import io.choerodon.iam.infra.dto.asgard.ScheduleMethodDTO
import io.choerodon.iam.infra.feign.AsgardFeignClient
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator
import io.choerodon.iam.infra.mapper.SysSettingMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Subject(SystemSettingC7nServiceImpl)
class SystemSettingC7nServiceImplSpec extends Specification {

    @Autowired
    SystemSettingC7nServiceImpl systemSettingC7nService

//    SysSettingMapper settingMapper = Mock()
    @Autowired
    SysSettingMapper settingMapper

    AsgardServiceClientOperator asgardServiceClientOperator = Mock()

    AsgardFeignClient asgardFeignClient = Mock()

    void setup() {
    }

    def "UpdateGeneralInfo"() {
        given:
        def systemName = "choerodon"
        ReflectionTestUtils.setField(systemSettingC7nService, "sysSettingMapper", settingMapper)
        ReflectionTestUtils.setField(systemSettingC7nService, "asgardFeignClient", asgardFeignClient)
        ReflectionTestUtils.setField(systemSettingC7nService, "asgardServiceClientOperator", asgardServiceClientOperator)

        def settingVO = new SysSettingVO()
        settingVO.setSystemName(systemName)
        settingVO.setAutoCleanSagaInstance(true)
        settingVO.setAutoCleanEmailRecord(true)
        settingVO.setAutoCleanEmailRecordInterval(180)
        settingVO.setAutoCleanWebhookRecord(true)
        settingVO.setAutoCleanWebhookRecordInterval(180)

        when:
        def sysSettingVO = systemSettingC7nService.updateGeneralInfo(settingVO)

        then:
        sysSettingVO.getSystemName() == systemName
        asgardServiceClientOperator.getMethodDTOSite(_ as String, _ as String) >> Mock(ScheduleMethodDTO)
    }

    def "getSetting"() {
        given:
        ReflectionTestUtils.setField(systemSettingC7nService, "sysSettingMapper", settingMapper)

        when:
        def sysSettingVO = systemSettingC7nService.getSetting()

        then:
        sysSettingVO != null
    }
}
