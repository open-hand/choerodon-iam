package io.choerodon.base.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.ApplicationSvcVersionRefService;
import io.choerodon.base.app.service.MktApplyAndPublishService;
import io.choerodon.base.app.service.RemoteTokenAuthorizationService;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.dto.devops.AppMarketFixVersionPayload;
import io.choerodon.base.infra.dto.devops.AppMarketUploadPayload;
import io.choerodon.base.infra.dto.devops.AppServiceUploadPayload;
import io.choerodon.base.infra.dto.devops.AppServiceVersionUploadPayload;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.base.infra.dto.mkt.HarborUserAndUrlVO;
import io.choerodon.base.infra.enums.AppPublishStatus;
import io.choerodon.base.infra.enums.ApplicationSvcVersionStatusEnum;
import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.base.infra.retrofit.PublishAppRetrofitCalls;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.base.app.service.impl.ApplicationServiceImpl.APPLICATION_DOES_NOT_EXIST_EXCEPTION;
import static io.choerodon.base.app.service.impl.ApplicationServiceImpl.APPLICATION_UPDATE_EXCEPTION;
import static io.choerodon.base.app.service.impl.ApplicationSvcVersionRefServiceImpl.APP_SVC_VERSION_REF_UPDATE_EXCEPTION;
import static io.choerodon.base.app.service.impl.ApplicationVersionServiceImpl.APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION;
import static io.choerodon.base.app.service.impl.MktPublishApplicationServiceImpl.*;
import static io.choerodon.base.app.service.impl.MktPublishVersionInfoServiceImpl.*;
import static io.choerodon.base.app.service.impl.RemoteTokenAuthorizationServiceImpl.REMOTE_TOKEN_AUTHORIZATION_DOES_NOT_EXIST_EXCEPTION;
import static io.choerodon.base.infra.utils.SagaTopic.PublishApp.PUBLISH_APP;
import static io.choerodon.base.infra.utils.SagaTopic.PublishApp.PUBLISH_APP_FIX_VERSION;

