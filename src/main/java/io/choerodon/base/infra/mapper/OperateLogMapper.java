package io.choerodon.base.infra.mapper;


import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author wuguokai
 */
public interface OperateLogMapper extends Mapper<OperateLogDTO> {

    List<OperateLogDTO> listNewOperateLogOrg(@Param("sourceId") Long sourceId);

    List<OperateLogDTO> listNewOperateLogSite();

    List<OperateLogDTO> listMoreOperateLogSite();

    List<OperateLogDTO> listMoreOperateLogOrg(@Param("sourceId") Long sourceId);
}
