package io.choerodon.base.api.controller.v1


import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.vo.ApplicationVO
import io.choerodon.base.infra.dto.ProjectDTO
import io.choerodon.base.infra.dto.ProjectMapCategoryDTO
import io.choerodon.base.infra.mapper.ProjectCategoryMapper
import io.choerodon.base.infra.mapper.ProjectMapCategoryMapper
import io.choerodon.base.infra.mapper.ProjectMapper
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dengyouquan
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class OrganizationProjectControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/organizations/{organization_id}/projects"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private ProjectMapper projectMapper
    @Autowired
    private ProjectCategoryMapper projectCategoryMapper
    @Autowired
    private ProjectMapCategoryMapper projectMapCategoryMapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def projectDOList = new ArrayList<ProjectDTO>()
    @Shared
    def projectMapCategoryList = new ArrayList<ProjectMapCategoryDTO>()
    def organizationId = 1L

    def setup() {
        if (needInit) {
            given: "构造请求参数"
            needInit = false
            for (int i = 0; i < 3; i++) {
                ProjectDTO projectDO = new ProjectDTO()
                projectDO.setId(i + 1)
                projectDO.setCode("hand" + i)
                projectDO.setName("汉得" + i)
                projectDO.setCategory("PROGRAM")
                projectDO.setOrganizationId(1L)
                projectDOList.add(projectDO)

                ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO()
                projectMapCategoryDTO.setProjectId(i + 1)
                projectMapCategoryDTO.setCategoryId(1)
                projectMapCategoryList.add(projectMapCategoryDTO)
            }

            given: "插入数据"
            def count = 0
            for (ProjectDTO dto : projectDOList) {
                projectMapper.insert(dto)
                count++
            }

            for (ProjectMapCategoryDTO projectMapCategoryDTO : projectMapCategoryList) {
                projectMapCategoryMapper.insert(projectMapCategoryDTO)
                count++
            }

            then: "校验结果"
            count == 6
        }
    }

    def cleanup() {
        if (needClean) {
            given: "构造请求参数"
            needClean = false
            def count = 0

            given: "插入数据"
            for (ProjectDTO projectDO : projectDOList) {
                count += projectMapper.deleteByPrimaryKey(projectDO)
            }

            then: "校验结果"
            count == 3
        }
    }

    def "Create"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("organization_id", organizationId)

        def projectDTO = new ProjectDTO()
        def applicationVO = new ApplicationVO()

        projectDTO.setName("测试")
        projectDTO.setCode("test")
        projectDTO.setCategory("PROGRAM")
        projectDTO.setApplicationVO(applicationVO)

        when: "调用方法"
        def entity = restTemplate.postForEntity(BASE_PATH, projectDTO, ProjectDTO, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getName().equals(projectDTO.getName())
        entity.getBody().getCode().equals(projectDTO.getCode())
        projectMapper.deleteByPrimaryKey(entity.getBody().getId())
    }

    def "Update"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("organization_id", organizationId)
        def projectDTO = projectDOList.get(0)

        when: "调用方法[异常-版本号不存在]"
        def projectDTO1 = new ProjectDTO()
        BeanUtils.copyProperties(projectDTO, projectDTO1)
        paramsMap.put("project_id", projectDTO1.getId())
        def httpEntity = new HttpEntity<Object>(projectDTO1)
        def entity = restTemplate.exchange(BASE_PATH + "/{project_id}", HttpMethod.PUT, httpEntity, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("hand0")

        when: "调用方法[异常-组织不存在]"
        paramsMap.put("organization_id", 1000L)
        projectDTO1.setObjectVersionNumber(1)
        httpEntity = new HttpEntity<Object>(projectDTO1)
        entity = restTemplate.exchange(BASE_PATH + "/{project_id}", HttpMethod.PUT, httpEntity, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用方法"
        def projectDTO2 = new ProjectDTO()
        def projectName = "handhand"

        BeanUtils.copyProperties(projectDTO, projectDTO2)
        paramsMap.put("project_id", projectDTO2.getId())
        paramsMap.put("organization_id", organizationId)

        projectDTO2.setObjectVersionNumber(2L)
        projectDTO2.setName(projectName)
        httpEntity = new HttpEntity<Object>(projectDTO2)
        entity = restTemplate.exchange(BASE_PATH + "/{project_id}", HttpMethod.PUT, httpEntity, ProjectDTO, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getName().equals(projectName)
        entity.getBody().getCode().equals(projectDTO.getCode())
    }

    def "EnableProject"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        def projectId = projectDOList.get(1).getId()
        def httpEntity = new HttpEntity<Object>()

        when: "调用对应方法[异常-组织id不存在]"
        paramsMap.put("project_id", projectId)
        paramsMap.put("organization_id", 1000L)
        def entity = restTemplate.exchange(BASE_PATH + "/{project_id}/enable", HttpMethod.PUT, httpEntity, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        paramsMap.put("project_id", projectId)
        paramsMap.put("organization_id", organizationId)
        entity = restTemplate.exchange(BASE_PATH + "/{project_id}/enable", HttpMethod.PUT, httpEntity, ProjectDTO, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getEnabled()
    }

    def "DisableProject"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        def projectId = projectDOList.get(1).getId()
        def httpEntity = new HttpEntity<Object>()

        when: "调用对应方法[异常-组织id不存在]"
        paramsMap.put("project_id", projectId)
        paramsMap.put("organization_id", 1000L)
        def entity = restTemplate.exchange(BASE_PATH + "/{project_id}/disable", HttpMethod.PUT, httpEntity, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.organization.not.exist")

        when: "调用对应方法"
        paramsMap.put("project_id", projectId)
        paramsMap.put("organization_id", organizationId)
        entity = restTemplate.exchange(BASE_PATH + "/{project_id}/disable", HttpMethod.PUT, httpEntity, ProjectDTO, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        !entity.getBody().getEnabled()
    }

    def "getAgileProjects"() {
        when: "调用方法"
        restTemplate.getForEntity(BASE_PATH + "/list", List, organizationId)

        then: "校验结果"
        noExceptionThrown()
    }

    def "getProjectsNotGroup"() {
        given: "构造请求参数"
        def projectDTO = projectDOList.get(1)

        when: "调用方法[异常-项目不存在]"
        def projectId = 1000L
        def entity = restTemplate.getForEntity(BASE_PATH + "/{project_id}/agile", ExceptionResponse, organizationId, projectId)

        then: "校验结果"
        entity.getBody().getCode().equals("error.project.not.exist")

        when: "调用方法[异常-类型不合法]"
        def projectId1 = projectDTO.getId()
        def projectDTO1 = new ProjectDTO()
        BeanUtils.copyProperties(projectDTO, projectDTO1)
        projectDTO1.setCategory("PROGRAM")

        def entity1 = restTemplate.getForEntity(BASE_PATH + "/{project_id}/agile", ExceptionResponse, organizationId, projectId1)

        then: "校验结果"
        entity1.getBody().code.equals("error.only.programs.can.configure.subprojects")
    }

    def "getGroupInfoByEnableProject"() {
        given: "构造请求参数"
        def projectId = projectDOList.get(0).getId()

        when: "调用方法"
        restTemplate.getForEntity(BASE_PATH + "/{project_id}/program", List, organizationId, projectId)

        then: "校验结果"
        noExceptionThrown()
    }

    def "pagingProjectByOptions"() {
        given: "构造请求参数"
        def doPage = true

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/projects_with_applications", ExceptionResponse, organizationId, doPage)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "getProjectsByType"() {
        when: "调用方法"
        restTemplate.getForEntity(BASE_PATH + "/under_the_type", Map, organizationId)

        then: "校验结果"
        noExceptionThrown()
    }

    def "Check"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("organization_id", organizationId)
        def projectDTO = projectDOList.get(1)

        when: "调用对应方法[异常-项目code为空]"
        def projectDTO1 = new ProjectDTO()
        BeanUtils.copyProperties(projectDTO, projectDTO1)
        projectDTO1.setCode(null)
        def entity = restTemplate.postForEntity(BASE_PATH + "/check", projectDTO1, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.project.code.empty")

        when: "调用对应方法[异常-项目存在]"
        def projectDTO2 = new ProjectDTO()
        BeanUtils.copyProperties(projectDTO, projectDTO2)
        projectDTO2.setId(10L)
        entity = restTemplate.postForEntity(BASE_PATH + "/check", projectDTO2, ExceptionResponse, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.project.code.exist")

        when: "调用对应方法"
        def projectDTO3 = new ProjectDTO()
        projectDTO3.setCode("check")
        projectDTO3.setOrganizationId(1L)
        entity = restTemplate.postForEntity(BASE_PATH + "/check", projectDTO3, Void, paramsMap)
        needClean = true

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }
}