package io.choerodon.iam;

import org.hzero.autoconfigure.iam.EnableHZeroIam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("io.choerodon.iam")
@EnableHZeroIam
@EnableDiscoveryClient
@SpringBootApplication
public class IamApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamApplication.class, args);
    }
    
}
