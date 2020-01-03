package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionVO;
import io.choerodon.base.app.service.ApplicationServiceService;
import io.choerodon.base.infra.dto.ApplicationServiceRefDTO;
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.dto.devops.AppServiceRepVO;
import io.choerodon.base.infra.dto.devops.AppServiceVO;
import io.choerodon.base.infra.dto.devops.AppServiceVersionUploadPayload;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.base.infra.mapper.ApplicationServiceRefMapper;
import io.choerodon.base.infra.mapper.ApplicationSvcVersionRefMapper;
import io.choerodon.base.infra.mapper.ApplicationVersionMapper;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/8/7
 */
@Service
public class ApplicationServiceServiceImpl implements ApplicationServiceService {

    private static final String SEARCH_PARAM = "searchParam";
    private static final String PARAMS = "params";
    private static final Gson gson = new Gson();

    private DevopsFeignClient devopsFeignClient;
    private ApplicationServiceRefMapper appServiceRefMapper;
    private ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper;
    private ApplicationVersionMapper applicationVersionMapper;

    @Value("${choerodon.market.saas.platform:false}")
    private boolean marketSaaSPlatform;

    public ApplicationServiceServiceImpl(DevopsFeignClient devopsFeignClient, ApplicationServiceRefMapper appServiceRefMapper,
                                         ApplicationVersionMapper applicationVersionMapper,
                                         ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper) {
        this.devopsFeignClient = devopsFeignClient;
        this.appServiceRefMapper = appServiceRefMapper;
        this.applicationVersionMapper = applicationVersionMapper;
        this.applicationSvcVersionRefMapper = applicationSvcVersionRefMapper;
    }

    @Override
    public void addAppSvcRef(Long applicationId, Set<Long> serviceIds) {
        serviceIds.forEach(v -> {
            ApplicationServiceRefDTO serviceRefInsert = new ApplicationServiceRefDTO();
            serviceRefInsert.setApplicationId(applicationId);
            serviceRefInsert.setServiceId(v);
            if (appServiceRefMapper.insert(serviceRefInsert) != 1) {
                throw new InsertException("error.app.service.ref.insert");
            }
        });
    }

    @Override
    public void deleteAppSvcRef(Long projectId, Long applicationId, Set<Long> serviceIds) {
        ApplicationVersionDTO appVersionQuery = new ApplicationVersionDTO();
        appVersionQuery.setApplicationId(applicationId);
        List<ApplicationVersionDTO> appVersions = applicationVersionMapper.select(appVersionQuery);

        appVersions.forEach(v -> checkAppVersionContainsNeedDelSvc(projectId, serviceIds, v));
        serviceIds.forEach(v -> {
            ApplicationServiceRefDTO serviceRefDelete = new ApplicationServiceRefDTO();
            serviceRefDelete.setServiceId(v);
            serviceRefDelete.setApplicationId(applicationId);
            appServiceRefMapper.delete(serviceRefDelete);
        });
    }

    @Override
    public PageInfo<AppServiceDetailsVO> pagingAppSvcByOptions(Pageable pageable, Long projectId, Long applicationId,
                                                               String name, String code, String type, String[] params) {
        Set<Long> serviceIds = getOldServiceIds(applicationId);
        if (serviceIds.isEmpty()) {
            return new PageInfo<>();
        }
        return devopsFeignClient.batchQueryAppService(projectId, serviceIds, true,
                pageable.getPageNumber(), pageable.getPageSize(), PageUtils.getPageableSorts(pageable),
                getParamsJson(name, code, type, params)).getBody();
    }

    @Override
    public Set<Long> listService(Long organizationId, String appType) {
        return appServiceRefMapper.selectServiceByOrgId(marketSaaSPlatform ? organizationId : null, appType);
    }

    @Override
    public Set<Long> listSvcVersion(Long organizationId, String appType) {
        return applicationSvcVersionRefMapper.selectSvcVersionByOrgId(marketSaaSPlatform ? organizationId : null, appType);
    }

    @Override
    public List<AppServiceDetailsVO> listAppSvc(Long organizationId, Long applicationId) {
        Set<Long> serviceIds = getOldServiceIds(applicationId);
        if (serviceIds.isEmpty()) {
            return new ArrayList<>();
        }
        PageInfo<AppServiceDetailsVO> pageInfo = devopsFeignClient.batchQueryAppServiceWithOrg(organizationId, serviceIds, false,
                0, 0, null, "").getBody();
        if (pageInfo == null) {
            return new ArrayList<>();
        }
        return pageInfo.getList();
    }

