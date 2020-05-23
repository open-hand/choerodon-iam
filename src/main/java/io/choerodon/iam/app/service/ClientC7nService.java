package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.Client;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * @author scp
 * @date 2020/3/27
 * @description
 */
public interface ClientC7nService {

    Client getDefaultCreateData(Long orgId);


    Client queryByName(Long orgId, String clientName);

    /**
     *
     * @param pageRequest
     * @param resourceType
     * @param sourceId
     * @param clientRoleQueryVO
     * @param roleId
     * @return
     */
    Page<Client> pagingQueryUsersByRoleId(PageRequest pageRequest,
                                          ResourceLevel resourceType,
                                          Long sourceId,
                                          ClientRoleQueryVO clientRoleQueryVO,
                                          Long roleId);

}
