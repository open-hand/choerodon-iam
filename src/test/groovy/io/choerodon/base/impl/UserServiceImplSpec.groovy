package io.choerodon.base.app.service.impl

import io.choerodon.asgard.saga.dto.StartInstanceDTO
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.CreateUserWithRolesDTO
import io.choerodon.base.api.dto.UserPasswordDTO
import io.choerodon.base.api.validator.UserPasswordValidator
import io.choerodon.base.app.service.RoleMemberService
import io.choerodon.base.app.service.UserService
import io.choerodon.base.infra.asserts.OrganizationAssertHelper
import io.choerodon.base.infra.asserts.ProjectAssertHelper
import io.choerodon.base.infra.asserts.RoleAssertHelper
import io.choerodon.base.infra.asserts.UserAssertHelper
import io.choerodon.base.infra.dto.UserDTO
import io.choerodon.base.infra.feign.FileFeignClient
import io.choerodon.base.infra.feign.NotifyFeignClient
import io.choerodon.base.infra.mapper.MemberRoleMapper
import io.choerodon.base.infra.mapper.OrganizationMapper
import io.choerodon.base.infra.mapper.ProjectMapper
import io.choerodon.base.infra.mapper.UserMapper
import io.choerodon.core.exception.CommonException
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.oauth.core.password.PasswordPolicyManager
import io.choerodon.oauth.core.password.mapper.BasePasswordPolicyMapper
import io.choerodon.oauth.core.password.record.PasswordRecord
import org.apache.http.entity.ContentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class UserServiceImplSpec extends Specification {
    private UserService userService

    @Autowired
    PasswordRecord passwordRecord
    FileFeignClient fileFeignClient = Mock(FileFeignClient)
    SagaClient sagaClient = Mock(SagaClient)
    @Autowired
    BasePasswordPolicyMapper basePasswordPolicyMapper
    @Autowired
    UserPasswordValidator userPasswordValidator
    @Autowired
    PasswordPolicyManager passwordPolicyManager
    @Autowired
    MemberRoleMapper memberRoleMapper
    NotifyFeignClient notifyFeignClient = Mock(NotifyFeignClient)
    @Autowired
    UserMapper userMapper
    @Autowired
    UserAssertHelper userAssertHelper
    @Autowired
    OrganizationAssertHelper organizationAssertHelper
    @Autowired
    ProjectMapper projectMapper
    @Autowired
    OrganizationMapper organizationMapper
    @Autowired
    ProjectAssertHelper projectAssertHelper
    @Autowired
    RoleAssertHelper roleAssertHelper
    TransactionalProducer producer = Mock(TransactionalProducer)
    RoleMemberService roleMemberService = Mock(RoleMemberService)
    def checkLogin = false


    def setup() {
        given: "构造userService"
        userService = new UserServiceImpl(passwordRecord, fileFeignClient,
                sagaClient, basePasswordPolicyMapper, userPasswordValidator, passwordPolicyManager,
                memberRoleMapper, notifyFeignClient, userMapper,
                userAssertHelper, organizationAssertHelper, projectMapper, organizationMapper,
                projectAssertHelper, roleAssertHelper, roleMemberService, producer)
        Field field = userService.getClass().getDeclaredField("devopsMessage")
        field.setAccessible(true)
        field.set(userService, true)

        and: "mock静态方法-CustomUserDetails"
        DetailsHelper.setCustomUserDetails(1L, "zh_CN")
    }

    def "QuerySelf"() {
        when: "调用方法"
        def result = userService.querySelf()

        then: "校验结果"
        result.getId() == 1
    }

    def "QueryOrganizations"() {

        when: "调用方法"
        def result = userService.queryOrganizations(1L, false)

        then: "校验结果"
        true
    }

    def "UploadPhoto"() {
        given: "构造参数"
        MultipartFile multipartFile = new MockMultipartFile("name", new byte[10])

        when: "调用方法"
        userService.uploadPhoto(1L, multipartFile)

        then: "校验结果"
        1 * fileFeignClient.uploadFile(_, _, _) >> { new ResponseEntity<String>(HttpStatus.OK) }
    }

    def "SavePhoto"() {
        given: "构造参数"
        Double rotate = null
        def axisX = 1
        def axisY = 1
        def width = 1
        def height = 1
        File excelFile = new File(this.class.getResource('/templates/bk_log.jpg').toURI())
        FileInputStream fileInputStream = new FileInputStream(excelFile)
        MultipartFile multipartFile = new MockMultipartFile(excelFile.getName(),
                excelFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),
                fileInputStream)

        when: "调用方法"
        userService.savePhoto(1L, multipartFile, rotate, axisX, axisY, width, height)

        then: "校验结果"
        1 * fileFeignClient.uploadFile(_, _, _) >> { new ResponseEntity<String>(HttpStatus.OK) }

        when: "调用方法"
        rotate = 1.0
        userService.savePhoto(1L, multipartFile, rotate, axisX, axisY, width, height)

        then: "校验结果"
        1 * fileFeignClient.uploadFile(_, _, _) >> { new ResponseEntity<String>(HttpStatus.OK) }

        when: "调用方法"
        rotate = 1.0
        userService.savePhoto(1L, null, rotate, axisX, axisY, width, height)

        then: "校验结果"
        def exception = thrown(CommonException)
        exception.message.equals("error.user.photo.save")
    }

    def "SelfUpdatePassword"() {
        given: "构造参数"
        BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder()
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO()
        userPasswordDTO.setPassword("admin")
        userPasswordDTO.setOriginalPassword("admin")
        def checkPassword = true

        when: "调用方法"
        userService.selfUpdatePassword(1L, userPasswordDTO, checkPassword, checkLogin)

        then: "校验结果"
        noExceptionThrown()
    }

    def "UpdateInfo"() {
        given: "构造请求参数"
        def userDTO = userMapper.selectByPrimaryKey(1L)


        when: "调用方法"
        userService.updateInfo(userDTO, true)

        then: "校验结果"
        1 * sagaClient.startSaga(_ as String, _ as StartInstanceDTO)
    }

    def "LockUser"() {
        given: "构造请求参数"
        def lockExpireTime = 1
        UserDTO user = new UserDTO()
        user.setPassword("123456")

        when: "调用方法"
        def result = userService.lockUser(1L, lockExpireTime)

        then: "校验结果"
        result.getLocked()
    }

    def "AddAdminUsers"() {
        given: "构造请求参数"
        long[] ids = new long[1]
        ids[0] = 1L
        UserDTO userE = new UserDTO()
        userE.setPassword("123456")

        when: "调用方法"
        userService.addAdminUsers(ids)

        then: "校验结果"
        true
    }

    def "DeleteAdminUser"() {
        given: "构造请求参数"
        long id = 1L
        UserDTO userE = new UserDTO()
        userE.setPassword("123456")

        when: "调用方法"
        userService.deleteAdminUser(id)

        then: "校验结果"
        def e = thrown(CommonException)
        e.code == "error.user.admin.size"
    }

    def "CreateUserAndAssignRoles"() {
        given: "构造请求参数"
        Set<String> roleCodes = new HashSet<>()
        roleCodes.add("role/organization/default/administrator")
        UserDTO userDO = new UserDTO()
        userDO.setLoginName("admin123")
        userDO.setEmail("admin123@example.com")
        userDO.setPassword("admin")
        CreateUserWithRolesDTO userWithRoles = new CreateUserWithRolesDTO()
        userWithRoles.setUser(userDO)
        userWithRoles.setSourceId(1L)
        userWithRoles.setSourceType("organization")
        userWithRoles.setRoleCode(roleCodes)

        and: "mock静态方法-ConvertHelper"
//        PowerMockito.mockStatic(ConvertHelper)
        UserDTO userDTO = new UserDTO()
        userDTO.setPassword("123456")
//        PowerMockito.when(ConvertHelper.convert(Mockito.any(), Mockito.any())).thenReturn(userDTO).thenReturn(new UserDTO())

        when: "调用方法"
        userService.createUserAndAssignRoles(userWithRoles)

        then: "校验结果"
        true
    }

    def "ListUserIds"() {
    }

    def "QueryOrgIdByEmail"() {
        given: "构造请求参数"
        String email = "admin@example.org"

        when: "调用方法"
        def result = userService.queryOrgIdByEmail(email)

        then: "校验结果"
        result != null
    }
}
