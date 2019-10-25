package io.choerodon.base.app.service.impl;

import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.base.infra.utils.SagaTopic.Application.*;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.payload.AppMarketDownloadPayload;
import io.choerodon.base.api.dto.payload.AppServiceDownloadPayload;
import io.choerodon.base.api.dto.payload.AppServiceVersionDownloadPayload;
import io.choerodon.base.api.dto.payload.MarketAppPayload;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.AppDownloadService;
import io.choerodon.base.infra.asserts.DetailsHelperAssert;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.enums.AppDownloadStatus;
import io.choerodon.base.infra.enums.ApplicationSvcVersionStatusEnum;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.base.infra.retrofit.AppDownloadRetrofitClient;
import io.choerodon.base.infra.retrofit.AppMarketRetrofitClient;
import io.choerodon.base.infra.utils.RetrofitCallExceptionParse;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.iam.ResourceLevel;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/9/5
 */
@Service
public class AppDownloadServiceImpl implements AppDownloadService {

    private ApplicationMapper applicationMapper;
    private ApplicationVersionMapper applicationVersionMapper;
    private TransactionalProducer producer;
    private RetrofitClientFactory retrofitClientFactory;
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private AppVerDownloadRecordMapper appVerDownloadRecordMapper;
    private ApplicationSvcVersionRefMapper appSvcVersionRefMapper;
    private MarketFeignClient marketFeignClient;
    private SvcVerDownloadRecordMapper svcVerDownloadRecordMapper;
    private ApplicationServiceRefMapper serviceRefMapper;
    private AppOrganizationRefMapper appOrganizationRefMapper;

    @Value("${choerodon.market.saas.platform:false}")
    private boolean marketSaaSPlatform;

    private final Logger logger = LoggerFactory.getLogger(AppDownloadServiceImpl.class);
    private static final String DEVOPS_SERVICE_TYPE = "normal";
    private static final Long SITE_APP_PROJECT_ID = 0L;
    private static final String QUERY_DOWNLOAD_INFO_EXCEPTION = "error.download.info.query";
    private static final String QUERY_SERVICE_VERSION_BY_APP_VERSION_EXCEPTION = "error.app.version.service.version.query";

