package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.SvcVerDownloadRecordDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/6
 */
public interface SvcVerDownloadRecordMapper extends Mapper<SvcVerDownloadRecordDTO> {
    List<SvcVerDownloadRecordDTO> listSvcVerDownloadRecordByVersionIds(@Param("versionIds") Set<Long> versionIds, @Param("organizationId") Long organizationId);

    SvcVerDownloadRecordDTO getSvcDownloadByVersionId(@Param("svcVerDownloadRecordDTO") SvcVerDownloadRecordDTO svcVerDownloadRecordDTO, @Param("organizationId") Long organizationId);
}
