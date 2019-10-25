package io.choerodon.base.app.service.impl

import io.choerodon.asgard.saga.dto.StartInstanceDTO
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.validator.ProjectValidator
import io.choerodon.base.app.service.OrganizationProjectService
import io.choerodon.base.app.service.RoleMemberService
import io.choerodon.base.app.service.UserService
import io.choerodon.base.infra.asserts.ApplicationAssertHelper
import io.choerodon.base.infra.asserts.OrganizationAssertHelper
import io.choerodon.base.infra.asserts.ProjectAssertHelper
import io.choerodon.base.infra.asserts.UserAssertHelper
import io.choerodon.base.infra.dto.ProjectDTO
import io.choerodon.base.infra.feign.AsgardFeignClient
import io.choerodon.base.infra.mapper.*
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class OrganizationProjectServiceImplSpec extends Specification {
    private SagaClient sagaClient = Mock(SagaClient)
    private AsgardFeignClient asgardFeignClient = Mock(AsgardFeignClient)
    @Autowired
    private UserService userService
    @Autowired
    private OrganizationProjectService organizationProjectService
    @Autowired
    private ProjectMapCategoryMapper projectMapCategoryMapper
    @Autowired
    private ProjectCategoryMapper projectCategoryMapper
    @Autowired
    private ProjectMapper projectMapper
    @Autowired
    private ProjectAssertHelper projectAssertHelper
    @Autowired
    private ProjectTypeMapper projectTypeMapper
    @Autowired
    private OrganizationAssertHelper organizationAssertHelper
    @Autowired
    UserAssertHelper userAssertHelper
    @Autowired
    private ApplicationAssertHelper applicationAssertHelper
    @Autowired
    private RoleMapper roleMapper
    @Autowired
    private LabelMapper labelMapper
    @Autowired
    private ProjectRelationshipMapper projectRelationshipMapper
    @Autowired
    private RoleMemberService roleMemberService
    @Autowired
    private ProjectValidator projectValidator;
    @Autowired
    private ApplicationMapper applicationMapper
    @Autowired
    private TransactionalProducer producer;

    def setup() {
        given: "构造organizationProjectService"
        organizationProjectService = new OrganizationProjectServiceImpl(sagaClient, userService,
                asgardFeignClient, projectMapCategoryMapper, projectCategoryMapper, projectMapper,
                projectAssertHelper, projectTypeMapper, organizationAssertHelper, userAssertHelper,
                applicationAssertHelper, roleMapper, labelMapper, projectRelationshipMapper,
                roleMemberService, applicationMapper, projectValidator, producer)
        Field field = organizationProjectService.getClass().getDeclaredField("devopsMessage")
        field.setAccessible(true)
        field.set(organizationProjectService, true)

        Field field1 = organizationProjectService.getClass().getDeclaredField("categoryEnable")
        field1.setAccessible(true)
        field1.set(organizationProjectService, false)
        DetailsHelper.setCustomUserDetails(1, "zh_CN")
    }

    @Transactional
    def "CreateProject"() {
        given: "构造请求参数"
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setName("name")
        projectDTO.setCode("code")
        projectDTO.setEnabled(true)
        projectDTO.setOrganizationId(1L)

        when: "调用方法"
        organizationProjectService.createProject(projectDTO)

        then: "校验结果"
        noExceptionThrown()
    }

    @Transactional
    def "Update"() {
        given: "mock"
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setName("name")
        projectDTO.setCode("code")
        projectDTO.setEnabled(true)
        projectDTO.setOrganizationId(1L)
        long id = organizationProjectService.create(projectDTO).getId()
        ProjectDTO dto = projectMapper.selectByPrimaryKey(id)
        dto.setName("name1")
        CustomUserDetails customUserDetails = new CustomUserDetails("admin", "admin")
        customUserDetails.setUserId(1L)
        customUserDetails.setLanguage("zh_CN")
        customUserDetails.setTimeZone("zkk")
        DetailsHelper.setCustomUserDetails(customUserDetails)

        when: "调用方法"
        def entity = organizationProjectService.update(1L, dto)

        then: "校验结果"
        entity.objectVersionNumber == 2

    }

    @Transactional
    def "EnableProject"() {
        given: ""
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setName("name")
        projectDTO.setCode("code")
        projectDTO.setEnabled(true)
        projectDTO.setOrganizationId(1L)
        long id = organizationProjectService.create(projectDTO).getId()

        when: "调用方法"
        organizationProjectService.enableProject(1L, id, 1L)

        then: "校验结果"
        1 * sagaClient.startSaga(_ as String, _ as StartInstanceDTO)
    }


    @Transactional
    def "DisableProject"() {
        given: ""
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setName("name")
        projectDTO.setCode("code")
        projectDTO.setEnabled(true)
        projectDTO.setOrganizationId(1L)
        long id = organizationProjectService.create(projectDTO).getId()

        when: "调用方法"
        organizationProjectService.enableProject(1L, id, 1L)

        then: "校验结果"
        1 * sagaClient.startSaga(_ as String, _ as StartInstanceDTO)
    }
}
