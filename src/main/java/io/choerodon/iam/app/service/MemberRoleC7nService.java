package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.MemberRole;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/4/17
 */
public interface MemberRoleC7nService {
    List<MemberRole> createOrUpdateRolesByMemberIdOnOrganizationLevel(
            Boolean isEdit, Long organizationId, List<Long> memberIds, List<MemberRole> memberRoleDTOList, String memberType);

    List<MemberRole> insertOrUpdateRolesOfUserByMemberId(
            Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType);

    List<MemberRole> insertOrUpdateRolesOfUserByMemberId(
            Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType, Boolean syncAll);

}
