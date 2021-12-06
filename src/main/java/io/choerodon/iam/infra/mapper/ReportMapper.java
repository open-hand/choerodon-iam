package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.infra.dto.ReportDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
public interface ReportMapper extends BaseMapper<ReportDTO> {

    List<ReportDTO> selectByProjectId(@Param("projectId") Long projectId,
                                      @Param("module") String module,
                                      @Param("lang") String language);
}
