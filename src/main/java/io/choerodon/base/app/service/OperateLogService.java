package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.OperateLogVO;
import org.springframework.data.domain.Pageable;


/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public interface OperateLogService {
    PageInfo<OperateLogVO> listOperateLog(Pageable pageable, Long sourceId);
}
