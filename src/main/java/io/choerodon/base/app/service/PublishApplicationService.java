package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.CustomerApplicationVersionVO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.api.vo.RemoteApplicationVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/8/7
 */
public interface PublishApplicationService {


    PageInfo<RemoteApplicationVO> pagingQueryPaasAppMarket(Pageable Pageable, String s, Long categoryId, String orderBy, String order, Boolean isMyDownload, Long organizationId);

    MarketApplicationVO getPaasAppMarketById(Long id, Long versionId, Long organizationId);


    List<AppCategoryDTO> getAppCategories();

    Boolean validateRemoteToken();

    /**
     * 根据应用Id查询应用版本列表（包含是否购买，以及下载记录状态）
     *
     * @param appId
     * @param version
     * @param organizationId
     * @return
     */
    List<CustomerApplicationVersionVO> listDownloadInfo(String appId, String version, Long organizationId);

    PageInfo<MarketApplicationVersionVO> getAppPublishVersionDetailsById(Pageable Pageable, Long id, Long organizationId);

    List<MarketApplicationVersionVO> getAppVersionsById(Long id);

    Integer getNewVersionNum(Long organizationId);
}