    @Override
    public PageInfo<AppServiceVO> pagingProServiceByOptions(Pageable pageable, Long projectId, Long applicationId,
                                                            AppServiceVO appServiceVO, String[] params, Boolean filter) {
        Set<Long> oldServiceIds = getOldServiceIds(applicationId);

        PageInfo<AppServiceVO> pageInfo = devopsFeignClient.listAppByProjectId(projectId, false,
                pageable.getPageNumber(), pageable.getPageSize(), PageUtils.getPageableSorts(pageable),
                getParamsJson(appServiceVO.getName(), appServiceVO.getCode(), appServiceVO.getType(), params)).getBody();

        if (filter) {
            Optional.ofNullable(pageInfo).ifPresent(page -> page.setList(page.getList()
                    .stream()
                    .filter(v -> !oldServiceIds.contains(v.getId()))
                    .collect(Collectors.toList())));
        }
        return PageUtils.createPageFromList(pageInfo.getList(), pageable);
    }

    @Override
    public PageInfo<AppServiceRepVO> pagingSharedServiceByOptions(Pageable pageable, Long projectId, Long applicationId,
                                                                  AppServiceVO appServiceVO, String[] params, Boolean filter) {
        Set<Long> oldServiceIds = getOldServiceIds(applicationId);

        PageInfo<AppServiceRepVO> pageInfo = devopsFeignClient.pageShareApps(projectId, false,
                pageable.getPageNumber(), pageable.getPageSize(), PageUtils.getPageableSorts(pageable),
                getParamsJson(appServiceVO.getName(), appServiceVO.getCode(), appServiceVO.getType(), params)).getBody();

        if (filter) {
            Optional.ofNullable(pageInfo).ifPresent(page -> page.setList(page.getList()
                    .stream()
                    .filter(v -> !oldServiceIds.contains(v.getId()))
                    .collect(Collectors.toList())));
        }
        return PageUtils.createPageFromList(pageInfo.getList(), pageable);
    }

    @Override
    public List<AppServiceVersionUploadPayload> listVersionsByAppServiceId(Long appServiceId) {
        ResponseEntity<List<AppServiceVersionUploadPayload>> appServiceVersions = devopsFeignClient.listVersionsByAppServiceId(appServiceId);

        if (appServiceVersions != null && appServiceVersions.getBody() != null) {
            return appServiceVersions.getBody();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<AppServiceVersionVO> getAppSvcWithAllVersion(Long projectId, String serviceId, String version) {
        return devopsFeignClient.listVersionById(projectId, serviceId, version).getBody();
    }

    private void checkAppVersionContainsNeedDelSvc(Long projectId, Set<Long> deleteServiceIds, ApplicationVersionDTO v) {
        ApplicationSvcVersionRefDTO svcVersionRefQuery = new ApplicationSvcVersionRefDTO();
        svcVersionRefQuery.setApplicationVersionId(v.getId());
        Set<Long> serviceVersionIds = applicationSvcVersionRefMapper.select(svcVersionRefQuery)
                .stream()
                .map(ApplicationSvcVersionRefDTO::getServiceVersionId)
                .collect(Collectors.toSet());
        // 如果服务版本Id不为空，先查询到每个版本Id的服务Id，然后拿服务Id与需要被删除的服务Id做交集
        // 如果交集不为空说明已经服务被加入版本中使用了，这时不允许删除
        if (!serviceVersionIds.isEmpty()) {
            List<AppServiceVO> appServiceVOS = devopsFeignClient.listServiceByVersionIds(projectId, serviceVersionIds).getBody();
            if (appServiceVOS == null) {
                throw new CommonException("error.feign.app.service.empty");
            }
            Set<Long> serviceIds = appServiceVOS.stream().map(AppServiceVO::getId).collect(Collectors.toSet());
            serviceIds.retainAll(deleteServiceIds);
            if (!serviceIds.isEmpty()) {
                throw new CommonException("error.service.ref.delete.for.app.version.exist");
            }
        }
    }

    private Set<Long> getOldServiceIds(Long applicationId) {
        ApplicationServiceRefDTO serviceRefDTO = new ApplicationServiceRefDTO();
        serviceRefDTO.setApplicationId(applicationId);
        return appServiceRefMapper.select(serviceRefDTO)
                .stream()
                .map(ApplicationServiceRefDTO::getServiceId)
                .collect(Collectors.toSet());
    }

    private String getParamsJson(String name, String code, String type, String[] params) {
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, String> searchMap = new HashMap<>();

        Optional.ofNullable(name).ifPresent(v -> searchMap.put("name", v));
        Optional.ofNullable(code).ifPresent(v -> searchMap.put("code", v));
        Optional.ofNullable(type).ifPresent(v -> searchMap.put("type", v));

        paramsMap.put(SEARCH_PARAM, searchMap);
        // devops处理 - 不论是否为空
        paramsMap.put(PARAMS, params);

        return gson.toJson(paramsMap);
    }
}
