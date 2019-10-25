package io.choerodon.base.api.service.impl

import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.base.api.dto.RemoteTokenBase64VO
import io.choerodon.base.api.dto.CommonCheckResultVO
import io.choerodon.base.app.service.RemoteConnectionRecordService
import io.choerodon.base.app.service.impl.RemoteTokenServiceImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import io.choerodon.base.infra.dto.RemoteTokenDTO
import io.choerodon.base.infra.enums.RemoteTokenCheckFailedType
import io.choerodon.base.infra.mapper.RemoteTokenMapper
import spock.lang.Specification

/**
 * @author AndersIes
 * @date 2019/8/14 11 43
 */
class RemoteTokenServiceImplSpec  extends Specification {

    private RemoteConnectionRecordService remoteConnectionRecordService = Mock(RemoteConnectionRecordService)

    private RemoteTokenMapper remoteTokenMapper = Mock(RemoteTokenMapper)

    private TransactionalProducer producer = Mock(TransactionalProducer)

    private RemoteTokenServiceImpl remoteTokenService = new RemoteTokenServiceImpl(remoteConnectionRecordService, remoteTokenMapper, producer)

    def "pagingTheHistoryList"() {
        given: "参数准备"
        Long organizationId = 1L
        PageRequest pageRequest = new PageRequest(1, 20, new Sort(Sort.Direction.ASC, "organization_id"))
        RemoteTokenDTO filterDTO = new RemoteTokenDTO()
        String params = "params"
        def remoteTokenBase64VOId = 2333L

        when: "调用方法"
        remoteTokenService.pagingTheHistoryList(organizationId, pageRequest ,filterDTO ,params)
        then: "结果比对"
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> null
        1 * remoteTokenMapper.filterHistoryUnderOrg(_, _, _, _)
        noExceptionThrown()

        when: "调用方法"
        remoteTokenService.pagingTheHistoryList(organizationId, pageRequest ,filterDTO ,params)
        then: "结果比对"
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> {
            RemoteTokenBase64VO remoteTokenBase64VO = new RemoteTokenBase64VO()
            remoteTokenBase64VO.setId(remoteTokenBase64VOId)
            return remoteTokenBase64VO
        }
        1 * remoteTokenMapper.filterHistoryUnderOrg(_, _, _, _)
        noExceptionThrown()

    }

    def "getTheLatest"() {
        given: "参数准备"
        Long organizationId = 1L

        when: "调用方法"
        remoteTokenService.getTheLatest(organizationId)
        then: "结果比对"
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> null
        noExceptionThrown()

        when: "调用方法"
        remoteTokenService.getTheLatest(organizationId)
        then: "结果比对"
        1 * remoteTokenMapper.selectLatestUnderOrg(_) >> {
            RemoteTokenBase64VO subRemoteTokenBase64VO1 = new RemoteTokenBase64VO()
            subRemoteTokenBase64VO1.setName("name")
            subRemoteTokenBase64VO1.setEmail("email")
            subRemoteTokenBase64VO1.setRemoteToken("token")
            return subRemoteTokenBase64VO1
        }
        noExceptionThrown()
    }

    def "checkToken"() {
        given: "参数准备"
        String remoteToken = "remoteToken"
        CommonCheckResultVO resultDTO

        when: "调用方法"
        resultDTO = remoteTokenService.checkToken(remoteToken)
        then: "结果比对"
        1 * remoteTokenMapper.selectOne(_) >> null
        resultDTO.getFailed()
        resultDTO.getMessage() == RemoteTokenCheckFailedType.NOTEXIST.value()
        noExceptionThrown()

        when: "调用方法"
        resultDTO = remoteTokenService.checkToken(remoteToken)
        then: "结果比对"
        1 * remoteTokenMapper.selectOne(_) >> {
            RemoteTokenDTO remoteTokenDTO = new RemoteTokenDTO()
            remoteTokenDTO.setExpired(true)
            return remoteTokenDTO
        }
        resultDTO.getFailed()
        resultDTO.getMessage() == RemoteTokenCheckFailedType.EXPIRED.value()
        noExceptionThrown()

        when: "调用方法"
        resultDTO = remoteTokenService.checkToken(remoteToken)
        then: "结果比对"
        1 * remoteTokenMapper.selectOne(_) >> {
            RemoteTokenDTO remoteTokenDTO = new RemoteTokenDTO()
            remoteTokenDTO.setExpired(false)
            return remoteTokenDTO
        }
        !resultDTO.getFailed()
        noExceptionThrown()
    }
}
