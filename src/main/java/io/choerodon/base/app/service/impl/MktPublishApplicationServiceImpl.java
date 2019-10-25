package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.*;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.enums.AppVersionStatusEnum;
import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum;
import io.choerodon.base.infra.enums.PublishTypeEnum;
import io.choerodon.base.infra.feign.FileFeignClient;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.ApplicationMapper;
import io.choerodon.base.infra.mapper.MktPublishApplicationMapper;
import io.choerodon.base.infra.mapper.MktPublishVersionInfoMapper;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.retrofit.PublishAppRetrofitCalls;
import io.choerodon.base.infra.utils.RegularExpression;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.web.util.PageableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.base.app.service.impl.ApplicationServiceImpl.APPLICATION_UPDATE_EXCEPTION;
import static io.choerodon.base.app.service.impl.MktPublishVersionInfoServiceImpl.MKT_PUBLISH_VERSION_INFO_STATUS_EXCEPTION;
import static io.choerodon.base.app.service.impl.OrganizationServiceImpl.ORGANIZATION_DOES_NOT_EXIST_EXCEPTION;

/**
 * @author Eugen
 **/
@Service
public class MktPublishApplicationServiceImpl implements MktPublishApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MktPublishApplicationService.class);


    private static final String FEIGN_EXECUTE_FAIL_EXCEPTION = "error.feign.execute.unsuccessful";
    private static final String PUBLISH_APP_PROJECT_ID_NOT_EQUALED_EXCEPTION = "error.publish.app.project.id.not.equaled";
    private static final String CURRENT_USER_NOT_FOUND_EXCEPTION = "error.current.user.not.found";
    public static final String APPLY_EXCEPTION = "error.mkt.publish.application.apply";
    private static final String PUBLISH_EXCEPTION = "error.mkt.publish.application.publish";
    private static final String REVOCATION_EXCEPTION = "error.mkt.publish.application.revocation";
    private static final String REPUBLISH_VERSION_STATUS_INVALID_EXCEPTION = "error.mkt.publish.application.republish.version.status.invalid";
    public static final String REVOCATION_VERSION_STATUS_INVALID_EXCEPTION = "error.mkt.publish.application.revocation.version.status.invalid";
    public static final String MARKET_SERVICE_PUBLISH_APP_UPDATE_FAIL = "error.market.service.published.app.update.fail";


    private static final String MKT_PUBLISH_APP_RELEASE_NOT_EQUALED_EXCEPTION = "error.mkt.publish.application.release.not.equaled";
    private static final String MKT_APPLICATION_NAME_ALREADY_EXISTS_EXCEPTION = "error.mkt.publish.application.name.already.exists";
    private static final String MKT_APPLICATION_PUBLISH_TYPE_INVALID_EXCEPTION = "error.mkt.publish.application.publish.type.invalid";
    private static final String MKT_APPLICATION_CREATE_VERSION_NULL_EXCEPTION = "error.mkt.publish.application.create.version.can.not.be.null";
    private static final String MKT_APPLICATION_LATEST_VERSION_ID_NULL_EXCEPTION = "error.mkt.publish.application.latest.version.id.can.not.be.null";
    private static final String MKT_APPLICATION_INSERT_EXCEPTION = "error.mkt.publish.application.insert";
    public static final String MKT_APPLICATION_UPDATE_EXCEPTION = "error.mkt.publish.application.update";
    public static final String MKT_APPLICATION_DELETE_EXCEPTION = "error.mkt.publish.application.delete";
    private static final String MKT_APPLICATION_DELETE_RELEASED_EXCEPTION = "error.mkt.publish.application.delete.released";

    private static final String MKT_APPLICATION_ALREADY_EXIST_EXCEPTION = "error.mkt.publish.application.already.exist";
    private static final String MKT_APPLICATION_ALREADY_RELEASED_EXCEPTION = "error.mkt.publish.application.already.released";
    private static final String MKT_APPLICATION_NEW_VERSION_ALREADY_EXIST_EXCEPTION = "error.mkt.publish.application.new.version.already.exist";
    public static final String MKT_APPLICATION_DOES_NOT_EXIST_EXCEPTION = "error.mkt.publish.application.does.not.exist";
    private static final String CUT_IMAGE_EXCEPTION = "error.cut.image";

    @Value("${choerodon.market.saas.platform:false}")
    private boolean saasPlatform;

    private ApplicationSvcVersionRefService applicationSvcVersionRefService;
    private MktPublishVersionInfoService mktPublishVersionInfoService;
    private ApplicationVersionService applicationVersionService;
    private MktApplyAndPublishService mktApplyAndPublishService;
    private MktPublishApplicationMapper mktPublishApplicationMapper;
    private OrganizationMapper organizationMapper;
    private ApplicationMapper applicationMapper;
    private MktPublishVersionInfoMapper mktPublishVersionInfoMapper;
    private PublishAppRetrofitCalls publishAppRetrofitCalls;
    private MarketFeignClient marketFeignClient;
    private FileFeignClient fileFeignClient;

    public MktPublishApplicationServiceImpl(ApplicationSvcVersionRefService applicationSvcVersionRefService, MktPublishVersionInfoService mktPublishVersionInfoService, ApplicationVersionService applicationVersionService, MktApplyAndPublishService mktApplyAndPublishService, MktPublishApplicationMapper mktPublishApplicationMapper, OrganizationMapper organizationMapper, ApplicationMapper applicationMapper, PublishAppRetrofitCalls publishAppRetrofitCalls, MarketFeignClient marketFeignClient, FileFeignClient fileFeignClient, MktPublishVersionInfoMapper mktPublishVersionInfoMapper) {
        this.applicationSvcVersionRefService = applicationSvcVersionRefService;
        this.mktPublishVersionInfoService = mktPublishVersionInfoService;
        this.applicationVersionService = applicationVersionService;
        this.mktApplyAndPublishService = mktApplyAndPublishService;
        this.mktPublishApplicationMapper = mktPublishApplicationMapper;
        this.organizationMapper = organizationMapper;
        this.applicationMapper = applicationMapper;
        this.publishAppRetrofitCalls = publishAppRetrofitCalls;
        this.marketFeignClient = marketFeignClient;
        this.fileFeignClient = fileFeignClient;
        this.mktPublishVersionInfoMapper = mktPublishVersionInfoMapper;
    }

    @Override
    public PageInfo<PublishAppPageVO> pageSearchPublishApps(Long projectId, Pageable pageable, MarketPublishApplicationVO filterDTO, String version, String status, String[] params) {
        List<PublishAppPageVO> publishAppPageVOList = new ArrayList<>();
        // 项目下没有应用直接返回
        if (mktPublishApplicationMapper.countProjectApps(projectId) == 0) {
            return new PageInfo<>();
        }
        // 1.查询项目下有哪些关联应用
        List<ApplicationDTO> selectAppDTOs = applicationMapper.select(new ApplicationDTO().setProjectId(projectId));
        // 2.对每个关联应用的发布应用信息查询
        // 3.根据IS_RELEASED判断最新版本以及应用信息是否可编辑检验
        Map<Long, Long> idRefs = new HashMap<>();
        selectAppDTOs.forEach(v -> {
            PublishAppPageVO publishAppPageVO = new PublishAppPageVO().setAppEditable(false).setEditableByStatus(false).setEditReleased(false);
            List<MktPublishApplicationDTO> selectMktAppDTOs = mktPublishApplicationMapper.select(new MktPublishApplicationDTO().setRefAppId(v.getId()));
            if (!CollectionUtils.isEmpty(selectMktAppDTOs) && (selectMktAppDTOs.size() > 1 || (selectMktAppDTOs.size() == 1 && selectMktAppDTOs.get(0).getReleased()))) {
                publishAppPageVO.setAppEditable(true).setEditReleased(true);
            }
            if (!CollectionUtils.isEmpty(selectMktAppDTOs) && selectMktAppDTOs.size() == 1) {
                MktPublishApplicationDTO mktPublishApplicationDTO = selectMktAppDTOs.get(0);
                publishAppPageVO.setId(mktPublishApplicationDTO.getId());
                publishAppPageVO.setName(mktPublishApplicationDTO.getName());
                publishAppPageVO.setDescription(mktPublishApplicationDTO.getDescription());
                if (!mktPublishApplicationDTO.getReleased()) {
                    publishAppPageVO.setEditableByStatus(true);
                }
            }
            if (!CollectionUtils.isEmpty(selectMktAppDTOs) && selectMktAppDTOs.size() == 2) {
                IdRef idRefv = new IdRef();
                selectMktAppDTOs.forEach(va -> {
                    if (!va.getReleased()) {
                        idRefv.setNotReleasedId(va.getId());
                        publishAppPageVO.setId(va.getId());
                    } else {
                        idRefv.setReleasedId(va.getId());
                        publishAppPageVO.setName(va.getName());
                        publishAppPageVO.setDescription(va.getDescription());
                    }
                });
                idRefs.put(idRefv.getReleasedId(), idRefv.getNotReleasedId());
            }
            if (!CollectionUtils.isEmpty(selectMktAppDTOs)) {
                publishAppPageVOList.add(publishAppPageVO);
            }
        });

        // 4.根据最新版本查询需展示应用信息
        Set<Long> appIds = publishAppPageVOList.stream().map(PublishAppPageVO::getId).collect(Collectors.toSet());

        // 查询应用信息
        PageInfo<PublishAppPageVO> pageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(
                () -> mktPublishApplicationMapper.pageSearchPublishApps(appIds, filterDTO, version, status, params));

        // 查询结果比对
        List<PublishAppPageVO> finalList = new ArrayList<>();
        pageInfo.getList().forEach(v -> {
            publishAppPageVOList.forEach(va -> {
                if (v.getId().equals(va.getId())) {
                    v.setAppEditable(va.getAppEditable());
                    v.setName(va.getName());
                    v.setDescription(va.getDescription());
                    v.setEditReleased(va.getEditReleased());
                    v.setEditableByStatus(va.getEditableByStatus());
                }
            });
            // ID展示为对应数据信息
            idRefs.entrySet().forEach(var -> {
                if (var.getValue().equals(v.getId())) {
                    v.setId(var.getKey());
                }
            });
            // 转换状态后返回
            finalList.add(mktPublishAppStatusConvert(v));
        });

        // 分页结果赋值
        pageInfo.setList(finalList);

        return pageInfo;
    }

    @Override
    public MarketPublishApplicationVO queryMktPublishAppDetail(Long projectId, Long appId) {
        MarketPublishApplicationVO marketApplicationVO = new MarketPublishApplicationVO().setAppNameEditable(false).setRemarkVisitable(true);
        // 查询市场发布应用信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(appId);

        if (!mktPublishApplicationDTO.getReleased()) {
            marketApplicationVO.setAppNameEditable(true);
        }
        // 查询应用信息
        ApplicationDTO applicationDTO = mktApplyAndPublishService.checkAppExist(mktPublishApplicationDTO.getRefAppId());

        // 辨别应用是否是给定组织下的应用
        if (!applicationDTO.getProjectId().equals(projectId)) {
            throw new CommonException(PUBLISH_APP_PROJECT_ID_NOT_EQUALED_EXCEPTION);
        }

        BeanUtils.copyProperties(mktPublishApplicationDTO, marketApplicationVO);

        marketApplicationVO.setCategoryDefault(!ObjectUtils.isEmpty(mktPublishApplicationDTO.getCategoryCode()));

        marketApplicationVO.setSourceApplicationName(applicationDTO.getName());

        // 若应用为本地应用 查询该应用发布版本信息根据版本状态判断备注字段是否显示
        if (marketApplicationVO.getReleased()) {
            marketApplicationVO.setRemarkVisitable(false);
        } else {
            List<MktPublishVersionInfoDTO> versions = mktPublishVersionInfoMapper.select(new MktPublishVersionInfoDTO().setPublishApplicationId(marketApplicationVO.getId()));
            if (!CollectionUtils.isEmpty(versions)) {
                if (!versions.get(0).getStatus().equals(PublishAppVersionStatusEnum.UNCONFIRMED.value())) {
                    marketApplicationVO.setRemarkVisitable(false);
                }
            }
        }

        return marketApplicationVO;
    }

    @Override
    @Transactional
    public MktPublishApplicationDTO updateMktPublishAppInfo(Long appId, MktPublishApplicationDTO updateVO, Boolean released) {
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(appId);

        if (!released.equals(mktPublishApplicationDTO.getReleased())) {
            throw new CommonException(MKT_PUBLISH_APP_RELEASE_NOT_EQUALED_EXCEPTION);
        }

        // 拷贝更新应用信息
        if (!released) {
            mktPublishApplicationDTO.setName(updateVO.getName()).setNotificationEmail(updateVO.getNotificationEmail()).setPublishType(updateVO.getPublishType())
                    .setFree(updateVO.getFree()).setCategoryName(updateVO.getCategoryName()).setCategoryCode(updateVO.getCategoryCode());
        }

        // 修改未发布应用信息overview为选填项
        mktPublishApplicationDTO.setOverview(updateVO.getOverview() == null ? mktPublishApplicationDTO.getOverview() : updateVO.getOverview());
        mktPublishApplicationDTO.setImageUrl(updateVO.getImageUrl()).setDescription(updateVO.getDescription())
                .setObjectVersionNumber(updateVO.getObjectVersionNumber());
        mktPublishApplicationDTO.setId(appId);
        // 更新数据
        if (mktPublishApplicationMapper.updateByPrimaryKey(mktPublishApplicationDTO) != 1) {
            throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
        }

        if (released) {
            ApplicationDTO applicationDTO = mktApplyAndPublishService.checkAppExist(mktPublishApplicationDTO.getRefAppId());

            MarketApplicationVO marketApplicationVO = new MarketApplicationVO();
            BeanUtils.copyProperties(mktPublishApplicationDTO, marketApplicationVO);
            marketApplicationVO.setType(mktPublishApplicationDTO.getPublishType());
            marketApplicationVO.setCode(applicationDTO.getCode());

            Boolean result;
            if (saasPlatform) {
                // 捕获feign调用不进fallback异常
                try {
                    result = marketFeignClient.updateAppPublishInfoDetails(applicationDTO.getCode(), marketApplicationVO).getBody();
                } catch (Exception e) {
                    throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION, e);
                }

                // 进fallback
                if (result == null) {
                    throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
                }
            } else {
                result = publishAppRetrofitCalls.updateMarketPublishAppInfo(applicationDTO.getCode(), marketApplicationVO);
            }

            // market更新结果
            if (!result) {
                throw new CommonException(MARKET_SERVICE_PUBLISH_APP_UPDATE_FAIL);
            }
        }

        return mktPublishApplicationDTO;
    }

    @Override
    public Boolean checkName(Long id, String name) {
        //0.获取 关联应用主键 与 应用编码
        Long refAppId = ObjectUtils.isEmpty(id) ? null : checkExist(id).getRefAppId();
        String code = ObjectUtils.isEmpty(refAppId) ? null : mktApplyAndPublishService.checkAppExist(refAppId).getCode();
        //1.校验本平台
        if (!CollectionUtils.isEmpty(mktPublishApplicationMapper.checkName(name, refAppId))) {
            return false;
        }
        //2.校验公开应用市场
        if (saasPlatform) {
            ResponseEntity<Boolean> availableEntity = marketFeignClient.checkMktAppName(name, code);
            if (ObjectUtils.isEmpty(availableEntity) || ObjectUtils.isEmpty(availableEntity.getBody())) {
                throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
            } else {
                return availableEntity.getBody();
            }
        } else {
            return publishAppRetrofitCalls.checkName(name, code);
        }
    }


    @Override
    public MktPublishApplicationDTO getInitialInfo(Long organizationId) {
        //1.查询组织
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(organizationId);
        if (ObjectUtils.isEmpty(organizationDTO)) {
            throw new CommonException(ORGANIZATION_DOES_NOT_EXIST_EXCEPTION);
        }
        //2.查询当前用户
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (ObjectUtils.isEmpty(userDetails)) {
            throw new CommonException(CURRENT_USER_NOT_FOUND_EXCEPTION);
        }
        //3.构造返回数据
        return new MktPublishApplicationDTO().setNotificationEmail(userDetails.getEmail()).setContributor(organizationDTO.getName());

    }

    @Override
    public MktPublishApplicationDTO createAndApply(Long organizationId, Boolean apply, MktPublishApplicationVO createVO) {
        MktPublishApplicationDTO mktPublishApplicationDTO = null;
        // 1.创建
        mktPublishApplicationDTO = ((MktPublishApplicationService) AopContext.currentProxy()).create(createVO);

        // 2. 申请
        if (apply && !((MktPublishApplicationService) AopContext.currentProxy()).oneClickApply(organizationId, mktPublishApplicationDTO.getId(),
                mktPublishVersionInfoService.checkExistByAppVersionId(mktPublishApplicationDTO.getLatestVersionId()).getId())) {
            throw new CommonException(APPLY_EXCEPTION);
        }
        return mktPublishApplicationDTO;
    }

    @Override
    @Transactional
    public MktPublishApplicationDTO create(MktPublishApplicationVO createVO) {
        // 0.1. 校验关联应用是否存在
        mktApplyAndPublishService.checkAppExist(createVO.getRefAppId());
        // 0.2. 校验名称是否重复
        if (!checkName(null, createVO.getName())) {
            throw new AlreadyExistedException(MKT_APPLICATION_NAME_ALREADY_EXISTS_EXCEPTION);
        }
        // 0.3. 校验发布类型是否合法
        if (!PublishTypeEnum.isInclude(createVO.getPublishType())) {
            throw new CommonException(MKT_APPLICATION_PUBLISH_TYPE_INVALID_EXCEPTION);
        }

        // 1. 创建应用版本信息
        if (!createVO.getWhetherToCreate() && ObjectUtils.isEmpty(createVO.getLatestVersionId())) {
            mktApplyAndPublishService.checkAppVersionExist(createVO.getLatestVersionId());
            throw new CommonException(MKT_APPLICATION_LATEST_VERSION_ID_NULL_EXCEPTION);
        } else if (createVO.getWhetherToCreate()) {
            if (ObjectUtils.isEmpty(createVO.getCreateVersion())) {
                throw new CommonException(MKT_APPLICATION_CREATE_VERSION_NULL_EXCEPTION);
            }
            ApplicationVersionDTO applicationVersionDTO = applicationVersionService.quickCreate(createVO.getRefAppId(), createVO.getCreateVersion());
            createVO.setLatestVersionId(applicationVersionDTO.getId());
        }
        // 2. 创建市场发布信息
        try {
            if (mktPublishApplicationMapper.insertSelective(createVO.setReleased(false)) != 1) {
                throw new InsertException(MKT_APPLICATION_INSERT_EXCEPTION);
            }
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistedException(MKT_APPLICATION_ALREADY_EXIST_EXCEPTION, e);
        }
        // 3. 创建市场发布版本信息
        mktPublishVersionInfoService.create(new MktPublishVersionInfoDTO().setApplicationVersionId(createVO.getLatestVersionId()).setPublishApplicationId(createVO.getId()).setRemark(createVO.getRemark()));
        // 4. 更改应用发布状态
        mktApplyAndPublishService.determineGeneration(createVO.getRefAppId());
        // 5.返回创建结果
        return mktPublishApplicationMapper.selectByPrimaryKey(createVO.getId());
    }

    @Override
    public Boolean oneClickApply(Long organizationId, Long id, Long versionInfoId) {
        //0.1. 校验市场发布信息存在 且 未完成发布
        if (checkExist(id).getReleased()) {
            throw new CommonException(MKT_APPLICATION_ALREADY_RELEASED_EXCEPTION);
        }
        //0.2. 校验市场发布版本信息存在 且 处于未发布状态
        if (!PublishAppVersionStatusEnum.UNPUBLISHED.value()
                .equalsIgnoreCase(mktApplyAndPublishService.checkMPVIExist(versionInfoId).getStatus())) {
            throw new CommonException(MKT_PUBLISH_VERSION_INFO_STATUS_EXCEPTION);
        }
        //1.申请
        try {
            mktApplyAndPublishService.apply(organizationId, id, versionInfoId);
        } catch (Exception e) {
            LOGGER.error("MKT:::An error occurred while applying the application", e);
            return false;
        }
        return true;
    }


    @Override
    public PageInfo<AppCategoryDTO> getEnableCategoryList(Pageable pageable) {
        if (saasPlatform) {
            ResponseEntity<PageInfo<AppCategoryDTO>> result = marketFeignClient.getEnableCategoryList(pageable.getPageNumber(), pageable.getPageSize());
            if (result == null) {
                throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
            } else {
                return result.getBody();
            }
        } else {
            return publishAppRetrofitCalls.getEnableCategoryList(pageable.getPageNumber(), pageable.getPageSize());
        }
    }

    @Override
    public Boolean categoriesCheck(String categoryName) {
        List<AppCategoryDTO> categoryDTOS = null;

        if (!categoryName.matches(RegularExpression.CHINESE_AND_ALPHANUMERIC_AND_SPACE_30)) {
            return false;
        } else {
            categoryDTOS = getEnableCategoryList(PageRequest.of(1, 0)).getList();
            if (!CollectionUtils.isEmpty(categoryDTOS)) {
                categoryDTOS = categoryDTOS.stream().filter(vo -> vo.getName().equals(categoryName)).collect(Collectors.toList());
            }
        }

        return CollectionUtils.isEmpty(categoryDTOS);
    }

    @Override
    public MktNewVersionVO getBeforeNewVersion(Long id) {
        //1. 获取市场发布信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(id);
        //2. 获取市场发布版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktPublishVersionInfoService.checkExistByAppVersionId(mktPublishApplicationDTO.getLatestVersionId());
        //3. 构造返回参数
        MktNewVersionVO mktNewVersionVO = new MktNewVersionVO();
        BeanUtils.copyProperties(mktPublishApplicationDTO, mktNewVersionVO);
        mktNewVersionVO.setDocument(mktPublishVersionInfoDTO.getDocument());
        mktNewVersionVO.setChangelog(mktPublishVersionInfoDTO.getChangelog());
        mktNewVersionVO.setRemark(mktPublishVersionInfoDTO.getRemark());
        return mktNewVersionVO;
    }

    @Override
    public MktNewVersionVO createNewVersionAndApply(Long organizationId, Long id, Boolean apply, MktNewVersionVO newVersionVO) {
        MktNewVersionVO mktNewVersionVO = null;
        // 1.创建
        mktNewVersionVO = ((MktPublishApplicationService) AopContext.currentProxy()).createNewVersion(id, newVersionVO);
        // 2. 申请
        if (apply && !((MktPublishApplicationService) AopContext.currentProxy()).oneClickApply(organizationId, mktNewVersionVO.getId(),
                mktPublishVersionInfoService.checkExistByAppVersionId(mktNewVersionVO.getLatestVersionId()).getId())) {
            throw new CommonException(APPLY_EXCEPTION);
        }
        return mktNewVersionVO;
    }

    @Override
    @Transactional
    public MktNewVersionVO createNewVersion(Long id, MktNewVersionVO newVersionVO) {
        //1. 校验原市场发布信息存在 且 可发布新版本
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(id);
        //2. 创建应用版本
        if (!newVersionVO.getWhetherToCreate() && ObjectUtils.isEmpty(newVersionVO.getLatestVersionId())) {
            throw new CommonException(MKT_APPLICATION_LATEST_VERSION_ID_NULL_EXCEPTION);
        } else if (newVersionVO.getWhetherToCreate()) {
            if (ObjectUtils.isEmpty(newVersionVO.getCreateVersion())
                    || ObjectUtils.isEmpty(newVersionVO.getCreateVersion().getServiceVersionIds())
                    || ObjectUtils.isEmpty(newVersionVO.getCreateVersion().getVersion())) {
                throw new CommonException(MKT_APPLICATION_CREATE_VERSION_NULL_EXCEPTION);
            }
            ApplicationVersionDTO applicationVersionDTO = applicationVersionService.quickCreate(mktPublishApplicationDTO.getRefAppId(), newVersionVO.getCreateVersion());
            newVersionVO.setLatestVersionId(applicationVersionDTO.getId());
        }
        //3. 创建新市场发布信息
        mktPublishApplicationDTO.setId(null)
                .setLatestVersionId(newVersionVO.getLatestVersionId())
                .setReleased(false)
                .setNotificationEmail(newVersionVO.getNotificationEmail());
        try {
            if (mktPublishApplicationMapper.insertSelective(mktPublishApplicationDTO) != 1) {
                throw new InsertException(MKT_APPLICATION_INSERT_EXCEPTION);
            }
        } catch (DuplicateKeyException e) {
            throw new CommonException(MKT_APPLICATION_NEW_VERSION_ALREADY_EXIST_EXCEPTION, e);
        }

        //4. 创建新市场发布版本信息
        mktPublishVersionInfoService.create(new MktPublishVersionInfoDTO()
                .setPublishApplicationId(mktPublishApplicationDTO.getId())
                .setApplicationVersionId(mktPublishApplicationDTO.getLatestVersionId())
                .setDocument(newVersionVO.getDocument())
                .setChangelog(newVersionVO.getChangelog())
                .setRemark(newVersionVO.getRemark()));
        //5. 返回数据
        return newVersionVO.setWhetherToCreate(null)
                .setCreateVersion(null)
                .setId(mktPublishApplicationDTO.getId());
    }

    /*
        校验市场发布信息是否存在
         */
    private MktPublishApplicationDTO checkExist(Long id) {
        MktPublishApplicationDTO mktPublishApplicationDTO = mktPublishApplicationMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(mktPublishApplicationDTO)) {
            throw new NotExistedException(MKT_APPLICATION_DOES_NOT_EXIST_EXCEPTION);
        }
        return mktPublishApplicationDTO;
    }

    @Override
    public List<MktPublishApplicationDTO> listApplicationInfoByIds(Set<Long> appIds) {
        List<MktPublishApplicationDTO> mktPublishApplicationDTOS = new ArrayList<>();
        appIds.forEach(v -> {
            MktPublishApplicationDTO mktPublishApplicationDTO = mktPublishApplicationMapper.selectByPrimaryKey(v);
            if (mktPublishApplicationDTO != null) {
                mktPublishApplicationDTOS.add(mktPublishApplicationDTO);
            }
        });

        return mktPublishApplicationDTOS;
    }

    private PublishAppPageVO mktPublishAppStatusConvert(PublishAppPageVO v) {
        if (v.getEditableByStatus() == null) {
            v.setEditableByStatus(false);
        }
        if (v.getStatus().equals(PublishAppVersionStatusEnum.UNPUBLISHED.value()) || v.getStatus().equals(PublishAppVersionStatusEnum.REJECTED.value()) || v.getStatus().equals(PublishAppVersionStatusEnum.WITHDRAWN.value())) {
            v.setStatus(AppVersionStatusEnum.UNPUBLISHED.value());
            if (v.getEditableByStatus()) {
                v.setAppEditable(true);
            }
        }
        if (v.getStatus().equals(PublishAppVersionStatusEnum.UNCONFIRMED.value()) || v.getStatus().equals(PublishAppVersionStatusEnum.UNDER_APPROVAL.value())) {
            v.setStatus(AppVersionStatusEnum.PUBLISHING.value());
            if (v.getEditableByStatus()) {
                v.setAppEditable(false);
            }
        }
        if (v.getStatus().equals(PublishAppVersionStatusEnum.PUBLISHED.value())) {
            v.setStatus(AppVersionStatusEnum.PUBLISHED.value());
            if (v.getEditableByStatus()) {
                v.setAppEditable(true);
            }
        }

        return v;
    }


    @Override
    public MktConfirmVO getBeforeConfirm(Long id, Long versionInfoId) {
        MktConfirmVO resultVO = new MktConfirmVO();
        //1.市场发布信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(id);
        BeanUtils.copyProperties(mktPublishApplicationDTO, resultVO);
        //2.市场发布版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktApplyAndPublishService.checkMPVIExist(versionInfoId);
        resultVO.setChangelog(mktPublishVersionInfoDTO.getChangelog());
        resultVO.setDocument(mktPublishVersionInfoDTO.getDocument());
        //3.校验应用版本存在
        ApplicationVersionDTO applicationVersionDTO = mktApplyAndPublishService.checkAppVersionExist(mktPublishVersionInfoDTO.getApplicationVersionId());
        resultVO.setVersion(applicationVersionDTO.getVersion());
        //4.获取版本下的服务版本信息
        resultVO.setAppServiceDetailsVOS(applicationSvcVersionRefService.getSvcVersions(applicationVersionDTO.getId()));
        //5.构造返回参数
        return resultVO;
    }

    @Override
    public MktConfirmVO confirmAndPublish(Long organizationId, Long id, Long versionInfoId, Boolean publish, MktConfirmVO confirmVO) {
        MktConfirmVO mktConfirmVO = null;
        // 1. 确认信息
        mktConfirmVO = ((MktPublishApplicationService) AopContext.currentProxy()).confirm(id, versionInfoId, confirmVO);
        // 2. 发布
        if (publish) {
            try {
                mktApplyAndPublishService.publish(organizationId, id, versionInfoId);
            } catch (Exception e) {
                throw new CommonException(PUBLISH_EXCEPTION, e);
            }
        }
        return mktConfirmVO;
    }

    @Override
    @Transactional
    public MktConfirmVO confirm(Long id, Long versionInfoId, MktConfirmVO confirmVO) {
        //1.更新市场发布信息
        if (mktPublishApplicationMapper.updateByPrimaryKeySelective(
                checkExist(id).setOverview(confirmVO.getOverview()).setDescription(confirmVO.getDescription())) != 1) {
            throw new UpdateException(MKT_APPLICATION_UPDATE_EXCEPTION);
        }
        //2.更新市场发布版本信息
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktApplyAndPublishService.checkMPVIExist(versionInfoId);
        mktPublishVersionInfoDTO.setDocument(confirmVO.getDocument());
        mktPublishVersionInfoDTO.setChangelog(confirmVO.getChangelog());
        mktPublishVersionInfoService.update(mktPublishVersionInfoDTO);
        //3.返回更新数据
        return getBeforeConfirm(id, versionInfoId);
    }


    @Override
    public Boolean republish(Long organizationId, Long id, Long versionInfoId) {
        //0.1. 校验市场发布信息存在 且 未完成发布
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(id);
        if (mktPublishApplicationDTO.getReleased()) {
            throw new CommonException(MKT_APPLICATION_ALREADY_RELEASED_EXCEPTION);
        }
        //0.2. 校验市场发布版本信息存在 且 处于待确认的失败状态
        MktPublishVersionInfoDTO mktPublishVersionInfoDTO = mktApplyAndPublishService.checkMPVIExist(versionInfoId);
        if (!PublishAppVersionStatusEnum.UNCONFIRMED.value().equalsIgnoreCase(mktPublishVersionInfoDTO.getStatus())
                || ObjectUtils.isEmpty(mktPublishVersionInfoDTO.getPublishErrorCode())) {
            throw new CommonException(REPUBLISH_VERSION_STATUS_INVALID_EXCEPTION);
        }
        //1.发布
        try {
            mktApplyAndPublishService.publish(organizationId, id, versionInfoId);
        } catch (Exception e) {
            LOGGER.error("MKT:::An error occurred while applying the application", e);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean revocation(Long id, Long versionInfoId) {
        Boolean revocationResult;
        //1.更新市场发布版本信息 至 已撤销 状态
        MktPublishVersionInfoDTO revocation = mktPublishVersionInfoService.revocation(versionInfoId);
        //2.获取应用与版本信息
        ApplicationVersionDTO applicationVersionDTO = mktApplyAndPublishService.checkAppVersionExist(revocation.getApplicationVersionId());
        ApplicationDTO applicationDTO = mktApplyAndPublishService.checkAppExist(applicationVersionDTO.getApplicationId());
        //3.向SaaS发送撤销申请
        try {
            if (saasPlatform) {
                ResponseEntity<Boolean> mktFeignResult = marketFeignClient.revocation(applicationVersionDTO.getVersion(), applicationDTO.getCode());
                if (mktFeignResult == null) {
                    throw new CommonException(FEIGN_EXECUTE_FAIL_EXCEPTION);
                } else {
                    revocationResult = mktFeignResult.getBody();
                }
            } else {
                revocationResult = publishAppRetrofitCalls.revocation(applicationVersionDTO.getVersion(), applicationDTO.getCode());
            }
            if (!revocationResult) {
                throw new CommonException(REVOCATION_EXCEPTION);
            }
        } catch (Exception e) {
            throw new CommonException(REVOCATION_EXCEPTION, e);
        }
        return true;
    }

    @Override
    @Transactional
    public void delete(Long id, Long versionInfoId) {
        //1. 删除市场发布版本信息
        mktPublishVersionInfoService.delete(versionInfoId);
        //2. 删除市场发布信息
        MktPublishApplicationDTO mktPublishApplicationDTO = checkExist(id);
        if (mktPublishApplicationDTO.getReleased()) {
            throw new CommonException(MKT_APPLICATION_DELETE_RELEASED_EXCEPTION);
        }
        if (mktPublishApplicationMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException(MKT_APPLICATION_DELETE_EXCEPTION);
        }
        //3. 查看关联应用有无已发布信息，若无则更新has_generate字段
        if (CollectionUtils.isEmpty(
                mktPublishApplicationMapper
                        .select(new MktPublishApplicationDTO().setReleased(true).setRefAppId(mktPublishApplicationDTO.getRefAppId())))) {
            ApplicationDTO applicationDTO = mktApplyAndPublishService.checkAppExist(mktPublishApplicationDTO.getRefAppId());
            applicationDTO.setHasGenerated(false);
            if (applicationMapper.updateByPrimaryKey(applicationDTO) != 1) {
                throw new UpdateException(APPLICATION_UPDATE_EXCEPTION);
            }
        }
    }

    @Override
    public String cutImage(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        if (saasPlatform) {
            ResponseEntity<String> stringResponseEntity = fileFeignClient.cutImage(file, rotate, axisX, axisY, width, height);
            if (ObjectUtils.isEmpty(stringResponseEntity) || StringUtils.isEmpty(stringResponseEntity.getBody())) {
                throw new CommonException(CUT_IMAGE_EXCEPTION);
            }
            return stringResponseEntity.getBody();
        }
        return publishAppRetrofitCalls.cutImage(file, rotate, axisX, axisY, width, height);
    }

    private class IdRef {
        Long releasedId;
        Long notReleasedId;

        private Long getReleasedId() {
            return releasedId;
        }

        private IdRef setReleasedId(Long releasedId) {
            this.releasedId = releasedId;
            return this;
        }

        private Long getNotReleasedId() {
            return notReleasedId;
        }

        private IdRef setNotReleasedId(Long notReleasedId) {
            this.notReleasedId = notReleasedId;
            return this;
        }
    }
}
