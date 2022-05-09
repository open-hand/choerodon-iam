package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.iam.api.vo.SimpleRoleVO;
import io.choerodon.iam.api.vo.SyncStatusVO;
import io.choerodon.iam.api.vo.UserPermissionVO;
import io.choerodon.iam.api.vo.agile.RoleVO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/12 16:59
 */
public interface RoleC7nService {

    /**
     * 项目层查询角色列表以及该角色下的用户数量
     *
     * @param roleAssignmentSearchDTO
     * @param projectId
     * @return RoleVO列表
     */
    List<RoleVO> listRolesWithUserCountOnProjectLevel(Long projectId, RoleAssignmentSearchDTO roleAssignmentSearchDTO);


    /**
     * 查询当前用户自己的角色
     *
     * @return RoleC7nDTO分页
     */
    Page<UserPermissionVO> listRole(PageRequest pageRequest, Long tenantId, String name, String level, String params);


    Page<io.choerodon.iam.api.vo.RoleVO> pagingSearch(PageRequest pageRequest, Long tenantId, String name, String code, String roleLevel,
                                                      Boolean builtIn, Boolean enabled, String params);

    Role getTenantAdminRole(Long organizationId);

    /**
     * 查询组织层角色
     *
     * @param organizationId
     * @param roleName
     * @param onlySelectEnable
     * @return RoleDTO列表
     */
    List<RoleDTO> listRolesByName(Long organizationId, String roleName, String code, String labelName, Boolean onlySelectEnable);

    List<RoleC7nDTO> listRolesWithUserCountOnOrganizationLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    List<RoleC7nDTO> listRolesWithClientCountOnProjectLevel(ClientRoleQueryVO clientRoleQueryVO, Long sourceId);

    List<RoleC7nDTO> listRolesWithClientCountOnOrganizationLevel(ClientRoleQueryVO clientRoleQueryVO, Long sourceId, Boolean enable);

    List<Long> queryIdsByLabelNameAndLabelType(String labelName, String labelType);


    List<Role> listByLabelNames(Long tenantId, String labelName);


    Role getSiteRoleByCode(String code);

    List<User> listVindicators();

    List<SimpleRoleVO> listRolesByIds(List<Long> roleIds, Long tenantId);

    void syncRolesAndPermission();

    void fixChildPermission();

    SyncStatusVO syncRolesAndPermissionStatus();
}