    public AppDownloadServiceImpl(ApplicationMapper applicationMapper, ApplicationVersionMapper applicationVersionMapper,
                                  TransactionalProducer producer, RetrofitClientFactory retrofitClientFactory,
                                  RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, AppVerDownloadRecordMapper appVerDownloadRecordMapper,
                                  ApplicationSvcVersionRefMapper appSvcVersionRefMapper, MarketFeignClient marketFeignClient,
                                  SvcVerDownloadRecordMapper svcVerDownloadRecordMapper, ApplicationServiceRefMapper serviceRefMapper,
                                  AppOrganizationRefMapper appOrganizationRefMapper) {
        this.applicationMapper = applicationMapper;
        this.applicationVersionMapper = applicationVersionMapper;
        this.producer = producer;
        this.retrofitClientFactory = retrofitClientFactory;
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.appVerDownloadRecordMapper = appVerDownloadRecordMapper;
        this.appSvcVersionRefMapper = appSvcVersionRefMapper;
        this.marketFeignClient = marketFeignClient;
        this.svcVerDownloadRecordMapper = svcVerDownloadRecordMapper;
        this.serviceRefMapper = serviceRefMapper;
        this.appOrganizationRefMapper = appOrganizationRefMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeDownloadApplication(Long appDownloadRecordId, Long appVersionId, Long organizationId, List<AppDownloadDevopsReqVO> appDownloadDevopsReqVOS) {
        logger.info("========Service has been download, start to call back complete download application function========, organizationId: {}, appVersionId: {}", marketSaaSPlatform ? organizationId : 0L, appVersionId);
        appDownloadDevopsReqVOS.forEach(s ->
                logger.info("========appDownloadDevopsReqVOS==========, serviceId: {}", s.getServiceId())
        );

        batchInsertAppSvcRef(appVersionId, appDownloadDevopsReqVOS);

        Set<Long> allSvcVersionIds = new HashSet<>();
        appDownloadDevopsReqVOS.forEach(v -> allSvcVersionIds.addAll(v.getServiceVersionIds()));
        batchInsertAppVerAppSvc(appVersionId, allSvcVersionIds);

        AppVerDownloadRecordDTO resVerRecord = appVerDownloadRecordMapper.selectByPrimaryKey(appDownloadRecordId);

        sendAppVersionDownloadCompletedOrFailedEvent(resVerRecord, APPLICATION_DOWNLOAD_COMPLETED);

        changeDownloadStatus(resVerRecord, AppDownloadStatus.COMPLETED);
        changeSvcVerDownloadStatusByMktVersion(organizationId, resVerRecord.getMktVersionId(), AppDownloadStatus.COMPLETED);

        logger.info("========Service has been download, end call back complete download application function========, organizationId: {}, appVersionId: {}", organizationId, appVersionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void failToDownloadApplication(Long appDownloadRecordId, Long appVersionId, Long organizationId) {
        logger.info("========Service has been download, start to call back fail to download application function========, organizationId: {}, appVersionId: {}", organizationId, appVersionId);

        AppVerDownloadRecordDTO resVerRecord = appVerDownloadRecordMapper.selectByPrimaryKey(appDownloadRecordId);

        changeDownloadStatus(resVerRecord, AppDownloadStatus.FAILED);
        changeSvcVerDownloadStatusByMktVersion(organizationId, resVerRecord.getMktVersionId(), AppDownloadStatus.FAILED);

        sendAppVersionDownloadCompletedOrFailedEvent(resVerRecord, APPLICATION_DOWNLOAD_FAILED);

        logger.info("========Service has been download, end call back fail to download application function========, organizationId: {}, appVersionId: {}", organizationId, appVersionId);
    }


    @Override
    @Saga(code = APPLICATION_DOWNLOAD, description = "base下载应用", inputSchemaClass = AppMarketDownloadPayload.class)
    public ApplicationDTO downloadApplication(Long versionId, Long organizationId) {
        DownloadInfoVO downloadInfoVO = getDownloadInfoVO(versionId, organizationId);
        AppVerDownloadRecordDTO resRecord = getRecordIfPresent(downloadInfoVO, organizationId);
        Set<Long> needDownloadSvcVersionIds = batchInsertVerSvcVerRecord(downloadInfoVO, organizationId);
        ApplicationDTO resApp;
        try {
            resApp = ((AppDownloadServiceImpl) AopContext.currentProxy()).startAppDownload(downloadInfoVO, resRecord, organizationId);
        } catch (Exception e) {
            changeDownloadStatus(resRecord, AppDownloadStatus.FAILED);
            changeSvcVersionDownloadStatus(organizationId, versionId, needDownloadSvcVersionIds, AppDownloadStatus.FAILED);

            // 发送下载失败信息
            sendAppVersionEvent(
                    new MarketAppPayload(downloadInfoVO.getMarketPublishApplicationVO().getName(),
                            downloadInfoVO.getMarketPublishApplicationVO().getCode(),
                            downloadInfoVO.getId(),
                            downloadInfoVO.getVersion(),
                            DetailsHelperAssert.userDetailNotExisted().getUserId(),
                            !getDownloadedSvcVersionIds(organizationId, versionId, true).isEmpty()),
                    marketSaaSPlatform ? 0 : organizationId,
                    marketSaaSPlatform ? ResourceLevel.SITE : ResourceLevel.ORGANIZATION,
                    APPLICATION_DOWNLOAD_FAILED);

            throw new CommonException("error.download.application", e);
        }
        return resApp;
    }

    private DownloadInfoVO getDownloadInfoVO(Long versionId, Long organizationId) {
        DownloadInfoVO downloadInfoVO;
        Set<Long> downloadedSvcVersionIds = getDownloadedSvcVersionIds(organizationId, versionId, false);
        Set<Long> needDownloadSvcVersionIds;
        if (marketSaaSPlatform) {
            Assert.notNull(organizationId, "error.organization.id.null");
            Set<Long> allSvcVersionIds = marketFeignClient.listServiceVersionsByVersionId(versionId).getBody();
            needDownloadSvcVersionIds = getNeedDownloadSvcVersionIds(downloadedSvcVersionIds, allSvcVersionIds);

            downloadInfoVO = CollectionUtils.isEmpty(needDownloadSvcVersionIds)
                    ? checkOrgIsDownloadSvcVersion(versionId, organizationId, null)
                    : marketFeignClient.queryDownloadInfo(versionId, organizationId, needDownloadSvcVersionIds).getBody();

            if (downloadInfoVO == null) {
                throw new CommonException(QUERY_DOWNLOAD_INFO_EXCEPTION);
            }
        } else {
            RemoteTokenAuthorizationVO authorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
            if (ObjectUtils.isEmpty(authorizationVO) || !RemoteTokenStatus.SUCCESS.value().equals(authorizationVO.getStatus())) {
                throw new CommonException("error.latest.remote.token");
            }
            needDownloadSvcVersionIds = getNeedDownloadSvcVersionIds(downloadedSvcVersionIds, getAllSvcVersionIds(versionId, authorizationVO));

            downloadInfoVO = CollectionUtils.isEmpty(needDownloadSvcVersionIds)
                    ? checkOrgIsDownloadSvcVersion(versionId, organizationId, authorizationVO)
                    : getDownloadInfoVOByRetrofit(versionId, authorizationVO, needDownloadSvcVersionIds);
        }

        sendAppVersionEvent(
                new MarketAppPayload(downloadInfoVO.getMarketPublishApplicationVO().getName(),
                        downloadInfoVO.getMarketPublishApplicationVO().getCode(),
                        versionId,
                        downloadInfoVO.getVersion(),
                        DetailsHelperAssert.userDetailNotExisted().getUserId(),
                        !downloadedSvcVersionIds.isEmpty()),
                marketSaaSPlatform ? 0 : organizationId,
                marketSaaSPlatform ? ResourceLevel.SITE : ResourceLevel.ORGANIZATION,
                APPLICATION_DOWNLOAD_PROCESSING);
        return downloadInfoVO;
    }

    private DownloadInfoVO checkOrgIsDownloadSvcVersion(Long versionId, Long organizationId, RemoteTokenAuthorizationVO authorizationVO) {
        AppVerDownloadRecordDTO appVerDownloadRecordQuery = new AppVerDownloadRecordDTO();
        appVerDownloadRecordQuery.setMktVersionId(versionId);
        if (marketSaaSPlatform) {
            appVerDownloadRecordQuery.setOrganizationId(organizationId);
        }
        appVerDownloadRecordQuery.setStatus(AppDownloadStatus.COMPLETED.getValue());
        if (appVerDownloadRecordMapper.selectOne(appVerDownloadRecordQuery) != null) {
            throw new CommonException("error.version.has.been.downloaded");
        }
        return marketSaaSPlatform ?
                marketFeignClient.queryDownloadAppInfo(versionId, organizationId).getBody() :
                getDownloadInfoVOWithoutService(versionId, authorizationVO);
    }

    private DownloadInfoVO getDownloadInfoVOWithoutService(Long versionId, RemoteTokenAuthorizationVO authorizationVO) {
        AppDownloadRetrofitClient appDownloadRetrofitClient = (AppDownloadRetrofitClient) retrofitClientFactory.getRetrofitBean(
                authorizationVO.getAuthorizationUrl(), AppDownloadRetrofitClient.class);
        Call<ResponseBody> call = appDownloadRetrofitClient.queryDownloadAppInfo(versionId, authorizationVO.getRemoteToken());
        return RetrofitCallExceptionParse.executeCall(call, QUERY_DOWNLOAD_INFO_EXCEPTION, DownloadInfoVO.class);
    }

    private void sendAppVersionEvent(MarketAppPayload payload, Long sourceId, ResourceLevel resourceLevel, String sagaCode) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(resourceLevel)
                        .withRefType("appDownload")
                        .withSagaCode(sagaCode),
                builder -> {
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(payload.getVersionId().toString())
                            .withSourceId(sourceId);
                    return payload;
                });
    }

    private void changeSvcVerDownloadStatusByMktVersion(Long organizationId, Long mktAppVersionId, AppDownloadStatus appDownloadStatus) {
        SvcVerDownloadRecordDTO svcVerDownloadRecordQuery = new SvcVerDownloadRecordDTO();
        svcVerDownloadRecordQuery.setMktVersionId(mktAppVersionId);
        if (marketSaaSPlatform) {
            svcVerDownloadRecordQuery.setOrganizationId(organizationId);
        }
        changeSvcVersionDownloadStatus(
                organizationId, mktAppVersionId, svcVerDownloadRecordMapper.select(svcVerDownloadRecordQuery)
                        .stream()
                        .map(SvcVerDownloadRecordDTO::getMktSvcVersionId)
                        .collect(Collectors.toSet()),
                appDownloadStatus
        );
    }

    private void changeSvcVersionDownloadStatus(Long organizationId, Long versionId, Set<Long> serviceVersionIds, AppDownloadStatus appDownloadStatus) {
        serviceVersionIds.forEach(v -> {
            SvcVerDownloadRecordDTO svcVerDownloadRecordQuery = new SvcVerDownloadRecordDTO();
            svcVerDownloadRecordQuery.setMktVersionId(versionId);
            svcVerDownloadRecordQuery.setMktSvcVersionId(v);
            svcVerDownloadRecordQuery.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
            logger.info("========Start to record service version========, organizationId: {}, mktVersionId: {}, svcVersionId: {}",
                    marketSaaSPlatform ? organizationId : 0L, versionId, v);
            SvcVerDownloadRecordDTO resSvcVerRecord = svcVerDownloadRecordMapper.selectOne(svcVerDownloadRecordQuery);

            SvcVerDownloadRecordDTO svcVerDownloadRecordUpdate = new SvcVerDownloadRecordDTO();
            svcVerDownloadRecordUpdate.setId(resSvcVerRecord.getId());
            svcVerDownloadRecordUpdate.setStatus(appDownloadStatus.getValue());
            svcVerDownloadRecordUpdate.setObjectVersionNumber(resSvcVerRecord.getObjectVersionNumber());
            try {
                if (svcVerDownloadRecordMapper.updateByPrimaryKeySelective(svcVerDownloadRecordUpdate) != 1) {
                    logger.info("Fail to change service version download record status to {} , service version Id:{}", appDownloadStatus.getValue(), resSvcVerRecord.getMktSvcVersionId());
                }
            } catch (Exception e) {
                logger.info("Fail to change app download record status to  {} , service version Id:", appDownloadStatus.getValue(), resSvcVerRecord.getMktSvcVersionId(), e);
            }
        });
    }

    @Transactional
    public ApplicationDTO startAppDownload(DownloadInfoVO downloadInfoVO, AppVerDownloadRecordDTO resRecord, Long organizationId) {
        // 判断以前下载过此类应用没，下载过就直接插入版本，没下载过就新建
        ApplicationDTO resApp = getAppByAppCode(downloadInfoVO.getMarketPublishApplicationVO().getCode());
        ApplicationVersionDTO resVersion;
        if (ObjectUtils.isEmpty(resApp)) {
            resApp = createApplication(downloadInfoVO.getMarketPublishApplicationVO());
            resVersion = startAppVersionDownload(downloadInfoVO, resApp, resRecord, organizationId);
        } else {
            logger.info("application.has.been.download,market app code:{}, start to check app version", downloadInfoVO.getMarketPublishApplicationVO().getCode());
            resVersion = checkIsAppVersionDownloaded(downloadInfoVO, resApp, resRecord, organizationId);
        }
        // 创建组织与应用及应用版本关联
        checkIsCreatedAppOrgRef(organizationId, resApp, resVersion, downloadInfoVO.getId());
        return resApp;
    }

    private ApplicationDTO createApplication(MarketPublishApplicationVO marketPublishApplicationVO) {
        // 创建应用
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setSourceCode(marketPublishApplicationVO.getCode());
        applicationDTO.setType(marketPublishApplicationVO.getType());
        applicationDTO.setName(marketPublishApplicationVO.getName());
        applicationDTO.setDescription(marketPublishApplicationVO.getDescription());

        applicationDTO.setFeedbackToken(UUID.randomUUID().toString());
        applicationDTO.setCode(UUID.randomUUID().toString());
        applicationDTO.setHasGenerated(false);
        applicationDTO.setProjectId(SITE_APP_PROJECT_ID);

        if (applicationMapper.insertSelective(applicationDTO) != 1) {
            throw new CommonException("error.application.insert");
        }
        return applicationMapper.selectByPrimaryKey(applicationDTO);
    }

    private void createAppOrganizationRef(Long organizationId, ApplicationDTO resApp, ApplicationVersionDTO resAppVersion, Long mktVersionId) {
        AppOrganizationRefDTO appOrganizationRefInsert = new AppOrganizationRefDTO();
        appOrganizationRefInsert.setAppId(resApp.getId());
        appOrganizationRefInsert.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        appOrganizationRefInsert.setAppVersionId(resAppVersion.getId());
        appOrganizationRefInsert.setMktVersionId(mktVersionId);
        if (appOrganizationRefMapper.insert(appOrganizationRefInsert) != 1) {
            throw new InsertException("error.app.organization.ref.insert");
        }
    }

    private ApplicationVersionDTO startAppVersionDownload(DownloadInfoVO downloadInfoVO, ApplicationDTO downloadedApp,
                                                          AppVerDownloadRecordDTO resRecord, Long organizationId) {
        // 创建应用版本
        ApplicationVersionDTO applicationVersionDTO = createAppVersion(downloadInfoVO, downloadedApp);
        // 发送saga -- devops完成后续流程
        sendDownloadAppEvent(downloadedApp, downloadInfoVO, applicationVersionDTO, resRecord, organizationId);
        return applicationVersionDTO;
    }

    private void checkIsCreatedAppOrgRef(Long organizationId, ApplicationDTO resApp, ApplicationVersionDTO resVersion, Long mktVersionId) {
        AppOrganizationRefDTO appOrganizationRefQuery = new AppOrganizationRefDTO();
        appOrganizationRefQuery.setAppId(resApp.getId());
        appOrganizationRefQuery.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        appOrganizationRefQuery.setAppVersionId(resVersion.getId());
        appOrganizationRefQuery.setMktVersionId(mktVersionId);
        if (appOrganizationRefMapper.selectOne(appOrganizationRefQuery) == null) {
            createAppOrganizationRef(organizationId, resApp, resVersion, mktVersionId);
        }
    }

    private ApplicationVersionDTO checkIsAppVersionDownloaded(DownloadInfoVO downloadInfoVO, ApplicationDTO downloadedApp,
                                                              AppVerDownloadRecordDTO resRecord, Long organizationId) {
        // 校验版本是否已下载
        ApplicationVersionDTO downloadedAppVersion = getAppVersionByAppIdAndVersionName(downloadInfoVO, downloadedApp);
        if (ObjectUtils.isEmpty(downloadedAppVersion)) {
            // 创建应用版本
            return startAppVersionDownload(downloadInfoVO, downloadedApp, resRecord, organizationId);
        } else {
            checkIsAppServiceDownloaded(downloadedApp, downloadInfoVO, resRecord, downloadedAppVersion, organizationId);
        }
        return downloadedAppVersion;
    }

    private ApplicationVersionDTO createAppVersion(DownloadInfoVO downloadInfoVO, ApplicationDTO downloadedApp) {
        // 创建下载的应用版本
        ApplicationVersionDTO applicationVersionDTO = getApplicationVersionDTO(downloadInfoVO, downloadedApp);
        if (applicationVersionMapper.insert(applicationVersionDTO) != 1) {
            throw new InsertException("error.application.version.insert");
        }
        return applicationVersionMapper.selectByPrimaryKey(applicationVersionDTO);
    }

    private ApplicationVersionDTO getApplicationVersionDTO(DownloadInfoVO downloadInfoVO, ApplicationDTO downloadedApp) {
        ApplicationVersionDTO applicationVersionDTO = new ApplicationVersionDTO();
        applicationVersionDTO.setApplicationId(downloadedApp.getId());
        applicationVersionDTO.setVersion(downloadInfoVO.getVersion());
        return applicationVersionDTO;
    }

    private void sendDownloadAppEvent(ApplicationDTO applicationDTO, DownloadInfoVO downloadInfoVO,
                                      ApplicationVersionDTO applicationVersionDTO, AppVerDownloadRecordDTO resRecord,
                                      Long organizationId) {
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("appDownload")
                        .withSagaCode(APPLICATION_DOWNLOAD),
                builder ->
                        builder
                                .withPayloadAndSerialize(
                                        loadAppMarketDownloadPayload(
                                                applicationDTO, downloadInfoVO, applicationVersionDTO, resRecord, organizationId)
                                )
                                .withRefId(applicationDTO.getId().toString())
                                .withSourceId(0L)
        );
    }