@Service
public class MktApplyAndPublishServiceImpl implements MktApplyAndPublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MktApplyAndPublishService.class);

    private static final String FEIGN_EXECUTE_FAIL_EXCEPTION = "error.feign.execute.unsuccessful";

    @Value("${choerodon.market.saas.platform:false}")
    private boolean saasPlatform;

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    private ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper;
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private MktPublishApplicationMapper mktPublishApplicationMapper;
    private MktPublishVersionInfoMapper mktPublishVersionInfoMapper;
    private ApplicationVersionMapper applicationVersionMapper;
    private MktAppPublishRecordMapper publishRecordMapper;
    private ApplicationMapper applicationMapper;

    private RemoteTokenAuthorizationService remoteTokenAuthorizationService;
    private ApplicationSvcVersionRefService applicationSvcVersionRefService;

    private PublishAppRetrofitCalls publishAppRetrofitCalls;

    private MarketFeignClient marketFeignClient;

    private TransactionalProducer producer;


    public MktApplyAndPublishServiceImpl(ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper, RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, MktPublishApplicationMapper mktPublishApplicationMapper, MktPublishVersionInfoMapper mktPublishVersionInfoMapper, ApplicationVersionMapper applicationVersionMapper, MktAppPublishRecordMapper publishRecordMapper, ApplicationMapper applicationMapper,
                                         RemoteTokenAuthorizationService remoteTokenAuthorizationService, ApplicationSvcVersionRefService applicationSvcVersionRefService,
                                         PublishAppRetrofitCalls publishAppRetrofitCalls,
                                         MarketFeignClient marketFeignClient,
                                         TransactionalProducer producer) {
        this.applicationSvcVersionRefMapper = applicationSvcVersionRefMapper;
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.mktPublishApplicationMapper = mktPublishApplicationMapper;
        this.mktPublishVersionInfoMapper = mktPublishVersionInfoMapper;
        this.applicationVersionMapper = applicationVersionMapper;
        this.publishRecordMapper = publishRecordMapper;
        this.applicationMapper = applicationMapper;

        this.remoteTokenAuthorizationService = remoteTokenAuthorizationService;
        this.applicationSvcVersionRefService = applicationSvcVersionRefService;

        this.publishAppRetrofitCalls = publishAppRetrofitCalls;

        this.marketFeignClient = marketFeignClient;

        this.producer = producer;
    }

    @Override
    public PermissionVerificationResultsVO verifyPermissions(Long projectId) {
        PermissionVerificationResultsVO resultsVO = new PermissionVerificationResultsVO();
        //0.如果为SaaS平台内部发布市场应用，则跳过校验
        resultsVO.setConfigurationValid(true).setTokenValid(true).setPublishingPermissionValid(true).setUpdateSuccessFlag(false);
        if (saasPlatform) {
            LOGGER.info(":::MKT:::Published within the SaaS platform.");
        } else {
            LOGGER.info(":::MKT:::Released from the PaaS platform.");
            //1.校验权限
            resultsVO = checkPermission(resultsVO);
        }
        if (!resultsVO.getPublishingPermissionValid()) {
            return resultsVO;
        }
        //2.更新应用状态
        Boolean updateSuccessFlag = true;
        try {
            updateStatus(projectId);
        } catch (Exception e) {
            LOGGER.error(":::MKT:::An error occurred while updating the status", e);
            updateSuccessFlag = false;
        }
        return resultsVO.setUpdateSuccessFlag(updateSuccessFlag);
    }

    /**
     * 校验权限
     *
     * @param resultsVO
     * @return
     */
    private PermissionVerificationResultsVO checkPermission(PermissionVerificationResultsVO resultsVO) {
        //1.校验平台是否有配置远程连接令牌
        RemoteTokenAuthorizationVO latestToken = remoteTokenAuthorizationMapper.selectLatestToken();
        if (ObjectUtils.isEmpty(latestToken) || RemoteTokenStatus.BREAK.value().equals(latestToken.getStatus())) {
            return resultsVO.setConfigurationValid(false).setTokenValid(false).setPublishingPermissionValid(false);
        }
        //2.远程连接令牌是否过期
        RemoteTokenAuthorizationVO remoteTokenCheckResultVO = remoteTokenAuthorizationService.checkLatestToken();
        if (!RemoteTokenStatus.SUCCESS.value().equals(remoteTokenCheckResultVO.getStatus())) {
            return resultsVO.setConfigurationValid(true).setTokenValid(false).setPublishingPermissionValid(false);
        }
        //3.客户是否被禁用
        if (!publishAppRetrofitCalls.checkCustomerAvailable(latestToken.getRemoteToken()) || !publishAppRetrofitCalls.checkPublishPermissions(latestToken.getRemoteToken())) {
            return resultsVO.setConfigurationValid(true).setTokenValid(true).setPublishingPermissionValid(false);
        }
        return resultsVO;
    }

    @Override
    public void updateStatus(Long projectId) {
        //1.查询项目下需更新的版本信息(版本名称/版本关联应用编码/修复批次)
        List<ApproveStatusVO> pendingUpdateList = mktPublishApplicationMapper.getUpdateList(projectId);
        if (CollectionUtils.isEmpty(pendingUpdateList)) {
            return;
        }
        pendingUpdateList = pendingUpdateList.stream()
                .filter(p -> !(PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(p.getStatus()) && p.getLatestFixVersion().equals(0)))
                .collect(Collectors.toList());
        pendingUpdateList.forEach(p -> p.setStatus(null));
        //2.从SaaS获取最新状态
        List<ApproveStatusVO> updateList;
        if (saasPlatform) {
            ResponseEntity<List<ApproveStatusVO>> mktFeignResult = marketFeignClient.getStatus(pendingUpdateList);
            if (mktFeignResult == null) {
                throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
            } else {
                updateList = mktFeignResult.getBody();
            }
        } else {
            updateList = publishAppRetrofitCalls.getStatus(pendingUpdateList);
        }
        if (CollectionUtils.isEmpty(updateList)) {
            return;
        }
        //3.更新发布/修复结果
        updateList.stream()
                .filter(a -> PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(a.getStatus()))
                .forEach(((MktApplyAndPublishService) AopContext.currentProxy())::updatePublishOrFixStatus);
        //4.更新审批结果
        updateList.stream()
                .filter(a -> PublishAppVersionStatusEnum.REJECTED.value().equalsIgnoreCase(a.getStatus())
                        || PublishAppVersionStatusEnum.UNCONFIRMED.value().equalsIgnoreCase(a.getStatus()))
                .forEach(((MktApplyAndPublishService) AopContext.currentProxy())::updateApproveStatus);

    }


    @Override
    @Transactional
    public void updatePublishOrFixStatus(ApproveStatusVO updateVO) {
        //0.判断是否是修复发布
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = getVersionInfoByVersion(updateVO.getCode(), updateVO.getVersion());
        Boolean fixFlag = !mktPublishVersionInfoDTO.getTimesOfFixes().equals(0);
        if (!fixFlag) {
            //1.更新应用发布信息
            MktPublishApplicationDTO unreleasedDTO = checkMPAExist(mktPublishVersionInfoDTO.getPublishApplicationId());
            MktPublishApplicationDTO releasedDTO = mktPublishApplicationMapper.selectOne(new MktPublishApplicationDTO()
                    .setRefAppId(unreleasedDTO.getRefAppId()).setReleased(true));
            if (ObjectUtils.isEmpty(releasedDTO)) {
                //       1.1.不存在已发布,则更新未发布为已发布
                unreleasedDTO.setReleased(true);
                if (mktPublishApplicationMapper.updateByPrimaryKeySelective(unreleasedDTO) != 1) {
                    throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
                }
            } else {
                //       1.2.存在已发布，则删除未发布，更新已发布，更新发布版本信息的publish app id字段
                if (mktPublishApplicationMapper.deleteByPrimaryKey(unreleasedDTO.getId()) != 1) {
                    throw new CommonException(MKT_APPLICATION_DELETE_EXCEPTION);
                }
                releasedDTO.setLatestVersionId(unreleasedDTO.getLatestVersionId())
                        .setImageUrl(unreleasedDTO.getImageUrl())
                        .setDescription(unreleasedDTO.getDescription())
                        .setOverview(unreleasedDTO.getOverview())
                        .setNotificationEmail(unreleasedDTO.getNotificationEmail());
                if (mktPublishApplicationMapper.updateByPrimaryKeySelective(releasedDTO) != 1) {
                    throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
                }
                mktPublishVersionInfoDTO.setPublishApplicationId(releasedDTO.getId());
            }
            //2.更新应用版本发布信息
            mktPublishVersionInfoDTO.setPublishDate(new Date());
            mktPublishVersionInfoDTO.setStatus(updateVO.getStatus());
            mktPublishVersionInfoDTO.setPublishErrorCode(null);
            if (mktPublishVersionInfoMapper.updateByPrimaryKey(mktPublishVersionInfoDTO) != 1) {
                throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
            }
        }
        //3.更新版本下服务状态 至 完成状态
        if (!fixFlag || mktPublishVersionInfoDTO.getTimesOfFixes().equals(updateVO.getLatestFixVersion())) {
            List<ApplicationSvcVersionRefDTO> applicationSvcVersionRefDTOS = applicationSvcVersionRefMapper.select(new ApplicationSvcVersionRefDTO().setApplicationVersionId(mktPublishVersionInfoDTO.getApplicationVersionId()).setStatus(ApplicationSvcVersionStatusEnum.PROCESSING.value()));
            if (!CollectionUtils.isEmpty(applicationSvcVersionRefDTOS)) {
                applicationSvcVersionRefDTOS.stream()
                        .forEach(ref -> {
                            ref.setStatus(ApplicationSvcVersionStatusEnum.DONE.value());
                            if (applicationSvcVersionRefMapper.updateByPrimaryKeySelective(ref) != 1) {
                                throw new UpdateException(APP_SVC_VERSION_REF_UPDATE_EXCEPTION);
                            }
                        });
            }
        }
        // 发布成功时更新发布记录
        if (!fixFlag) {
            try {
                updateMktAppPublishRecordSuccess(updateVO.getCode(), updateVO.getVersion());
            } catch (Exception e) {
                LOGGER.error("执行市场应用发布记录状态(成功)更新时发生异常:", e);
            }
        }

    }

    @Override
    @Transactional
    public void updateApproveStatus(ApproveStatusVO updateVO) {
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = getVersionInfoByVersion(updateVO.getCode(), updateVO.getVersion());
        mktPublishVersionInfoDTO.setApproveMessage(updateVO.getApproveMessage());
        mktPublishVersionInfoDTO.setStatus(updateVO.getStatus());

        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(mktPublishVersionInfoDTO) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }

        MktPublishApplicationDTO mktPublishApplicationDTO = checkMPAExist(mktPublishVersionInfoDTO.getPublishApplicationId());
        mktPublishApplicationDTO.setCategoryCode(updateVO.getCategoryCode());
        mktPublishApplicationDTO.setCategoryName(updateVO.getCategoryName());

        if (mktPublishApplicationMapper.updateByPrimaryKeySelective(mktPublishApplicationDTO) != 1) {
            throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
        }
    }

    /**
     * 根据version获取版本发布信息
     *
     * @param appCode
     * @param version
     * @return
     */
    private MktPublishVersionInfoDTO getVersionInfoByVersion(String appCode, String version) {
        ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper
                .selectOne(new ApplicationVersionDTO()
                        .setVersion(version)
                        .setApplicationId(Optional.ofNullable(applicationMapper.selectOne(new ApplicationDTO().setCode(appCode)))
                                .orElseThrow(() -> new NotExistedException(APPLICATION_DOES_NOT_EXIST_EXCEPTION)).getId()));
        if (ObjectUtils.isEmpty(applicationVersionDTO)) {
            throw new NotExistedException(APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION);
        }
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktPublishVersionInfoMapper
                .selectOne(new MktPublishVersionInfoDTO().setApplicationVersionId(applicationVersionDTO.getId()));
        if (ObjectUtils.isEmpty(mktPublishVersionInfoDTO)) {
            throw new NotExistedException(MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION);
        }
        return mktPublishVersionInfoDTO;
    }

    @Override
    @Transactional
    public void apply(Long organizationId, Long mktPublishApplicationId, Long mktPublishVersionInfoId) {
        Boolean applyResult = false;
        //0. 校验市场发布版本信息处于可申请状态
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkExistAndCanApply(mktPublishVersionInfoId);
        //1. 构造申请数据
        MarketApplicationVO sendVO = constructingApplicationData(mktPublishApplicationId, mktPublishVersionInfoId);
        //2. 向Saas发起申请
        if (saasPlatform) {
            ResponseEntity<Boolean> mktFeignResult;
            if (PublishAppVersionStatusEnum.REJECTED.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus())) {
                mktFeignResult = marketFeignClient.reapply(sendVO, organizationId);
            } else {
                mktFeignResult = marketFeignClient.apply(sendVO, organizationId);
            }
            if (mktFeignResult == null) {
                throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
            } else {
                applyResult = mktFeignResult.getBody();
            }
        } else {
            applyResult = publishAppRetrofitCalls.apply(PublishAppVersionStatusEnum.REJECTED.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus()), sendVO);
        }
        if (!applyResult) {
            throw new CommonException(APPLY_EXCEPTION);
        }
        //3.申请成功则更新市场发布版本信息状态
        if (mktPublishVersionInfoMapper
                .updateByPrimaryKey(mktPublishVersionInfoDTO.setStatus(PublishAppVersionStatusEnum.UNDER_APPROVAL.value()).setApproveMessage(null)) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }
    }

    @Override
    public MarketApplicationVO constructingApplicationData(Long mktPublishApplicationId, Long mktPublishVersionInfoId) {
        MarketApplicationVO resultVO = new MarketApplicationVO();
        //0.1. 查询市场发布应用信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkMPAExist(mktPublishApplicationId);
        //0.2. 查询应用信息
        ApplicationDTO applicationDTO = checkAppExist(mktPublishApplicationDTO.getRefAppId());
        //0.3. 查询市场发布应用版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkMPVIExist(mktPublishVersionInfoId);
        //0.4. 查询应用版本信息
        ApplicationVersionDTO applicationVersionDTO = checkAppVersionExist(mktPublishVersionInfoDTO.getApplicationVersionId());

        MarketApplicationVersionVO tmpAppVersion = new MarketApplicationVersionVO();
        //1. 补充服务版本信息
        tmpAppVersion.setMarketAppServiceVOS(constructingSvcVersionInfo(applicationVersionDTO.getId()));
        //2. 补充应用版本信息
        tmpAppVersion.setMarketAppCode(applicationDTO.getCode())
                .setChangelog(mktPublishVersionInfoDTO.getChangelog())
                .setDocument(mktPublishVersionInfoDTO.getDocument())
                .setVersion(applicationVersionDTO.getVersion())
                .setVersionCreationDate(applicationVersionDTO.getCreationDate());
        //3. 补充应用信息
        BeanUtils.copyProperties(mktPublishApplicationDTO, resultVO);
        resultVO.setRemark(mktPublishVersionInfoDTO.getRemark())
                .setMarketApplicationVersionVO(tmpAppVersion)
                .setCode(applicationDTO.getCode())
                .setType(mktPublishApplicationDTO.getPublishType())
                .setNotificationEmail(mktPublishApplicationDTO.getNotificationEmail())
                .setId(null);
        //4. 返回结果
        return resultVO;
    }

    @Override
    @Transactional
    public void publish(Long organizationId, Long mktPublishApplicationId, Long mktPublishVersionInfoId) {
        //0.1. 查询市场发布应用信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkMPAExist(mktPublishApplicationId);
        //0.2. 查询应用信息
        ApplicationDTO applicationDTO = checkAppExist(mktPublishApplicationDTO.getRefAppId());
        //0.3. 查询市场发布应用版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkMPVIExist(mktPublishVersionInfoId);
        //0.4. 查询应用版本信息
        ApplicationVersionDTO applicationVersionDTO = checkAppVersionExist(mktPublishVersionInfoDTO.getApplicationVersionId());

        //1.删除市场发布版本信息的错误信息
        if (ObjectUtils.isEmpty(mktPublishVersionInfoDTO.getPublishErrorCode())
                && mktPublishVersionInfoMapper.updateByPrimaryKey(mktPublishVersionInfoDTO.setPublishErrorCode(null)) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }
        //2.更新应用版本与服务版本的 未发布状态 的 关联关系 至 发布中状态
        List<ApplicationSvcVersionRefDTO> applicationSvcVersionRefDTOS = applicationSvcVersionRefMapper
                .select(new ApplicationSvcVersionRefDTO()
                        .setApplicationVersionId(mktPublishVersionInfoDTO.getApplicationVersionId())
                        .setStatus(ApplicationSvcVersionStatusEnum.UNPUBLISHED.value()));
        if (!CollectionUtils.isEmpty(applicationSvcVersionRefDTOS)) {
            applicationSvcVersionRefDTOS.forEach(v -> {
                if (applicationSvcVersionRefMapper.updateByPrimaryKeySelective(v.setStatus(ApplicationSvcVersionStatusEnum.PROCESSING.value())) != 1) {
                    throw new UpdateException(APP_SVC_VERSION_REF_UPDATE_EXCEPTION);
                }
            });
        }
        //3.构造发送至SaaS的确认信息
        MarketApplicationVO sendVO = constructingConfirmData(mktPublishApplicationDTO, mktPublishVersionInfoDTO, applicationVersionDTO);
        //4.向SaaS确认并获取Harbor信息
        HarborUserAndUrlVO harborUserAndUrlVO;
        if (saasPlatform) {
            ResponseEntity<HarborUserAndUrlVO> mktFeignResult
                    = marketFeignClient.confirm(applicationDTO.getCode(), applicationVersionDTO.getVersion(), sendVO);
            if (mktFeignResult == null) {
                throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
            } else {
                harborUserAndUrlVO = mktFeignResult.getBody();
            }
        } else {
            harborUserAndUrlVO = publishAppRetrofitCalls.confirm(applicationDTO.getCode(), applicationVersionDTO.getVersion(), sendVO);
        }
        //5.发起发布事务
        if (ObjectUtils.isEmpty(harborUserAndUrlVO)) {
            throw new CommonException("error.publish.harbor.user.and.url.can.not.be.empty");
        }
        if (devopsMessage) {
            sendPublishEvent(applicationDTO.getProjectId(),
                    constructingPublishPayload(mktPublishApplicationDTO, mktPublishVersionInfoDTO, applicationDTO,
                            applicationVersionDTO, harborUserAndUrlVO, false));
        }
        //5.创建发布记录（或许是重试）
        try {
            createMktAppPublishRecord(applicationDTO.getCode(), applicationVersionDTO.getVersion());
        } catch (Exception e) {
            LOGGER.error("执行市场应用发布记录添加时发生异常:", e);
        }
    }

    @Override
    @Transactional
    public void fixPublish(Long organizationId, Long mktPublishApplicationId, Long mktPublishVersionInfoId, Boolean whetherToFix) {
        //0.1. 查询市场发布应用信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkMPAExist(mktPublishApplicationId);
        //0.2. 查询应用信息
        ApplicationDTO applicationDTO = checkAppExist(mktPublishApplicationDTO.getRefAppId());
        //0.3. 查询市场发布应用版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkMPVIExist(mktPublishVersionInfoId);
        //0.4. 查询应用版本信息
        ApplicationVersionDTO applicationVersionDTO = checkAppVersionExist(mktPublishVersionInfoDTO.getApplicationVersionId());

        //1.构造更新数据
        MarketApplicationVersionVO marketApplicationVersionVO
                = new MarketApplicationVersionVO()
                .setMarketAppCode(applicationDTO.getCode())
                .setVersion(applicationVersionDTO.getVersion())
                .setChangelog(mktPublishVersionInfoDTO.getChangelog())
                .setDocument(mktPublishVersionInfoDTO.getDocument());

        //2.更新已发布
        if (whetherToFix) {
            //2.1 发送修复版本事务
            //2.1.0.更新市场发布应用版本的修复批次
            if (mktPublishVersionInfoMapper.updateByPrimaryKey(mktPublishVersionInfoDTO.setPublishErrorCode(null).setTimesOfFixes(mktPublishVersionInfoDTO.getTimesOfFixes() + 1)) != 1) {
                throw new CommonException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
            }
            //2.1.1.更新应用版本下 服务版本状态 从 未发布/失败 至 发布中
            List<ApplicationSvcVersionRefDTO> applicationSvcVersionRefDTOS = applicationSvcVersionRefMapper.select(new ApplicationSvcVersionRefDTO().setApplicationVersionId(applicationVersionDTO.getId()));
            if (CollectionUtils.isEmpty(applicationSvcVersionRefDTOS)) {
                return;
            }
            Set<ApplicationSvcVersionRefDTO> toBeFixList = applicationSvcVersionRefDTOS.stream().filter(ref -> ApplicationSvcVersionStatusEnum.FAILURE.value().equalsIgnoreCase(ref.getStatus()) || ApplicationSvcVersionStatusEnum.UNPUBLISHED.value().equalsIgnoreCase(ref.getStatus())).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(toBeFixList)) {
                return;
            }
            toBeFixList.forEach(ref -> {
                if (applicationSvcVersionRefMapper.updateByPrimaryKey(ref.setStatus(ApplicationSvcVersionStatusEnum.PROCESSING.value())) != 1) {
                    throw new CommonException("error.application.svc.version.ref.update");
                }
            });
            //2.1.2添加服务版本信息
            marketApplicationVersionVO.setMarketAppServiceVOS(constructingFixSvcVersionInfo(applicationVersionDTO.getId()));
            MarketApplicationVO marketApplicationVO = new MarketApplicationVO();
            marketApplicationVO.setMarketApplicationVersionVO(marketApplicationVersionVO);
            //2.1.3.查询harbor参数
            HarborUserAndUrlVO harborUserAndUrlVO;
            if (saasPlatform) {
                ResponseEntity<HarborUserAndUrlVO> mktFeignResult = marketFeignClient.fixConfirm(applicationDTO.getCode());
                if (mktFeignResult == null) {
                    throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
                } else {
                    harborUserAndUrlVO = mktFeignResult.getBody();
                }
            } else {
                harborUserAndUrlVO = publishAppRetrofitCalls.fixConfirm(applicationDTO.getCode());
            }
            //2.1.4.发送修复事务
            sendFixVersionEvent(applicationDTO.getProjectId(), marketApplicationVO, constructingPublishPayload(mktPublishApplicationDTO, mktPublishVersionInfoDTO, applicationDTO, applicationVersionDTO, harborUserAndUrlVO, true));
        } else {
            //2.2 向SaaS发送信息更新
            if (saasPlatform) {
                ResponseEntity<Boolean> mktFeignResult = marketFeignClient.updatePublishAppVersionInfo(applicationDTO.getCode(), applicationVersionDTO.getVersion(), marketApplicationVersionVO);
                if (mktFeignResult == null) {
                    throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
                } else if (!mktFeignResult.getBody()) {
                    throw new CommonException("error.mkt.publish.version.info.update");
                }
            } else if (!publishAppRetrofitCalls.updateMktPublishVersionInfo(applicationDTO.getCode(), applicationVersionDTO.getVersion(), marketApplicationVersionVO)) {
                throw new CommonException("error.mkt.publish.version.info.update");
            }
        }
    }

    @Override
    public MarketApplicationVO constructingConfirmData(MktPublishApplicationDTO mktPublishApplicationDTO,
                                                       MktPublishVersionInfoDTO mktPublishVersionInfoDTO,
                                                       ApplicationVersionDTO applicationVersionDTO) {
        MarketApplicationVO sendVO = new MarketApplicationVO();
        sendVO.setName(mktPublishApplicationDTO.getName());
        sendVO.setOverview(mktPublishApplicationDTO.getOverview());

        MarketApplicationVersionVO tmpVersion = new MarketApplicationVersionVO();
        tmpVersion.setVersion(applicationVersionDTO.getVersion());
        tmpVersion.setChangelog(mktPublishVersionInfoDTO.getChangelog());
        tmpVersion.setDocument(mktPublishVersionInfoDTO.getDocument());

        sendVO.setMarketApplicationVersionVO(tmpVersion);
        return sendVO;
    }

    @Override
    public List<MarketAppServiceVO> constructingSvcVersionInfo(Long appVersionId) {
        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(appVersionId);
        //2.1.2.构造SaaS数据
        List<MarketAppServiceVO> tmpSvcs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(svcVersions)) {
            svcVersions.forEach(svc -> {
                //应用服务信息
                MarketAppServiceVO tmpSvc = new MarketAppServiceVO();
                BeanUtils.copyProperties(svc, tmpSvc);
                tmpSvc.setCode(svc.getId() + "_" + svc.getCode());
                tmpSvc.setId(null);
                //应用服务版本信息
                svc.getAppServiceVersions().forEach(v -> {
                    MarketServiceVersionDTO tmpVersion = new MarketServiceVersionDTO();
                    tmpVersion.setVersion(v.getVersion());
                    tmpSvc.setMarketServiceVersionCreateDTO(tmpVersion);
                });
                tmpSvcs.add(tmpSvc);
            });
        }
        return tmpSvcs;
    }

    @Override
    public List<MarketAppServiceVO> constructingFixSvcVersionInfo(Long appVersionId) {
        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(appVersionId);
        //2.1.2.构造SaaS数据
        List<MarketAppServiceVO> tmpSvcs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(svcVersions)) {
            svcVersions.forEach(svc -> {
                //应用服务信息
                MarketAppServiceVO tmpSvc = new MarketAppServiceVO();
                //应用服务版本信息
                svc.getAppServiceVersions().stream().filter(a -> !ApplicationSvcVersionStatusEnum.DONE.value().equalsIgnoreCase(a.getStatus())).forEach(v -> {
                    MarketServiceVersionDTO tmpVersion = new MarketServiceVersionDTO();
                    tmpVersion.setVersion(v.getVersion());
                    tmpSvc.setMarketServiceVersionCreateDTO(tmpVersion);
                });
                if (!ObjectUtils.isEmpty(tmpSvc.getMarketServiceVersionCreateDTO())) {
                    BeanUtils.copyProperties(svc, tmpSvc);
                    tmpSvc.setId(null);
                    tmpSvc.setCode(svc.getId() + "_" + svc.getCode());
                    tmpSvcs.add(tmpSvc);
                }
            });
        }
        return tmpSvcs;
    }

    @Override
    public AppMarketUploadPayload constructingPublishPayload(MktPublishApplicationDTO mktPublishApplicationDTO,
                                                             MktPublishVersionInfoDTO mktPublishVersionInfoDTO,
                                                             ApplicationDTO applicationDTO,
                                                             ApplicationVersionDTO applicationVersionDTO,
                                                             HarborUserAndUrlVO harborUserAndUrlVO,
                                                             Boolean fixFlag) {
        AppMarketUploadPayload payload = new AppMarketUploadPayload();
        payload.setMarketSaaSPlatform(saasPlatform);
        payload.setMktAppId(mktPublishApplicationDTO.getId());
        payload.setMktAppCode(applicationDTO.getCode());
        payload.setMktAppVersionId(mktPublishVersionInfoDTO.getId());
        payload.setProjectId(applicationDTO.getProjectId());
        if (!saasPlatform) {
            RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();
            payload.setSaasGetawayUrl(latestEnabledToken.getAuthorizationUrl());
        }
        payload.setStatus(mktPublishApplicationDTO.getPublishType());
        payload.setAppVersion(applicationVersionDTO.getVersion());
        payload.setUpdateVersion(fixFlag);
        payload.setUser(harborUserAndUrlVO.getUser());

        List<AppServiceUploadPayload> appServiceUploadPayloads = new ArrayList<>();

        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(applicationVersionDTO.getId());

        if (!CollectionUtils.isEmpty(svcVersions)) {
            svcVersions.forEach(s -> {
                if (!CollectionUtils.isEmpty(s.getAppServiceVersions())) {
                    Set<AppServiceVersionDetailsVO> versionDetailsVOS = s.getAppServiceVersions().stream().filter(v -> ApplicationSvcVersionStatusEnum.PROCESSING.value().equalsIgnoreCase(v.getStatus())).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(versionDetailsVOS)) {
                        List<AppServiceVersionUploadPayload> appServiceVersionUploadPayloadList = new ArrayList<>();

                        versionDetailsVOS.forEach(v -> appServiceVersionUploadPayloadList.add(new AppServiceVersionUploadPayload(v.getId(), v.getVersion())));

                        appServiceUploadPayloads.add(new AppServiceUploadPayload(s.getId(), s.getCode(), s.getName(),
                                harborUserAndUrlVO.getHarborUrl() + File.separator + s.getId() + "_" + s.getCode(),
                                appServiceVersionUploadPayloadList));
                    }
                }
            });
        }
        return payload.setAppServiceUploadPayloads(appServiceUploadPayloads);
    }

    @Override
    public ApplicationDTO determineGeneration(Long appId) {
        ApplicationDTO applicationDTO = checkAppExist(appId);
        if (!applicationDTO.getHasGenerated()) {
            applicationDTO.setHasGenerated(true);
            if (applicationMapper.updateByPrimaryKeySelective(applicationDTO) != 1) {
                throw new CommonException(APPLICATION_UPDATE_EXCEPTION);
            }
        }
        return checkAppExist(appId);
    }

    @Override
    public ApplicationDTO cancelGeneration(Long appId) {
        ApplicationDTO applicationDTO = checkAppExist(appId);
        if (applicationDTO.getHasGenerated()) {
            applicationDTO.setHasGenerated(false);
            if (applicationMapper.updateByPrimaryKeySelective(applicationDTO) != 1) {
                throw new UpdateException(APPLICATION_UPDATE_EXCEPTION);
            }
        }
        return checkAppExist(appId);
    }

    @Override
    public MktPublishVersionInfoDTO checkExistAndCanApply(Long mktPublishVersionInfoId) {
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktPublishVersionInfoMapper.selectByPrimaryKey(mktPublishVersionInfoId);
        if (ObjectUtils.isEmpty(mktPublishVersionInfoDTO)) {
            throw new NotExistedException(MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION);
        }
        if (!(mktPublishVersionInfoDTO.getStatus().equalsIgnoreCase(PublishAppVersionStatusEnum.UNPUBLISHED.value())
                || mktPublishVersionInfoDTO.getStatus().equalsIgnoreCase(PublishAppVersionStatusEnum.REJECTED.value())
                || mktPublishVersionInfoDTO.getStatus().equalsIgnoreCase(PublishAppVersionStatusEnum.WITHDRAWN.value()))) {
            throw new CommonException(MKT_PUBLISH_VERSION_INFO_STATUS_EXCEPTION);
        }
        return mktPublishVersionInfoDTO;
    }

    @Override
    public MktPublishApplicationDTO checkMPAExist(Long mpaId) {
        MktPublishApplicationDTO mktPublishApplicationDTO = mktPublishApplicationMapper.selectByPrimaryKey(mpaId);
        if (ObjectUtils.isEmpty(mktPublishApplicationDTO)) {
            throw new NotExistedException(MKT_APPLICATION_DOES_NOT_EXIST_EXCEPTION);
        }
        return mktPublishApplicationDTO;
    }

    @Override
    public MktPublishVersionInfoDTO checkMPVIExist(Long mpviId) {
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktPublishVersionInfoMapper.selectByPrimaryKey(mpviId);
        if (ObjectUtils.isEmpty(mktPublishVersionInfoDTO)) {
            throw new NotExistedException(MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION);
        }
        return mktPublishVersionInfoDTO;
    }

    @Override
    public ApplicationDTO checkAppExist(Long appId) {
        ApplicationDTO applicationDTO = applicationMapper.selectByPrimaryKey(appId);
        if (ObjectUtils.isEmpty(applicationDTO)) {
            throw new NotExistedException(APPLICATION_DOES_NOT_EXIST_EXCEPTION);
        }
        return applicationDTO;
    }

    @Override
    public ApplicationVersionDTO checkAppVersionExist(Long appVersionId) {
        ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(appVersionId);
        if (ObjectUtils.isEmpty(applicationVersionDTO)) {
            throw new NotExistedException(APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION);
        }
        return applicationVersionDTO;
    }

    /**
     * 发送发布事务
     */
    @Saga(code = PUBLISH_APP, description = "发布市场应用", inputSchemaClass = AppMarketUploadPayload.class)
    private void sendPublishEvent(Long projectId, AppMarketUploadPayload payload) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("project")
                        .withSagaCode(PUBLISH_APP),
                builder -> {
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(String.valueOf(projectId))
                            .withSourceId(projectId);
                    return payload;
                });
    }

    /**
     * 发送修复事务
     */
    @Saga(code = PUBLISH_APP_FIX_VERSION, description = "发布市场应用修复版本", inputSchemaClass = AppMarketFixVersionPayload.class)
    private void sendFixVersionEvent(Long projectId, MarketApplicationVO marketApplicationVO, AppMarketUploadPayload appServiceUploadPayloads) {
        //1.构造Payload
        AppMarketFixVersionPayload fixVersionPayload = new AppMarketFixVersionPayload();
        fixVersionPayload.setMarketApplicationVO(marketApplicationVO);
        fixVersionPayload.setFixVersionUploadPayload(appServiceUploadPayloads);
        //2.发送事务
        if (devopsMessage) {
            producer.applyAndReturn(
                    StartSagaBuilder
                            .newBuilder()
                            .withLevel(ResourceLevel.PROJECT)
                            .withRefType("project")
                            .withSagaCode(PUBLISH_APP_FIX_VERSION),
                    builder -> {
                        builder
                                .withPayloadAndSerialize(fixVersionPayload)
                                .withRefId(String.valueOf(projectId))
                                .withSourceId(projectId);
                        return fixVersionPayload;
                    });
        }
    }


    /**
     * 获取最新的有效的远程连接令牌
     *
     * @return 最新的启用的远程连接令牌
     */
    private RemoteTokenAuthorizationVO getLatestEnabledToken() {
        RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
        if (remoteTokenAuthorizationVO == null || !RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
            throw new NotExistedException(REMOTE_TOKEN_AUTHORIZATION_DOES_NOT_EXIST_EXCEPTION);
        }
        return remoteTokenAuthorizationVO;
    }


    /**
     * 添加市场应用发布记录
     *
     * @param applicationCode    应用编码
     * @param applicationVersion 应用版本
     */
    private void createMktAppPublishRecord(String applicationCode, String applicationVersion) {
        MktAppPublishRecordDTO publishRecordDTO = new MktAppPublishRecordDTO();
        final CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails != null) {
            publishRecordDTO.setPublishUserId(userDetails.getUserId());
            publishRecordDTO.setMktAppCode(applicationCode);
            publishRecordDTO.setMktAppVersion(applicationVersion);
            int insertResult = publishRecordMapper.insertSelective(publishRecordDTO);
            if (insertResult != 1) {
                LOGGER.error("市场应用发布记录添加失败。");
            }
        } else {
            LOGGER.error("市场应用发布记录添加操作未执行(用户信息获取失败。)");
        }
    }

    /**
     * 市场应用发布记录状态更新为成功
     *
     * @param applicationCode    应用编码
     * @param applicationVersion 应用版本
     */
    private void updateMktAppPublishRecordSuccess(String applicationCode, String applicationVersion) {
        //TODO
        MktAppPublishRecordDTO publishRecordDTO = publishRecordMapper.selectOneByCodeAndVersion(applicationCode, applicationVersion);
        if (publishRecordDTO != null) {
            publishRecordDTO.setPublishStatus(AppPublishStatus.SUCCESS.value());
            publishRecordDTO.setHandleTime(new Date());
            int updateResult = publishRecordMapper.updateByPrimaryKeySelective(publishRecordDTO);
            if (updateResult != 1) {
                LOGGER.error("市场应用发布记录状态(success)更新失败。");
            }
        } else {
            LOGGER.error("市场应用发布记录获取失败。");
        }
    }
}
