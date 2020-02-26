package io.choerodon.base.infra.mapper;


import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author wuguokai
 */
public interface OperateLogMapper extends Mapper<OperateLogDTO> {


    List<OperateLogDTO> listOperateLogSite();

    List<OperateLogDTO> listOperateLogOrg(@Param("sourceId") Long sourceId);
}
