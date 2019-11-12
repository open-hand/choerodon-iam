package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.LookupDTO;
import io.choerodon.mybatis.common.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author superlee
 */
public interface LookupMapper extends Mapper<LookupDTO> {

    List<LookupDTO> fulltextSearch(@Param("code") String code,
                                   @Param("description") String description,
                                   @Param("param") String param);

    LookupDTO queryById(@Param("id") Long id);

    LookupDTO queryByCode(@Param("code") String code);

    Long check(@Param("lookupId") Long lookupId, @Param("code") String code);
}
