package io.choerodon.base.app.service.impl

import spock.lang.*
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.api.dto.RemoteTokenBase64VO
import io.choerodon.base.app.service.RemoteConnectionRecordService
import io.choerodon.base.app.service.RemoteTokenService
import io.choerodon.base.infra.dto.RemoteTokenDTO
import io.choerodon.base.infra.mapper.RemoteTokenMapper
import io.choerodon.core.exception.CommonException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT


/**
 * @author PENGYUHUA 2019/08/14 17:46
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class RemoteTokenServiceImplTeSpec extends Specification {
    private remoteTokenMapper = Mock(RemoteTokenMapper)
    private remoteConnectionRecordService = Mock(RemoteConnectionRecordService)
    private producer = Mock(TransactionalProducer)
    private RemoteTokenService remoteTokenService
    @Shared private remoteTokenDTO
    @Shared RemoteTokenBase64VO remoteTokenBase64VO

    def "setup" () {
        remoteTokenService = new RemoteTokenServiceImpl(remoteConnectionRecordService, remoteTokenMapper, producer)
        remoteTokenDTO = new RemoteTokenDTO()
        remoteTokenDTO.setName("remoteToken")
        remoteTokenDTO.setEmail("relaxingchu@qq.com")
        remoteTokenBase64VO = new RemoteTokenBase64VO()
        remoteTokenBase64VO.setId(256L)
    }

    def "test createNewOne"() {
        given: "构建测试数据"
        def organizationId = 1

        when: "调用创建令牌方法[查询所有令牌结果不为空且更新失败]"
        remoteTokenService.createNewOne(organizationId, remoteTokenDTO)

        then: "结果比对"
        1 * remoteTokenMapper.select(_) >> {
            List<RemoteTokenDTO> allUnexpired = new ArrayList<>()
            allUnexpired.add(new RemoteTokenDTO())
            return allUnexpired
        }
        1 * remoteTokenMapper.updateByPrimaryKeySelective(_) >> -1
        CommonException exception = thrown()
        exception.code == "error.remote.token.expired"

        when: "调用创建令牌方法[查询所有令牌结果为空，添加令牌失败]"
        remoteTokenService.createNewOne(organizationId, remoteTokenDTO)

        then: "结果比对"
        1 * remoteTokenMapper.select(_) >> null
        1 * remoteTokenMapper.insertSelective(_) >> -1
        exception = thrown()
        exception.code == "error.remote.token.insert"

        when: "调用创建令牌方法[]"
        def result = remoteTokenService.createNewOne(organizationId, remoteTokenDTO)

        then: "结果比对"
        1 * remoteTokenMapper.select(_) >> null
        1 * remoteTokenMapper.insertSelective(_) >> 1
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> {
            return remoteTokenBase64VO
        }
        result.getId() == 256L

    }

    def "test expired"() {
        given: "构建测试数据"
        def organizationId = 1
        def id = 1
        RemoteTokenDTO remoteTokenDTO1 = new RemoteTokenDTO()

        when: "调用令牌失效方法[mapper查询结果为空]"
        remoteTokenService.expired(organizationId, id)

        then: "结果检验"
        1 * remoteTokenMapper.selectByPrimaryKey(_) >> null
        CommonException exception = thrown()
        exception.code == "error.remote.token.not.exist"

        when: "调用令牌失效方法[获取失败Expired值为true]"

        remoteTokenService.expired(organizationId, id)

        then: "结果检验"
        1 * remoteTokenMapper.selectByPrimaryKey(_) >> {
            remoteTokenDTO1.setExpired(true)
            return remoteTokenDTO1
        }
        exception = thrown()
        exception.code == "error.remote.token.already.expired"

        when: "调用令牌失效方法[mapper更新信息失败]"
        remoteTokenService.expired(organizationId, id)

        then: "结果检验"
        1 * remoteTokenMapper.selectByPrimaryKey(_) >> {
            remoteTokenDTO1.setExpired(false)
            return remoteTokenDTO1
        }
        1 * remoteTokenMapper.updateByPrimaryKeySelective(_) >> 0
        exception = thrown()
        exception.code == "error.remote.token.expired"

        when: "调用令牌失效方法[正常返回]"
        def result = remoteTokenService.expired(organizationId, id)

        then: "结果检验"
        1 * remoteTokenMapper.selectByPrimaryKey(_) >> {
            remoteTokenDTO1.setExpired(false)
            return remoteTokenDTO1
        }
        1 * remoteTokenMapper.updateByPrimaryKeySelective(_) >> 1
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> {
            return remoteTokenBase64VO
        }
        result.getId() == 256L
    }
}
