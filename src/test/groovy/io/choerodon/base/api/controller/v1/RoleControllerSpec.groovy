package io.choerodon.base.api.controller.v1

import com.github.pagehelper.PageInfo
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.infra.dto.PermissionDTO
import io.choerodon.base.infra.dto.RoleDTO
import io.choerodon.base.infra.dto.RolePermissionDTO
import io.choerodon.base.infra.mapper.PermissionMapper
import io.choerodon.base.infra.mapper.RoleMapper
import io.choerodon.base.infra.mapper.RolePermissionMapper
import io.choerodon.core.exception.ExceptionResponse
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
class RoleControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/roles"
    @Autowired
    private TestRestTemplate restTemplate
    @Shared
    def roleId = 1L
    @Autowired
    private PermissionMapper permissionMapper
    @Autowired
    private RoleMapper roleMapper
    @Autowired
    RolePermissionMapper rolePermissionMapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def permissionDOList = new ArrayList<PermissionDTO>()
    @Shared
    def rolePermissionDOList = new ArrayList<RolePermissionDTO>()
    @Shared
    def roleDTO = new RoleDTO()

    def setup() {
        if (needInit) {
            given: "构造参数"
            needInit = false
            for (int i = 0; i < 3; i++) {
                PermissionDTO permissionDTO = new PermissionDTO()
                permissionDTO.setCode("iam-service.permission.get" + i)
                permissionDTO.setPath("/v1/permission/" + i)
                permissionDTO.setMethod("get")
                permissionDTO.setResourceLevel("site")
                permissionDTO.setDescription("Description" + i)
                permissionDTO.setAction("get")
                permissionDTO.setController("service" + i)
                permissionDTO.setLoginAccess(false)
                permissionDTO.setPublicAccess(false)
                permissionDTO.setServiceCode("iam-service")
                permissionDOList.add(permissionDTO)
            }
            roleDTO.setCode("role")
            roleDTO.setName("权限管理员")
            roleDTO.setResourceLevel("site")
            roleDTO.setEnabled(true)
            roleDTO.setModified(true)
            roleDTO.setEnableForbidden(true)
            roleDTO.setBuiltIn(false)
            when: "插入记录"
            int count = 0;
            for (PermissionDTO dto : permissionDOList) {
                permissionMapper.insert(dto)
                count++
            }
            roleDTO.setPermissions(permissionDOList)
            count += roleMapper.insert(roleDTO)
            for (int i = 0; i < 3; i++) {
                RolePermissionDTO rolePermissionDTO = new RolePermissionDTO()
                rolePermissionDTO.setPermissionCode(permissionDOList.get(i).getCode())
                rolePermissionDTO.setRoleCode(roleDTO.getCode())
                rolePermissionDOList.add(rolePermissionDTO)
            }
            for (RolePermissionDTO dto : rolePermissionDOList) {
                rolePermissionMapper.insert(dto)
                count++
            }

            then: "校验结果"
            count == 7
        }
    }

    def cleanup() {
        if (needClean) {
            given: ""
            def count = 0
            needClean = false

            when: "删除记录"
            for (PermissionDTO permissionDO : permissionDOList) {
                count += permissionMapper.deleteByPrimaryKey(permissionDO)
            }
            for (RolePermissionDTO rolePermissionDO : rolePermissionDOList) {
                count += rolePermissionMapper.deleteByPrimaryKey(rolePermissionDO)
            }
            count += roleMapper.deleteByPrimaryKey(roleDTO)

            then: "校验结果"
            count == 7
        }
    }

    def "List"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("level", "site")

        when: "调用方法[全局层]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/search?level={level}", PageInfo, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().pageNum != 0

        when: "调用方法[组织层]"
        paramsMap.put("level", "organization")
        entity = restTemplate.getForEntity(BASE_PATH + "/search?level={level}", PageInfo, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().pageNum != 0

        when: "调用方法[项目层]"
        paramsMap.put("level", "project")
        entity = restTemplate.getForEntity(BASE_PATH + "/search?level={level}", PageInfo, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().pageNum != 0
    }

    def "QueryIdsByLabelNameAndLabelType"() {
        given: "构造请求参数"
        def paramsMap = new HashMap<String, Object>()
        paramsMap.put("label_name", "organization.owner")
        paramsMap.put("label_type", "role")

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/id?label_name={label_name}&label_type={label_type}", List, paramsMap)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().size() != 0
    }

    def "QueryWithPermissionsAndLabels"() {
        given: "构造请求参数"
        def roleDTO = roleMapper.selectByPrimaryKey(roleId)

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{id}", RoleDTO, roleId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getId().equals(roleDTO.getId())
        entity.getBody().getCode().equals(roleDTO.getCode())
        entity.getBody().getName().equals(roleDTO.getName())
        entity.getBody().getBuiltIn().equals(roleDTO.getBuiltIn())
        entity.getBody().getModified().equals(roleDTO.getModified())
        entity.getBody().getEnableForbidden().equals(roleDTO.getEnableForbidden())
        entity.getBody().getEnabled().equals(roleDTO.getEnabled())
        entity.getBody().getResourceLevel().equals(roleDTO.getResourceLevel())
    }

    def "Create"() {
        given: "构造请求参数"
        def roleDTO = new RoleDTO()
        roleDTO.setCode("role/site/default/tester")
        roleDTO.setName("测试管理员")
        roleDTO.setResourceLevel("site")
        roleDTO.setBuiltIn(false)
        roleDTO.setModified(false)
        roleDTO.setEnabled(true)
        roleDTO.setEnableForbidden(true)

        when: "调用方法[异常-层级不合法]"
        roleDTO.setResourceLevel("error")
        def entity = restTemplate.postForEntity(BASE_PATH, roleDTO, ExceptionResponse)

        then: "校验结果"
        !entity.statusCode.is2xxSuccessful()
        entity.getBody().getMessage().equals("Request processing failed; nested exception is java.lang.IllegalArgumentException: error.role.illegal.level")

        when: "调用方法[异常-没有权限]"
        roleDTO.setResourceLevel("site")
        entity = restTemplate.postForEntity(BASE_PATH, roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role_permission.empty")

        when: "调用方法"
        def permissionDTOList = permissionDOList
        roleDTO.setPermissions(permissionDTOList)
        entity = restTemplate.postForEntity(BASE_PATH, roleDTO, RoleDTO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals(roleDTO.getCode())
        entity.getBody().getName().equals(roleDTO.getName())
        entity.getBody().getBuiltIn().equals(roleDTO.getBuiltIn())
        entity.getBody().getEnableForbidden().equals(roleDTO.getEnableForbidden())
        entity.getBody().getEnabled().equals(roleDTO.getEnabled())
        entity.getBody().getResourceLevel().equals(roleDTO.getResourceLevel())
        roleMapper.deleteByPrimaryKey(entity.getBody().getId())
    }

    def "CreateBaseOnRoles"() {
        given: "构造请求参数"
        def roleDTO = new RoleDTO()
        roleDTO.setCode("role/site/default/tester1")
        roleDTO.setName("测试管理员")
        roleDTO.setResourceLevel("site")
        roleDTO.setBuiltIn(false)
        roleDTO.setModified(false)
        roleDTO.setEnabled(true)
        roleDTO.setEnableForbidden(true)
        roleDTO.setRoleIds(new ArrayList<Long>())
        def permissionDTOList = permissionDOList
        roleDTO.setPermissions(permissionDTOList)

        when: "调用方法[角色id为空]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/base_on_roles", roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role_permission.empty")

        when: "调用方法[异常-角色层级不相同]"
        def roleIds = new ArrayList<Long>()
        roleIds.add(1L)
        roleIds.add(2L)
        roleIds.add(3L)
        roleDTO.setRoleIds(roleIds)
        entity = restTemplate.postForEntity(BASE_PATH + "/base_on_roles", roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role_permission.empty")

        when: "调用方法"
        roleIds = new ArrayList<Long>()
        roleIds.add(1L)
        roleDTO.setRoleIds(roleIds)
        entity = restTemplate.postForEntity(BASE_PATH + "/base_on_roles", roleDTO, RoleDTO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "Update"() {
        given: "构造请求参数"
        def roleDTO = roleDTO
        roleDTO.setId(10L)
        roleDTO.setDescription("update")
        roleDTO.setObjectVersionNumber(1)
        def httpEntity = new HttpEntity<Object>(roleDTO)

        when: "调用方法"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}", HttpMethod.PUT, httpEntity, RoleDTO, roleDTO.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getDescription().equals(roleDTO.getDescription())
    }

    def "EnableRole"() {
        given: "构造请求参数"
        def httpEntity = new HttpEntity<Object>()
        def roleDTO = roleDTO

        when: "调用方法[角色id为空]"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}/enable", HttpMethod.PUT, httpEntity, ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role.not.exist")

        when: "调用方法"
        entity = restTemplate.exchange(BASE_PATH + "/{id}/enable", HttpMethod.PUT, httpEntity, RoleDTO, roleDTO.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getEnabled()
    }


    def "DisableRole"() {
        given: "构造请求参数"
        def roleDTO = roleDTO
        def httpEntity = new HttpEntity<Object>()

        when: "调用方法[角色id为空]"
        def entity = restTemplate.exchange(BASE_PATH + "/{id}/disable", HttpMethod.PUT, httpEntity, ExceptionResponse, 1000L)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role.not.exist")

        when: "调用方法"
        entity = restTemplate.exchange(BASE_PATH + "/{id}/disable", HttpMethod.PUT, httpEntity, RoleDTO, roleDTO.getId())

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        !entity.getBody().getEnabled()
    }

    def "Check"() {
        given: "构造请求参数"
        def roleDTO = new RoleDTO()
        roleDTO.setCode("")
        roleDTO.setName("测试管理员")
        roleDTO.setResourceLevel("site")

        when: "调用方法[角色code为空]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/check", roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role.code.empty")

        when: "调用方法[角色code存在]"
        roleDTO.setCode("role/site/default/administrator")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.role.code.existed")

        when: "调用方法"
        roleDTO.setCode("role/site/default/checker")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", roleDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "ListPermissionById"() {
        when: "调用方法[角色code为空]"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{id}/permissions", PageInfo, roleDTO.getId())
        needClean = true

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().size() == 0
    }

    def "pagedSearch"() {
        given: "构造请求参数"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/search", PageInfo)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "queryByCode"() {
        given: "构造请求参数"
        def code = "role/site/default/administrator"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "?code={code}", RoleDTO, code)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode() == code
    }

    def "queryIdByCode"() {
        given: "构造请求参数"
        def code = "role/site/default/administrator"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/idByCode?code={code}", Long, code)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "selectByLabel"() {
        given: "构造请求参数"
        def label = "project.owner"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/selectByLabel?label={label}", List, label)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }
}
