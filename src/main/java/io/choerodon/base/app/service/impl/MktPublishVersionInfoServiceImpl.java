package io.choerodon.base.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.payload.MarketAppPayload;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionDetailsVO;
import io.choerodon.base.api.vo.MktPublishAppVersionVO;
import io.choerodon.base.api.vo.MktUnPublishVersionInfoVO;
import io.choerodon.base.api.vo.MktVersionUpdateVO;
import io.choerodon.base.app.service.ApplicationService;
import io.choerodon.base.app.service.ApplicationSvcVersionRefService;
import io.choerodon.base.app.service.MktApplyAndPublishService;
import io.choerodon.base.app.service.MktPublishVersionInfoService;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.dto.MktAppPublishRecordDTO;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.dto.MktPublishVersionInfoDTO;
import io.choerodon.base.infra.enums.AppPublishStatus;
import io.choerodon.base.infra.enums.ApplicationSvcVersionStatusEnum;
import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum;
import io.choerodon.base.infra.mapper.ApplicationSvcVersionRefMapper;
import io.choerodon.base.infra.mapper.ApplicationVersionMapper;
import io.choerodon.base.infra.mapper.MktAppPublishRecordMapper;
import io.choerodon.base.infra.mapper.MktPublishApplicationMapper;
import io.choerodon.base.infra.mapper.MktPublishVersionInfoMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.choerodon.base.app.service.impl.ApplicationSvcVersionRefServiceImpl.APP_SVC_VERSION_REF_UPDATE_EXCEPTION;
import static io.choerodon.base.app.service.impl.ApplicationVersionServiceImpl.APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION;
import static io.choerodon.base.app.service.impl.MktPublishApplicationServiceImpl.MKT_APPLICATION_DOES_NOT_EXIST_EXCEPTION;
import static io.choerodon.base.app.service.impl.MktPublishApplicationServiceImpl.MKT_APPLICATION_UPDATE_EXCEPTION;
import static io.choerodon.base.app.service.impl.MktPublishApplicationServiceImpl.REVOCATION_VERSION_STATUS_INVALID_EXCEPTION;
import static io.choerodon.base.infra.utils.SagaTopic.PublishApp.PUBLISH_APP_FAIL;

/**
 * @author Eugen
 **/
