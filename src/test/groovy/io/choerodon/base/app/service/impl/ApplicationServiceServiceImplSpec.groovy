package io.choerodon.base.app.service.impl

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.ApplicationServiceService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import io.choerodon.base.infra.dto.ApplicationServiceDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author wanghao
 * @Date 2019/8/15 22:20
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ApplicationServiceServiceImplSpec extends Specification{
//    @Autowired
//    private ApplicationServiceMapper applicationServiceMapper
    @Autowired
    private ApplicationServiceService applicationServiceService
    void init() {
        def applicationServiceDTO = null
        for (int i = 0; i < 5; i++) {
            applicationServiceDTO = new ApplicationServiceDTO()
            applicationServiceDTO.setId(i)
            applicationServiceDTO.setName("apim总前端" + i)
            applicationServiceDTO.setCode("chot-c7napim" + i)
            applicationServiceDTO.setActive(true)
            applicationServiceDTO.setAppId("1" + i)
            applicationServiceDTO.setType("custom")
            applicationServiceMapper.insert(applicationServiceDTO)
        }
    }
    def "pagingByOptions"() {
        given: "构建参数"
        init()
        def organizationId = 1L
        def pageRequest = new PageRequest(1, 3)
        pageRequest.setSort(new Sort(new Sort.Order("id")))
        def name = "apim总前端1"
        def code = "chot-c7napim1"
        def active = true
        def type = "custom"
        def params = new String[2]
        params[0] = "ts"
        params[1] = "ch"

        when: "查询应用下的版本"
        def res = applicationServiceService.pagingByOptions(pageRequest, organizationId, name, code, active, type, params)
        then: "校验结果"
        res.list.size() > 0
    }
}
