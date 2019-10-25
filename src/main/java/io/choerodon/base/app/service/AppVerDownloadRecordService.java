package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;

import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.AppVerDownloadRecordDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
public interface AppVerDownloadRecordService {

    /**
     * 查询应用下的版本
     *
     * @param Pageable    分页参数
     * @param name           应用名称
     * @param category       服务类型
     * @param organizationId 组织id
     * @param downloader     下载人
     * @param versionName    应用版本名称
     * @param status         下载状态
     * @param params         全局过滤参数
     * @return 应用版本DTO
     */
    PageInfo<AppVerDownloadRecordDTO> pagingAppDownloadRecord(Pageable Pageable, String name, String category, Long organizationId,
                                                              String downloader, String versionName, String status, String[] params);

}
