package io.choerodon.iam.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.MemberRole;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/4/23
 * @description
 */
public interface MemberRoleC7nMapper {

    int selectCountBySourceId(@Param("id") Long id, @Param("type") String type);

}
