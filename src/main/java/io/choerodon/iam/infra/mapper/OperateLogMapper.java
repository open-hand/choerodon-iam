package io.choerodon.iam.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.mybatis.common.BaseMapper;


/**
 * @author wuguokai
 */
public interface OperateLogMapper extends BaseMapper<OperateLogDTO> {


    List<OperateLogVO> listOperateLogSite();

    List<OperateLogVO> listOperateLogOrg(@Param("sourceId") Long sourceId);
}
