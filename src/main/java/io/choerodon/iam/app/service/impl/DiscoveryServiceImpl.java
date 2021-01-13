package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.constant.ServerConstants.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.DiscoveryService;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/1/13
 * @Modified By:
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 有模块中一个服务启动成功，这认为有该模块
     */
    @Override
    public Set<String> getServiceInstance() {
        Set<String> services = new HashSet<>();
        discoveryClient.getServices().forEach(t -> {
            if (t.equals(SERVER_AGILE)) {
                services.add(AGILE);
            } else if (t.equals(SERVER_DEVOPS) || t.equals(SERVER_GITLAB) || t.equals(SERVER_WORKFLOW)
                    || t.equals(SERVER_CODE) || t.equals(SERVER_PROD)) {
                services.add(DEVOPS);
            } else if (t.equals(SERVER_TEST)) {
                services.add(TEST);
            }
        });
        return services;
    }

}
