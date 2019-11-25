package io.choerodon.base.impl

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

import java.lang.reflect.Field

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.OrganizationProjectService
import io.choerodon.base.app.service.ProjectService
import io.choerodon.base.app.service.impl.ProjectServiceImpl
import io.choerodon.base.infra.asserts.ProjectAssertHelper
import io.choerodon.base.infra.asserts.UserAssertHelper
import io.choerodon.base.infra.dto.ProjectDTO
import io.choerodon.base.infra.mapper.OrganizationMapper
import io.choerodon.base.infra.mapper.ProjectMapCategoryMapper
import io.choerodon.base.infra.mapper.ProjectMapper
import io.choerodon.base.infra.mapper.UserMapper
import io.choerodon.core.exception.CommonException
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper

/**
 * @author dengyouquan*      */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ProjectServiceImplSpec extends Specification {

    ProjectService projectService

    @Autowired
    OrganizationProjectService organizationProjectService
    SagaClient sagaClient = Mock(SagaClient)
    @Autowired
    UserMapper userMapper
    @Autowired
    ProjectMapper projectMapper
    @Autowired
    ProjectAssertHelper projectAssertHelper
    @Autowired
    ProjectMapCategoryMapper projectMapCategoryMapper
    @Autowired
    UserAssertHelper userAssertHelper
    @Autowired
    OrganizationMapper organizationMapper


    def setup() {
        projectService = new ProjectServiceImpl(organizationProjectService, sagaClient,
                userMapper, projectMapper, projectAssertHelper, projectMapCategoryMapper, userAssertHelper, organizationMapper)
        //反射注入属性
        Field field = projectService.getClass().getDeclaredField("devopsMessage")
        field.setAccessible(true)
        field.set(projectService, true)
        CustomUserDetails customUserDetails = new CustomUserDetails("admin","admin")
        customUserDetails.setUserId(1L);
        customUserDetails.setLanguage("zh_CN")
        DetailsHelper.setCustomUserDetails(customUserDetails)
    }

    @Transactional
    def "Update"() {
        given: "构造请求参数"
        ProjectDTO dto = new ProjectDTO()
        dto.setCode("code123")
        dto.setName("name")
        dto.setEnabled(true)
        projectMapper.insert(dto)
        ProjectDTO example = projectMapper.selectByPrimaryKey(dto)


        when: "调用方法"
        def result = projectService.update(example)

        then: "校验结果"
        result.getObjectVersionNumber() == 2
    }

    def "DisableProject"() {
        given: "构造请求参数"
        ProjectDTO dto = new ProjectDTO()
        dto.setCode("code123")
        dto.setName("name")
        dto.setEnabled(true)
        projectMapper.insert(dto)


        when: "调用方法"
        projectService.disableProject(dto.getId())

        then: "校验结果"
        noExceptionThrown()
    }

    def "ListUserIds"() {
        given: "构造请求参数"
        Long projectId = 1L

        when: "调用方法"
        projectService.listUserIds(projectId)

        then: "校验结果"
        noExceptionThrown()
    }

    def "batchUpdateAgileProjectCode"() {
        given: "构造"
        def newAgileCode = "agile-code"
        insertMockProject(10000L, "aaa", "aaa", null)
        insertMockProject(10001L, "bbb", "bbb", "bbb")
        List<ProjectDTO> updateList = new ArrayList<>()
        updateList.add(constructProject(10000L, null, null, newAgileCode))
        updateList.add(constructProject(10001L, null, null, newAgileCode))

        when: "调用方法"
        projectService.batchUpdateAgileProjectCode(updateList)

        then: "校验结果"
        projectMapper.selectByPrimaryKey(10000L).getAgileProjectCode() == newAgileCode
        projectMapper.selectByPrimaryKey(10001L).getAgileProjectCode() == "bbb"

        and: "构造错误数据"
        updateList.get(0).setId(null)

        when: "调用错误的数据"
        projectService.batchUpdateAgileProjectCode(updateList)

        then: "抛出异常"
        thrown(CommonException)
    }

    private insertMockProject(Long id, String name, String code, String agileProjectCode) {
        ProjectDTO projectDTO = constructProject(id, name, code, agileProjectCode)
        projectMapper.insertSelective(projectDTO)
    }

    private static ProjectDTO constructProject(Long id, String name, String code, String agileProjectCode) {
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setId(id)
        projectDTO.setName(name)
        projectDTO.setCode(code)
        projectDTO.setAgileProjectCode(agileProjectCode)
        return projectDTO
    }

}

