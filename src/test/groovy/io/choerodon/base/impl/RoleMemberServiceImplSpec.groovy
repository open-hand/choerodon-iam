package io.choerodon.base.app.service.impl

import com.github.pagehelper.PageInfo
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.RoleAssignmentDeleteDTO
import io.choerodon.base.api.query.ClientRoleQuery
import io.choerodon.base.app.service.RoleMemberService
import org.springframework.data.domain.PageRequest
import io.choerodon.core.enums.ResourceType
import io.choerodon.base.infra.asserts.UserAssertHelper
import io.choerodon.base.infra.dto.ClientDTO
import io.choerodon.base.infra.dto.MemberRoleDTO
import io.choerodon.base.infra.dto.OrganizationDTO
import io.choerodon.base.infra.dto.ProjectDTO
import io.choerodon.base.infra.enums.MemberType
import io.choerodon.base.infra.mapper.*
import io.choerodon.base.infra.utils.excel.ExcelImportUserTask
import io.choerodon.core.oauth.DetailsHelper
import org.apache.http.entity.ContentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RoleMemberServiceImplSpec extends Specification {

    @Autowired
    ExcelImportUserTask excelImportUserTask
    @Autowired
    ExcelImportUserTask.FinishFallback finishFallback
    @Autowired
    OrganizationMapper organizationMapper
    @Autowired
    ProjectMapper projectMapper
    @Autowired
    MemberRoleMapper memberRoleMapper
    @Autowired
    RoleMapper roleMapper
    @Autowired
    UserAssertHelper userAssertHelper
    SagaClient sagaClient = Mock(SagaClient)
    @Autowired
    LabelMapper labelMapper
    @Autowired
    ClientMapper clientMapper
    @Autowired
    UploadHistoryMapper UploadHistoryMapper

    RoleMemberService roleMemberService

    def setup() {
        given: "构造 roleMemberService"
        roleMemberService = new RoleMemberServiceImpl(excelImportUserTask, finishFallback,
                organizationMapper, projectMapper, memberRoleMapper, roleMapper, userAssertHelper, sagaClient,
                labelMapper, clientMapper, UploadHistoryMapper)

        DetailsHelper.setCustomUserDetails(1L, "zh_CN")
    }

    @Transactional
    def "Import2MemberRole"() {
        given: "构造请求参数"
        File excelFile = new File(this.class.getResource('/templates/roleAssignment.xlsx').toURI())
        FileInputStream fileInputStream = new FileInputStream(excelFile)
        MultipartFile multipartFile = new MockMultipartFile(excelFile.getName(),
                excelFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(),
                fileInputStream)


        when: "调用方法"
        roleMemberService.import2MemberRole(0L, "site", multipartFile)

        then: "校验结果"
        noExceptionThrown()
    }

//    @Transactional
//    def "createOrUpdateRolesByMemberIdOnSiteLevel"(){
//
//        given: "构造请求参数"
//        //(Boolean isEdit, List<Long> memberIds, List<MemberRoleDTO> memberRoleDTOList, String memberType)
//        List<Long> memberIds =new ArrayList<>()//构建id集合
//        for (int i=1;i<3;i++){
//            memberIds.add((long)i)
//        }
//        //构建MemberRoleDTO集合
//        List<MemberRoleDTO> memberRoleDTOList=new ArrayList<>()
//        MemberRoleDTO memberRoleDTO=null
//        for (int i=1;i<3;i++){
//            memberRoleDTO=new MemberRoleDTO();
//            memberRoleDTO.setMemberId(1L)
//            memberRoleDTO.setMemberType("client")
//            memberRoleDTO.setSourceId(1L)
//            memberRoleDTO.setSourceType("组织"+i)
//            memberRoleDTOList.add(memberRoleDTO)
//        }
//
//        when: "调用方法"
//        List<MemberRoleDTO> results= roleMemberService.createOrUpdateRolesByMemberIdOnSiteLevel(true,memberIds,memberRoleDTOList,"client")
//
//        then: "校验结果"
//        results.size() !=0
//    }


    def "insertSelective"() {
        //MemberRoleDTO memberRoleDTO
        given: "构造参数"
        MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
        memberRoleDTO.setRoleId(1L)
        memberRoleDTO.setSourceType("ssm")
        memberRoleDTO.setSourceId(1L)
        memberRoleDTO.setMemberType("client")

        when: "调用方法"
        def num = roleMemberService.insertSelective(memberRoleDTO)

        then: "结果校验"
        num != null
    }

    def "pagingQueryClientsWithRoles"() {
        //(PageRequest pageRequest, ClientRoleQuery clientRoleSearchDTO, Long sourceId, ResourceType resourceType)
        given: "构造参数"
        PageRequest pageRequest = new PageRequest(1, 20)
        ClientRoleQuery clientRoleSearchDTO = new ClientRoleQuery()
        clientRoleSearchDTO.setRoleName("user")
        def params = ['1', '2', '3', '4'] as String[]
        clientRoleSearchDTO.setParam(params)
        clientRoleSearchDTO.setClientName("client")
        //esourceType resourceType=new ResourceType()

        when: "调用方法"
        PageInfo<ClientDTO> pageInfo = roleMemberService.pagingQueryClientsWithRoles(pageRequest, clientRoleSearchDTO, 1L, ResourceType.SITE)

        then: "校验"
        pageInfo.getPageSize() == 20
    }

    def "createOrUpdateRolesByMemberIdOnProjectLevel"() {
        //(
        //Boolean isEdit, Long projectId, List<Long> memberIds, List<MemberRoleDTO> memberRoleDTOList, String memberType)
        given: "构造参数"
        //插入project
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("汉得研发")
        projectDTO.setCode("ssm")

        //SELECT object_version_number,created_by,creation_date,last_updated_by,last_update_date,id,role_id,member_id,member_type,source_id,source_type  FROM iam_member_role
        List<Long> memberIds = new ArrayList<>()
        memberIds.add(1L)
        List<MemberRoleDTO> memberRoleDOList1 = new ArrayList<MemberRoleDTO>()
        MemberRoleDTO memberRoleDO1 = new MemberRoleDTO()
        memberRoleDO1.setMemberId(1L)
        memberRoleDO1.setMemberType(MemberType.CLIENT.value())
        memberRoleDO1.setRoleId(3L)
        memberRoleDO1.setSourceId(1L)
        memberRoleDO1.setSourceType("project")
        memberRoleDOList1.add(memberRoleDO1)

        when: "调用方法"
        def m = projectMapper.insert(projectDTO)

        then: "校验"
        m > 0

        when: "调用方法"
        List<MemberRoleDTO> result = roleMemberService.createOrUpdateRolesByMemberIdOnProjectLevel(true, 1L, memberIds, memberRoleDOList1, "client")

        then: "校验结果"
        result.size() != null
    }

    def "deleteOnSiteLevel"() {
        //(RoleAssignmentDeleteDTO roleAssignmentDeleteDTO)
        given: "构造参数"
        RoleAssignmentDeleteDTO roleAssignmentDeleteDTO = new RoleAssignmentDeleteDTO()
        roleAssignmentDeleteDTO.setMemberType("client")
        roleAssignmentDeleteDTO.setSourceId(1L)
        roleAssignmentDeleteDTO.setView("视图")
        List<Long> memberIds = new ArrayList<>()//构建id集合
        for (int i = 1; i < 3; i++) {
            memberIds.add((long) i)
        }
        Map<Long, List<Long>> data = new HashMap<>();
        data.put("mytask", memberIds)
        roleAssignmentDeleteDTO.setData(data)

        when: "调用方法"
        roleMemberService.deleteOnSiteLevel(roleAssignmentDeleteDTO)

        then: "结果校验"
        noExceptionThrown()
    }

    def "deleteOnOrganizationLevel"() {
        //(RoleAssignmentDeleteDTO roleAssignmentDeleteDTO)
        given: "参数构造"
        RoleAssignmentDeleteDTO roleAssignmentDeleteDTO = new RoleAssignmentDeleteDTO()
        roleAssignmentDeleteDTO.setMemberType("client")
        roleAssignmentDeleteDTO.setSourceId(1L)
        roleAssignmentDeleteDTO.setView("视图")
        List<Long> memberIds = new ArrayList<>()//构建id集合
        for (int i = 1; i < 3; i++) {
            memberIds.add((long) i)
        }
        Map<Long, List<Long>> data = new HashMap<>();
        data.put("mytask", memberIds)
        roleAssignmentDeleteDTO.setData(data)

        when: "调用方法"
        roleMemberService.deleteOnOrganizationLevel(roleAssignmentDeleteDTO)

        then: "结果校验"
        noExceptionThrown()
    }

    def "deleteOnProjectLevel"() {
        given: "构造参数"
        RoleAssignmentDeleteDTO roleAssignmentDeleteDTO = new RoleAssignmentDeleteDTO()
        roleAssignmentDeleteDTO.setMemberType("client")
        roleAssignmentDeleteDTO.setSourceId(1L)
        roleAssignmentDeleteDTO.setView("视图")
        List<Long> memberIds = new ArrayList<>()//构建id集合
        for (int i = 1; i < 3; i++) {
            memberIds.add((long) i)
        }
        Map<Long, List<Long>> data = new HashMap<>();
        data.put("mytask", memberIds)
        roleAssignmentDeleteDTO.setData(data)

        when: "调用方法"
        roleMemberService.deleteOnSiteLevel(roleAssignmentDeleteDTO)

        then: "结果校验"
        noExceptionThrown()
    }

    def "downloadTemplates"() {
        //(String suffix)
        given: "构造参数"
        String suffix = "xls"

        when: "调用方法"
        def entity = roleMemberService.downloadTemplates(suffix)

        then: "结果校验"
        entity != null
    }

    def "insertOrUpdateRolesOfUserByMemberId"() {
        //(Boolean isEdit, Long sourceId, Long memberId, List<MemberRoleDTO> memberRoles, String sourceType)
        given: "构造参数"
        //插入project
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName("汉得研发")
        projectDTO.setCode("ssm")

        List<MemberRoleDTO> memberRoleDOList1 = new ArrayList<MemberRoleDTO>()
        MemberRoleDTO memberRoleDO1 = new MemberRoleDTO()
        memberRoleDO1.setMemberId(1L)
        memberRoleDO1.setMemberType(MemberType.CLIENT.value())
        memberRoleDO1.setRoleId(2L)
        memberRoleDO1.setSourceId(1L)
        memberRoleDO1.setSourceType("organization")
        memberRoleDOList1.add(memberRoleDO1)
        when: "调用方法"
        def m = projectMapper.insert(projectDTO)

        then: "校验"
        m > 0

        when: "调用方法"
        List<MemberRoleDTO> memberRoleDTO = roleMemberService.insertOrUpdateRolesOfUserByMemberId(true, 1L, 1L, memberRoleDOList1, ResourceType.ORGANIZATION.value())

        then: "结果校验"
        memberRoleDTO.size() != 0
    }

    def "createOrUpdateRolesByMemberIdOnOrganizationLevel"() {
        //(Boolean isEdit, Long organizationId, List<Long> memberIds, List<MemberRoleDTO> memberRoleDTOList, String memberType)
        given: "构造参数"
        //构建organization
        OrganizationDTO organizationDTO = new OrganizationDTO()
        organizationDTO.setName("研发中心")
        organizationDTO.setCode("operation1")
        //构造memberId
        List<Long> memberIds = new ArrayList<>()
        for (int i = 1; i < 3; i++) {
            memberIds.add((long) i)
        }
        //构造memberRoleDOList
        List<MemberRoleDTO> memberRoleDOList1 = new ArrayList<MemberRoleDTO>()
        MemberRoleDTO memberRoleDO1 = new MemberRoleDTO()
        memberRoleDO1.setMemberId(2L)
        memberRoleDO1.setMemberType(MemberType.CLIENT.value())
        memberRoleDO1.setRoleId(2L)
        memberRoleDO1.setSourceId(1L)
        memberRoleDO1.setSourceType("organization")
        memberRoleDOList1.add(memberRoleDO1)

        when: "调用方法"
        def m = organizationMapper.insert(organizationDTO)

        then: "结果校验"
        m > 0

        when: "调用方法"
        OrganizationDTO organizationDTO1 = organizationMapper.selectByPrimaryKey(organizationDTO)

        then: "结果校验"
        organizationDTO1 != null

        when: "调用方法"
        def entity = roleMemberService.createOrUpdateRolesByMemberIdOnOrganizationLevel(true, organizationDTO1.getId(), memberIds, memberRoleDOList1, MemberType.CLIENT.value())

        then: "结果校验"
        entity.size() != 0
    }

    def "insertOrUpdateRolesOfClientByMemberId"() {
        //(Boolean isEdit, Long sourceId, Long memberId, List<MemberRoleDTO> memberRoles, String sourceType)
        given: "参数构造"
        //构造memberRoleDOList
        List<MemberRoleDTO> memberRoleDOList1 = new ArrayList<MemberRoleDTO>()
        MemberRoleDTO memberRoleDO1 = new MemberRoleDTO()
        memberRoleDO1.setMemberId(1L)
        memberRoleDO1.setMemberType(MemberType.CLIENT.value())
        memberRoleDO1.setRoleId(2L)
        memberRoleDO1.setSourceId(1L)
        memberRoleDO1.setSourceType("organization")
        memberRoleDOList1.add(memberRoleDO1)
        when: "调用方法"
        def result = roleMemberService.insertOrUpdateRolesOfClientByMemberId(true, 1L, 1L, memberRoleDOList1, "ssm")
        then: "结果校验"
        result != null
    }

    def "deleteClientAndRole"() {
        //(RoleAssignmentDeleteDTO roleAssignmentDeleteDTO, String sourceType)
        given: "构造参数"
        RoleAssignmentDeleteDTO roleAssignmentDeleteDTO = new RoleAssignmentDeleteDTO()
        roleAssignmentDeleteDTO.setMemberType("client")
        roleAssignmentDeleteDTO.setSourceId(1L)
        roleAssignmentDeleteDTO.setView("视图")
        List<Long> memberIds = new ArrayList<>()//构建id集合
        for (int i = 1; i < 3; i++) {
            memberIds.add((long) i)
        }
        Map<Long, List<Long>> data = new HashMap<>();
        data.put("mytask", memberIds)
        roleAssignmentDeleteDTO.setData(data)

        when: "调用方法"
        roleMemberService.deleteClientAndRole(roleAssignmentDeleteDTO, "ssh")

        then: "结果校验"
        noExceptionThrown()
    }

    def "insertAndSendEvent"() {
        //(MemberRoleDTO memberRole, String loginName)
        given: "构造参数"
        MemberRoleDTO memberRoleDO1 = new MemberRoleDTO()
        memberRoleDO1.setMemberId(1L)
        memberRoleDO1.setMemberType(MemberType.CLIENT.value())
        memberRoleDO1.setRoleId(2L)
        memberRoleDO1.setSourceId(1L)
        memberRoleDO1.setSourceType("organization")

        when: "调用方法"
        roleMemberService.insertAndSendEvent(memberRoleDO1, "tom")

        then: "结果校验"
        noExceptionThrown()
    }
}
