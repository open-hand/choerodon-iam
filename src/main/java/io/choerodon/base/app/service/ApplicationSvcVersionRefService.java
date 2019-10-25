package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @author Eugen
 **/
public interface ApplicationSvcVersionRefService {
    /**
     * 查询应用版本下的服务版本列表
     *
     * @param appVersionId 应用版本主键
     * @return 服务版本列表
     */
    List<AppServiceDetailsVO> getSvcVersions(Long appVersionId);

    /**
     * 分页查询应用版本下的服务及其版本列表
     *
     * @param appVersionId 应用版本主键
     * @return 服务版本列表
     */
    PageInfo<AppServiceDetailsVO> pagingAppSvcAndVersions(Long appVersionId, Pageable Pageable);

    /**
     * 删除应用服务版本关系
     *
     * @param appVersionId
     */
    void deleteAppSvcVersionRefByAppVerId(Long appVersionId);

    /**
     * 批量添加应用服务关系
     *
     * @param appVersionId
     * @param serviceVersionIds
     */
    void batchInsert(Long appVersionId, List<Long> serviceVersionIds);

    /**
     * 批量删除应用服务关系
     *
     * @param serviceVersionIds
     */
    void batchDelete(Set<Long> serviceVersionIds);

    /**
     * 创建应用版本下服务版本关系
     *
     * @param appVersionId      应用版本主键
     * @param serviceVersionIds 服务版本主键集合
     * @return 创建结果
     */
    List<AppServiceDetailsVO> quickCreate(Long appVersionId, Set<Long> serviceVersionIds);

    List<ApplicationSvcVersionRefDTO> listAppServiceVersionRefByAppVersionId(Long id);

    /**
     * 根据应用版本id和下载状态查询所有服务版本id
     * @param versionId
     * @param status
     * @return
     */
    List<ApplicationSvcVersionRefDTO> listSvcVersionsByVersionIdAndStatus(Long versionId, String status);
}
