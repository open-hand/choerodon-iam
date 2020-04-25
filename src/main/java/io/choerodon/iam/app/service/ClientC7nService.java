package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.Client;


/**
 * @author scp
 * @date 2020/3/27
 * @description
 */
public interface ClientC7nService {

    Client getDefaultCreateData(Long orgId);


    Client queryByName(Long orgId, String clientName);

}