    private AppMarketDownloadPayload loadAppMarketDownloadPayload(ApplicationDTO applicationDTO, DownloadInfoVO downloadInfoVO,
                                                                  ApplicationVersionDTO applicationVersionDTO, AppVerDownloadRecordDTO resRecord,
                                                                  Long organizationId) {
        AppMarketDownloadPayload appMarketDownloadPayload = new AppMarketDownloadPayload();
        appMarketDownloadPayload.setAppId(applicationDTO.getId());
        appMarketDownloadPayload.setAppCode(applicationDTO.getCode());
        appMarketDownloadPayload.setAppName(applicationDTO.getName());
        appMarketDownloadPayload.setIamUserId(DetailsHelperAssert.userDetailNotExisted().getUserId());
        appMarketDownloadPayload.setUser(downloadInfoVO.getUser());
        appMarketDownloadPayload.setAppVersionId(applicationVersionDTO.getId());
        appMarketDownloadPayload.setAppDownloadRecordId(resRecord.getId());
        appMarketDownloadPayload.setDownloadAppType(downloadInfoVO.getMarketPublishApplicationVO().getType());
        appMarketDownloadPayload.setMktAppVersionId(downloadInfoVO.getId());
        appMarketDownloadPayload.setOrganizationId(organizationId);

        List<AppServiceDownloadPayload> appServiceDownloadPayloads = loadAppServiceDownloadPayloads(applicationDTO, downloadInfoVO);
        appMarketDownloadPayload.setAppServiceDownloadPayloads(appServiceDownloadPayloads);

        return appMarketDownloadPayload;
    }