@Service
public class MktPublishVersionInfoServiceImpl implements MktPublishVersionInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MktPublishVersionInfoServiceImpl.class);
    public static final String MKT_PUBLISH_VERSION_INFO_STATUS_EXCEPTION = "error.mkt.publish.version.info.status.invalid";
    public static final String MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION = "error.mkt.publish.version.info.update";
    private static final String MKT_PUBLISH_VERSION_INFO_INSERT_EXCEPTION = "error.mkt.publish.version.info.insert";
    public static final String MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION = "error.mkt.publish.version.info.does.not.exist";
    private static final String MKT_PUBLISH_VERSION_INFO_UPDATE_PUBLISHED_STATUS_EXCEPTION = "error.mkt.publish.version.info.update.published.status";

    private MktPublishVersionInfoMapper mktPublishVersionInfoMapper;
    private ApplicationVersionMapper applicationVersionMapper;
    private MktPublishApplicationMapper mktPublishApplicationMapper;
    private ApplicationSvcVersionRefService applicationSvcVersionRefService;
    private MktAppPublishRecordMapper mktAppPublishRecordMapper;
    private ApplicationService applicationService;
    private ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper;
    private MktApplyAndPublishService mktApplyAndPublishService;
    private TransactionalProducer producer;

    private static final String MKT_PUBLISH_APP_VERSION_INFO_NOT_EXIT = "error.mkt.publish.version.info.not.exit";
    private static final String APPLICATION_VERSION_NOT_EXIT = "error.application.version.not.exit";
    private static final String APPLICATION_SERVICE_NOT_EXIT = "error.application.service.not.exit";
    private static final String PUBLISH_APP_VERSION_UPDATE_EXCEPTION = "error.publish.app.version.update";
    private static final String MKT_PUBLISH_VERSION_UPDATE_STATUS_NOT_ALLOW = "error.mkt.publish.version.info.update.status.not.allow";

    public MktPublishVersionInfoServiceImpl(MktPublishVersionInfoMapper mktPublishVersionInfoMapper, ApplicationVersionMapper applicationVersionMapper, MktPublishApplicationMapper mktPublishApplicationMapper, ApplicationSvcVersionRefService applicationSvcVersionRefService, MktAppPublishRecordMapper mktAppPublishRecordMapper, ApplicationService applicationService, ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper, MktApplyAndPublishService mktApplyAndPublishService, TransactionalProducer producer) {
        this.mktPublishVersionInfoMapper = mktPublishVersionInfoMapper;
        this.applicationVersionMapper = applicationVersionMapper;
        this.mktPublishApplicationMapper = mktPublishApplicationMapper;
        this.applicationSvcVersionRefService = applicationSvcVersionRefService;
        this.mktAppPublishRecordMapper = mktAppPublishRecordMapper;
        this.applicationService = applicationService;
        this.applicationSvcVersionRefMapper = applicationSvcVersionRefMapper;
        this.mktApplyAndPublishService = mktApplyAndPublishService;
        this.producer = producer;
    }

    @Override
    public List<MktPublishAppVersionVO> listMktAppVersions(Long appId) {
        List<MktPublishAppVersionVO> mktPublishAppVersionVOS = new ArrayList<>();
        List<MktPublishVersionInfoDTO> select = new ArrayList<>();

        List<MktPublishApplicationDTO> selectAllAppDTOs = mktPublishApplicationMapper.select(new MktPublishApplicationDTO().setRefAppId(checkMktPublishAppExit(appId).getRefAppId()));

        // 查询版本信息
        if (!CollectionUtils.isEmpty(selectAllAppDTOs)) {
            selectAllAppDTOs.forEach(v -> {
                List<MktPublishVersionInfoDTO> selectTemp = mktPublishVersionInfoMapper.select(new MktPublishVersionInfoDTO().setPublishApplicationId(v.getId()));
                if (!CollectionUtils.isEmpty(selectTemp)) {
                    select.addAll(selectTemp);
                }
            });
        }
        // 查询版本服务信息
        if (!CollectionUtils.isEmpty(select)) {
            select.forEach(v -> {
                // 查询应用包含的服务 以及 服务的版本信息
                List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(v.getApplicationVersionId());
                // 查询引用版本信息
                ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(new ApplicationVersionDTO().setId(v.getApplicationVersionId()));
                // 拷贝查询信息
                MktPublishAppVersionVO mktPublishAppVersionVO = new MktPublishAppVersionVO();
                BeanUtils.copyProperties(v, mktPublishAppVersionVO);
                mktPublishAppVersionVO.setVersion(applicationVersionDTO.getVersion()).setMktAppId(v.getPublishApplicationId()).setContainServices(svcVersions);

                // 处理发布中状态：待确认是否正在发布/已发布是否在正在发布修复版本
                if (PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(v.getStatus()) || PublishAppVersionStatusEnum.UNCONFIRMED.value().equalsIgnoreCase(v.getStatus())) {
                    List<ApplicationSvcVersionRefDTO> selectSvcVersionRefs = applicationSvcVersionRefMapper.select(new ApplicationSvcVersionRefDTO().setApplicationVersionId(v.getApplicationVersionId()));
                    mktPublishAppVersionVO.setPublishing(
                            !CollectionUtils.isEmpty(selectSvcVersionRefs)
                                    && !CollectionUtils.isEmpty(
                                    selectSvcVersionRefs.stream()
                                            .filter(tmp -> ApplicationSvcVersionStatusEnum.PROCESSING.value()
                                                    .equalsIgnoreCase(tmp.getStatus()))
                                            .collect(Collectors.toSet())
                            )
                    );
                }

                mktPublishAppVersionVOS.add(mktPublishAppVersionVO);
            });
        }

        // 排序返回
        return mktPublishAppVersionVOS.stream().sorted(Comparator.comparing(MktPublishAppVersionVO::getCreationDate).reversed()).collect(Collectors.toList());
    }

    @Override
    public MktPublishVersionInfoDTO create(MktPublishVersionInfoDTO createDTO) {
        if (mktPublishVersionInfoMapper.insertSelective(createDTO) != 1) {
            throw new InsertException(MKT_PUBLISH_VERSION_INFO_INSERT_EXCEPTION);
        }
        return mktPublishVersionInfoMapper.selectByPrimaryKey(createDTO.getId());
    }

    @Override
    public MktPublishVersionInfoDTO update(MktPublishVersionInfoDTO updateDTO) {
        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(updateDTO) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }
        return mktPublishVersionInfoMapper.selectByPrimaryKey(updateDTO.getId());
    }

    @Override
    public MktPublishAppVersionVO queryMktPublishAppVersionDetail(Long versionId) {
        // 查询市场发布应用版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkMktPublishVersionInfoExit(versionId);

        // 查询市场发布应用信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkMktPublishAppExit(mktPublishVersionInfoDTO.getPublishApplicationId());

        // 查询应用版本信息
        ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(mktPublishVersionInfoDTO.getApplicationVersionId());

        if (applicationVersionDTO == null) {
            throw new CommonException(APPLICATION_VERSION_NOT_EXIT);
        }
        // 查询应用包含的服务 以及 服务的版本信息
        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(applicationVersionDTO.getId());

        if (CollectionUtils.isEmpty(svcVersions)) {
            throw new CommonException(APPLICATION_SERVICE_NOT_EXIT);
        }

        MktPublishAppVersionVO versionVO = new MktPublishAppVersionVO()
                .setMktAppId(mktPublishApplicationDTO.getId())
                .setVersion(applicationVersionDTO.getVersion())
                .setContainServices(svcVersions)
                .setReleased(false);

        BeanUtils.copyProperties(mktPublishApplicationDTO, versionVO);
        BeanUtils.copyProperties(mktPublishVersionInfoDTO, versionVO);

        // 展示页面字段展示情况判断
        if (versionVO.getStatus().equals(PublishAppVersionStatusEnum.PUBLISHED.value())) {
            versionVO.setReleased(true);
        }

        return versionVO;
    }

    @Override
    public MktPublishAppVersionVO updateUnPublished(Long organizationId, Long projectId, MktUnPublishVersionInfoVO updateVO, Boolean apply) {
        // 保存版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = ((MktPublishVersionInfoServiceImpl) AopContext.currentProxy()).updatePublishVersionInfo(updateVO);
        // 重新申请
        if (apply) {
            mktApplyAndPublishService.apply(organizationId, mktPublishVersionInfoDTO.getPublishApplicationId(), mktPublishVersionInfoDTO.getId());
        }

        return queryMktPublishAppVersionDetail(mktPublishVersionInfoDTO.getId());
    }

    @Transactional
    public MktPublishVersionInfoDTO updatePublishVersionInfo(MktUnPublishVersionInfoVO updateVO) {
        // 1.更新应用版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkMktPublishVersionInfoExit(updateVO.getId())
                .setChangelog(updateVO.getChangelog())
                .setDocument(updateVO.getDocument())
                .setRemark(updateVO.getRemark());
        mktPublishVersionInfoDTO.setObjectVersionNumber(updateVO.getObjectVersionNumber());

        if (!(mktPublishVersionInfoDTO.getStatus().equals(PublishAppVersionStatusEnum.UNPUBLISHED.value()) ||
                mktPublishVersionInfoDTO.getStatus().equals(PublishAppVersionStatusEnum.REJECTED.value()) ||
                mktPublishVersionInfoDTO.getStatus().equals(PublishAppVersionStatusEnum.WITHDRAWN.value()))) {
            throw new CommonException(MKT_PUBLISH_VERSION_UPDATE_STATUS_NOT_ALLOW);
        }

        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(mktPublishVersionInfoDTO) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }

        // 2. 更新应用信息
        MktPublishApplicationDTO updateApp = checkMktPublishAppExit(mktPublishVersionInfoDTO.getPublishApplicationId())
                .setImageUrl(updateVO.getImageUrl())
                .setDescription(updateVO.getDescription());
        // 应用信息为选填项
        updateApp.setOverview(updateVO.getOverview() == null ? updateApp.getOverview() : updateVO.getOverview());

        if (mktPublishApplicationMapper.updateByPrimaryKeySelective(updateApp) != 1) {
            throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
        }

        return mktPublishVersionInfoDTO;
    }

    @Override
    public MktPublishVersionInfoDTO checkMktPublishVersionInfoExit(Long id) {
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktPublishVersionInfoMapper.selectByPrimaryKey(id);

        if (mktPublishVersionInfoDTO == null) {
            throw new CommonException(MKT_PUBLISH_APP_VERSION_INFO_NOT_EXIT);
        }

        return mktPublishVersionInfoDTO;
    }

    @Override
    public MktPublishVersionInfoDTO checkExistByAppVersionId(Long applicationVersionId) {
        return Optional.ofNullable(mktPublishVersionInfoMapper.selectOne(new MktPublishVersionInfoDTO().setApplicationVersionId(applicationVersionId)))
                .orElseThrow(() -> new NotExistedException(MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION));
    }

    /**
     * 检查市场发布应用是否存在
     *
     * @param id 市场应用ID
     * @return
     */
    private MktPublishApplicationDTO checkMktPublishAppExit(Long id) {
        MktPublishApplicationDTO mktPublishApplicationDTO = mktPublishApplicationMapper.selectByPrimaryKey(id);

        if (mktPublishApplicationDTO == null) {
            throw new NotExistedException(MKT_APPLICATION_DOES_NOT_EXIST_EXCEPTION);
        }

        return mktPublishApplicationDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = PUBLISH_APP_FAIL, description = "应用发布/更新修复版本失败", inputSchemaClass = MarketAppPayload.class)
    public Boolean publishFail(Long id, String errorCode, Boolean fixFlag, Long projectId) {
        // 更新市场应用版本信息表的发布错误信息
        MktPublishVersionInfoDTO errorVersion = checkExistById(id);
        errorVersion.setPublishErrorCode(errorCode).setTimesOfFixes(fixFlag ? errorVersion.getTimesOfFixes() - 1 : null);
        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(errorVersion) != 1) {
            throw new UpdateException(PUBLISH_APP_VERSION_UPDATE_EXCEPTION);
        }
        // 更新服务版本发布状态
        List<ApplicationSvcVersionRefDTO> errorSvcVersions = applicationSvcVersionRefMapper.selectByVersionAndStatus(errorVersion.getApplicationVersionId(), ApplicationSvcVersionStatusEnum.PROCESSING.value());
        if (!CollectionUtils.isEmpty(errorSvcVersions)) {
            errorSvcVersions.forEach(v -> {
                v.setStatus(fixFlag ? ApplicationSvcVersionStatusEnum.FAILURE.value() : ApplicationSvcVersionStatusEnum.UNPUBLISHED.value());
                if (applicationSvcVersionRefMapper.updateByPrimaryKeySelective(v) != 1) {
                    throw new UpdateException(APP_SVC_VERSION_REF_UPDATE_EXCEPTION);
                }
            });
        }
        // 如果是发布新版本，更新发布记录状态为失败
        if (Boolean.FALSE.equals(fixFlag)) {
            try {
                ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(errorVersion.getApplicationVersionId());
                MktAppPublishRecordDTO publishRecordDTO = updateMktAppPublishRecordFail(errorVersion.getPublishApplicationId(), applicationVersionDTO.getVersion(), errorCode, projectId);
                ApplicationDTO applicationDTO = applicationService.selectByPublishAppId(errorVersion.getPublishApplicationId());
                if (publishRecordDTO != null && applicationDTO != null) {
                    sendPublishEvent(publishRecordDTO.getCreatedBy(), applicationDTO.getName(), applicationVersionDTO.getVersion(), applicationDTO.getId(), projectId, false);
                }
            } catch (Exception e) {
                logger.error("执行市场应用发布记录状态(失败)更新时发生异常:", e);
            }
        } else {
            try {
                Long createBy = applicationSvcVersionRefMapper.selectByVersionAndStatus(errorVersion.getApplicationVersionId(), ApplicationSvcVersionStatusEnum.FAILURE.value()).get(0).getLastUpdatedBy();
                logger.info("createBy" + createBy + "updateBy" + applicationSvcVersionRefMapper.selectByVersionAndStatus(errorVersion.getApplicationVersionId(), ApplicationSvcVersionStatusEnum.FAILURE.value()).get(0).getLastUpdatedBy());
                ApplicationDTO applicationDTO = applicationService.selectByPublishAppId(errorVersion.getPublishApplicationId());
                ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(errorVersion.getApplicationVersionId());
                if (createBy == null) {
                    createBy = DetailsHelper.getUserDetails().getUserId();
                    logger.info("塞的值" + createBy);
                }
                sendPublishEvent(createBy, applicationDTO.getName(), applicationVersionDTO.getVersion(), applicationDTO.getId(), projectId, true);
            } catch (Exception e) {
                logger.error("执行saga发生异常:", e);
            }
        }

        return true;
    }

    @Override
    public void delete(Long id) {
        //1.校验市场发布版本信息是否存在 且 处于 可删除状态
        mktApplyAndPublishService.checkExistAndCanApply(id);
        //2.删除
        mktPublishVersionInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public MktPublishVersionInfoDTO revocation(Long id) {
        //1.校验 市场发布版本信息状态 是否在 审批中
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkExistById(id);
        if (!PublishAppVersionStatusEnum.UNDER_APPROVAL.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus())) {
            throw new CommonException(REVOCATION_VERSION_STATUS_INVALID_EXCEPTION);
        }
        //2.更新 市场发布版本信息状态 至 已撤销
        mktPublishVersionInfoDTO.setStatus(PublishAppVersionStatusEnum.WITHDRAWN.value());
        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(mktPublishVersionInfoDTO) != 1) {
            throw new CommonException(PUBLISH_APP_VERSION_UPDATE_EXCEPTION);
        }
        return checkExistById(id);
    }


    @Override
    public MktVersionUpdateVO getBeforeUpdateAndFix(Long id) {
        MktVersionUpdateVO resultVO = new MktVersionUpdateVO().setWhetherToFix(false);
        //1.填充市场发布版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkExistById(id);
        BeanUtils.copyProperties(mktPublishVersionInfoDTO, resultVO);
        //2.填充版本名称
        resultVO.setVersion(Optional.ofNullable(applicationVersionMapper.selectByPrimaryKey(mktPublishVersionInfoDTO.getApplicationVersionId()))
                .orElseThrow(() -> new CommonException(APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION)).getVersion());
        //3.填充包含的应用服务信息
        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(mktPublishVersionInfoDTO.getApplicationVersionId());
        resultVO.setAppServiceDetailsVOS(svcVersions);
        //4.是否可更新修复版本
        svcVersions.forEach(s -> {
            if (!CollectionUtils.isEmpty(s.getAppServiceVersions())) {
                Set<String> status = s.getAppServiceVersions().stream().map(AppServiceVersionDetailsVO::getStatus).collect(Collectors.toSet());
                if (status.contains(ApplicationSvcVersionStatusEnum.UNPUBLISHED.value()) || status.contains(ApplicationSvcVersionStatusEnum.FAILURE.value())) {
                    resultVO.setWhetherToFix(true);
                    return;
                }
            }
        });
        return resultVO;
    }

    @Override
    @Transactional
    public MktVersionUpdateVO updateAndFix(Long organizationId, Long publishAppId, Long id, MktVersionUpdateVO updateVO) {
        //1.填充市场发布版本信息存在 且处于 已发布状态
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkExistById(id);
        if (!PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus())) {
            throw new CommonException(MKT_PUBLISH_VERSION_INFO_UPDATE_PUBLISHED_STATUS_EXCEPTION);
        }
        //2.修改发布版本
        if (mktPublishVersionInfoMapper.updateByPrimaryKeySelective(mktPublishVersionInfoDTO.setChangelog(updateVO.getChangelog()).setDocument(updateVO.getDocument())) != 1) {
            throw new UpdateException(MKT_PUBLISH_VERSION_INFO_UPDATE_EXCEPTION);
        }
        //3.是否修复版本
        mktApplyAndPublishService.fixPublish(organizationId, publishAppId, id, updateVO.getWhetherToFix());
        //4.返回更新后信息
        return getBeforeUpdateAndFix(id);
    }

    @Override
    @Transactional
    public MktVersionUpdateVO refix(Long publishAppId, Long id) {
        //1.填充市场发布版本信息存在 且处于 已发布状态/失败状态
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = checkExistById(id);
        if (!PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus())
                || ObjectUtils.isEmpty(mktPublishVersionInfoDTO.getPublishErrorCode())) {
            throw new CommonException(MKT_PUBLISH_VERSION_INFO_UPDATE_PUBLISHED_STATUS_EXCEPTION);
        }
        //1.重新修复
        mktApplyAndPublishService.fixPublish(null, publishAppId, id, true);
        //2.返回修复后数据
        return getBeforeUpdateAndFix(id);
    }

    private MktPublishVersionInfoDTO checkExistById(Long id) {
        return Optional.ofNullable(mktPublishVersionInfoMapper.selectByPrimaryKey(id))
                .orElseThrow(() -> new NotExistedException(MKT_PUBLISH_VERSION_INFO_NOT_EXIST_EXCEPTION));
    }

    /**
     * 市场应用发布记录状态更新为失败
     */
    private MktAppPublishRecordDTO updateMktAppPublishRecordFail(Long publishApplicationId, String version, String errorCode, Long projectId) {
        //TODO
        MktAppPublishRecordDTO publishRecordDTO = null;
        ApplicationDTO applicationDTO = applicationService.selectByPublishAppId(publishApplicationId);
        if (applicationDTO != null) {
            publishRecordDTO = mktAppPublishRecordMapper.selectOneByCodeAndVersion(applicationDTO.getCode(), version);
        } else {
            logger.error("未发布应用信息获取失败");
        }
        if (publishRecordDTO != null) {
            publishRecordDTO.setPublishStatus(AppPublishStatus.FAILURE.value());
            publishRecordDTO.setHandleTime(new Date());
            publishRecordDTO.setPublishErrorCode(errorCode);
            int updateResult = mktAppPublishRecordMapper.updateByPrimaryKeySelective(publishRecordDTO);
            if (updateResult != 1) {
                logger.error("市场应用发布记录状态(failure)更新失败。");
            }

        } else {
            logger.error("市场应用发布记录获取失败。");
        }
        return publishRecordDTO;
    }

    private void sendPublishEvent(Long createdBy, String name, String version, Long appId, Long projectId, Boolean fixFlag) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("project")
                        .withSagaCode(PUBLISH_APP_FAIL),
                builder -> {
                    MarketAppPayload payload = new MarketAppPayload();
                    payload.setAppName(name);
                    payload.setVersion(version);
                    payload.setId(appId.toString());
                    payload.setActionId(createdBy);
                    payload.setFixFlag(fixFlag);
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(String.valueOf(projectId))
                            .withSourceId(projectId);
                    return payload;
                });
    }
}
