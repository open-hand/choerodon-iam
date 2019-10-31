package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.LookupDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author superlee
 */
public interface LookupMapper extends Mapper<LookupDTO> {

    List<LookupDTO> fulltextSearch(@Param("lookupDTO") LookupDTO lookupDTO,
                                   @Param("param") String param);

    LookupDTO queryByCode(@Param("code") String code);

    LookupDTO queryByDes(@Param("des") String des);

    Long check(@Param("lookupId") Long lookupId, @Param("code") String code);
}
