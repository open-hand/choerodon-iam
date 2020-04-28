package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Label;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author zmf
 * @since 20-4-22
 */
public interface C7nLabelMapper  {
    Set<String> selectLabelNamesInRoleIds(List<Long> roleIds);
}