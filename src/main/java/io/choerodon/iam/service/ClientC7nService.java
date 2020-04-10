package io.choerodon.iam.service;

import java.util.List;

import org.hzero.iam.domain.entity.Client;

import io.choerodon.base.api.vo.ClientVO;

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

    /**
     * 创建带类型的客户端
     * @param organizationId
     * @param clientVO
     * @return
     */
    Client createClientWithType(Long organizationId, ClientVO clientVO);

    /**
     * 根据source_id查询客户端信息
     * @param organizationId
     * @param sourceId
     * @return
     */
    Client queryClientBySourceId(Long organizationId, Long sourceId);

    Client queryByName(Long orgId, String clientName);

}
