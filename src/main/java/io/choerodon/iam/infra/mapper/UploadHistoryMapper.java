package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author superlee
 */
public interface UploadHistoryMapper extends BaseMapper<UploadHistoryDTO> {
    UploadHistoryDTO latestHistory(@Param("userId") Long userId,
                                   @Param("type") String type,
                                   @Param("sourceId") Long sourceId,
                                   @Param("sourceType") String sourceType);
}
