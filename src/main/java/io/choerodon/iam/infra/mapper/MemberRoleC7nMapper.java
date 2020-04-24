package io.choerodon.iam.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.infra.utils.SagaTopic;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/4/23
 * @description
 */
public interface MemberRoleC7nMapper extends BaseMapper<SagaTopic.MemberRole> {

    int selectCountBySourceId(@Param("id") Long id, @Param("type") String type);

}
