package io.choerodon.base.app.service;

import java.util.List;
import java.util.Set;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;

import io.choerodon.base.api.dto.OrgSharesDTO;
import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.api.vo.RemoteTokenManagementResultVO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.UserDTO;

/**
 * @author wuguokai
 */
public interface OrganizationService {

    OrganizationDTO updateOrganization(Long organizationId, OrganizationDTO organizationDTO, String level, Long sourceId);

    OrganizationDTO queryOrganizationById(Long organizationId);

    List<OrganizationDTO> queryOrganizationsByName(String organizationName);

    OrganizationDTO queryOrganizationWithRoleById(Long organizationId);

    PageInfo<OrganizationDTO> pagingQuery(Pageable Pageable, String name, String code, String ownerRealName, Boolean enabled, String params);

    OrganizationDTO enableOrganization(Long organizationId, Long userId);

    OrganizationDTO disableOrganization(Long organizationId, Long userId);

    void check(OrganizationDTO organization);

    PageInfo<UserDTO> pagingQueryUsersInOrganization(Long organizationId,
                                                     Long userId, String email, Pageable Pageable, String param);

    List<OrganizationDTO> queryByIds(Set<Long> ids);

    /**
     * 获取所有组织{id,name}
     *
     * @return list
     */
    PageInfo<OrganizationSimplifyDTO> getAllOrgs(Pageable Pageable);


    /**
     * 分页获取 指定id范围 的 组织简要信息
     *
     * @param orgIds   指定的组织范围
     * @param name     组织名查询参数
     * @param code     组织编码查询参数
     * @param enabled  组织启停用查询参数
     * @param params   全局模糊搜索查询参数
     * @param Pageable 分页参数
     * @return 分页结果
     */
    PageInfo<OrgSharesDTO> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, Pageable Pageable);

    /**
     * 启用远程令牌功能
     *
     * @param organizationId      组织ID
     * @param objectVersionNumber 乐观锁编号
     * @return RemoteTokenManagementResultVO 远程令牌管理结果VO
     */
    RemoteTokenManagementResultVO remoteTokenFunctionEnable(Long organizationId, Long objectVersionNumber);

    /**
     * 停用远程令牌功能
     *
     * @param organizationId      组织ID
     * @param objectVersionNumber 乐观锁编号
     * @return RemoteTokenManagementResultVO 远程令牌管理结果VO
     */
    RemoteTokenManagementResultVO remoteTokenFunctionDisable(Long organizationId, Long objectVersionNumber);

    /**
     * 检查组织是否具有远程连接功能
     *
     * @param organizationId 组织编号
     * @return @return RemoteTokenManagementResultVO 远程令牌管理结果VO
     */
    RemoteTokenManagementResultVO remoteTokenFunctionCheck(Long organizationId);

}
