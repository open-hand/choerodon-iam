package io.choerodon.base.app.service;

import io.choerodon.base.infra.dto.OperateLogDTO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public interface OperateLogService {
    List<OperateLogDTO> listNewOperateLod(Long organizationId);
}
