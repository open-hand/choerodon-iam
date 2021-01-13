package io.choerodon.iam.app.service;

import java.util.Set;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/1/13
 * @Modified By:
 */
public interface DiscoveryService {

    Set<String> getServiceInstance();
}
