package io.choerodon.base.api.controller.v1

import com.github.pagehelper.PageInfo
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.OrgSharesDTO
import io.choerodon.base.api.vo.ResendEmailMsgDTO
import io.choerodon.base.app.service.CaptchaService
import io.choerodon.base.app.service.DemoRegisterService
import io.choerodon.base.app.service.OrganizationService
import io.choerodon.base.app.service.impl.DemoRegisterServiceImpl
import io.choerodon.base.infra.dto.OrganizationDTO
import io.choerodon.base.infra.feign.DevopsFeignClient
import io.choerodon.base.infra.mapper.OrganizationMapper
import io.choerodon.base.infra.mapper.UserMapper
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author wangkaijie*     */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class OrganizationControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/organizations"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private OrganizationMapper organizationMapper
    @Autowired
    private OrganizationService organizationService
    @Autowired
    private UserMapper userMapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def organizationDOList = new ArrayList<OrganizationDTO>()

    def setup() {
        if (needInit) {
            given: "构造参数"
            for (int i = 0; i < 3; i++) {
                def organizationDO = new OrganizationDTO()
                organizationDO.setCode("hand" + i)
                organizationDO.setName("汉得" + i)
                organizationDOList.add(organizationDO)
            }

            when: "调用方法"
            needInit = false
            int count = 0
            for (OrganizationDTO dto : organizationDOList) {
                organizationMapper.insert(dto)
                count++
            }

            then: "校验结果"
            count == 3
        }

    }

    def cleanup() {
        if (needClean) {
            when: "调用方法"
            needClean = false
            def count = 0
            for (OrganizationDTO organizationDO : organizationDOList) {
                count += organizationMapper.deleteByPrimaryKey(organizationDO)
            }

            then: "校验结果"
            count == 3
        }
    }

    def "SendCaptcha"() {
        given: "构造请求参数"
        def email = "xxx@xxx.com"
        CaptchaService captchaService = Mock(CaptchaService)
        OrganizationController organizationController = new OrganizationController(null, null, captchaService)
        def resendMsgDto = new ResendEmailMsgDTO()
        resendMsgDto.setEmail(email)
        resendMsgDto.setCanResend(true)
        resendMsgDto.setSuccessful(true)
        captchaService.sendEmailCaptcha(_) >> resendMsgDto
        when: "调用对应方法"
        def entity = organizationController.sendEmailCaptcha(email)
        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getEmail().equals(email)
        entity.getBody().getSuccessful()
    }

    def "CheckCaptcha"() {
        given: "构造请求参数"
        def email = "xxx@xxx.com"
        def captcha = "1234"
        CaptchaService captchaService = Mock(CaptchaService)
        OrganizationController organizationController = new OrganizationController(null, null, captchaService)
        captchaService.validateCaptcha(_) >> void
        when: "调用对应方法"
        def entity = organizationController.checkEmailCaptcha(email, captcha)
        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "CheckEmail"() {
        given: "构造请求参数"
        DevopsFeignClient devopsFeignClient = Mock(DevopsFeignClient)
        DemoRegisterService demoRegisterService = new DemoRegisterServiceImpl(userMapper, devopsFeignClient)
        OrganizationController organizationController = new OrganizationController(null, demoRegisterService, null)
        def email = "xxx@xxx.com"
        def response = new ResponseEntity<Boolean>(false, HttpStatus.OK)
        devopsFeignClient.checkGitlabEmail("xxx@xxx.com") >> response

        when: "调用对应方法"
        def entity = organizationController.checkEmailIsExist(email)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "UpdateOnOrganizationLevel"() {
        given: "构造请求参数"
        def organizationDto1 = new OrganizationDTO()
        organizationDto1.setCode("update-hand5")
        organizationDto1.setName("汉得5")
        organizationDto1.setId(1000L)
        //这个Version是必不可少的
        organizationDto1.setObjectVersionNumber(1L)
        def httpEntity = new HttpEntity<Object>(organizationDto1)

        when: "调用对应方法，[异常-更新的数据不存在]"
        def entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/organization_level", HttpMethod.PUT, httpEntity, ExceptionResponse, organizationDto1.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        organizationDto1.setId(1L)
        httpEntity = new HttpEntity<Object>(organizationDto1)
        entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/organization_level", HttpMethod.PUT, httpEntity, OrganizationDTO, organizationDto1.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        !entity.getBody().getCode().equals(organizationDto1.getCode())
        entity.getBody().getName().equals(organizationDto1.getName())
    }

    def "Update"() {
        given: "构造请求参数"
        def updateDto = organizationDOList.get(1)
        updateDto.setCode("update-hand")
        updateDto.setName("汉得")
        updateDto.setObjectVersionNumber(1)
        def httpEntity = new HttpEntity<Object>(updateDto)

        when: "调用对应方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{organization_id}", HttpMethod.PUT, httpEntity, OrganizationDTO, updateDto.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        //code不可更新
        !entity.getBody().getCode().equals(updateDto.getCode())
        entity.getBody().getName().equals(updateDto.getName())
    }

    def "Query"() {
        given: "构造请求参数"
        def organizationId = 1L
        def organizationDO = organizationMapper.selectByPrimaryKey(organizationId)

        when: "调用对应方法[异常-组织id不存在]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{organization_id}", ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/{organization_id}", OrganizationDTO, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getId().equals(organizationId)
        entity.getBody().getCode().equals(organizationDO.getCode())
        entity.getBody().getName().equals(organizationDO.getName())
    }

    def "QueryOrgLevel"() {
        given: "构造请求参数"
        def organizationId = 1L
        def organizationDO = organizationMapper.selectByPrimaryKey(organizationId)

        when: "调用对应方法[异常-组织id不存在]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{organization_id}/org_level", ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        entity = restTemplate.getForEntity(BASE_PATH + "/{organization_id}/org_level", OrganizationDTO, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getId().equals(organizationId)
        entity.getBody().getCode().equals(organizationDO.getCode())
        entity.getBody().getName().equals(organizationDO.getName())
    }

    def "List"() {
        given: "构造请求参数"
        def name = "汉得"
        def code = "hand"

        when: "调用对应方法[全查询]"
        def entity = restTemplate.getForEntity(BASE_PATH, PageInfo)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
//        entity.getBody().total == 4
        !entity.getBody().list.isEmpty()

        when: "调用对应方法"
        entity = restTemplate.getForEntity(BASE_PATH + "?code={code}&name={name}", PageInfo, code, name)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().total == 3
    }

    def "EnableOrganization"() {
        given: "构造请求参数"
        def organizationId = 1L
        def httpEntity = new HttpEntity<Object>()

        when: "调用对应方法[异常-组织id不存在]"
        def entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/enable", HttpMethod.PUT, httpEntity, ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/enable", HttpMethod.PUT, httpEntity, OrganizationDTO, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getEnabled()
    }

    def "DisableOrganization"() {
        given: "构造请求参数"
        def organizationId = 1L
        def httpEntity = new HttpEntity<Object>()

        when: "调用对应方法[异常-组织id不存在]"
        def entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/disable", HttpMethod.PUT, httpEntity, ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        entity = restTemplate.exchange(BASE_PATH + "/{organization_id}/disable", HttpMethod.PUT, httpEntity, OrganizationDTO, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        !entity.getBody().getEnabled()
    }

    def "Check"() {
        given: "构造请求参数"
        def organizationDTO = organizationDOList.get(1)

        when: "调用对应方法[异常-组织code为空]"
        def organizationDTO1 = new OrganizationDTO()
        BeanUtils.copyProperties(organizationDTO, organizationDTO1)
        organizationDTO1.setCode(null)
        def entity = restTemplate.postForEntity(BASE_PATH + "/check", organizationDTO1, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.code.empty")

        when: "调用对应方法[异常-组织id存在,用户id存在]"
        def organizationDTO2 = new OrganizationDTO()
        BeanUtils.copyProperties(organizationDTO, organizationDTO2)
        organizationDTO2.setCode("operation")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", organizationDTO2, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.code.exist")

        when: "调用对应方法[异常-组织id存在,用户id不存在]"
        def organizationDTO4 = new OrganizationDTO()
        BeanUtils.copyProperties(organizationDTO, organizationDTO4)
        organizationDTO4.setCode("operation")
        organizationDTO4.setId(100000L)
        entity = restTemplate.postForEntity(BASE_PATH + "/check", organizationDTO4, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.code.exist")

        when: "调用对应方法"
        def organizationDTO3 = new OrganizationDTO()
        organizationDTO3.setCode("test")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", organizationDTO3, Void)
        needClean = true

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "queryByIds"() {
        given: "构造请求参数"
        OrganizationController controller = new OrganizationController(organizationService, null, null)
        def ids = new HashSet()

        when: "调用对应方法，ids为空异常"
        def value = controller.queryByIds(ids)

        then: "校验结果"
        value.getBody().size() == 0

        when: "调用对应方法"
        ids << 1L
        value = controller.queryByIds(ids)

        then: "校验结果"
        value.body.size() > 0
    }

    def "PageQueryUsersOnOrganization"() {
        given: "构造请求参数"
        def email = "xxx@xxx.com"
        def id = 1
        def userId = 1
        def param = "String"
        when: "调用对应方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{organization_id}/users?id={id}&email={email}&param={param}",
                PageInfo, id, userId, email, param)
        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "ListAllOrgs"() {
        given: "构造请求参数"
        def pageRequest = new PageRequest(1, 10)
        when: "调用对应方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/all?page={page}&size={size}", PageInfo, pageRequest.getPageNumber(), pageRequest.getPageSize())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getPageSize().equals(pageRequest.getPageSize())
    }

    def "PagingSpecified"() {
        given: "构造请求参数"
        def orgSharesDto = new OrgSharesDTO()
        orgSharesDto.setCode("hand-list")
        orgSharesDto.setName("hand")
        orgSharesDto.setEnabled(true)
        orgSharesDto.setId(1)
        def orgSet = new HashSet<Long>()
        def httpEntity = new HttpEntity<Object>(orgSet)

        when: "调用对应方法，ordId集合为空"
        def entity = restTemplate.postForEntity(BASE_PATH + "/specified?name={name}&code={code}&enabled={enabled}&params={params}", httpEntity, PageInfo, orgSharesDto.getName(), orgSharesDto.getCode(),
                orgSharesDto.getEnabled(), "test")

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getSize() == 0


        when: "调用对应方法"
        orgSet.add(1L)
        orgSet.add(2L)
        entity = restTemplate.postForEntity(BASE_PATH + "/specified?name={name}&code={code}&enabled={enabled}&params={params}", httpEntity, PageInfo, orgSharesDto.getName(), orgSharesDto.getCode(),
                orgSharesDto.getEnabled(), "test")

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }
}
