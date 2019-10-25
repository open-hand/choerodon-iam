package io.choerodon.base.impl

import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.OrgSharesDTO
import io.choerodon.base.app.service.OrganizationService
import io.choerodon.base.app.service.UserService
import io.choerodon.base.app.service.impl.OrganizationServiceImpl
import io.choerodon.base.infra.asserts.OrganizationAssertHelper
import io.choerodon.base.infra.dto.OrganizationDTO
import io.choerodon.base.infra.dto.UserDTO
import io.choerodon.base.infra.feign.AsgardFeignClient
import io.choerodon.base.infra.mapper.*
import io.choerodon.core.iam.ResourceLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author wkj* @Date 2019/8/14 16:09
 * @Version 1.0* @email foxnotail@foxmail.com
 * */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class OrganizationServiceImplSpec extends Specification {
    private SagaClient sagaClient = Mock(SagaClient)
    private AsgardFeignClient asgardFeignClient = Mock(AsgardFeignClient)
    @Autowired
    private TestRestTemplate restTemplate
    @Shared
    private OrganizationService organizationService
    @Autowired
    private RoleMapper roleMapper
    //@Autowired
    private UserMapper userMapper = Mock(UserMapper)
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def organizationDOList = new ArrayList<OrganizationDTO>()
    def devopsMessage = false
    @Autowired
    UserService userService
    @Autowired
    OrganizationAssertHelper organizationAssertHelper
    @Autowired
    ProjectMapper projectMapper
    @Autowired
    OrganizationMapper organizationMapper
    @Autowired
    RoleMapper roleMapper
    @Autowired
    MemberRoleMapper memberRoleMapper

    def setup() {
        if (needInit) {
            given: "构造参数"
            organizationService = new OrganizationServiceImpl(devopsMessage,
                    sagaClient,
                    userService,
                    asgardFeignClient,
                    organizationAssertHelper,
                    projectMapper,
                    userMapper,
                    organizationMapper,
                    roleMapper,
                    memberRoleMapper)
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

    def "Create"() {
        given: "构造请求参数"
        def createDto = new OrganizationDTO()
        createDto.setUserId(1L)
        createDto.setCode("create-hand")
        createDto.setName("创建组织")
        createDto.setObjectVersionNumber(1)

        when: "调用对应方法"
        def entity = organizationService.create(createDto)

        then: "校验结果"
        entity.getName().equals(createDto.getName())
    }

    def "QueryOrganizationById"() {
        given: "构造请求参数"
        def orgId = 1L
        def user = new UserDTO()
        user.setId(1L)
        userMapper.selectByPrimaryKey(_) >> user
        when: "调用对应方法"
        def entity = organizationService.queryOrganizationById(orgId)
        then: "校验结果"
        entity.getId().equals(orgId)
    }

    def "UpdateOrganization"() {
        given: "构造请求参数"
        def orgId = 1L
        def updateDto = new OrganizationDTO()
        updateDto.setUserId(4L)
        updateDto.setCode("create-hand")
        updateDto.setName("修改组织信息")
        updateDto.setObjectVersionNumber(1)
        when: "调用对应方法"
        def entity = organizationService.updateOrganization(orgId, updateDto, ResourceLevel.ORGANIZATION.value(), orgId)
        then: "校验结果"
        entity.getName().equals(updateDto.getName())
    }

//    def "QueryOrganizationWithRoleById"() {
//        given: "构造请求参数"
//        def orgId = 1L
//        when: "调用对应方法"
//        def entity = organizationService.queryOrganizationWithRoleById(orgId)
//        then: "校验结果"
//        entity.getId().equals(orgId)
//    }

    def "PagingQuery"() {
        given: "构造请求参数"
        def page = 1
        def size = 10
        def pageRequest = new PageRequest(page, size)
        def pageDto = new OrganizationDTO()
        pageDto.setUserId(2L)
        pageDto.setCode("page-hand")
        pageDto.setName("分页查询")
        pageDto.setObjectVersionNumber(1)
        def param = "test"
        when: "调用对应方法"
        def entity = organizationService.pagingQuery(pageDto, pageRequest, param)
        then: "校验结果"
        entity.getPageNum().equals(page)
        entity.getPageSize().equals(size)
    }

    def "EnableOrganization"() {
        given: "构造请求参数"
        def organizationId = 1L
        def userId = 1L

        when: "调用对应方法[异常-组织id不存在]"
        def entity = organizationService.enableOrganization(organizationId, userId)

        then: "校验结果"
        entity.getUserId().equals(userId)
        entity.getId().equals(organizationId)
    }

    def "DisableOrganization"() {
        given: "构造请求参数"
        def organizationId = 1L
        def userId = 1L

        when: "调用对应方法[异常-组织id不存在]"
        def entity = organizationService.disableOrganization(organizationId, userId)

        then: "校验结果"
        entity.getUserId().equals(userId)
        entity.getId().equals(organizationId)
    }


//    def "Check"() {
//        given: "构造请求参数"
//        def checkDto = new OrganizationDTO()
//        checkDto.setUserId(2L)
//        checkDto.setCode("page-hand")
//        checkDto.setName("分页查询")
//        checkDto.setObjectVersionNumber(1)
//        organizationMapper.selectOne(_) >> checkDto
//        when: "调用对应方法[异常-组织code为空]"
//        def entity = organizationService.check(checkDto)
//        then: "校验结果"
//        1 * organizationMapper.selectOne(_)
//    }

    def "QueryByIds"() {
        given:
        def ids = new HashSet()
        ids << 1L
        when:
        def value = organizationService.queryByIds(ids)
        then:
        value.size() > 0
    }

    def "PagingQueryUsersInOrganization"() {
        given: "构造请求参数"
        def email = "xxx@xxx.com"
        def orgId = 1
        def userId = 1
        def param = "String"
        def page = 1
        def size = 10
        def pageRequest = new PageRequest(page, size)
        when: "调用对应方法"
        def entity = organizationService.pagingQueryUsersInOrganization(orgId, userId, email, pageRequest, param)
        then: "校验结果"
        entity.getPageNum().equals(page)
        entity.getPageSize().equals(size)
    }

    def "GetAllOrgs"() {
        given: "构造请求参数"
        def page = 1
        def size = 10
        def pageRequest = new PageRequest(1, 10)
        when: "调用对应方法"
        def entity = organizationService.getAllOrgs(pageRequest)
        then: "校验结果"
        entity.getPageNum().equals(page)
        entity.getPageSize().equals(size)
    }

    def "PagingSpecified"() {
        given: "构造请求参数"
        def orgSharesDto = new OrgSharesDTO()
        orgSharesDto.setCode("hand-list")
        orgSharesDto.setName("hand")
        orgSharesDto.setEnabled(true)
        orgSharesDto.setId(1)
        def orgSet = new HashSet<Long>()
        orgSet.add(1L)
        orgSet.add(2L)
        def params = "string"
        def page = 1
        def size = 10
        def pageRequest = new PageRequest(page, size)
        def sort = new Sort(Sort.Direction.ASC, "1")
        pageRequest.setSort(sort)

        when: "调用对应方法"
        def entity = organizationService.pagingSpecified(orgSet, orgSharesDto.getName(), orgSharesDto.getCode(), orgSharesDto.getEnabled(), params, pageRequest)

        then: "校验结果"
        entity.getPageNum().equals(page)
        entity.getPageSize().equals(size)
    }
}
