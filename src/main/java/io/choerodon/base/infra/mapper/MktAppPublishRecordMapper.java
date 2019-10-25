package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.MktAppPublishRecordDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 市场应用发布记录表Mapper
 *
 * @author PENGYUHUA
 * @data 2019/08/21
 */
public interface MktAppPublishRecordMapper extends Mapper<MktAppPublishRecordDTO> {

    /**
     * 查询市场应用发布记录
     *
     * @param code 市场应用编码
     * @param version 市场应用发布版本
     * @return 市场应用发布记录 MktAppPublishRecordDTO
     */
    MktAppPublishRecordDTO selectOneByCodeAndVersion(@Param("code") String code,
                                                     @Param("version") String version);
}
