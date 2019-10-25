package io.choerodon.base.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.base.api.query.ClientRoleQuery;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.RoleDTO;

/**
 * @author superlee
 * @author wuguokai
 */
public interface RoleService {

    PageInfo<RoleDTO> pagingSearch(Pageable Pageable, String name, String code, String level,
                                   Boolean builtIn, Boolean enabled, String params);

    PageInfo<RoleDTO> pagingQueryOrgRoles(Long orgId, Pageable Pageable, String name, String code, String level,
                                          Boolean builtIn, Boolean enabled, String params);

    RoleDTO create(RoleDTO roleDTO);

    RoleDTO createBaseOnRoles(RoleDTO roleDTO);

    RoleDTO update(RoleDTO roleDTO);

    RoleDTO orgUpdate(RoleDTO roleDTO, Long orgId);

    void delete(Long id);

    RoleDTO queryById(Long id);

    RoleDTO enableRole(Long id);

    RoleDTO disableRole(Long id);

    RoleDTO orgEnableRole(Long roleId, Long orgId);

    RoleDTO orgDisableRole(Long roleId, Long orgId);

    RoleDTO queryWithPermissionsAndLabels(Long id);

    List<RoleDTO> listRolesWithUserCountOnSiteLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    List<RoleDTO> listRolesWithClientCountOnSiteLevel(ClientRoleQuery clientRoleSearchDTO);

    List<RoleDTO> listRolesWithUserCountOnOrganizationLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    List<RoleDTO> listRolesWithClientCountOnOrganizationLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId);

    List<RoleDTO> listRolesWithUserCountOnProjectLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    List<RoleDTO> listRolesWithClientCountOnProjectLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId);

    void check(RoleDTO role);

    List<Long> queryIdsByLabelNameAndLabelType(String labelName, String labelType);

    List<RoleDTO> selectByLabel(String label, Long organizationId);

    List<RoleDTO> listRolesBySourceIdAndTypeAndUserId(String sourceType, Long sourceId, Long userId);

    RoleDTO queryByCode(String code);

    List<RoleDTO> listRolesByName(String sourceType, Long sourceId, String roleName, Boolean onlySelectEnable);
}
