package io.choerodon.iam.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.MemberRole;

import java.util.List;
import java.util.Set;

/**
 * @author carllhw
 */
public interface MemberRoleC7nMapper  {


    List<Long> selectDeleteList(@Param("memberId") long memberId, @Param("sourceId") long sourceId, @Param("memberType") String memberType, @Param("sourceType") String sourceType, @Param("list") List<Long> deleteList);

    int selectCountBySourceId(@Param("id") Long id, @Param("type") String type);

    List<MemberRole> listMemberRoleByOrgIdAndUserIds(Long organizationId, Set<Long> userIds, String realName, String value);
}
