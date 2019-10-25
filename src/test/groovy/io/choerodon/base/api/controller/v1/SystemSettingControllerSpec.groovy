package io.choerodon.base.api.controller.v1

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.ResetPasswordDTO
import io.choerodon.base.api.vo.SysSettingVO
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author qiang.zeng
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class SystemSettingControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/system/setting"
    @Autowired
    private TestRestTemplate restTemplate

    private SysSettingVO settingVO

    void setup() {
        settingVO = new SysSettingVO()
        settingVO.setFavicon("http://minio.staging.saas.hand-china.com/iam-service/file_2913c259dc524231909f5e6083e4c2bf_test.png")
        settingVO.setSystemLogo("http://minio.staging.saas.hand-china.com/iam-service/file_2913c259dc524231909f5e6083e4c2bf_test.png")
        settingVO.setSystemName("Choerodon")
        settingVO.setSystemTitle("Choerodon Platform")
        settingVO.setDefaultLanguage("zh_CN")
        settingVO.setThemeColor("#0000CD")
        settingVO.setRegisterEnabled(true)
        settingVO.setRegisterUrl("http://register.test.com")
        settingVO.setResetGitlabPasswordUrl("http://gitlab.test.com")
        settingVO.setDefaultPassword("12345678")
    }

    def "add general info with invalid system name"() {
        given: "构造请求参数"
        settingVO.setSystemName(systemName)
        def httpEntity = new HttpEntity<Object>(settingVO)

        when: "调用方法[异常]"
        def entity = restTemplate.exchange(BASE_PATH, HttpMethod.POST, httpEntity, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == code

        where: "边界请求"
        systemName                 | code
        null                       | "error.setting.name.null"
        "112312412412412412412412" | "error.setting.name.too.long"
    }

    def "add general info without default language"() {
        given: "构造请求参数"
        settingVO.setDefaultLanguage(null)
        def httpEntity = new HttpEntity<Object>(settingVO)

        when: "调用方法[异常]"
        def entity = restTemplate.exchange(BASE_PATH, HttpMethod.POST, httpEntity, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == "error.setting.default.language.null"
    }

    def "add general info"() {
        given: "构造请求参数"
        def httpEntity = new HttpEntity<Object>(settingVO)

        when: "调用方法[成功]"
        def entity = restTemplate.exchange(BASE_PATH, HttpMethod.POST, httpEntity, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "update general info"() {
        given: "构造请求参数"
        settingVO.setFavicon("change")
        settingVO.setSystemLogo("change")
        settingVO.setSystemName("change")
        settingVO.setSystemTitle("change")
        settingVO.setDefaultLanguage("change")
        settingVO.setThemeColor("change")
        settingVO.setRegisterEnabled(false)
        settingVO.setRegisterUrl("change")
        settingVO.setResetGitlabPasswordUrl("change")
        def httpEntity = new HttpEntity<Object>(settingVO)
        when: "调用方法[成功]"
        def entity = restTemplate.exchange(BASE_PATH, HttpMethod.POST, httpEntity, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getFavicon() == "change"
        entity.getBody().getSystemLogo() == "change"
        entity.getBody().getSystemName() == "change"
        entity.getBody().getSystemTitle() == "change"
        entity.getBody().getDefaultLanguage() == "change"
        entity.getBody().getThemeColor() == "change"
        !entity.getBody().getRegisterEnabled()
        entity.getBody().getRegisterUrl() == "change"
        entity.getBody().getResetGitlabPasswordUrl() == "change"
    }

    def "add password policy with invalid password"() {
        given: "构造请求参数"
        settingVO.setDefaultPassword(password)
        def httpEntity = new HttpEntity<Object>(settingVO)

        when: "调用方法[异常]"
        def entity = restTemplate.exchange(BASE_PATH + "/passwordPolicy", HttpMethod.POST, httpEntity, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == code

        where: "边界请求"
        password                   | code
        null                       | "error.setting.default.password.null"
        "112312412412412412412412" | "error.setting.default.password.length.invalid"
        "11"                       | "error.setting.default.password.length.invalid"
        "12214441#"                | "error.setting.default.password.format.invalid"
    }

    def "add password policy"() {
        given: "构造请求参数"
        def httpEntity = new HttpEntity<Object>(settingVO)

        when: "调用方法[成功]"
        def entity = restTemplate.exchange(BASE_PATH + "/passwordPolicy", HttpMethod.POST, httpEntity, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "update password policy"() {
        given: "构造请求参数"
        settingVO.setDefaultPassword("98765432")
        settingVO.setMinPasswordLength(1)
        settingVO.setMaxPasswordLength(10)
        def httpEntity = new HttpEntity<Object>(settingVO)
        when: "调用方法[成功]"
        def entity = restTemplate.exchange(BASE_PATH + "/passwordPolicy", HttpMethod.POST, httpEntity, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getDefaultPassword() == "98765432"
        entity.getBody().getMinPasswordLength() == 1
        entity.getBody().getMaxPasswordLength() == 10
    }

    def "reset general info"() {
        when: "调用方法[成功]"
        restTemplate.delete(BASE_PATH)
        def entity = restTemplate.getForEntity(BASE_PATH, SysSettingVO)

        then: "校验结果"
        entity.getBody().getFavicon() == null
        entity.getBody().getSystemLogo() == null
        entity.getBody().getSystemName() == null
        entity.getBody().getSystemTitle() == null
        entity.getBody().getDefaultLanguage() == null
        entity.getBody().getThemeColor() == null
        entity.getBody().getRegisterEnabled() == null
        entity.getBody().getRegisterUrl() == null
        entity.getBody().getResetGitlabPasswordUrl() == null
    }

    def "GetSetting"() {
        when: "调用方法[成功]"
        def entity = restTemplate.getForEntity(BASE_PATH, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()

        when: "调用方法[成功]"
        def httpEntity = new HttpEntity<Object>(settingVO)
        restTemplate.postForEntity(BASE_PATH, httpEntity, SysSettingVO)
        restTemplate.postForEntity(BASE_PATH + "/passwordPolicy", httpEntity, SysSettingVO)
        entity = restTemplate.getForEntity(BASE_PATH, SysSettingVO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getSystemName() != null
        entity.getBody().getDefaultPassword() != null
    }

    def "UploadFavicon"() {
        when: "调用方法[异常]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/upload/favicon", null, ExceptionResponse, 0L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == "error.upload.multipartSize"
    }

    def "UploadLogo"() {
        when: "调用方法[异常]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/upload/logo", null, ExceptionResponse, 0L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == "error.upload.multipartSize"
    }

    def "enableResetPassword"() {
        when: "调用方法[成功]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/enable_resetPassword", ResetPasswordDTO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getEnable_reset()
    }

    def "getEnabledStateOfTheCategory"() {
        when: "调用方法[成功]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/enable_category", Boolean)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        !entity.getBody()
    }
}
