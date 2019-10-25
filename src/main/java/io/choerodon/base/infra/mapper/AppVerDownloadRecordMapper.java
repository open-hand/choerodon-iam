package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.AppVerDownloadRecordDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/22
 */
public interface AppVerDownloadRecordMapper extends Mapper<AppVerDownloadRecordDTO> {

    List<AppVerDownloadRecordDTO> fulltextSearch(@Param("appName") String appName,
                                                 @Param("categoryName") String categoryName,
                                                 @Param("organizationId") Long organizationId,
                                                 @Param("downloader") String downloader,
                                                 @Param("versionName") String versionName,
                                                 @Param("status") String status,
                                                 @Param("params") String[] params);

    AppVerDownloadRecordDTO getLastDownloadStatus(@Param("appVerDownloadRecordDTO") AppVerDownloadRecordDTO appVerDownloadRecordDTO);


    List<String> getMyDownloadAppCode();

    List<AppVerDownloadRecordDTO> queryDownloadRecordByAppCodeAndOrgId(@Param("appCode") String appCode, @Param("organizationId") Long organizationId);

    Integer getVersionDownloadCountByCodeAndOrgId(@Param("appCode") String marketAppCode, @Param("organizationId") Long organizationId);

    AppVerDownloadRecordDTO getVersionDownloadCompletedCountByCodeAndOrgId(@Param("verDownloadRecordDTO") AppVerDownloadRecordDTO verDownloadRecordDTO, @Param("organizationId") Long organizationId);

    List<AppVerDownloadRecordDTO> getAppDownloadCompletedByCodeAndOrgId(@Param("recordDTO") AppVerDownloadRecordDTO recordDTO, @Param("organizationId") Long organizationId);

    AppVerDownloadRecordDTO getLastDownloadStatusWithOrgId(@Param("appVerDownloadRecordDTO") AppVerDownloadRecordDTO appVerDownloadRecordDTO, @Param("organizationId") Long organizationId);

    AppVerDownloadRecordDTO getVersionDownloadCompletedCountByCodeAndOrgIdAndVersion(@Param("verDownloadRecordDTO") AppVerDownloadRecordDTO verDownloadRecordDTO, @Param("organizationId") Long organizationId);

    List<String> getCompletedAppCodeByOrgId(@Param("organizationId") Long organizationId);
}
