package io.choerodon.iam;

import org.hzero.autoconfigure.iam.saas.EnableHZeroIamSaas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableFeignClients("io.choerodon.iam")
@EnableHZeroIamSaas
@EnableDiscoveryClient
@SpringBootApplication
public class IamApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(IamApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(IamApplication.class, args);
        } catch (Exception e) {
            LOGGER.error("Start exception:", e);
        }
    }

    @Bean
    @Qualifier("excel-executor")
    public AsyncTaskExecutor excelImportUserExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("excel-executor");
        executor.setMaxPoolSize(5);
        executor.setCorePoolSize(4);
        return executor;
    }

} 
