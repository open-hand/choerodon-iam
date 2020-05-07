package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.query.ClientRoleQuery;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.Role;

import java.util.List;

/**
 * @author superlee
 * @author wuguokai
 */
public interface RoleService {

    Page<Role> pagingSearch(PageRequest pageRequest, String name, String code, String level,
                            Boolean builtIn, Boolean enabled, String params);

    Page<Role> pagingQueryOrgRoles(Long orgId, PageRequest pageRequest, String name, String code, String level,
                                   Boolean builtIn, Boolean enabled, String params);

    Role create(Role roleDTO);

    Role createBaseOnRoles(Role roleDTO);

    Role update(Role roleDTO);

    Role orgUpdate(Role roleDTO, Long orgId);

    void delete(Long id);

    Role queryById(Long id);

    Role enableRole(Long id);

    Role disableRole(Long id);

    Role orgEnableRole(Long roleId, Long orgId);

    Role orgDisableRole(Long roleId, Long orgId);

    Role queryWithPermissionsAndLabels(Long id);

    List<Role> listRolesWithUserCountOnSiteLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    List<Role> listRolesWithClientCountOnSiteLevel(ClientRoleQuery clientRoleSearchDTO);

    List<Role> listRolesWithUserCountOnOrganizationLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    List<Role> listRolesWithClientCountOnOrganizationLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId);

    List<Role> listRolesWithUserCountOnProjectLevel(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    List<Role> listRolesWithClientCountOnProjectLevel(ClientRoleQuery clientRoleSearchDTO, Long sourceId);

    void check(Role role);

    List<Long> queryIdsByLabelNameAndLabelType(String labelName, String labelType);

    List<Role> selectByLabel(String label, Long organizationId);

    List<Role> listRolesBySourceIdAndTypeAndUserId(String sourceType, Long sourceId, Long userId);

    Role queryByCode(String code);

    List<Role> listRolesByName(String sourceType, Long sourceId, String roleName, Boolean onlySelectEnable);
}
