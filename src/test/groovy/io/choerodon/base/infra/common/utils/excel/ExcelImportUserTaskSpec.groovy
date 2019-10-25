package io.choerodon.base.infra.common.utils.excel

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.ExcelMemberRoleDTO
import io.choerodon.base.api.validator.UserPasswordValidator
import io.choerodon.base.app.service.OrganizationUserService
import io.choerodon.base.app.service.RoleMemberService
import io.choerodon.base.app.service.UserService
import io.choerodon.base.infra.asserts.RoleAssertHelper
import io.choerodon.base.infra.dto.UploadHistoryDTO
import io.choerodon.base.infra.dto.UserDTO
import io.choerodon.base.infra.feign.FileFeignClient
import io.choerodon.base.infra.mapper.MemberRoleMapper
import io.choerodon.base.infra.mapper.RoleMapper
import io.choerodon.base.infra.mapper.UploadHistoryMapper
import io.choerodon.base.infra.mapper.UserMapper
import io.choerodon.base.infra.utils.RandomInfoGenerator
import io.choerodon.base.infra.utils.excel.ExcelImportUserTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ExcelImportUserTaskSpec extends Specification {
    @Autowired
    private RoleMemberService roleMemberService
    @Autowired
    private OrganizationUserService organizationUserService
    private FileFeignClient fileFeignClient = Mock(FileFeignClient)
    @Autowired
    private UserService userService
    @Autowired
    private UserPasswordValidator userPasswordValidator

    private int count = 3
    private ExcelImportUserTask excelImportUserTask
    @Autowired
    UserMapper userMapper

    @Autowired
    RoleMapper roleMapper
    @Autowired
    private MemberRoleMapper memberRoleMapper

    @Autowired
    private UploadHistoryMapper uploadHistoryMapper
    @Autowired
    private ExcelImportUserTask.FinishFallback finishFallback
    @Autowired
    private RoleAssertHelper roleAssertHelper;
    @Autowired
    private RandomInfoGenerator randomInfoGenerator;

    def setup() {
        excelImportUserTask = new ExcelImportUserTask(roleMemberService, organizationUserService,
                fileFeignClient, userService, userPasswordValidator, userMapper, roleMapper,
                memberRoleMapper, roleAssertHelper, randomInfoGenerator)
    }

    @Transactional
    def "ImportUsers"() {
        given: "构造请求参数"
        long userId = 1L
        UserDTO dto = new UserDTO()
        dto.setLoginName("login")
        dto.setPassword("password")
        dto.setPhone("110")
        dto.setEmail("6260299@qq.com")
        dto.setRealName("real")
        List<UserDTO> users = new ArrayList<>()
        users << dto

        UploadHistoryDTO uploadHistoryDTO = new UploadHistoryDTO()
        uploadHistoryDTO.setUserId(1L)
        uploadHistoryDTO.setType("user")
        uploadHistoryMapper.insert(uploadHistoryDTO)

        UploadHistoryDTO history = uploadHistoryMapper.selectByPrimaryKey(uploadHistoryDTO.getId())

        when: "调用方法"
        excelImportUserTask.importUsers(userId, users, 1L, history, finishFallback)

        then: "校验结果"
        noExceptionThrown()
        1 * fileFeignClient.uploadFile(_, _, _) >> { new ResponseEntity<String>(HttpStatus.OK) }
    }

    @Transactional
    def "ImportMemberRole"() {
        given: "构造请求参数"
        ExcelMemberRoleDTO excelMemberRole = new ExcelMemberRoleDTO()
        excelMemberRole.setLoginName("admin")
        excelMemberRole.setRoleCode("role/site/default/site-user")
        List<ExcelMemberRoleDTO> excelMemberRoles = new ArrayList<>()
        excelMemberRoles << excelMemberRole

        UploadHistoryDTO uploadHistoryDTO = new UploadHistoryDTO()
        uploadHistoryDTO.setUserId(1L)
        uploadHistoryDTO.setType("member-role")
        uploadHistoryDTO.setSourceId(0L)
        uploadHistoryDTO.setSourceType("site")
        uploadHistoryMapper.insert(uploadHistoryDTO)
        UploadHistoryDTO history = uploadHistoryMapper.selectByPrimaryKey(uploadHistoryDTO.getId())

        when: "调用方法"
        excelImportUserTask.importMemberRole(excelMemberRoles, history, finishFallback)

        then: "校验结果"
        noExceptionThrown()
    }
}