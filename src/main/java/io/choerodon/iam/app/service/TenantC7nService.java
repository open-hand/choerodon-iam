package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.springframework.core.io.Resource;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/21
 */
public interface TenantC7nService {

    void updateTenant(Long tenantId, TenantVO tenantVO);

    TenantVO queryTenantById(Long tenantId, Boolean withMoreInfo);

    List<TenantVO> queryTenantByName(String tenantName);

    TenantVO queryTenantWithRoleById(Long tenantId);

    Page<TenantVO> pagingQuery(PageRequest pageRequest, String name, String code, String ownerRealName, Boolean enabled, String homePage, String params, String orgOrigin);

    void setVisitor(Long tenantId, TenantConfigVO tenantConfigVO);

    Page<TenantVO> getAllTenants(PageRequest pageable);

    Tenant enableOrganization(Long organizationId, Long userId);

    Tenant disableOrganization(Long organizationId, Long userId);

    Boolean check(TenantVO tenantVO);

    Page<User> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, PageRequest pageRequest, String param);

    /**
     * 分页获取 指定id范围 的 组织简要信息
     *
     * @param orgIds      指定的组织范围
     * @param name        组织名查询参数
     * @param code        组织编码查询参数
     * @param enabled     组织启停用查询参数
     * @param params      全局模糊搜索查询参数
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    Page<Tenant> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, PageRequest pageRequest);


    ProjectOverViewVO projectOverview(Long organizationId);

    List<ProjectOverViewVO> appServerOverview(Long organizationId);


    /**
     * 根据组织id集合查询组织
     *
     * @param ids id集合
     * @return 组织信息
     */
    List<Tenant> queryTenantsByIds(Set<Long> ids);

    List<TenantVO> selectSelfTenants(TenantDTO params);

    /**
     * 查询用户组织列表，根据into字段判断是否能够进入
     *
     * @param userId 用户id
     */
    List<TenantVO> listOwnedOrganizationByUserId(Long userId);

    /**
     * 统计组织下的用户数量
     *
     * @param organizationId 组织id
     */
    int countUserNum(Long organizationId);

    /**
     * 统计组织下的项目数量
     *
     * @param organizationId 组织id
     */
    int countProjectNum(Long organizationId);

    Tenant queryDefault();

    void createDefaultTenant(String tenantName, String tenantNum);

    Page<User> pagingQueryUsersOnOrganizationAgile(Long id, Long userId, String email, PageRequest pageRequest, String param, List<Long> notSelectUserIds);

    List<Tenant> queryTenants(Set<Long> tenantIds);

    Resource exportTenant(Boolean isAll, List<Long> tenantIds);

}
