package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.app.service.AppServiceRefService;
import io.choerodon.base.infra.dto.ApplicationServiceRefDTO;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.base.infra.mapper.ApplicationServiceRefMapper;
import io.choerodon.base.infra.utils.PageUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanghao
 * @since 2019/9/13 12:48
 */
@Service
public class AppServiceRefServiceImpl implements AppServiceRefService {
    private static final String SEARCH_PARAM = "searchParam";
    private ApplicationServiceRefMapper appServiceRefMapper;
    private DevopsFeignClient devopsFeignClient;
    private static final Gson gson = new Gson();

    public AppServiceRefServiceImpl(ApplicationServiceRefMapper appServiceRefMapper, DevopsFeignClient devopsFeignClient) {
        this.appServiceRefMapper = appServiceRefMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public PageInfo<AppServiceDetailsVO> pagingServicesWithVersionsByAppId(Long projectId, Pageable pageable, Long applicationId, String name) {
        PageInfo<AppServiceDetailsVO> appServiceVOPageInfo = new PageInfo<>();
        ApplicationServiceRefDTO applicationServiceRefDTO = new ApplicationServiceRefDTO();
        applicationServiceRefDTO.setApplicationId(applicationId);
        // 查询应用下的服务列表
        List<ApplicationServiceRefDTO> serviceRefDTOS = appServiceRefMapper.select(applicationServiceRefDTO);

        if (CollectionUtils.isEmpty(serviceRefDTOS)) {
            return appServiceVOPageInfo;
        }
        Set<Long> serviceIds = serviceRefDTOS.stream().map(ApplicationServiceRefDTO::getServiceId).collect(Collectors.toSet());
        //调用devops接口查询服务的所有版本
        appServiceVOPageInfo = devopsFeignClient.batchQueryAppService(projectId, serviceIds, true,
                pageable.getPageNumber(), pageable.getPageSize(), PageUtils.getPageableSorts(pageable),
                getParamsJson(name)).getBody();
        return appServiceVOPageInfo;
    }

    @Override
    public Set<Long> listAppServiceIdsByAppId(Long appId) {
        ApplicationServiceRefDTO applicationServiceRefDTO = new ApplicationServiceRefDTO();
        applicationServiceRefDTO.setApplicationId(appId);
        List<ApplicationServiceRefDTO> selectRefs = appServiceRefMapper.select(applicationServiceRefDTO);

        if (CollectionUtils.isEmpty(selectRefs)) {
            return new HashSet<>();
        }

        return selectRefs.stream().map(ApplicationServiceRefDTO::getServiceId).collect(Collectors.toSet());
    }

    private String getParamsJson(String name) {
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, String> searchMap = new HashMap<>();

        Optional.ofNullable(name).ifPresent(v -> searchMap.put("name", v));

        paramsMap.put(SEARCH_PARAM, searchMap);
        // devops处理 - 不论是否为空
        return gson.toJson(paramsMap);
    }
}
