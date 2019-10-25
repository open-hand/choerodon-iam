package io.choerodon.base.infra.utils

import io.choerodon.asgard.saga.consumer.MockHttpServletRequest
import io.undertow.servlet.spec.HttpServletRequestImpl
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest


/**
 * @author Anders
 * @date 2019/8/15 9:27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([RequestContextHolder.class])
class IpUtilsSpec extends Specification {
    private IpUtils ipUtils = new IpUtils()

    def "getIPAddr"() {
        given: "参数准备"
        def ipAddress = "127.0.0.1"

        and: "Mock"
        MockHttpServletRequest request = PowerMockito.mock(MockHttpServletRequest)

        PowerMockito.mockStatic(RequestContextHolder.class)

        PowerMockito.when(RequestContextHolder.getRequestAttributes()).thenReturn(new ServletRequestAttributes(request))

        PowerMockito.when(request.getRemoteAddr()).thenReturn(ipAddress)

        when: "调用方法"
        ipUtils.getIPAddr()
        then: "结果比对"
        noExceptionThrown()
    }
}
