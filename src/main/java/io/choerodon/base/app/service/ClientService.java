package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.ClientVO;
import org.springframework.data.domain.Pageable;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.api.query.ClientRoleQuery;
import io.choerodon.base.api.dto.SimplifiedClientDTO;
import io.choerodon.base.infra.dto.ClientDTO;

import java.util.List;

/**
 * @author wuguokai
 */
public interface ClientService {
    ClientDTO create(Long orgId, ClientDTO clientDTO);

    ClientDTO getDefaultCreateData(Long orgId);

    /**
     * 更新客户端
     *
     * @param clientDTO
     * @return
     */
    ClientDTO update(ClientDTO clientDTO);

    /**
     * 根据id删除客户端，同时删除member_role里的关系数据
     *
     * @param orgId
     * @param clientId
     */
    void delete(Long orgId, Long clientId);

    ClientDTO query(Long orgId, Long clientId);

    ClientDTO queryByName(Long orgId, String clientName);

    /**
     * 分页查询client
     *
     * @param clientDTO
     * @param Pageable
     * @param param
     * @return
     */
    PageInfo<ClientDTO> list(ClientDTO clientDTO, Pageable Pageable, String param);

    void check(ClientDTO client);

    PageInfo<ClientDTO> pagingQueryUsersByRoleId(Pageable Pageable, ResourceType resourceType, Long sourceId, ClientRoleQuery clientRoleSearchDTO, Long roleId);


    PageInfo<SimplifiedClientDTO> pagingQueryAllClients(Pageable Pageable, String params);

    /**
     * 根据角色id给client分配角色
     *
     * @param organizationId
     * @param clientId
     * @param roleIds
     * @return
     */
    ClientDTO assignRoles(Long organizationId, Long clientId, List<Long> roleIds);
    /**
     * 创建带类型的客户端
     * @param organizationId
     * @param clientVO
     * @return
     */
    ClientDTO createClientWithType(Long organizationId, ClientVO clientVO);

}
