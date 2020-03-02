package io.choerodon.base.infra.mapper;


import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author wuguokai
 */
public interface OperateLogMapper extends Mapper<OperateLogDTO> {


    List<OperateLogVO> listOperateLogSite();

    List<OperateLogVO> listOperateLogOrg(@Param("sourceId") Long sourceId);
}
