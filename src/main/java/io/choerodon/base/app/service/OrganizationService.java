package io.choerodon.base.app.service;

import java.util.List;
import java.util.Set;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.ProjectOverViewVO;
import org.springframework.data.domain.Pageable;

import io.choerodon.base.api.dto.OrgSharesDTO;
import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
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

    PageInfo<OrganizationDTO> pagingQuery(Pageable pageable, String name, String code, String ownerRealName, Boolean enabled, String params);

    OrganizationDTO enableOrganization(Long organizationId, Long userId);

    OrganizationDTO disableOrganization(Long organizationId, Long userId);

    void check(OrganizationDTO organization);

    PageInfo<UserDTO> pagingQueryUsersInOrganization(Long organizationId,
                                                     Long userId, String email, Pageable pageable, String param);

    List<OrganizationDTO> queryByIds(Set<Long> ids);

    /**
     * 获取所有组织{id,name}
     *
     * @return list
     */
    PageInfo<OrganizationSimplifyDTO> getAllOrgs(Pageable pageable);


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
    PageInfo<OrgSharesDTO> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, Pageable pageable);

    /**
     * 检查组织是否存在
     * @param orgId
     * @return 存在返回组织信息，不存在抛出not.exist exception
     */
    OrganizationDTO checkNotExistAndGet(Long orgId);

    ProjectOverViewVO projectOverview(Long organizationId);

    List<ProjectOverViewVO> appServerOverview(Long organizationId);

    /**
     * 判读组织是否是新组织
     * @param organizationId
     * @return
     */
    boolean checkOrganizationIsNew(Long organizationId);

    /**
     * 统计组织下的项目数量
     * @param organizationId
     * @return
     */
    int countProjectNum(Long organizationId);

    /**
     * 统计组织下的用户数量
     * @param organizationId
     * @return
     */
    int countUserNum(Long organizationId);
}
