package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/4/21
 * @description
 */
public interface TenantC7nService {

    void updateOrganization(Long tenantId, TenantVO tenantVO);

    TenantVO queryTenantById(Long tenantId);

    List<TenantVO> queryTenantByName(String tenantName);

    TenantVO queryTenantWithRoleById(Long tenantId);

    Page<TenantVO> pagingQuery(PageRequest pageRequest, String name, String code, String ownerRealName, Boolean enabled, String params);

    Page<TenantVO> getAllOrgs(PageRequest pageable);

    Tenant enableOrganization(Long organizationId, Long userId);

    Tenant disableOrganization(Long organizationId, Long userId);

    void check(TenantVO tenantVO);

    Page<User> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, PageRequest pageRequest, String param);

    /**
     * 分页获取 指定id范围 的 组织简要信息
     *
     * @param orgIds   指定的组织范围
     * @param name     组织名查询参数
     * @param code     组织编码查询参数
     * @param enabled  组织启停用查询参数
     * @param params   全局模糊搜索查询参数
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    Page<Tenant> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, PageRequest pageRequest);


    ProjectOverViewVO projectOverview(Long organizationId);

    List<ProjectOverViewVO> appServerOverview(Long organizationId);

    /**
     * 判读组织是否是新组织
     * @param organizationId
     * @return
     */
    boolean checkOrganizationIsNew(Long organizationId);

}