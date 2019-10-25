package io.choerodon.base.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.ApplicationSvcVersionRefService;
import io.choerodon.base.app.service.ApplicationVersionService;
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.enums.*;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.base.infra.mapper.ApplicationVersionMapper;
import io.choerodon.base.infra.utils.PageHelperTool;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/7/30
 */
@Service
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    public static final String APPLICATION_VERSION_INSERT_EXCEPTION = "error.application.version.insert";
    public static final String APPLICATION_VERSION_DOES_NOT_EXIST_EXCEPTION = "error.application.version.does.not.exist";
    private ApplicationVersionMapper applicationVersionMapper;
    private ApplicationSvcVersionRefService applicationSvcVersionRefService;
    private DevopsFeignClient devopsFeignClient;

    private final ModelMapper modelMapper = new ModelMapper();

    public ApplicationVersionServiceImpl(ApplicationVersionMapper applicationVersionMapper, ApplicationSvcVersionRefService applicationSvcVersionRefService, DevopsFeignClient devopsFeignClient) {
        this.applicationVersionMapper = applicationVersionMapper;
        this.applicationSvcVersionRefService = applicationSvcVersionRefService;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public List<ApplicationVersionWithStatusVO> getBriefInfo(Long applicationId) {
        //1. 查询应用下版本 及 版本的发布状态
        List<ApplicationVersionWithStatusVO> withStatusVOS = applicationVersionMapper.selectWithStatus(applicationId);
        if (CollectionUtils.isEmpty(withStatusVOS)) {
            return new ArrayList<>();
        }
        //2. 整理版本的发布状态（发布中：除已发布/无状态之外的都属发布中；已发布：已发布；未发布：无状态；）
        withStatusVOS.forEach(v -> {
            if (ObjectUtils.isEmpty(v.getStatus())) {
                v.setStatus(ApplicationVersionStatus.FRESH.getValue());
            } else if (PublishAppVersionStatusEnum.PUBLISHED.value().equalsIgnoreCase(v.getStatus())) {
                v.setStatus(ApplicationVersionStatus.RELEASED.getValue());
            } else {
                v.setStatus(ApplicationVersionStatus.PUBLISHING.getValue());
            }
        });
        //3. 排序 fresh,publishing,released
        withStatusVOS.sort((v1, v2) -> {
            if (v1.getStatus().equalsIgnoreCase(v2.getStatus())) {
                return 0;
            }
            if (ApplicationVersionStatus.FRESH.getValue().equalsIgnoreCase(v1.getStatus())) {
                return -1;
            }
            if (ApplicationVersionStatus.FRESH.getValue().equalsIgnoreCase(v2.getStatus())) {
                return 1;
            }
            if (ApplicationVersionStatus.PUBLISHING.getValue().equalsIgnoreCase(v1.getStatus())) {
                return -1;
            }
            return 1;
        });
        //4. 返回版本及版本状态
        return withStatusVOS;
    }

    @Override
    public Boolean checkName(Long applicationId, String version) {
        ApplicationVersionDTO applicationVersionDTO = new ApplicationVersionDTO();
        applicationVersionDTO.setApplicationId(applicationId);
        applicationVersionDTO.setVersion(version);
        return applicationVersionMapper.select(applicationVersionDTO).isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationVersionDTO createAppVersion(Long applicationId, ApplicationVersionVO applicationVersionVO) {
        applicationVersionVO.setApplicationId(applicationId);
        ApplicationVersionDTO versionDTO = modelMapper.map(applicationVersionVO, ApplicationVersionDTO.class);
        if (applicationVersionMapper.insertSelective(versionDTO) != 1) {
            throw new InsertException(APPLICATION_VERSION_INSERT_EXCEPTION);
        }
        List<Long> ids = applicationVersionVO.getAppServiceDetailsVOS().stream()
                .flatMap(appServiceDetailsVO -> appServiceDetailsVO.getAppServiceVersions().stream())
                .map(AppServiceVersionDetailsVO::getId).collect(Collectors.toList());
        applicationSvcVersionRefService.batchInsert(versionDTO.getId(), ids);
        return applicationVersionMapper.selectByPrimaryKey(versionDTO.getId());
    }

    @Override
    public PageInfo<ApplicationVersionVO> pagingByOptions(Pageable pageable, Long applicationId, String version, String description, String status, String[] params) {
        // 查询应用下所有版本
        List<ApplicationVersionVO> applicationVersionVOS = applicationVersionMapper.fulltextSearch(applicationId, version, description, status, params);
        if (CollectionUtils.isEmpty(applicationVersionVOS)) {
            return new PageInfo<>();
        }
        // 计算发布状态并设置应用版本排序大小
        calPublishStatus(applicationVersionVOS);
        // 封装服务版本信息
        applicationVersionVOS.forEach(applicationVersionVO -> {
            List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(applicationVersionVO.getId());
            svcVersions.forEach(appServiceDetailsVO -> {
                // 对服务版本排序
                List<AppServiceVersionDetailsVO> sortedServiceVersions = appServiceDetailsVO
                        .getAppServiceVersions()
                        .stream()
                        .sorted((Comparator.comparing(AppServiceVersionDetailsVO::getId).reversed()))
                        .collect(Collectors.toList());

                if (AppVersionStatusEnum.PUBLISHING.value().equals(applicationVersionVO.getStatus())) {
                    sortedServiceVersions.forEach(v -> {
                        // 如果应用版本是发布中的状态 则服务版本状态也为发布中
                        if (ApplicationSvcVersionStatusEnum.UNPUBLISHED.value().equals(v.getStatus())) {
                            v.setStatus(ApplicationSvcVersionStatusEnum.PROCESSING.value());
                        }
                    });
                }
                appServiceDetailsVO.setAppServiceVersions(sortedServiceVersions);
            });
            applicationVersionVO.setAppServiceDetailsVOS(svcVersions);
        });
        // 对应用版本排序
        List<ApplicationVersionVO> sortedAppVersion = applicationVersionVOS.stream()
                .sorted((Comparator.comparing(ApplicationVersionVO::getOrder).reversed()))
                .collect(Collectors.toList());
        // 内存分页并返回
        return warpPageInfo(pageable, sortedAppVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationVersionDTO update(ApplicationVersionVO applicationVersionVO) {
        ApplicationVersionVO applicationVersionRecord = applicationVersionMapper.selectVersionWithPublishStatusById(applicationVersionVO.getId());
        // 计算版本发布状态
        calPublishStatus(Arrays.asList(applicationVersionRecord));
        String status = applicationVersionRecord.getStatus();
        // 需要新增的服务版本列表
        List<Long> insertIds = new ArrayList<>();
        // 未发布版本修改需要删除之前的服务版本
        if (AppVersionStatusEnum.UNPUBLISHED.value().equals(status)) {
            applicationSvcVersionRefService.deleteAppSvcVersionRefByAppVerId(applicationVersionVO.getId());
            // 更新版本相关信息
            updateAppVersion(applicationVersionVO);
            applicationVersionVO.getAppServiceDetailsVOS().forEach(appServiceDetailsVO -> {
                List<Long> ids = appServiceDetailsVO.getAppServiceVersions().stream().map(AppServiceVersionDetailsVO::getId).collect(Collectors.toList());
                insertIds.addAll(ids);
            });
        }
        // 已发布的应用版本下所有已发布和发布中的服务版本不可修改
        if (AppVersionStatusEnum.PUBLISHED.value().equals(status)) {
            // 数据库原有应用版本和服务版本关系
            List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(applicationVersionVO.getId());
            Map<Long, List<AppServiceVersionDetailsVO>> map = applicationVersionVO.getAppServiceDetailsVOS().stream().collect(Collectors.toMap(AppServiceDetailsVO::getId,
                    v -> Optional.ofNullable(v.getAppServiceVersions()).orElse(new ArrayList<>())));
            svcVersions.forEach(appServiceDetailsVO -> {
                Long serviceId = appServiceDetailsVO.getId();
                List<AppServiceVersionDetailsVO> appServiceVersionDetailsVOS = map.get(serviceId);
                if (appServiceVersionDetailsVOS != null) {
                    // 已发布和发布中的版本不能修改，筛选出所有未发布和发布失败版本
                    Set<Long> notPublishedIds = appServiceDetailsVO.getAppServiceVersions().stream()
                            .filter(appServiceVersionDetailsVO -> ApplicationSvcVersionStatusEnum.UNPUBLISHED.value().equals(appServiceVersionDetailsVO.getStatus()) ||
                                    ApplicationSvcVersionStatusEnum.FAILURE.value().equals(appServiceVersionDetailsVO.getStatus()))
                            .map(AppServiceVersionDetailsVO::getId).collect(Collectors.toSet());
                    List<Long> newIds = appServiceVersionDetailsVOS.stream().map(AppServiceVersionDetailsVO::getId).collect(Collectors.toList());
                    // 需要删除的服务版本
                    Set<Long> delIds = new HashSet<>();
                    // 不修改的服务版本
                    Set<Long> notUpdateIds = new HashSet<>();
                    notPublishedIds.forEach(id -> {
                        if (!newIds.contains(id)) {
                            // 数据库中原有服务版本没有包含在传入数据中则删除
                            delIds.add(id);
                        } else {
                            // 包含则不更新
                            notUpdateIds.add(id);
                        }
                    });
                    if (!delIds.isEmpty()) {
                        applicationSvcVersionRefService.batchDelete(delIds);
                    }
                    List<Long> ids = appServiceVersionDetailsVOS.stream().map(AppServiceVersionDetailsVO::getId).collect(Collectors.toList());
                    // 去掉传入数据中不更新的版本
                    ids.removeAll(notUpdateIds);
                    insertIds.addAll(ids);
                }
            });
        }
        // 执行新增操作
        if (!insertIds.isEmpty()) {
            applicationSvcVersionRefService.batchInsert(applicationVersionVO.getId(), insertIds);
        }
        return applicationVersionMapper.selectByPrimaryKey(applicationVersionVO.getId());
    }


    @Override
    @Transactional
    public void delete(Long id) {
        // 判断状态,只有未发布状态的版本可以删除
        ApplicationVersionVO applicationVersionRecord = applicationVersionMapper.selectVersionWithPublishStatusById(id);
        calPublishStatus(Arrays.asList(applicationVersionRecord));
        String status = applicationVersionRecord.getStatus();
        if (!AppVersionStatusEnum.UNPUBLISHED.value().equals(status)) {
            throw new CommonException("error.application.version.status.invalid");
        }
        // 删除应用版本
        if (applicationVersionMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.application.version.delete");
        }
        // 删除应用版本和服务版本关系
        applicationSvcVersionRefService.deleteAppSvcVersionRefByAppVerId(id);
    }


    @Override
    @Transactional
    public ApplicationVersionDTO quickCreate(Long applicationId, ApplicationVersionQuickCreateVO quickCreateVO) {
        //0.校验版本名称
        checkName(applicationId, quickCreateVO.getVersion());
        //1.创建版本
        ApplicationVersionDTO createDTO = new ApplicationVersionDTO()
                .setApplicationId(applicationId)
                .setVersion(quickCreateVO.getVersion());
        if (applicationVersionMapper.insertSelective(createDTO) != 1) {
            throw new InsertException(APPLICATION_VERSION_INSERT_EXCEPTION);
        }
        //2.创建应用版本与服务版本关联关系
        applicationSvcVersionRefService.quickCreate(createDTO.getId(), quickCreateVO.getServiceVersionIds());
        //3.返回创建结果
        return applicationVersionMapper.selectByPrimaryKey(createDTO.getId());
    }

    @Override
    public ApplicationVersionVO getAppVersionWithServicesAndServiceVersions(Long projectId, Long versionId) {
        ApplicationVersionDTO applicationVersionDTO = applicationVersionMapper.selectByPrimaryKey(versionId);
        ApplicationVersionVO applicationVersionVO = modelMapper.map(applicationVersionDTO, ApplicationVersionVO.class);

        List<AppServiceDetailsVO> svcVersions = applicationSvcVersionRefService.getSvcVersions(applicationVersionVO.getId());

        // 添加服务的所有版本信息，不包含当前版本
        Set<Long> serviceIds = svcVersions.stream().map(AppServiceDetailsVO::getId).collect(Collectors.toSet());
        // 调用devops接口,查询应用版本下包含的服务信息及服务版本信息
        PageInfo<AppServiceDetailsVO> appServiceVOPageInfo = devopsFeignClient.batchQueryAppService(projectId, serviceIds, false, 0, 0, null, "").getBody();
        List<AppServiceDetailsVO> appServices = appServiceVOPageInfo.getList();

        Map<Long, List<AppServiceVersionDetailsVO>> serviceVersionMap = appServices.stream()
                .collect(Collectors.toMap(AppServiceDetailsVO::getId,
                        appServiceDetailsVO ->
                                !CollectionUtils.isEmpty(appServiceDetailsVO.getAllAppServiceVersions()) ?
                                        appServiceDetailsVO.getAllAppServiceVersions() : new ArrayList<>()));
        // 遍历当前服务版本(已完成,发布中),把所有服务版主中当前服务版本去掉
        svcVersions.forEach(appServiceDetailsVO -> {
            Long serviceId = appServiceDetailsVO.getId();
            List<AppServiceVersionDetailsVO> allServiceVersions = serviceVersionMap.get(serviceId);
            // 需要去掉的版本
            List<Long> doneOrProcessingVersionIds = appServiceDetailsVO.getAppServiceVersions()
                    .stream()
                    .filter(appServiceVersionDetailsVO -> ApplicationSvcVersionStatusEnum.DONE.value().equals(appServiceVersionDetailsVO.getStatus()) ||
                            ApplicationSvcVersionStatusEnum.PROCESSING.value().equals(appServiceVersionDetailsVO.getStatus()))
                    .map(AppServiceVersionDetailsVO::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(allServiceVersions)) {
                if (CollectionUtils.isEmpty(doneOrProcessingVersionIds)) {
                    appServiceDetailsVO.setAllAppServiceVersions(allServiceVersions);
                } else {
                    // 去掉已发布和发布中版本
                    List<AppServiceVersionDetailsVO> filteredAppServiceVersionVOS = allServiceVersions
                            .stream()
                            .filter(appServiceVersionDTO -> !doneOrProcessingVersionIds.contains(appServiceVersionDTO.getId()))
                            .collect(Collectors.toList());
                    appServiceDetailsVO.setAllAppServiceVersions(filteredAppServiceVersionVOS);
                }
            } else {
                appServiceDetailsVO.setAllAppServiceVersions(new ArrayList<>());
            }
            // 添加未发布版本
            List<AppServiceVersionDetailsVO> notPublishedOrFailerVersion = appServiceDetailsVO.getAppServiceVersions()
                    .stream()
                    .filter(appServiceVersionDetailsVO -> ApplicationSvcVersionStatusEnum.UNPUBLISHED.value().equals(appServiceVersionDetailsVO.getStatus()) ||
                            ApplicationSvcVersionStatusEnum.FAILURE.value().equals(appServiceVersionDetailsVO.getStatus()))
                    .collect(Collectors.toList());
            // 添加未发布或发布失败的版本
            appServiceDetailsVO.setAppServiceVersions(notPublishedOrFailerVersion);

        });

        applicationVersionVO.setAppServiceDetailsVOS(svcVersions);
        return applicationVersionVO;
    }

    @Override
    public AppVersionInfoVO getAppVersionInfo(Long projectId, Long versionId) {
        ApplicationVersionDTO applicationVersion = applicationVersionMapper.selectByPrimaryKey(versionId);
        return modelMapper.map(applicationVersion, AppVersionInfoVO.class);
    }

    @Override
    public List<AppServiceVersionVO> getAppSvcWithAllVersion(Long projectId, Long versionId, String serviceId, String version) {
        // 调用devops接口，查询服务下所有版本
        List<AppServiceVersionVO> versions = devopsFeignClient.listVersionById(projectId, serviceId, version).getBody();
        // 查询版本下已发布的版本
        List<ApplicationSvcVersionRefDTO> applicationSvcVersionRefs = applicationSvcVersionRefService.listSvcVersionsByVersionIdAndStatus(versionId, ApplicationSvcVersionStatusEnum.DONE.value());
        // 已发布版本ids
        List<Long> svcVersionIds = applicationSvcVersionRefs.stream().map(ApplicationSvcVersionRefDTO::getServiceVersionId).collect(Collectors.toList());
        // 过滤已发布版本
        List<AppServiceVersionVO> filteredVersions = versions.stream().filter(appServiceVersionVO -> !svcVersionIds.contains(appServiceVersionVO.getId())).collect(Collectors.toList());
        return filteredVersions;
    }

    /**
     * 计算发布状态，设置排序
     *
     * @param applicationVersionVOS
     */
    private void calPublishStatus(List<ApplicationVersionVO> applicationVersionVOS) {
        List<String> notPublishStatus = Arrays.asList(
                PublishAppVersionStatusEnum.UNPUBLISHED.value(),
                PublishAppVersionStatusEnum.REJECTED.value(),
                PublishAppVersionStatusEnum.WITHDRAWN.value()
        );
        List<String> publishingStatus = Arrays.asList(
                PublishAppVersionStatusEnum.UNDER_APPROVAL.value(),
                PublishAppVersionStatusEnum.UNCONFIRMED.value()
        );

        // 未发布
        applicationVersionVOS.stream()
                .filter(applicationVersionVO -> applicationVersionVO.getStatus() == null || notPublishStatus.contains(applicationVersionVO.getStatus()))
                .forEach(applicationVersionVO -> {
                    applicationVersionVO.setStatus(AppVersionStatusEnum.UNPUBLISHED.value());
                    applicationVersionVO.setOrder(AppVersionOrderEnums.UPPUBLISHED.value());
                });
        // 发布中
        applicationVersionVOS.stream()
                .filter(applicationVersionVO -> publishingStatus.contains(applicationVersionVO.getStatus()))
                .forEach(applicationVersionVO -> {
                    applicationVersionVO.setStatus(AppVersionStatusEnum.PUBLISHING.value());
                    applicationVersionVO.setOrder(AppVersionOrderEnums.PUBLISHING.value());
                });
        // 已发布
        applicationVersionVOS.stream()
                .filter(applicationVersionVO -> PublishAppVersionStatusEnum.PUBLISHED.value().equals(applicationVersionVO.getStatus()))
                .forEach(applicationVersionVO -> {
                    applicationVersionVO.setStatus(AppVersionStatusEnum.PUBLISHED.value());
                    applicationVersionVO.setOrder(AppVersionOrderEnums.PUBLISHED.value());
                });
    }

    private void updateAppVersion(ApplicationVersionVO applicationVersionVO) {
        ApplicationVersionDTO versionDTO = modelMapper.map(applicationVersionVO, ApplicationVersionDTO.class);
        versionDTO.setApplicationId(null);
        if (applicationVersionMapper.updateByPrimaryKeySelective(versionDTO) != 1) {
            throw new CommonException("error.application.version.update");
        }
    }

    /**
     * 内存分页
     *
     * @param pageable
     * @param applicationVersions
     * @return
     */
    private PageInfo<ApplicationVersionVO> warpPageInfo(Pageable pageable, List<ApplicationVersionVO> applicationVersions) {
        PageInfo<ApplicationVersionVO> pageInfo;
        int versionsSize = applicationVersions.size();
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = PageUtils.getBegin(page, size);
        int end = start + size;
        end = end > versionsSize ? versionsSize : end;
        if (start > versionsSize) {
            //分页起始页越界
            pageInfo = new PageInfo<>();
        } else {
            pageInfo = new PageInfo<>(applicationVersions.subList(start, end));
        }
        pageInfo = PageHelperTool.initPageInfoObj(page, versionsSize, size, pageInfo);
        return pageInfo;
    }
}
