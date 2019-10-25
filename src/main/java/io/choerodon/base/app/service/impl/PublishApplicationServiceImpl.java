package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.AppVersionVO;
import io.choerodon.base.api.vo.CustomerApplicationVersionVO;
import io.choerodon.base.api.vo.MarketAppServiceVO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.api.vo.PublishedApplicationVO;
import io.choerodon.base.api.vo.RemoteApplicationVO;
import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.api.vo.ServiceVersionVO;
import io.choerodon.base.app.service.MarketCallService;
import io.choerodon.base.app.service.PublishApplicationService;
import io.choerodon.base.app.service.VerSvcVerDownloadRecordService;
import io.choerodon.base.infra.dto.AppVerDownloadRecordDTO;
import io.choerodon.base.infra.dto.SvcVerDownloadRecordDTO;
import io.choerodon.base.infra.enums.AppDownloadStatus;
import io.choerodon.base.infra.enums.AppVersionDisplayStatus;
import io.choerodon.base.infra.enums.DownloadOrder;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.AppVerDownloadRecordMapper;
import io.choerodon.base.infra.mapper.RemoteTokenAuthorizationMapper;
import io.choerodon.base.infra.mapper.SvcVerDownloadRecordMapper;
import io.choerodon.base.infra.retrofit.AppDownloadRetrofitClient;
import io.choerodon.base.infra.retrofit.AppMarketRetrofitClient;
import io.choerodon.base.infra.utils.RetrofitCallExceptionParse;
import io.choerodon.core.exception.CommonException;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jiameng.cao
 * @since 2019/8/7
 */
@Service
public class PublishApplicationServiceImpl implements PublishApplicationService {

    private static final String APPLICATION_NOT_FOUND = "error.application.not.found";
    private static final String ERROR_REMOTE_TOKEN_VALIDATE = "error.remoteToken.validate";
    public static final Logger logger = LoggerFactory.getLogger(PublishApplicationServiceImpl.class);

    @Value("${choerodon.market.saas.platform}")
    private boolean isSassPlatform;
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private AppVerDownloadRecordMapper appVerDownloadRecordMapper;
    private RetrofitClientFactory retrofitClientFactory;
    private VerSvcVerDownloadRecordService verSvcVerDownloadRecordService;
    private MarketFeignClient marketFeignClient;
    private MarketCallService marketCallService;
    private SvcVerDownloadRecordMapper svcVerDownloadRecordMapper;

    public PublishApplicationServiceImpl(RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, AppVerDownloadRecordMapper appVerDownloadRecordMapper, RetrofitClientFactory retrofitClientFactory, VerSvcVerDownloadRecordService verSvcVerDownloadRecordService, MarketFeignClient marketFeignClient, MarketCallService marketCallService, SvcVerDownloadRecordMapper svcVerDownloadRecordMapper) {
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.appVerDownloadRecordMapper = appVerDownloadRecordMapper;
        this.retrofitClientFactory = retrofitClientFactory;
        this.verSvcVerDownloadRecordService = verSvcVerDownloadRecordService;
        this.marketFeignClient = marketFeignClient;
        this.marketCallService = marketCallService;
        this.svcVerDownloadRecordMapper = svcVerDownloadRecordMapper;
    }

