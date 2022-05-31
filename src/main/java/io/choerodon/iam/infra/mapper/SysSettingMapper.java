package io.choerodon.iam.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.mybatis.common.BaseMapper;

public interface SysSettingMapper extends BaseMapper<SysSettingDTO> {
    SysSettingDTO queryByKey(@Param("key") String key);
    List<SysSettingDTO> listByLikeCode(@Param("code") String code);
}
