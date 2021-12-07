package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ExternalTenantVO;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/21
 */
public interface TenantC7nService {

    TenantVO queryTenantById(Long tenantId, Boolean withMoreInfo);

    List<TenantVO> queryTenantByName(String tenantName);

    TenantVO queryTenantWithRoleById(Long tenantId);

    Page<TenantVO> getAllTenants(PageRequest pageable);

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
     * 查询默认组织
     * @return
     */
    Tenant queryDefault();

    /**
     * 创建默认组织
     * @param tenantName
     * @param tenantNum
     */
    void createDefaultTenant(String tenantName, String tenantNum);

    Page<User> pagingQueryUsersOnOrganizationAgile(Long id, Long userId, String email, PageRequest pageRequest, String param, List<Long> notSelectUserIds);

    List<Tenant> queryTenants(Set<Long> tenantIds);

    ExternalTenantVO queryTenantByIdWithExternalInfo(Long organizationId);
    /**
     * 修复部分组织没有多语言问题
     */
    void syncTenantTl();

}
