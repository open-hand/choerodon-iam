package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/12
 */
public interface ApplicationVersionService {

    /**
     * 校验应用版本名称的唯一性
     *
     * @param version 应用版本名称
     */
    Boolean checkName(Long applicationId, String version);

    /**
     * 新建应用版本
     *
     * @param applicationId
     * @param applicationVersionVO
     * @return
     */
    ApplicationVersionDTO createAppVersion(Long applicationId, ApplicationVersionVO applicationVersionVO);

    /**
     * 分页查询应用的版本
     *
     * @param Pageable
     * @param applicationId
     * @param version
     * @param description
     * @param status
     * @param params
     * @return
     */
    PageInfo<ApplicationVersionVO> pagingByOptions(Pageable Pageable, Long applicationId, String version, String description, String status, String[] params);

    /**
     * 更新版本信息或发布修复版本
     *
     * @param applicationVersionVO
     * @return
     */
    ApplicationVersionDTO update(ApplicationVersionVO applicationVersionVO);

    /**
     * 未发布版本删除
     *
     * @param id
     */
    void delete(Long id);


    /**
     * 查询应用下的版本及版本状态
     *
     * @param applicationId 应用主键
     * @return 版本信息
     */
    List<ApplicationVersionWithStatusVO> getBriefInfo(Long applicationId);

    /**
     * 快捷创建应用版本
     *
     * @param applicationId 应用主键
     * @param quickCreateVO 创建信息
     * @return
     */
    ApplicationVersionDTO quickCreate(Long applicationId,
                                      ApplicationVersionQuickCreateVO quickCreateVO);

    /**
     * 查询应用版本信息以及应用版本下的服务信息，服务列表信息
     *
     * @param versionId
     * @return
     */
    ApplicationVersionVO getAppVersionWithServicesAndServiceVersions(Long projectId, Long versionId);

    /**
     * 获取应用版本基本信息
     * @param projectId
     * @param versionId
     * @return
     */
    AppVersionInfoVO getAppVersionInfo(Long projectId, Long versionId);

    /**
     * 根据应用版本id,服务id,查询服务下所有版本（不包含已发布版本，支持模糊搜索）
     * @param projectId
     * @param versionId
     * @param serviceId
     * @param version
     * @return
     */
    List<AppServiceVersionVO> getAppSvcWithAllVersion(Long projectId, Long versionId, String serviceId, String version);
}
