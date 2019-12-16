package io.choerodon.base.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.*;

import io.choerodon.base.infra.dto.LovDTO;
import io.choerodon.mybatis.common.Mapper;

public interface LovMapper extends Mapper<LovDTO> {
    List<LovDTO> selectLovList(@Param("code") String code,
                               @Param("description") String description,
                               @Param("level") String level,
                               @Param("param") String param);
}