    private List<AppServiceDownloadPayload> loadAppServiceDownloadPayloads(ApplicationDTO applicationDTO, DownloadInfoVO downloadInfoVO) {
        List<AppServiceDownloadPayload> appServiceDownloadPayloads = new ArrayList<>();
        downloadInfoVO.getMarketServiceVOS().forEach(service -> {
            List<AppServiceVersionDownloadPayload> appServiceVersionDownloadPayloads = loadAppServiceVersionDownloadPayloads(service);
            appServiceDownloadPayloads.add(new AppServiceDownloadPayload(applicationDTO.getId(),
                    service.getName(), service.getCode(), DEVOPS_SERVICE_TYPE, appServiceVersionDownloadPayloads));
        });
        return appServiceDownloadPayloads;
    }

    private List<AppServiceVersionDownloadPayload> loadAppServiceVersionDownloadPayloads(MarketServiceVO service) {
        List<AppServiceVersionDownloadPayload> appServiceVersionDownloadPayloads = new ArrayList<>();
        service.getMarketServiceVersionVOS().forEach(serviceVersion ->
                appServiceVersionDownloadPayloads.add(new AppServiceVersionDownloadPayload(serviceVersion.getVersion(),
                        serviceVersion.getImageUrl(), serviceVersion.getCodeUrl(), serviceVersion.getChartUrl())));
        return appServiceVersionDownloadPayloads;
    }

