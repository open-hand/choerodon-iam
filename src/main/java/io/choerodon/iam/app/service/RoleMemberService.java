package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;

/**
 * @author superlee
 * @author wuguokai
 * @author zmf
 */
public interface RoleMemberService {

    MemberRole insertSelective(MemberRole memberRoleDTO);

    List<MemberRole> createOrUpdateRolesByMemberIdOnOrganizationLevel(
            Boolean isEdit, Long organizationId, List<Long> memberIds, List<MemberRole> memberRoleDTOList, String memberType);

    List<MemberRole> createOrUpdateRolesByMemberIdOnProjectLevel(
            Boolean isEdit, Long projectId, List<Long> memberIds, List<MemberRole> memberRoleDTOList, String memberType);

    /**
     * @param projectId
     * @param userId
     * @param syncAll 删除子项目所有权限
     */
    void deleteOnProjectLevel(Long projectId, Long userId, Boolean syncAll);

    void deleteProjectRole(Long projectId, Long userId, List<Long> roleIds, Boolean syncAll);

    List<MemberRole> insertOrUpdateRolesOfUserByMemberId(
            Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType);

    List<MemberRole> insertOrUpdateRolesOfUserByMemberId(
            Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType, Boolean syncAll);

    List<MemberRole> insertOrUpdateRolesOfClientByMemberId(
            Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType);


    void insertAndSendEvent(Long fromUserId, User userDTO, MemberRole memberRole, String loginName);

    Set<Long> insertOrUpdateRolesByMemberIdExecute(Long fromUserId, Boolean isEdit, Long sourceId,
                                                    Long memberId, String sourceType,
                                                    List<MemberRole> memberRoleList,
                                                    List<MemberRole> returnList, String memberType);

    ResponseEntity<Resource> downloadTemplatesByResourceLevel(String suffix, String resourceLevel);

    void import2MemberRole(Long sourceId, String sourceType, MultipartFile file);

    void updateMemberRole(Long fromUserId, List<UserMemberEventPayload> userMemberEventPayloads, ResourceLevel level, Long sourceId);

    void deleteMemberRoleForSaga(Long userId, List<UserMemberEventPayload> userMemberEventPayloads, ResourceLevel level, Long sourceId);

    void updateOrganizationMemberRole(Long tenantId, Long userId, List<Role> roleList);

    void addTenantRoleForUser(Long tenantId, Long userId, Set<Long> roleIds);
}
