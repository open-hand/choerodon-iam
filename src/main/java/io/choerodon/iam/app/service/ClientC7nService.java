package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Client;

import io.choerodon.iam.api.vo.ClientVO;


/**
 * @author scp
 * @date 2020/3/27
 * @description
 */
public interface ClientC7nService {

    Client getDefaultCreateData(Long orgId);

    /**
     * 根据角色id给client分配角色
     *
     * @param organizationId
     * @param clientId
     * @param roleIds
     * @return
     */
    Client assignRoles(Long organizationId, Long clientId, List<Long> roleIds);

    Client queryByName(Long orgId, String clientName);

}
