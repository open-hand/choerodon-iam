package io.choerodon.base.api.controller.v1

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.infra.dto.MenuDTO
import io.choerodon.base.infra.mapper.MenuMapper
import io.choerodon.core.exception.ExceptionResponse
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
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
class MenuControllerSpec extends Specification {
    private static final String BASE_PATH = "/v1/menus"
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private MenuMapper menuMapper
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    def menuDOList = new ArrayList<MenuDTO>()

    def setup() {
        if (needInit) {
            given: "构造参数"
            needInit = false
            for (int i = 0; i < 3; i++) {
                MenuDTO menuDO = new MenuDTO()
                menuDO.setCode("choerodon.code.testroot" + i)
                menuDO.setName("菜单测试" + i)
                menuDO.setResourceLevel("site")
                menuDO.setParentCode("1")
                menuDO.setType("root")
                menuDO.setIcon("icon")
                menuDO.setDefault(true)
                menuDOList.add(menuDO)
            }
            for (int i = 0; i < 3; i++) {
                MenuDTO menuDO = new MenuDTO()
                menuDO.setCode("choerodon.code.testmenu" + i)
                menuDO.setName("菜单测试" + i)
                menuDO.setResourceLevel("site")
                menuDO.setParentCode("1")
                menuDO.setType("menu")
                menuDO.setIcon("icon")
                menuDOList.add(menuDO)
            }

            when: "插入记录"
            //不能insertList，否则不能插入多语言表
            def count = 0
            for (MenuDTO menuDO : menuDOList) {
                menuMapper.insert(menuDO)
            }

            then: "校验结果"
            count == 6
        }
    }

    def cleanup() {
        if (needClean) {
            given: ""
            needClean = false
            def count = 0

            when: "删除记录"
            for (MenuDTO menuDO : menuDOList) {
                count += menuMapper.deleteByPrimaryKey(menuDO)
            }

            then: "校验结果"
            count == 6
        }
    }

    def "Query"() {
        given: "构造请求参数"
        def menuId = menuDOList.get(0).getId()

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/{menu_id}", MenuDTO, menuId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getId().equals(menuDOList.get(0).getId())
        entity.getBody().getCode().equals(menuDOList.get(0).getCode())
        entity.getBody().getName().equals(menuDOList.get(0).getName())
        entity.getBody().getResourceLevel().equals(menuDOList.get(0).getResourceLevel())
        entity.getBody().getParentCode().equals(menuDOList.get(0).getParentCode())
    }

    def "Check"() {
        given: "构造请求参数"
        def menuDTO = new MenuDTO()

        when: "调用方法[异常-菜单编码为空]"
        def entity = restTemplate.postForEntity(BASE_PATH + "/check", menuDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
        entity.getBody().getCode().equals("error.menu.code.empty")

        when: "调用方法[异常-菜单level为空]"
        menuDTO.setCode("check")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", menuDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
//        entity.getBody().getCode().equals("error.menu.level.empty")

        when: "调用方法[异常-菜单type为空]"
        menuDTO.setCode("check")
        menuDTO.setResourceLevel("site")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", menuDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
//        entity.getBody().getCode().equals("error.menu.type.empty")

        when: "调用方法[异常-菜单重复]"
        menuDTO.setCode("choerodon.code.testroot1")
        menuDTO.setResourceLevel("site")
        menuDTO.setType("root")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", menuDTO, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
//        entity.getBody().getCode().equals("error.menu.code-level-type.exist")

        when: "调用方法"
        menuDTO.setCode("check")
        menuDTO.setResourceLevel("site")
        menuDTO.setType("root")
        entity = restTemplate.postForEntity(BASE_PATH + "/check", menuDTO, Void)
        needClean = true

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "menuConfig"() {
        given: "构造请求参数"
        def code = "choerodon.code.top.site"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/menu_config?code=" + code, MenuDTO)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "saveMenuConfig"() {
        given: "构造请求参数"
        def menuDTO = menuDOList.get(0)
        def code = "choerodon.code.top.site"
        def menuList = new ArrayList()

        when: "调用方法"
        def menuDTO1 = new MenuDTO()
        BeanUtils.copyProperties(menuDTO, menuDTO1)
        menuDTO1.setType("menu")
        menuDTO1.setCode("choerodon.code.top.site")
        menuDTO1.setParentCode("choerodon.code.top.site")
        menuList.add(menuDTO1)
        def entity = restTemplate.postForEntity(BASE_PATH + "/menu_config?code=" + code, menuList, ExceptionResponse)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "orgMenuConfig"() {
        given: "构造请求参数"
        def organizationId = 0
        def code = "choerodon.code.top.organization"

        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/org/{organization_id}/menu_config?code=" + code, MenuDTO, organizationId)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }

    def "list"() {
        when: "调用方法"
        def entity = restTemplate.getForEntity(BASE_PATH + "/list", List)

        then: "校验结果"
        entity.statusCode.is2xxSuccessful()
    }
}