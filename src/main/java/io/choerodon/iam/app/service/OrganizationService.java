package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.infra.dto.OrgSharesDTO;
import io.choerodon.iam.infra.dto.OrganizationDTO;
import io.choerodon.iam.infra.dto.OrganizationSimplifyDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.User;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 */
public interface OrganizationService {

    OrganizationDTO updateOrganization(Long organizationId, OrganizationDTO organizationDTO, String level, Long sourceId);

    OrganizationDTO queryOrganizationById(Long organizationId);

    List<OrganizationDTO> queryOrganizationsByName(String organizationName);

    OrganizationDTO queryOrganizationWithRoleById(Long organizationId);

    Page<OrganizationDTO> pagingQuery(PageRequest pageRequest, String name, String code, String ownerRealName, Boolean enabled, String params);

    OrganizationDTO enableOrganization(Long organizationId, Long userId);

    OrganizationDTO disableOrganization(Long organizationId, Long userId);

    void check(OrganizationDTO organization);

    Page<User> pagingQueryUsersInOrganization(Long organizationId,
                                              Long userId, String email, PageRequest pageRequest, String param);

    List<OrganizationDTO> queryByIds(Set<Long> ids);

    /**
     * 获取所有组织{id,name}
     *
     * @return list
     */
    Page<OrganizationSimplifyDTO> getAllOrgs(PageRequest pageRequest);


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
    Page<OrgSharesDTO> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, PageRequest pageRequest);

    /**
     * 检查组织是否存在
     *
     * @param orgId
     * @return 存在返回组织信息，不存在抛出not.exist exception
     */
    OrganizationDTO checkNotExistAndGet(Long orgId);

    ProjectOverViewVO projectOverview(Long organizationId);

    List<ProjectOverViewVO> appServerOverview(Long organizationId);

    /**
     * 判读组织是否是新组织
     *
     * @param organizationId
     * @return
     */
    boolean checkOrganizationIsNew(Long organizationId);

    /**
     * 统计组织下的项目数量
     *
     * @param organizationId
     * @return
     */
    int countProjectNum(Long organizationId);

    /**
     * 统计组织下的用户数量
     *
     * @param organizationId
     * @return
     */
    int countUserNum(Long organizationId);

    List<Long> getoRoganizationByName(String name);

    Set<TenantVO> selectSelfTenants(TenantDTO params);
}