    private Set<Long> getDownloadedSvcVersionIds(Long organizationId, Long versionId, Boolean filterDownloading) {
        SvcVerDownloadRecordDTO verSvcVerRecordQuery = new SvcVerDownloadRecordDTO();
        verSvcVerRecordQuery.setMktVersionId(versionId);
        verSvcVerRecordQuery.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        return svcVerDownloadRecordMapper.select(verSvcVerRecordQuery)
                .stream()
                .filter(v ->
                        filterDownloading ?
                                !AppDownloadStatus.FAILED.getValue().equals(v.getStatus()) :
                                AppDownloadStatus.COMPLETED.getValue().equals(v.getStatus()))
                .map(SvcVerDownloadRecordDTO::getMktSvcVersionId)
                .collect(Collectors.toSet());
    }

    private DownloadInfoVO getDownloadInfoVOByRetrofit(Long versionId, RemoteTokenAuthorizationVO authorizationVO, Set<Long> needDownloadSvcVersionIds) {
        AppDownloadRetrofitClient appDownloadRetrofitClient = (AppDownloadRetrofitClient) retrofitClientFactory.getRetrofitBean(
                authorizationVO.getAuthorizationUrl(), AppDownloadRetrofitClient.class);
        Call<ResponseBody> call = appDownloadRetrofitClient.queryDownloadInfo(versionId, needDownloadSvcVersionIds, authorizationVO.getRemoteToken());
        return RetrofitCallExceptionParse.executeCall(call, QUERY_DOWNLOAD_INFO_EXCEPTION, DownloadInfoVO.class);
    }

