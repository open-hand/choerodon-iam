package io.choerodon.iam.infra.mapper;


import io.choerodon.iam.api.vo.OperateLogVO;
import io.choerodon.iam.infra.dto.OperateLogDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author wuguokai
 */
public interface OperateLogMapper extends BaseMapper<OperateLogDTO> {
    List<OperateLogVO> listOperateLogSite();

    List<OperateLogVO> listOperateLogOrg(@Param("sourceId") Long sourceId);
}
