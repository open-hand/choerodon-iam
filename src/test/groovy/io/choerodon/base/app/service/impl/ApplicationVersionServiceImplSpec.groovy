package io.choerodon.base.app.service.impl

import io.choerodon.asgard.saga.producer.StartSagaBuilder
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.DependencyInjectUtil
import io.choerodon.base.IntegrationTestConfiguration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import io.choerodon.base.infra.dto.ApplicationVersionDTO
import io.choerodon.core.exception.ext.InsertException
import io.choerodon.core.exception.ext.UpdateException
import io.choerodon.base.infra.mapper.ApplicationVersionMapper
import io.choerodon.base.infra.mapper.ProjectMapper
import io.choerodon.core.exception.CommonException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import java.util.function.Function

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ApplicationVersionServiceImplSpec extends Specification {

    @Autowired
    private ApplicationVersionServiceImpl applicationVersionService
    @Autowired
    private ApplicationVersionMapper realApplicationVersionMapper
    private TransactionalProducer producer = Mock(TransactionalProducer)
    private ApplicationVersionMapper applicationVersionMapper = Mock(ApplicationVersionMapper)
    private ProjectMapper projectMapper = Mock(ProjectMapper)

    void setup() {
        DependencyInjectUtil.setAttribute(applicationVersionService, "applicationVersionMapper", applicationVersionMapper)
        DependencyInjectUtil.setAttribute(applicationVersionService, "producer", producer)
        DependencyInjectUtil.setAttribute(applicationVersionService, "projectMapper", projectMapper)
    }
    void init() {
        def applicationVersionDTO = null
        for (int i = 0; i < 5; i++) {
            applicationVersionDTO = new ApplicationVersionDTO()
            applicationVersionDTO.setName("version-test-1")
            applicationVersionDTO.setStatusCode("released")
            applicationVersionDTO.setDescription("描述信息")
            applicationVersionDTO.setApplicationId(1L)
            realApplicationVersionMapper.insert(applicationVersionDTO)
        }
    }
    void create(ApplicationVersionDTO applicationVersionDTO) {
        if (applicationVersionMapper.insertSelective(applicationVersionDTO) != 1) {
            throw new InsertException("error.application.version.insert")
        }
    }
    void update(ApplicationVersionDTO applicationVersionDTO) {
        applicationVersionDTO.setReleaseDate(null);
        if (applicationVersionMapper.updateByPrimaryKeySelective(applicationVersionDTO) != 1) {
            throw new UpdateException("error.application.version.update");
        }
    }

    def "Create"() {
        given: "设置参数"
        def applicationVersionDTO = new ApplicationVersionDTO()
        applicationVersionDTO.setName("version-test-1")
        applicationVersionDTO.setStatusCode("released")
        applicationVersionDTO.setApplicationId(1L)

        when: "新建应用版本,新增失败"
        applicationVersionService.create(applicationVersionDTO)
        then: "校验结果"
        thrown(InsertException)
        1 * applicationVersionMapper.insertSelective(_) >> 0
        1 * producer.applyAndReturn(_ as StartSagaBuilder, _ as Function<StartSagaBuilder, ApplicationVersionDTO>) >> {
            create(applicationVersionDTO)
        }

        when: "新建应用版本,新增成功"
        applicationVersionService.create(applicationVersionDTO)
        then: "校验结果"
        1 * applicationVersionMapper.insertSelective(_) >> 1
        1 * producer.applyAndReturn(_ as StartSagaBuilder, _ as Function<StartSagaBuilder, ApplicationVersionDTO>) >> {
            create(applicationVersionDTO)
        }
    }

    def "Update"() {
        given: "设置参数"
        def applicationVersionDTO = new ApplicationVersionDTO()
        applicationVersionDTO.setId(1L)
        applicationVersionDTO.setName("version-test-1")
        applicationVersionDTO.setStatusCode("released")
        applicationVersionDTO.setApplicationId(1L)

        when: "更新应用版本,更新失败"
        applicationVersionService.update(applicationVersionDTO)
        then: "校验结果"
        thrown(UpdateException)
        1 * applicationVersionMapper.updateByPrimaryKeySelective(_) >> 0
        1 * producer.applyAndReturn(_ as StartSagaBuilder, _ as Function<StartSagaBuilder, ApplicationVersionDTO>) >> {
            update(applicationVersionDTO)
        }

        when: "更新应用版本,更新成功"
        applicationVersionService.update(applicationId, applicationVersionDTO)
        then: "校验结果"
        1 * applicationVersionMapper.updateByPrimaryKeySelective(_) >> 1
        1 * producer.applyAndReturn(_ as StartSagaBuilder, _ as Function<StartSagaBuilder, ApplicationVersionDTO>) >> {
            update(applicationVersionDTO)
        }
    }

    def "Delete"() {
        given: "设置参数"
        def appVersionId = 1L

        when: "根据主键删除应用,删除失败"
        applicationVersionService.delete(appVersionId)
        then: "校验结果"
        thrown(CommonException)
        1 * applicationVersionMapper.deleteByPrimaryKey(_) >> 0

        when: "根据主键删除应用,删除成功"
        applicationVersionService.delete(appVersionId)
        then: "校验结果"
        1 * applicationVersionMapper.deleteByPrimaryKey(_) >> 1
    }

    def "CheckName"() {
        given: "设置参数"
        def applicationId = 1L
        def name = "version-test-1"
        when: "校验name的唯一性"
        applicationVersionService.checkName(applicationId, name)
        then: "校验结果"
        1 * applicationVersionMapper.select(_) >> Mock(ArrayList)
    }

    def "ListByOptions"() {
        given: "设置参数"
        init()
        def applicationId = 1L
        def pageRequest = new PageRequest(1, 3)
        pageRequest.setSort(new Sort(new Sort.Order("id")))
        def name = "version-test-1"
        def description = "描述信息"
        def status = "released"
        def params = new String[2]
        params[0] = "ve"
        params[1] = "ch"
        def applicationVersionServiceImpl = new ApplicationVersionServiceImpl(realApplicationVersionMapper, producer, projectMapper)
        when: "查询应用下的版本"
        def res = applicationVersionServiceImpl.listByOptions(pageRequest, applicationId, name, description, status, params)
        then: "校验结果"
        res.list.size() > 0
    }
}