    private Set<Long> getAllSvcVersionIds(Long versionId, RemoteTokenAuthorizationVO authorizationVO) {
        AppMarketRetrofitClient appMarketRetrofitClient = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                authorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
        Call<ResponseBody> listServiceVersionsCall = appMarketRetrofitClient.listServiceVersionsByVersionId(versionId, authorizationVO.getRemoteToken());
        return new HashSet<>(RetrofitCallExceptionParse.executeCallForList(listServiceVersionsCall, QUERY_SERVICE_VERSION_BY_APP_VERSION_EXCEPTION, Long.class));
    }

    private Set<Long> getNeedDownloadSvcVersionIds(Set<Long> downloadedSvcVersionIds, Set<Long> allSvcVersionIds) {
        if (CollectionUtils.isEmpty(allSvcVersionIds)) {
            throw new CommonException("error.app.version.service.version.is.empty");
        }
        allSvcVersionIds.removeAll(downloadedSvcVersionIds);
        return allSvcVersionIds;
    }

    private AppVerDownloadRecordDTO getRecordIfPresent(DownloadInfoVO downloadInfoVO, Long organizationId) {
        // 已包含此应用创建下载记录，不作处理
        AppVerDownloadRecordDTO queryRecord = new AppVerDownloadRecordDTO();
        queryRecord.setMktAppCode(downloadInfoVO.getMarketPublishApplicationVO().getCode());
        queryRecord.setMktVersionId(downloadInfoVO.getId());
        queryRecord.setStatus(AppDownloadStatus.COMPLETED.getValue());
        queryRecord.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        AppVerDownloadRecordDTO resRecord = appVerDownloadRecordMapper.selectOne(queryRecord);
        if (resRecord == null) {
            resRecord = createAppDownloadRecord(downloadInfoVO, organizationId);
        }
        return resRecord;
    }

