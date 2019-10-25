package io.choerodon.base.app.service;

import java.util.List;

import io.choerodon.base.api.vo.AppDownloadDevopsReqVO;
import io.choerodon.base.infra.dto.ApplicationDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/5
 */
public interface AppDownloadService {

    /**
     * 下载应用
     *
     * @param versionId 发布的应用版本Id
     * @return 下载的应用信息
     */
    ApplicationDTO downloadApplication(Long versionId, Long organizationId);

    /**
     * 完成应用下载
     *
     * @param appDownloadRecordId     历史记录Id
     * @param appVersionId            PaaS应用市场版本Id
     * @param organizationId          下载应用的组织Id
     * @param appDownloadDevopsReqVOS 服务及其版本Ids
     */
    void completeDownloadApplication(Long appDownloadRecordId, Long appVersionId, Long organizationId, List<AppDownloadDevopsReqVO> appDownloadDevopsReqVOS);

    /**
     * 应用下载失败
     *
     * @param appDownloadRecordId 历史记录Id
     * @param organizationId      Pass应用版本Id
     * @param appVersionId        Pass应用版本Id
     */
    void failToDownloadApplication(Long appDownloadRecordId, Long appVersionId, Long organizationId);

}