    @Override
    public PageInfo<RemoteApplicationVO> pagingQueryPaasAppMarket(Pageable pageable, String param, Long categoryId, String orderBy, String
            order, Boolean isMyDownload, Long organizationId) {

        PageInfo<RemoteApplicationVO> pageInfo = new PageInfo<>();
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                String accessToken = remoteTokenAuthorizationVO.getRemoteToken();
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                Response<PageInfo<RemoteApplicationVO>> repos = null;
                try {
                    if (!isMyDownload) {
                        List<String> downloadAppCode = appVerDownloadRecordMapper.getMyDownloadAppCode();
                        repos = client.getSaasAppMarket(accessToken, param, categoryId, orderBy, order, pageable.getPageNumber(), pageable.getPageSize(), downloadAppCode).execute();
                        pageInfo = repos.body();
                        getHasNewVersion(pageInfo, false);
                    } else {
                        List<String> downloadAppCode = appVerDownloadRecordMapper.getMyDownloadAppCode();
                        if (!CollectionUtils.isEmpty(downloadAppCode)) {
                            repos = client.getMyDownloadApp(remoteTokenAuthorizationVO.getRemoteToken(), param, orderBy, order, pageable.getPageNumber(), pageable.getPageSize(), downloadAppCode).execute();
                            pageInfo = repos.body();
                            getHasNewVersion(pageInfo, true);
                        }
                    }
                } catch (IOException e) {
                    logger.error(String.valueOf(e));
                }

            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            if (categoryId == null) {
                categoryId = 0L;
            }
            if (!isMyDownload) {
                List<String> appCodeList = appVerDownloadRecordMapper.getCompletedAppCodeByOrgId(organizationId);
                pageInfo = marketFeignClient.getAppPublishesWithin(pageable.getPageNumber(), pageable.getPageSize(), categoryId, param, orderBy, order, appCodeList, organizationId).getBody();
                getHasNewVersionWithin(pageInfo, organizationId, false);
            } else {
                List<String> appCodeList = appVerDownloadRecordMapper.getCompletedAppCodeByOrgId(organizationId);
                pageInfo = marketFeignClient.getMyDownloadAppWithin(pageable.getPageNumber(), pageable.getPageSize(), param, orderBy, order, appCodeList, organizationId).getBody();
                getHasNewVersionWithin(pageInfo, organizationId, true);
            }
        }
        return pageInfo;
    }

    private Integer getHasNewVersionWithin(PageInfo<RemoteApplicationVO> pageInfo, Long organizationId, Boolean isSort) {
        List<RemoteApplicationVO> list = pageInfo.getList();
        Integer newVersionNum = 0;
        if (!CollectionUtils.isEmpty(list)) {
            for (RemoteApplicationVO remoteApplicationVO : list) {
                remoteApplicationVO.setHasNewVersion(false);
                AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
                appVerDownloadRecordDTO.setMktAppCode(remoteApplicationVO.getCode());
                appVerDownloadRecordDTO.setMktVersionName(remoteApplicationVO.getLatestVersion());
                appVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                appVerDownloadRecordDTO.setOrganizationId(organizationId);
                appVerDownloadRecordDTO = appVerDownloadRecordMapper.getVersionDownloadCompletedCountByCodeAndOrgIdAndVersion(appVerDownloadRecordDTO, organizationId);
                AppVerDownloadRecordDTO recordDTO = new AppVerDownloadRecordDTO();
                recordDTO.setMktAppCode(remoteApplicationVO.getCode());
                recordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                if (!CollectionUtils.isEmpty(appVerDownloadRecordMapper.getAppDownloadCompletedByCodeAndOrgId(recordDTO, organizationId))) {
                    if (appVerDownloadRecordDTO == null) {
                        remoteApplicationVO.setHasNewVersion(true);
                        newVersionNum++;
                    } else if (!CollectionUtils.isEmpty(remoteApplicationVO.getVersionDTOS())) {
                        List<MarketApplicationVersionVO> versionVOS = remoteApplicationVO.getVersionDTOS();
                        for (MarketApplicationVersionVO versionVO : versionVOS) {
                            List<Long> serviceVersionIds = versionVO.getServiceVersionId();
                            for (Long serviceVersionId : serviceVersionIds) {
                                SvcVerDownloadRecordDTO svcVerDownloadRecordDTO = new SvcVerDownloadRecordDTO();
                                svcVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                                svcVerDownloadRecordDTO.setMktSvcVersionId(serviceVersionId);
                                svcVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                                if (svcVerDownloadRecordMapper.getSvcDownloadByVersionId(svcVerDownloadRecordDTO, organizationId) == null) {
                                    remoteApplicationVO.setHasNewVersion(true);
                                    newVersionNum++;
                                    break;
                                }
                            }
                            if (remoteApplicationVO.getHasNewVersion() != null && remoteApplicationVO.getHasNewVersion()) {
                                break;
                            }
                        }
                    }
                }
            }
            if (isSort) {
                sortApp(list);
            }
            pageInfo.setList(list);
        }
        return newVersionNum;
    }

    private void sortApp(List<RemoteApplicationVO> list) {
        Collections.sort(list, (RemoteApplicationVO o1, RemoteApplicationVO o2) -> {

            if (o1.getHasNewVersion() && !o2.getHasNewVersion()) {
                return -1;
            } else if (!o1.getHasNewVersion() && o2.getHasNewVersion()) {
                return 1;
            }
            return 0;
        });
    }

    private Integer getHasNewVersion(PageInfo<RemoteApplicationVO> pageInfo, Boolean isSort) {
        List<RemoteApplicationVO> list = pageInfo.getList();
        Integer newVersionNum = 0;
        if (!CollectionUtils.isEmpty(list)) {
            for (RemoteApplicationVO remoteApplicationVO : list) {
                remoteApplicationVO.setHasNewVersion(false);
                AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
                appVerDownloadRecordDTO.setMktAppCode(remoteApplicationVO.getCode());
                appVerDownloadRecordDTO.setMktVersionName(remoteApplicationVO.getLatestVersion());
                appVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                appVerDownloadRecordDTO = appVerDownloadRecordMapper.selectOne(appVerDownloadRecordDTO);
                AppVerDownloadRecordDTO recordDTO = new AppVerDownloadRecordDTO();
                recordDTO.setMktAppCode(remoteApplicationVO.getCode());
                recordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                if (!CollectionUtils.isEmpty(appVerDownloadRecordMapper.select(recordDTO))) {
                    if (appVerDownloadRecordDTO == null) {
                        remoteApplicationVO.setHasNewVersion(true);
                        newVersionNum++;
                    } else if (!CollectionUtils.isEmpty(remoteApplicationVO.getVersionDTOS())) {
                        List<MarketApplicationVersionVO> versionVOS = remoteApplicationVO.getVersionDTOS();
                        for (MarketApplicationVersionVO versionVO : versionVOS) {
                            List<Long> serviceVersionIds = versionVO.getServiceVersionId();
                            for (Long serviceVersionId : serviceVersionIds) {
                                SvcVerDownloadRecordDTO svcVerDownloadRecordDTO = new SvcVerDownloadRecordDTO();
                                svcVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                                svcVerDownloadRecordDTO.setMktSvcVersionId(serviceVersionId);
                                svcVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                                if (svcVerDownloadRecordMapper.selectOne(svcVerDownloadRecordDTO) == null) {
                                    remoteApplicationVO.setHasNewVersion(true);
                                    newVersionNum++;
                                    break;
                                }
                            }
                            if (remoteApplicationVO.getHasNewVersion() != null && remoteApplicationVO.getHasNewVersion()) {
                                break;
                            }
                        }
                    }
                }
            }
            if (isSort) {
                sortApp(list);
            }
            pageInfo.setList(list);
        }
        return newVersionNum;
    }

    private Boolean getPurchased(PublishedApplicationVO publishedApplicationVO, MarketApplicationVersionVO versionVO) {
        List<CustomerApplicationVersionVO> customerApplicationVersionVOS;
        if (publishedApplicationVO != null) {
            customerApplicationVersionVOS = publishedApplicationVO.getCustomerApplicationVersionVOS();
            if (!CollectionUtils.isEmpty(customerApplicationVersionVOS)) {
                for (CustomerApplicationVersionVO customerApplicationVersionVO : customerApplicationVersionVOS) {
                    if (customerApplicationVersionVO.getId().equals(versionVO.getId())) {
                        versionVO.setPurchased(customerApplicationVersionVO.getPurchased());
                        break;
                    }
                }
            }
        }
        return versionVO.getPurchased();
    }

    @Override
    public MarketApplicationVO getPaasAppMarketById(Long id, Long versionId, Long organizationId) {

        MarketApplicationVO marketApplicationVO = null;
        if (!isSassPlatform) {
            List<MarketApplicationVersionVO> versionVOS;
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                String accessToken = remoteTokenAuthorizationVO.getRemoteToken();
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                Call<MarketApplicationVO> repos = client.getAppPublishDetailsById(id, accessToken, versionId);
                AppDownloadRetrofitClient client2 = (AppDownloadRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppDownloadRetrofitClient.class);
                try {
                    marketApplicationVO = repos.execute().body();
                    Response<List<MarketApplicationVersionVO>> reposVersions = client.getAppPublishVersionsById(id, remoteTokenAuthorizationVO.getRemoteToken()).execute();
                    versionVOS = reposVersions.body();

                    if (!CollectionUtils.isEmpty(versionVOS)) {
                        for (MarketApplicationVersionVO versionVO : versionVOS) {
                            AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
                            appVerDownloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                            appVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                            appVerDownloadRecordDTO = appVerDownloadRecordMapper.getLastDownloadStatus(appVerDownloadRecordDTO);
                            String status = null;
                            if (appVerDownloadRecordDTO != null) {
                                status = appVerDownloadRecordDTO.getStatus();
                            }
                            Call<ResponseBody> reposDownload = client2.listDownloadInfo(marketApplicationVO.getCode(), remoteTokenAuthorizationVO.getRemoteToken());
                            PublishedApplicationVO publishedApplicationVO = RetrofitCallExceptionParse.executeCall(reposDownload, APPLICATION_NOT_FOUND, PublishedApplicationVO.class);
                            Boolean purchased = getPurchased(publishedApplicationVO, versionVO);
                            getEnableDownload(status, purchased, marketApplicationVO, versionVO.getVersion(), null);
                            if (marketApplicationVO.getEnableDownload() != null && marketApplicationVO.getEnableDownload()) {
                                return marketApplicationVO;
                            }

                        }
                    }
                } catch (IOException e) {
                    logger.error("IOException", e);
                }

            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            marketApplicationVO = marketFeignClient.getAppPublishDetailsByIdWithin(versionId, id).getBody();
            List<MarketApplicationVersionVO> versionVOS = marketFeignClient.getAppPublishVersionsByIdWithin(id).getBody();
            if (!CollectionUtils.isEmpty(versionVOS)) {
                for (MarketApplicationVersionVO versionVO : versionVOS) {
                    AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
                    appVerDownloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                    appVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                    appVerDownloadRecordDTO = appVerDownloadRecordMapper.getLastDownloadStatusWithOrgId(appVerDownloadRecordDTO, organizationId);
                    String status = null;
                    if (appVerDownloadRecordDTO != null) {
                        status = appVerDownloadRecordDTO.getStatus();
                    }
                    PublishedApplicationVO publishedApplicationVO = marketFeignClient.getAppInfoWithVersionsAndPurchaseInfo(marketApplicationVO.getCode(), organizationId).getBody();
                    Boolean purchased = getPurchased(publishedApplicationVO, versionVO);
                    getEnableDownload(status, purchased, marketApplicationVO, versionVO.getVersion(), organizationId);
                    if (marketApplicationVO.getEnableDownload() != null && marketApplicationVO.getEnableDownload()) {
                        return marketApplicationVO;
                    }
                }
            }
        }
        return marketApplicationVO;
    }

    private void getEnableDownload(String status, Boolean purchased, MarketApplicationVO marketApplicationVO, String version, Long organizationId) {
        if (purchased != null && !purchased) {
            marketApplicationVO.setEnableDownload(false);
        } else if ((purchased == null && status == null) || (purchased != null && status == null)) {
            marketApplicationVO.setEnableDownload(true);
        } else if (AppDownloadStatus.DOWNLOADING.getValue().equals(status)) {
            marketApplicationVO.setEnableDownload(false);
        } else if (AppDownloadStatus.COMPLETED.getValue().equals(status)) {
            if (this.listDownloadInfo(marketApplicationVO.getCode(), version, organizationId).get(0).getDisplayStatus().equals("upgrade")) {
                marketApplicationVO.setEnableDownload(true);
            } else {
                marketApplicationVO.setEnableDownload(false);
            }
        } else if (AppDownloadStatus.FAILED.getValue().equals(status)) {
            marketApplicationVO.setEnableDownload(true);
        }
    }

    @Override
    public List<AppCategoryDTO> getAppCategories() {
        List<AppCategoryDTO> categoryDTOS = new ArrayList<>();
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                Call<List<AppCategoryDTO>> repos = client.getCategoryList(remoteTokenAuthorizationVO.getRemoteToken());
                try {
                    categoryDTOS = repos.execute().body();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            categoryDTOS = marketFeignClient.getCategoryListWithin().getBody();
        }
        return categoryDTOS;
    }

    @Override
    public Boolean validateRemoteToken() {
        Boolean isValidate = null;
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                Call<Boolean> repos = client.validateToken(remoteTokenAuthorizationVO.getRemoteToken());
                try {
                    isValidate = repos.execute().body();
                    if (isValidate == null) {
                        isValidate = false;
                    }
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            return true;
        }
        return isValidate;
    }


    @Override
    public List<CustomerApplicationVersionVO> listDownloadInfo(String appCode, String version, Long organizationId) {
        // 调用Sass接口，根据appCode查询应用的版本列表，收费应用则包含下载方对各个版本的购买信息
        PublishedApplicationVO publishedApplicationVO = Optional
                .ofNullable(marketCallService.getPublishedApplication(appCode, organizationId))
                .orElseThrow(() -> new CommonException(APPLICATION_NOT_FOUND));
        List<CustomerApplicationVersionVO> customerApplicationVersionVOS = publishedApplicationVO.getCustomerApplicationVersionVOS();
        if (CollectionUtils.isEmpty(customerApplicationVersionVOS)) {
            throw new CommonException("error.app.versions.not.exist");
        }
        // 如果输入了版本名过滤，则对版本列表过滤
        if (!StringUtils.isEmpty(version)) {
            customerApplicationVersionVOS = customerApplicationVersionVOS.stream().filter(customerApplicationVersionVO -> customerApplicationVersionVO.getVersion().contains(version)).collect(Collectors.toList());
            // 如果没有与version匹配的版本，则直接返回空的pageInfo
            if (CollectionUtils.isEmpty(customerApplicationVersionVOS)) {
                return customerApplicationVersionVOS;
            }
        }
        // 根据appCode查询应用的下载记录
        if (!isSassPlatform) {
            organizationId = null;
        }
        List<AppVerDownloadRecordDTO> appVerDownloadRecordDTOS = queryDownloadRecordByAppCode(appCode, organizationId);
        appVerDownloadRecordDTOS = calLatestRecord(appVerDownloadRecordDTOS);
        if (Boolean.TRUE.equals(publishedApplicationVO.getFree())) {
            // 1. 免费应用直接根据下载记录排序
            // 展示顺序 ：存在修复版本 6 下载失败 5 -> 下载中 2 -> 下载完成 1
            customerApplicationVersionVOS.forEach(
                    customerApplicationVersionVO -> {
                        customerApplicationVersionVO.setOrder(DownloadOrder.NOT_DOWNLOADED.getValue());
                        customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.NOT_DOWNLOADED.getValue());
                    });
            // 存在下载记录，对结果排序
            if (!CollectionUtils.isEmpty(appVerDownloadRecordDTOS)) {
                setDownloadStatusAndOrder(customerApplicationVersionVOS, appVerDownloadRecordDTOS);
            }
        } else {
            // 2. 收费应用以下规则排序
            // 展示顺序 ：可更新 7 -> 更新失败 -> 下载失败 5 -> 已购买，未下载 4 -> 未购买 3 -> 下载中 2 -> 下载完成 1
            // 1.未购买的版本,设置排序数字 3
            List<CustomerApplicationVersionVO> notPurchasedVersions = customerApplicationVersionVOS
                    .stream()
                    .filter(customerApplicationVersionVO -> Boolean.FALSE.equals(customerApplicationVersionVO.getPurchased()))
                    .collect(Collectors.toList());
            notPurchasedVersions.forEach(
                    customerApplicationVersionVO -> {
                        customerApplicationVersionVO.setOrder(DownloadOrder.NOT_PURCHASE.getValue());
                        customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.NOT_PURCHASED.getValue());
                    });
            // 2.已购买版本，设置排序数字 4
            List<CustomerApplicationVersionVO> purchasedVersions = customerApplicationVersionVOS
                    .stream()
                    .filter(customerApplicationVersionVO -> Boolean.TRUE.equals(customerApplicationVersionVO.getPurchased()))
                    .collect(Collectors.toList());
            purchasedVersions.forEach(
                    customerApplicationVersionVO -> {
                        customerApplicationVersionVO.setOrder(DownloadOrder.NOT_DOWNLOADED.getValue());
                        customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.NOT_DOWNLOADED.getValue());
                    });
            // 已购买版本，存在下载记录，更新排序数字以及添加下载状态
            if (!CollectionUtils.isEmpty(appVerDownloadRecordDTOS)) {
                setDownloadStatusAndOrder(purchasedVersions, appVerDownloadRecordDTOS);
            }
        }
        // 检查已下载版本是否有修复版本
        List<CustomerApplicationVersionVO> downloadedVersions = customerApplicationVersionVOS.stream()
                .filter(customerApplicationVersionVO -> AppDownloadStatus.COMPLETED.getValue().equals(customerApplicationVersionVO.getDownloadStatus()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(downloadedVersions)) {
            checkHasNewVersion(downloadedVersions, organizationId);
        }
        // 根据排序数字降序排列
        List<CustomerApplicationVersionVO> sortedApplicationVersions = sortVersionByDownloadRecord(customerApplicationVersionVOS);
        return sortedApplicationVersions;
    }

    /**
     * 计算出最新下载记录
     *
     * @param appVerDownloadRecordDTOS
     * @return
     */
    private List<AppVerDownloadRecordDTO> calLatestRecord(List<AppVerDownloadRecordDTO> appVerDownloadRecordDTOS) {
        // 对所有下载记录按应用版本id分组，用于计算出各个版本的最新下载记录
        Map<Long, List<AppVerDownloadRecordDTO>> verDownloadRecordMap = appVerDownloadRecordDTOS.stream()
                // 按下载时间降序排序，第一条即为最新记录
                .sorted(Comparator.comparing(AppVerDownloadRecordDTO::getCreationDate).reversed())
                .collect(Collectors.groupingBy(AppVerDownloadRecordDTO::getMktVersionId));
        // 最新下载记录
        List<AppVerDownloadRecordDTO> latestVerDownloadRecords = new ArrayList<>();
        // 遍历map，取出最新记录
        verDownloadRecordMap.keySet().forEach(id -> latestVerDownloadRecords.add(verDownloadRecordMap.get(id).get(0)));
        return latestVerDownloadRecords;
    }

    /**
     * 检查是否有修复版本，并给修复版本设置新的排序
     *
     * @param customerApplicationVersionVOS
     */
    private void checkHasNewVersion(List<CustomerApplicationVersionVO> customerApplicationVersionVOS, Long organizationId) {
        Set<Long> versionIds = customerApplicationVersionVOS.stream().map(CustomerApplicationVersionVO::getId).collect(Collectors.toSet());
        // 查询所有应用版本下的服务版本
        if (CollectionUtils.isEmpty(versionIds)) {
            throw new CommonException("error.service.version.download.record.is.empty");
        }
        List<AppVersionVO> appVersionVOS = marketCallService.listServiceVersionsByVersionIds(versionIds);
        // 查询所有应用版本下的服务版本下载记录
        if (!isSassPlatform) {
            organizationId = null;
        }
        List<SvcVerDownloadRecordDTO> svcVerDownloadRecordDTOS = verSvcVerDownloadRecordService.listSvcVerDownloadRecordByVersionIds(versionIds, organizationId);
        Map<Long, AppVersionVO> appVersionVOMap = appVersionVOS.stream().collect(Collectors.toMap(AppVersionVO::getVersionId, v -> v));
        Map<Long, List<SvcVerDownloadRecordDTO>> svcVerDownloadRecordDTOMap = svcVerDownloadRecordDTOS.stream().collect(Collectors.groupingBy(SvcVerDownloadRecordDTO::getMktVersionId));
        customerApplicationVersionVOS.forEach(version -> {
            Long versionId = version.getId();
            AppVersionVO appVersionVO = appVersionVOMap.get(versionId);
            List<SvcVerDownloadRecordDTO> svcVerDownloadRecordDTOList = svcVerDownloadRecordDTOMap.get(versionId);
            // 如果服务版本下载记录中存在失败的记录，则表示更新失败
            if (svcVerDownloadRecordDTOList.stream().anyMatch(svcVerDownloadRecordDTO -> AppDownloadStatus.FAILED.getValue().equals(svcVerDownloadRecordDTO.getStatus()))) {
                version.setHasNewVersion(true);
                // 设置排序大小
                version.setOrder(DownloadOrder.UPDATE_FAILED.getValue());
                // 设置展示状态
                version.setDisplayStatus(AppVersionDisplayStatus.UPDATE_FAILED.getValue());
            } else if (svcVerDownloadRecordDTOList.stream().anyMatch(svcVerDownloadRecordDTO -> AppDownloadStatus.DOWNLOADING.getValue().equals(svcVerDownloadRecordDTO.getStatus()))) {
                // 该版本下存在下载中的服务，则将状态改为下载中
                version.setHasNewVersion(false);
                // 设置排序大小
                version.setOrder(DownloadOrder.DOWNLOADING.getValue());
                // 设置展示状态
                version.setDisplayStatus(AppVersionDisplayStatus.DOWNLOADING.getValue());
            } else {
                // 该版本下不存在下载失败和下载中的服务
                // 当应用版本下的服务版本数量大于下载记录中已下载的服务版本数量时，表示有新版本发布
                if (appVersionVO.getServiceVersionIds().size() > svcVerDownloadRecordDTOList.size()) {
                    version.setHasNewVersion(true);
                    version.setOrder(DownloadOrder.UPGRADE.getValue());
                    version.setDisplayStatus(AppVersionDisplayStatus.UPGRADE.getValue());
                }
            }
        });

    }

    @Override
    public PageInfo<MarketApplicationVersionVO> getAppPublishVersionDetailsById(Pageable Pageable, Long
            id, Long organizationId) {
        PageInfo<MarketApplicationVersionVO> pageInfo = null;
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                // 调用Sass接口，根据appCode查询应用的版本列表，收费应用则包含下载方对各个版本的购买信息
                try {
                    Call<PageInfo<MarketApplicationVersionVO>> repos = client.getAppPublishVersionDetailsById(id, remoteTokenAuthorizationVO.getRemoteToken(), Pageable.getPageNumber(), Pageable.getPageSize());

                    pageInfo = repos.execute().body();
                    List<MarketApplicationVersionVO> versionVOS = pageInfo.getList();
                    String appCode = versionVOS.get(0).getMarketAppCode();
                    List<CustomerApplicationVersionVO> customerApplicationVersionVOS = this.listDownloadInfo(appCode, null, null);
                    getVersionStatusAndPurchased(versionVOS, customerApplicationVersionVOS, pageInfo);
                } catch (IOException e) {
                    logger.error("IOException", e);
                }

            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            pageInfo = marketFeignClient.getAppPublishVersionDetailsByIdWithin(Pageable.getPageNumber(), Pageable.getPageSize(), id).getBody();
            List<MarketApplicationVersionVO> versionVOS = pageInfo.getList();
            String appCode = versionVOS.get(0).getMarketAppCode();
            List<CustomerApplicationVersionVO> customerApplicationVersionVOS = this.listDownloadInfo(appCode, null, organizationId);
            getVersionStatusAndPurchasedWithin(versionVOS, customerApplicationVersionVOS, pageInfo, organizationId);
        }
        return pageInfo;
    }

    private void getVersionStatusAndPurchasedWithin(List<MarketApplicationVersionVO> versionVOS, List<CustomerApplicationVersionVO> customerApplicationVersionVOS, PageInfo<MarketApplicationVersionVO> pageInfo, Long organizationId) {
        if (!CollectionUtils.isEmpty(versionVOS)) {
            for (MarketApplicationVersionVO versionVO : versionVOS) {
                if (!CollectionUtils.isEmpty(customerApplicationVersionVOS)) {
                    for (CustomerApplicationVersionVO customerApplicationVersionVO : customerApplicationVersionVOS) {
                        if (customerApplicationVersionVO.getId().equals(versionVO.getId())) {
                            versionVO.setDisplayStatus(customerApplicationVersionVO.getDisplayStatus());
                            break;
                        }
                    }
                }
                versionVO.setNewVersion(false);
                AppVerDownloadRecordDTO downloadRecordDTO = new AppVerDownloadRecordDTO();
                downloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                Integer downloadCount = appVerDownloadRecordMapper.getVersionDownloadCountByCodeAndOrgId(versionVO.getMarketAppCode(), organizationId);
                if (downloadCount > 0) {
                    AppVerDownloadRecordDTO verDownloadRecordDTO = new AppVerDownloadRecordDTO();
                    verDownloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                    verDownloadRecordDTO.setMktVersionId(versionVO.getId());
                    verDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                    verDownloadRecordDTO = appVerDownloadRecordMapper.getVersionDownloadCompletedCountByCodeAndOrgId(verDownloadRecordDTO, organizationId);
                    AppVerDownloadRecordDTO recordDTO = new AppVerDownloadRecordDTO();
                    recordDTO.setMktAppCode(versionVO.getMarketAppCode());
                    recordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                    if (!CollectionUtils.isEmpty(appVerDownloadRecordMapper.getAppDownloadCompletedByCodeAndOrgId(recordDTO, organizationId))) {
                        if (verDownloadRecordDTO == null) {
//                            versionVO.setNewVersion(true);
                        } else if (versionVO.getLatestFixVersion() > 0) {
                            List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                            if (!CollectionUtils.isEmpty(marketAppServiceVOS)) {
                                for (MarketAppServiceVO marketAppServiceVO : marketAppServiceVOS) {
                                    List<ServiceVersionVO> serviceVersionVOS = marketAppServiceVO.getServiceVersionVOS();
                                    if (!CollectionUtils.isEmpty(serviceVersionVOS)) {
                                        for (ServiceVersionVO serviceVersionVO : serviceVersionVOS) {
                                            serviceVersionVO.setNewFixVersion(false);
                                            if (serviceVersionVO.getFixVersion() > 0) {
                                                SvcVerDownloadRecordDTO svcVerDownloadRecordDTO = new SvcVerDownloadRecordDTO();
                                                svcVerDownloadRecordDTO.setMktSvcVersionId(serviceVersionVO.getId());
                                                svcVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                                                svcVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                                                svcVerDownloadRecordDTO = svcVerDownloadRecordMapper.getSvcDownloadByVersionId(svcVerDownloadRecordDTO, organizationId);
                                                if (svcVerDownloadRecordDTO == null) {
                                                    serviceVersionVO.setNewFixVersion(true);
                                                    versionVO.setNewVersion(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                            handleWithNoSVCDownload(marketAppServiceVOS);
                        }
                    } else {
                        List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                        handleWithNoSVCDownload(marketAppServiceVOS);
                    }
                } else {
                    List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                    handleWithNoSVCDownload(marketAppServiceVOS);
                }
            }
            pageInfo.setList(versionVOS);
        }
    }

    private void getVersionStatusAndPurchased(List<MarketApplicationVersionVO> versionVOS, List<CustomerApplicationVersionVO> customerApplicationVersionVOS, PageInfo<MarketApplicationVersionVO> pageInfo) {
        if (!CollectionUtils.isEmpty(versionVOS)) {
            for (MarketApplicationVersionVO versionVO : versionVOS) {
                if (!CollectionUtils.isEmpty(customerApplicationVersionVOS)) {
                    for (CustomerApplicationVersionVO customerApplicationVersionVO : customerApplicationVersionVOS) {
                        if (customerApplicationVersionVO.getId().equals(versionVO.getId())) {
                            versionVO.setDisplayStatus(customerApplicationVersionVO.getDisplayStatus());
                            break;
                        }
                    }
                }
                versionVO.setNewVersion(false);
                AppVerDownloadRecordDTO downloadRecordDTO = new AppVerDownloadRecordDTO();
                downloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                if (appVerDownloadRecordMapper.selectCount(downloadRecordDTO) > 0) {
                    AppVerDownloadRecordDTO verDownloadRecordDTO = new AppVerDownloadRecordDTO();
                    verDownloadRecordDTO.setMktAppCode(versionVO.getMarketAppCode());
                    verDownloadRecordDTO.setMktVersionId(versionVO.getId());
                    verDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                    verDownloadRecordDTO = appVerDownloadRecordMapper.selectOne(verDownloadRecordDTO);
                    AppVerDownloadRecordDTO recordDTO = new AppVerDownloadRecordDTO();
                    recordDTO.setMktAppCode(versionVO.getMarketAppCode());
                    recordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                    if (!CollectionUtils.isEmpty(appVerDownloadRecordMapper.select(recordDTO))) {
                        if (verDownloadRecordDTO == null) {
//                            versionVO.setNewVersion(true);
                        } else if (versionVO.getLatestFixVersion() > 0) {
                            List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                            if (!CollectionUtils.isEmpty(marketAppServiceVOS)) {
                                for (MarketAppServiceVO marketAppServiceVO : marketAppServiceVOS) {
                                    List<ServiceVersionVO> serviceVersionVOS = marketAppServiceVO.getServiceVersionVOS();
                                    if (!CollectionUtils.isEmpty(serviceVersionVOS)) {
                                        for (ServiceVersionVO serviceVersionVO : serviceVersionVOS) {
                                            serviceVersionVO.setNewFixVersion(false);
                                            if (serviceVersionVO.getFixVersion() > 0) {
                                                SvcVerDownloadRecordDTO svcVerDownloadRecordDTO = new SvcVerDownloadRecordDTO();
                                                svcVerDownloadRecordDTO.setMktSvcVersionId(serviceVersionVO.getId());
                                                svcVerDownloadRecordDTO.setMktVersionId(versionVO.getId());
                                                svcVerDownloadRecordDTO.setStatus(AppDownloadStatus.COMPLETED.getValue());
                                                svcVerDownloadRecordDTO = svcVerDownloadRecordMapper.selectOne(svcVerDownloadRecordDTO);
                                                if (svcVerDownloadRecordDTO == null) {
                                                    serviceVersionVO.setNewFixVersion(true);
                                                    versionVO.setNewVersion(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                            handleWithNoSVCDownload(marketAppServiceVOS);
                        }
                    } else {
                        List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                        handleWithNoSVCDownload(marketAppServiceVOS);
                    }
                } else {
                    List<MarketAppServiceVO> marketAppServiceVOS = versionVO.getMarketAppServiceVOS();
                    handleWithNoSVCDownload(marketAppServiceVOS);
                }
            }
            pageInfo.setList(versionVOS);
        }
    }

    private void handleWithNoSVCDownload(List<MarketAppServiceVO> marketAppServiceVOS) {
        if (!CollectionUtils.isEmpty(marketAppServiceVOS)) {
            for (MarketAppServiceVO marketAppServiceVO : marketAppServiceVOS) {
                List<ServiceVersionVO> marketServiceVersionDTOS = marketAppServiceVO.getServiceVersionVOS();
                if (!CollectionUtils.isEmpty(marketServiceVersionDTOS)) {
                    for (ServiceVersionVO marketServiceVersionDTO : marketServiceVersionDTOS) {
                        marketServiceVersionDTO.setNewFixVersion(false);
                    }
                }
            }
        }
    }

    @Override
    public List<MarketApplicationVersionVO> getAppVersionsById(Long id) {
        List<MarketApplicationVersionVO> versionVOS = new ArrayList<>();
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                try {
                    Response<List<MarketApplicationVersionVO>> repos = client.getAppPublishVersionsById(id, remoteTokenAuthorizationVO.getRemoteToken()).execute();
                    if (!repos.isSuccessful() && repos.body() != null) {
                        throw new CommonException("error.app.version.query", new CommonException(repos.message()));
                    }
                    versionVOS = repos.body();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
        } else {
            versionVOS = marketFeignClient.getAppPublishVersionsByIdWithin(id).getBody();
        }
        return versionVOS;
    }

    @Override
    public Integer getNewVersionNum(Long organizationId) {
        PageInfo<RemoteApplicationVO> pageInfo = null;
        if (!isSassPlatform) {
            RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (remoteTokenAuthorizationVO != null && RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
                AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                        remoteTokenAuthorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
                Response<PageInfo<RemoteApplicationVO>> repos = null;
                List<String> downloadAppCode = appVerDownloadRecordMapper.getMyDownloadAppCode();
                try {
                    repos = client.getMyDownloadApp(remoteTokenAuthorizationVO.getRemoteToken(), null, null, "DESC", 0, 0, downloadAppCode).execute();
                    pageInfo = repos.body();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            } else {
                throw new CommonException(ERROR_REMOTE_TOKEN_VALIDATE);
            }
            return getHasNewVersion(pageInfo, false);

        } else {
            List<String> appCodeList = appVerDownloadRecordMapper.getCompletedAppCodeByOrgId(organizationId);
            pageInfo = marketFeignClient.getMyDownloadAppWithin(0, 0, null, null, "DESC", appCodeList, organizationId).getBody();
            return getHasNewVersionWithin(pageInfo, organizationId, false);
        }

    }

    /**
     * 根据sass应用code查询下载记录
     *
     * @param appCode
     * @return
     */
    private List<AppVerDownloadRecordDTO> queryDownloadRecordByAppCode(String appCode, Long organizationId) {
        List<AppVerDownloadRecordDTO> appVerDownloadRecordDTOS;
        if (!isSassPlatform) {
            AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
            appVerDownloadRecordDTO.setMktAppCode(appCode);
            appVerDownloadRecordDTOS = appVerDownloadRecordMapper.select(appVerDownloadRecordDTO);
        } else {
            if (organizationId == null) {
                throw new CommonException("error.organizationId.is.null");
            }
            appVerDownloadRecordDTOS = appVerDownloadRecordMapper.queryDownloadRecordByAppCodeAndOrgId(appCode, organizationId);
        }
        return appVerDownloadRecordDTOS;
    }

    private void setDownloadStatusAndOrder(List<CustomerApplicationVersionVO> customerApplicationVersionVOS, List<AppVerDownloadRecordDTO> appVerDownloadRecordDTOS) {
        Map<Long, CustomerApplicationVersionVO> versionVOMap = customerApplicationVersionVOS.stream().collect(Collectors.toMap(CustomerApplicationVersionVO::getId, v -> v));
        for (AppVerDownloadRecordDTO downloadRecordDTO : appVerDownloadRecordDTOS) {
            CustomerApplicationVersionVO customerApplicationVersionVO = versionVOMap.get(downloadRecordDTO.getMktVersionId());
            if (customerApplicationVersionVO != null) {
                String status = downloadRecordDTO.getStatus();
                customerApplicationVersionVO.setDownloadStatus(status);
                if (AppDownloadStatus.FAILED.getValue().equals(status)) {
                    customerApplicationVersionVO.setOrder(DownloadOrder.FAILED.getValue());
                    customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.DOWNLOAD_FAILED.getValue());
                }
                if (AppDownloadStatus.DOWNLOADING.getValue().equals(status)) {
                    customerApplicationVersionVO.setOrder(DownloadOrder.DOWNLOADING.getValue());
                    customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.DOWNLOADING.getValue());
                }
                if (AppDownloadStatus.COMPLETED.getValue().equals(status)) {
                    customerApplicationVersionVO.setOrder(DownloadOrder.COMPLETED.getValue());
                    customerApplicationVersionVO.setDisplayStatus(AppVersionDisplayStatus.COMPLETED.getValue());
                }
            }
        }
    }

    private List<CustomerApplicationVersionVO> sortVersionByDownloadRecord
            (List<CustomerApplicationVersionVO> customerApplicationVersionVOS) {
        return customerApplicationVersionVOS.stream().sorted(Comparator.comparing(CustomerApplicationVersionVO::getOrder).reversed()).collect(Collectors.toList());
    }
}
