package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import io.choerodon.base.infra.dto.devops.AppServiceAndVersionDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * @author Eugen
 */
public interface ApplicationSvcVersionRefMapper extends Mapper<ApplicationSvcVersionRefDTO> {
    int batchInsert(@Param("versionId") Long id, @Param("serviceVersionIds") List<Long> serviceVersionIds);

    /**
     * 查询应用版本下 服务版本信息
     *
     * @param appVersionId
     * @return （versionId,versionStatus）
     */
    List<AppServiceAndVersionDTO> selectByAppVersionId(@Param("application_version_id") Long appVersionId);

    int batchDelete(@Param("serviceVersionIds") Set<Long> serviceVersionIds);

    List<ApplicationSvcVersionRefDTO> selectByVersionAndStatus(@Param("appVersionId") Long appVersionId, @Param("status") String status);

    Set<Long> selectSvcVersionByOrgId(@Param("organizationId") Long organizationId, @Param("appType") String appType);
}