    private AppVerDownloadRecordDTO createAppDownloadRecord(DownloadInfoVO downloadInfoVO, Long organizationId) {
        // 添加历史记录
        AppVerDownloadRecordDTO appVerDownloadRecordDTO = new AppVerDownloadRecordDTO();
        appVerDownloadRecordDTO.setMktAppId(downloadInfoVO.getMarketPublishApplicationVO().getId());
        appVerDownloadRecordDTO.setMktAppCode(downloadInfoVO.getMarketPublishApplicationVO().getCode());
        appVerDownloadRecordDTO.setMktAppName(downloadInfoVO.getMarketPublishApplicationVO().getName());
        appVerDownloadRecordDTO.setMktVersionId(downloadInfoVO.getId());
        appVerDownloadRecordDTO.setMktVersionName(downloadInfoVO.getVersion());
        appVerDownloadRecordDTO.setCategoryName(downloadInfoVO.getMarketPublishApplicationVO().getCategoryName());
        appVerDownloadRecordDTO.setStatus(AppDownloadStatus.DOWNLOADING.getValue());
        appVerDownloadRecordDTO.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        try {
            if (appVerDownloadRecordMapper.insertSelective(appVerDownloadRecordDTO) != 1) {
                logger.info("Fail to insert app download record ,market app code:{}", appVerDownloadRecordDTO.getMktAppCode());
            }
        } catch (Exception e) {
            logger.info("Fail to insert app download record ,market app code:{}", appVerDownloadRecordDTO.getMktAppCode(), e);
        }
        return appVerDownloadRecordMapper.selectByPrimaryKey(appVerDownloadRecordDTO);
    }

    private Set<Long> batchInsertVerSvcVerRecord(DownloadInfoVO downloadInfoVO, Long organizationId) {
        SvcVerDownloadRecordDTO svcVerDownloadRecordInsert = new SvcVerDownloadRecordDTO();
        svcVerDownloadRecordInsert.setMktVersionId(downloadInfoVO.getId());
        Set<Long> needDownloadSvcVersionIds = new HashSet<>();

        downloadInfoVO.getMarketServiceVOS().forEach(v ->
                needDownloadSvcVersionIds.addAll(
                        v.getMarketServiceVersionVOS()
                                .stream()
                                .map(MarketServiceVersionVO::getId)
                                .collect(Collectors.toSet())));

        recordNeedDownloadSvcVer(svcVerDownloadRecordInsert, needDownloadSvcVersionIds, organizationId);
        return needDownloadSvcVersionIds;
    }

    private void recordNeedDownloadSvcVer(SvcVerDownloadRecordDTO svcVerDownloadRecordInsert,
                                          Set<Long> needDownloadSvcVersionIds, Long organizationId) {
        svcVerDownloadRecordInsert.setStatus(AppDownloadStatus.DOWNLOADING.getValue());
        svcVerDownloadRecordInsert.setOrganizationId(marketSaaSPlatform ? organizationId : 0L);
        needDownloadSvcVersionIds.forEach(v -> {
            svcVerDownloadRecordInsert.setId(null);
            svcVerDownloadRecordInsert.setMktSvcVersionId(v);
            logger.info("========Start to record service version========, organizationId: {}, mktVersionId: {}, svcVersionId: {}",
                    marketSaaSPlatform ? organizationId : 0L,
                    svcVerDownloadRecordInsert.getMktVersionId(),
                    v);
            try {
                if (svcVerDownloadRecordMapper.selectOne(svcVerDownloadRecordInsert) == null && svcVerDownloadRecordMapper.insert(svcVerDownloadRecordInsert) != 1) {
                    logger.info("Fail to insert service version download record ,market app service version Id:{}", v);
                }
            } catch (Exception e) {
                logger.info("Fail to insert service version record ,market app service version Id:{}", v, e);
            }
        });
    }

    private ApplicationDTO getAppByAppCode(String mktAppCode) {
        ApplicationDTO checkIsDownloadApp = new ApplicationDTO();
        checkIsDownloadApp.setSourceCode(mktAppCode);
        return applicationMapper.selectOne(checkIsDownloadApp);
    }

    private void checkIsAppServiceDownloaded(ApplicationDTO downloadedApp, DownloadInfoVO downloadInfoVO, AppVerDownloadRecordDTO resRecord,
                                             ApplicationVersionDTO downloadedAppVersion, Long organizationId) {
        AppVerDownloadRecordDTO verDownloadRecordQuery = new AppVerDownloadRecordDTO();
        verDownloadRecordQuery.setMktVersionId(downloadInfoVO.getId());
        if (marketSaaSPlatform) {
            verDownloadRecordQuery.setOrganizationId(organizationId);
        }
        boolean downloaded = appVerDownloadRecordMapper.select(verDownloadRecordQuery)
                .stream()
                .anyMatch(v -> AppDownloadStatus.COMPLETED.getValue().equals(v.getStatus()));
        if (downloaded && downloadInfoVO.getMarketServiceVOS().isEmpty()) {
            logger.info("application.version.has.been.download, version: {}", downloadedAppVersion.getVersion());
            checkIsCreatedAppOrgRef(organizationId, downloadedApp, downloadedAppVersion, downloadInfoVO.getId());
            sendAppVersionDownloadCompletedOrFailedEvent(resRecord, APPLICATION_DOWNLOAD_COMPLETED);
        } else {
            // 服务没有下载成功，发saga，让devops重下
            logger.info("Restart to download service version , app version: {}", downloadedAppVersion.getVersion());
            sendDownloadAppEvent(downloadedApp, downloadInfoVO, downloadedAppVersion, resRecord, organizationId);
        }
    }

