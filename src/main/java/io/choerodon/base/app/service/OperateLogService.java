package io.choerodon.base.app.service;

import io.choerodon.base.api.vo.OperateLogVO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public interface OperateLogService {
    List<OperateLogVO> listNewOperateLog(Long sourceId);

    List<OperateLogVO>  listMoreOperateLog(Long sourceId);
}
