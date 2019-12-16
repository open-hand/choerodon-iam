package io.choerodon.base.app.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;

import io.choerodon.base.api.dto.LdapAccountDTO;
import io.choerodon.base.api.dto.LdapConnectionDTO;
import io.choerodon.base.api.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.base.infra.dto.LdapAutoDTO;
import io.choerodon.base.infra.dto.LdapDTO;
import io.choerodon.base.infra.dto.LdapErrorUserDTO;
import io.choerodon.base.infra.dto.LdapHistoryDTO;

/**
 * @author wuguokai
 */
public interface LdapService {
    LdapDTO create(Long organizationId, LdapDTO ldapDTO);

    LdapDTO update(Long organizationId, LdapDTO ldapDTO);

    LdapDTO queryByOrganizationId(Long organizationId);

    void delete(Long organizationId);

    /**
     * 测试是否能连接到ldap
     *
     * @param organizationId 组织id
     * @return LdapConnectionDTO 连接测试结构体
     */
    LdapConnectionDTO testConnect(Long organizationId, LdapAccountDTO ldapAccountDTO);

    Map<String, Object> testConnect(LdapDTO ldap);

    /**
     * 根据ldap配置同步用户
     *
     * @param organizationId
     */
    void syncLdapUser(Long organizationId);

    LdapDTO validateLdap(Long organizationId, Long id);

    /**
     * 根据ldap id 查询最新的一条记录
     *
     * @return
     */
    LdapHistoryDTO queryLatestHistory(Long organizationId);

    LdapDTO enableLdap(Long organizationId);

    LdapDTO disableLdap(Long organizationId);

    LdapHistoryDTO stop(Long organizationId);

    /**
     * 根据ldapId分页查询ldap history
     * @return
     */
    PageInfo<LdapHistoryDTO> pagingQueryHistories(Pageable Pageable, Long organizationId);

    PageInfo<LdapErrorUserDTO> pagingQueryErrorUsers(Pageable Pageable, Long id, LdapErrorUserDTO ldapErrorUserDTO);

    LdapAutoDTO createLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    void handleLdapAutoTask(LdapAutoTaskEventPayload ldapAutoTaskEventPayload);

    LdapAutoDTO updateLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    LdapAutoDTO queryLdapAutoDTO(Long organizationId);
}