    private ApplicationVersionDTO getAppVersionByAppIdAndVersionName(DownloadInfoVO downloadInfoVO, ApplicationDTO downloadedApp) {
        ApplicationVersionDTO checkIsDownloadAppVersion = new ApplicationVersionDTO();
        checkIsDownloadAppVersion.setApplicationId(downloadedApp.getId());
        checkIsDownloadAppVersion.setVersion(downloadInfoVO.getVersion());
        return applicationVersionMapper.selectOne(checkIsDownloadAppVersion);
    }

    private void changeDownloadStatus(AppVerDownloadRecordDTO appVerDownloadRecordDTO, AppDownloadStatus appDownloadStatus) {
        appVerDownloadRecordDTO.setStatus(appDownloadStatus.getValue());
        // 历史更新出错不影响正常的下载流程
        try {
            if (appVerDownloadRecordMapper.updateByPrimaryKeySelective(appVerDownloadRecordDTO) != 1) {
                logger.info("Fail to change app download record status to {} ,market app code:{}", appDownloadStatus.getValue(), appVerDownloadRecordDTO.getMktAppCode());
            }
        } catch (Exception e) {
            logger.info("Fail to change app download record status to  {} ,market app code:{}", appDownloadStatus.getValue(), appVerDownloadRecordDTO.getMktAppCode(), e);
        }
    }

    private void batchInsertAppVerAppSvc(Long appVersionId, Set<Long> serviceVersionIds) {
        serviceVersionIds.forEach(v -> {
            ApplicationSvcVersionRefDTO appSvcVersionRefDTO = new ApplicationSvcVersionRefDTO();
            appSvcVersionRefDTO.setApplicationVersionId(appVersionId);
            appSvcVersionRefDTO.setStatus(ApplicationSvcVersionStatusEnum.UNPUBLISHED.value());
            appSvcVersionRefDTO.setServiceVersionId(v);
            if (appSvcVersionRefMapper.selectOne(appSvcVersionRefDTO) == null && appSvcVersionRefMapper.insert(appSvcVersionRefDTO) != 1) {
                throw new InsertException("error.app.service.version.ref.insert");
            }
        });
    }

    private void batchInsertAppSvcRef(Long appVersionId, List<AppDownloadDevopsReqVO> appDownloadDevopsReqVOS) {
        Long appId = applicationVersionMapper.selectByPrimaryKey(appVersionId).getApplicationId();
        ApplicationServiceRefDTO serviceRefDTO = new ApplicationServiceRefDTO();
        serviceRefDTO.setApplicationId(appId);
        appDownloadDevopsReqVOS.forEach(v -> {
            serviceRefDTO.setServiceId(v.getServiceId());
            // 如果存在已经应用服务关联，说明此次下载的是服务修复版本，不需要再插入关联
            if (serviceRefMapper.select(serviceRefDTO).isEmpty()) {
                serviceRefDTO.setId(null);
                if (serviceRefMapper.insert(serviceRefDTO) != 1) {
                    throw new InsertException("error.application.service.ref.insert");
                }
            }
        });
    }

    private void sendAppVersionDownloadCompletedOrFailedEvent(AppVerDownloadRecordDTO resVerRecord, String sagaCode) {
        // 下载之前下载过哪些服务版本
        Set<Long> downloadedSvcVersionIds = getDownloadedSvcVersionIds(resVerRecord.getOrganizationId(),
                resVerRecord.getMktVersionId(), false);
        // 发送下载信息
        sendAppVersionEvent(
                new MarketAppPayload(resVerRecord.getMktAppName(),
                        resVerRecord.getMktAppCode(),
                        resVerRecord.getMktVersionId(),
                        resVerRecord.getMktVersionName(),
                        DetailsHelperAssert.userDetailNotExisted().getUserId(),
                        !downloadedSvcVersionIds.isEmpty()),
                marketSaaSPlatform ? 0 : resVerRecord.getOrganizationId(),
                marketSaaSPlatform ? ResourceLevel.SITE : ResourceLevel.ORGANIZATION,
                sagaCode);
    }

}
