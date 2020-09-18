package io.choerodon.iam

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.asgard.saga.producer.TransactionalProducer
import io.choerodon.core.exception.CommonException
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.iam.app.service.MessageSendService
import io.choerodon.iam.infra.feign.fallback.AsgardFeignClientFallback
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.hzero.core.redis.RedisHelper
import org.hzero.iam.domain.repository.MenuRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

/**
 * Created by hailuoliu@choerodon.io on 2018/7/13.
 */
@TestConfiguration
@Import(LiquibaseConfig)
class IntegrationTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @Value('${choerodon.oauth.jwt.key:hzero}')
    String key

    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    LiquibaseExecutor liquibaseExecutor
    final ObjectMapper objectMapper = new ObjectMapper()


    @PostConstruct
    void init() {
        liquibaseExecutor.execute()
        setTestRestTemplateJWT()
    }

    @Bean("mockRedisHelper")
    @Primary
    RedisHelper redisHelper() {
        detachedMockFactory.Mock(RedisHelper)
    }


    @Bean
    @Primary
    MessageSendService messageSendService() {
        detachedMockFactory.Mock(MessageSendService)
    }

    @Bean
    @Primary
    MenuRepository menuRepository() {
        detachedMockFactory.Mock(TestMenuRepositoryImpl)
    }

    @Bean
    AsgardFeignClientFallback asgardFeignClientFallback() {
        detachedMockFactory.Mock(AsgardFeignClientFallback)
    }

    @Bean
    @Primary
    SagaClient sagaClient() {
        detachedMockFactory.Mock(SagaClient)
    }

//    @Bean
//    @Primary
//    UserCaptchaService userCaptchaService() {
//        detachedMockFactory.Mock(UserCaptchaService)
//    }
//
//    @Bean
//    @Primary
//    MessageClient messageClient() {
//        detachedMockFactory.Mock(MessageClient)
//    }
//    @Bean
//    @Primary
//    CaptchaMessageHelper captchaMessageHelper() {
//        detachedMockFactory.Mock(CaptchaMessageHelper)
//    }

    @Bean
    @Primary
    TransactionalProducer transactionalProducer() {
        detachedMockFactory.Mock(TransactionalProducer)
    }

    /**
     * 请求头添加jwt_token
     */
    private void setTestRestTemplateJWT() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('Jwt_token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
    }

    String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails details = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        details.setUserId(1L);
        details.setLanguage("zh_CN");
        details.setTimeZone("GMT+8");
        details.setEmail("hand@hand-china.com");
        details.setOrganizationId(1L);
        try {
            return 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(details), signer).getEncoded()
        } catch (IOException e) {
            throw new CommonException(e)
        }
    }
}

