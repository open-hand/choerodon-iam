package io.choerodon.base.app.service.impl;

import java.util.List;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.InstanceService;

/**
 * @author wkj
 * @since 2019/11/1
 **/
@Service
public class InstanceServiceImpl implements InstanceService {
    private DiscoveryClient discoveryClient;

    public InstanceServiceImpl(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }
    @Override
    public List<String> listAll() {
        List<String> services = discoveryClient.getServices();
        return services;
    }
}
