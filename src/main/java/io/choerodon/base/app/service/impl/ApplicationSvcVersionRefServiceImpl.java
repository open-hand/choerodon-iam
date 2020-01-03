package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionDetailsVO;
import io.choerodon.base.app.service.ApplicationSvcVersionRefService;
import io.choerodon.base.infra.dto.ApplicationSvcVersionRefDTO;
import io.choerodon.base.infra.dto.devops.AppServiceAndVersionDTO;
import io.choerodon.base.infra.enums.ApplicationSvcVersionStatusEnum;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.base.infra.mapper.ApplicationSvcVersionRefMapper;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Eugen
 **/
@Service
public class ApplicationSvcVersionRefServiceImpl implements ApplicationSvcVersionRefService {

    public static final String APP_SVC_VERSION_REF_INSERT_EXCEPTION = "error.application.svc.version.ref.insert";
    public static final String APP_SVC_VERSION_REF_DELETE_EXCEPTION = "error.application.svc.version.ref.delete";
    public static final String APP_SVC_VERSION_REF_UPDATE_EXCEPTION = "error.application.svc.version.ref.update";

    private ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper;
    private DevopsFeignClient devopsFeignClient;

    public ApplicationSvcVersionRefServiceImpl(ApplicationSvcVersionRefMapper applicationSvcVersionRefMapper, DevopsFeignClient devopsFeignClient) {
        this.applicationSvcVersionRefMapper = applicationSvcVersionRefMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public List<AppServiceDetailsVO> getSvcVersions(Long appVersionId) {
        //1.获取应用版本下的 服务版本及服务版本状态信息
        List<AppServiceAndVersionDTO> refList = applicationSvcVersionRefMapper.selectByAppVersionId(appVersionId);
        if (CollectionUtils.isEmpty(refList)) {
            return new ArrayList<>();
        }
        //2.Feign:获取服务及版本具体信息
        ResponseEntity<List<AppServiceAndVersionDTO>> feignEntity = devopsFeignClient.getSvcVersionByVersionIds(refList);
        if (ObjectUtils.isEmpty(feignEntity)) {
            return new ArrayList<>();
        }
        //3.组装成两层结构
        return buildingGroup(feignEntity.getBody());
    }

    @Override
    public PageInfo<AppServiceDetailsVO> pagingAppSvcAndVersions(Long appVersionId, Pageable pageable) {
        return PageUtils.createPageFromList(getSvcVersions(appVersionId), pageable);
    }

    @Override
    public void deleteAppSvcVersionRefByAppVerId(Long appVersionId) {
        ApplicationSvcVersionRefDTO applicationSvcVersionRefDTO = new ApplicationSvcVersionRefDTO();
        applicationSvcVersionRefDTO.setApplicationVersionId(appVersionId);
        if (applicationSvcVersionRefMapper.delete(applicationSvcVersionRefDTO) < 1) {
            throw new CommonException(APP_SVC_VERSION_REF_DELETE_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public void batchInsert(Long appVersionId, List<Long> serviceVersionIds) {
        if (!CollectionUtils.isEmpty(serviceVersionIds)) {
            serviceVersionIds.stream().collect(Collectors.toSet()).forEach(svcVersionId -> {
                if (applicationSvcVersionRefMapper.insertSelective(new ApplicationSvcVersionRefDTO().setApplicationVersionId(appVersionId).setServiceVersionId(svcVersionId)) != 1) {
                    throw new InsertException(APP_SVC_VERSION_REF_INSERT_EXCEPTION);
                }
            });
        }
    }

    @Override
    public void batchDelete(Set<Long> serviceVersionIds) {
        if (applicationSvcVersionRefMapper.batchDelete(serviceVersionIds) < 1) {
            throw new CommonException(APP_SVC_VERSION_REF_DELETE_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public List<AppServiceDetailsVO> quickCreate(Long appVersionId, Set<Long> serviceVersionIds) {
        //1.创建应用版本 与 服务版本 关联关系
        serviceVersionIds.forEach(svcVersionId -> {
            ApplicationSvcVersionRefDTO createDTO = new ApplicationSvcVersionRefDTO()
                    .setApplicationVersionId(appVersionId)
                    .setServiceVersionId(svcVersionId)
                    .setStatus(ApplicationSvcVersionStatusEnum.UNPUBLISHED.value());
            if (applicationSvcVersionRefMapper.insertSelective(createDTO) != 1) {
                throw new InsertException(APP_SVC_VERSION_REF_INSERT_EXCEPTION);
            }
        });
        //2.返回应用版本下服务版本的关联关系
        return getSvcVersions(appVersionId);
    }

    @Override
    public List<ApplicationSvcVersionRefDTO> listAppServiceVersionRefByAppVersionId(Long id) {
        ApplicationSvcVersionRefDTO applicationSvcVersionRefDTO = new ApplicationSvcVersionRefDTO();
        applicationSvcVersionRefDTO.setApplicationVersionId(id);
        List<ApplicationSvcVersionRefDTO> applicationSvcVersionRefDTOS = applicationSvcVersionRefMapper.select(applicationSvcVersionRefDTO);
        return applicationSvcVersionRefDTOS;


    }

    @Override
    public List<ApplicationSvcVersionRefDTO> listSvcVersionsByVersionIdAndStatus(Long versionId, String status) {
        ApplicationSvcVersionRefDTO applicationSvcVersionRefDTO = new ApplicationSvcVersionRefDTO();
        applicationSvcVersionRefDTO.setStatus(status);
        applicationSvcVersionRefDTO.setApplicationVersionId(versionId);
        return applicationSvcVersionRefMapper.select(applicationSvcVersionRefDTO);
    }

    private List<AppServiceDetailsVO> buildingGroup(List<AppServiceAndVersionDTO> srcList) {
        if (ObjectUtils.isEmpty(srcList)) {
            return new ArrayList<>();
        }
        List<AppServiceDetailsVO> desList = new ArrayList<>();
        srcList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(t -> t.getId()))), ArrayList::new))
                .forEach(sav -> {
                    //构建服务列表
                    AppServiceDetailsVO svcItem = new AppServiceDetailsVO();
                    BeanUtils.copyProperties(sav, svcItem);
                    List<AppServiceVersionDetailsVO> svcVersionItemList = new ArrayList<>();
                    //构建服务版本列表
                    srcList.parallelStream().filter(sav2 -> sav2.getId().equals(sav.getId()))
                            .forEachOrdered(v -> svcVersionItemList.add(new AppServiceVersionDetailsVO().setId(v.getVersionId()).setVersion(v.getVersion()).setStatus(v.getVersionStatus())));
                    svcItem.setAppServiceVersions(svcVersionItemList);
                    desList.add(svcItem);
                });
        return desList;
    }

}
