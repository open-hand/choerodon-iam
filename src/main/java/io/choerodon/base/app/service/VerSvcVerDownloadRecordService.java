package io.choerodon.base.app.service;

import io.choerodon.base.infra.dto.SvcVerDownloadRecordDTO;

import java.util.List;
import java.util.Set;

/**
 * @author wanghao
 * @Date 2019/9/9 10:49
 */
public interface VerSvcVerDownloadRecordService {
    List<SvcVerDownloadRecordDTO> listSvcVerDownloadRecordByVersionIds( Set<Long> versionIds,Long organizationId);
}
