package io.choerodon.base.api.validator

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.infra.dto.SysSettingDTO
import io.choerodon.base.infra.mapper.PasswordPolicyMapper
import io.choerodon.base.infra.mapper.SysSettingMapper
import io.choerodon.core.exception.CommonException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author zmf
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class UserPasswordValidatorSpec extends Specification {
    @Autowired
    PasswordPolicyMapper passwordPolicyMapper
    SysSettingMapper settingMapper = Mock(SysSettingMapper)


    def "Validate"() {
        given: '配置validator'
        UserPasswordValidator userPasswordValidator = new UserPasswordValidator(passwordPolicyMapper, settingMapper)
        List<SysSettingDTO> settingDTOS = new ArrayList<SysSettingDTO> ()
        SysSettingDTO settingDTO = new SysSettingDTO()
        settingDTO.setSettingKey("minPasswordLength")
        settingDTO.setSettingValue("0")
        settingDTOS.add(settingDTO)
        settingDTO = new SysSettingDTO()
        settingDTO.setSettingKey("maxPasswordLength")
        settingDTO.setSettingValue("20")
        settingDTOS.add(settingDTO)
        settingMapper.selectAll() >> settingDTOS

        when: "调用方法[异常-密码超出要求]"
        boolean result = userPasswordValidator.validate("12323232232321212121212121212121212121212121221", 1L, true)

        then: '校验结果'
        def ex = thrown(Exception)
        ex.message.equals("error.password.length.out.of.setting")

        when: "调用方法"

        result = userPasswordValidator.validate("12", 1L, false)

        then: '校验结果'
        result

    }
}
