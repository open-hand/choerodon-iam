package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

/**
 * @author carllhw
 */
public interface MemberRoleC7nMapper {


    List<Long> selectDeleteList(@Param("memberId") long memberId, @Param("sourceId") long sourceId, @Param("memberType") String memberType, @Param("sourceType") String sourceType, @Param("list") List<Long> deleteList);

    int selectCountBySourceId(@Param("id") Long id, @Param("type") String type);

    List<MemberRole> listMemberRoleByOrgIdAndUserIds(@Param("organizationId") Long organizationId,
                                                     @Param("userIds") Set<Long> userIds,
                                                     @Param("roleName") String realName,
                                                     @Param("label") String label);

    List<MemberRole> listMemberRoleByOrgIdAndUserIdAndRoleLable(@Param("organizationId") Long organizationId,
                                                                @Param("userId") Long userId,
                                                                @Param("labelName") String labelName);

    List<Role> listRoleByUserIdAndLevel(@Param("userId") Long userId,
                                        @Param("level") String level);


    List<Role> listRoleByUserIdAndTenantId(@Param("userId") Long userId,
                                           @Param("tenantId") Long tenantId);

    Set<Long> listUserPermission(@Param("userId") Long userId,
                                 @Param("psIds") Set<Long> psIds,
                                 @Param("organizationId") Long organizationId);

    Integer checkRole(@Param("userId") Long userId);

}
