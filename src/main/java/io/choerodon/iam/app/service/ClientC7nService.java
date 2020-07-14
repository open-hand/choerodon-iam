package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.Client;

import java.util.List;


/**
 * @author scp
 * @since 2020/3/27
 */
public interface ClientC7nService {

    Client getDefaultCreateData(Long orgId);


    Client queryByName(Long orgId, String clientName);

    /**
     * @param pageRequest
     * @param resourceType
     * @param sourceId
     * @param clientRoleQueryVO
     * @param roleId
     * @return client 分页
     */
    Page<Client> pagingQueryUsersByRoleId(PageRequest pageRequest,
                                          ResourceLevel resourceType,
                                          Long sourceId,
                                          ClientRoleQueryVO clientRoleQueryVO,
                                          Long roleId);

    /**
     * 根据角色id给client分配角色
     *
     * @param organizationId
     * @param clientId
     * @param roleIds
     */
    void assignRoles(Long organizationId, Long clientId, List<Long> roleIds);

    ClientVO create(ClientVO clientVO);
}
