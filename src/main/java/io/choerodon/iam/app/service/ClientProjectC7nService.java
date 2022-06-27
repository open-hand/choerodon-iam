package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.Role;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/11/3
 * @description
 */
public interface ClientProjectC7nService {

    ClientVO create(ClientVO clientVO);

    void delete(Long tenantId, Long projectId, Long clientId);

    void update(Long tenantId, Long projectId, Client client);

    Page<Client> pageClient(Long organizationId, Long projectId, String name, String params, PageRequest pageRequest);

    void assignRoles(Long organizationId, Long projectId, Long clientId, List<Long> roleIds);

    List<Role> selectMemberRoles(Long organizationId, Long projectId, Long clientId, String roleName);
}
