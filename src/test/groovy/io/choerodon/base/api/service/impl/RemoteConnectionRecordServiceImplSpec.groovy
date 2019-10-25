package io.choerodon.base.api.service.impl

import io.choerodon.base.IntegrationTestConfiguration
import io.choerodon.base.app.service.impl.RemoteConnectionRecordServiceImpl
import io.choerodon.base.infra.mapper.RemoteConnectionRecordMapper
import io.choerodon.base.infra.utils.IpUtils
import io.choerodon.core.exception.CommonException
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Anders
 * @date 2019/8/15 9:16
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([RemoteConnectionRecordServiceImpl.class])
class RemoteConnectionRecordServiceImplSpec extends Specification {
    private final static String REMOTE_CONNECTION_RECORD_INSERT_EXCEPTION = "error.remote.connection.record.insert";

    private RemoteConnectionRecordMapper remoteConnectionRecordMapper = Mock(RemoteConnectionRecordMapper)

    private RemoteConnectionRecordServiceImpl remoteConnectionRecordService = new RemoteConnectionRecordServiceImpl(remoteConnectionRecordMapper)

    def "successRecord"() {
        given: "参数准备"
        Long remoteTokenId = 1L

        and:"Mock"
        IpUtils ipUtils = PowerMockito.mock(IpUtils.class)
        PowerMockito.whenNew(IpUtils.class).withNoArguments().thenReturn(ipUtils)
        PowerMockito.when(ipUtils.getIPAddr()).thenReturn(null)

        when: "调用方法"
        remoteConnectionRecordService.successRecord(remoteTokenId)

        then: "结果比对"
        1 * remoteConnectionRecordMapper.insertSelective(_) >> 1
        1 * remoteConnectionRecordMapper.selectByPrimaryKey(_)
        // noExceptionThrown()

        when: "调用方法"
        remoteConnectionRecordService.successRecord(remoteTokenId)

        then: "结果比对"
        1 * remoteConnectionRecordMapper.insertSelective(_) >> 0
        def exception = thrown(CommonException)
        exception.code == REMOTE_CONNECTION_RECORD_INSERT_EXCEPTION

    }
}

