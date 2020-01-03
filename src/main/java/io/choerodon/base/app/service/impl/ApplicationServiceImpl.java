package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.vo.ApplicationReqVO;
import io.choerodon.base.api.vo.ApplicationRespVO;
import io.choerodon.base.api.vo.ApplicationVO;
import io.choerodon.base.api.vo.ProjectAndAppVO;
import io.choerodon.base.app.service.ApplicationService;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.base.infra.dto.ApplicationServiceRefDTO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.enums.ApplicationType;
import io.choerodon.base.infra.mapper.ApplicationMapper;
import io.choerodon.base.infra.mapper.ApplicationServiceRefMapper;
import io.choerodon.base.infra.mapper.ApplicationVersionMapper;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.web.util.PageableHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/7/30
 */

@Service
public class ApplicationServiceImpl implements ApplicationService {

    public static final String APPLICATION_UPDATE_EXCEPTION = "error.application.update";
    public static final String APPLICATION_DOES_NOT_EXIST_EXCEPTION = "error.application.does.not.exist";

    private ApplicationMapper applicationMapper;
    private ProjectMapper projectMapper;
    private ApplicationServiceRefMapper serviceRefMapper;
    private ApplicationVersionMapper applicationVersionMapper;

    @Value("${choerodon.market.saas.platform:false}")
    private boolean marketSaaSPlatform;

    public ApplicationServiceImpl(ApplicationMapper applicationMapper, ProjectMapper projectMapper,
                                  ApplicationServiceRefMapper serviceRefMapper, ApplicationVersionMapper applicationVersionMapper) {
        this.applicationMapper = applicationMapper;
        this.projectMapper = projectMapper;
        this.applicationVersionMapper = applicationVersionMapper;
        this.serviceRefMapper = serviceRefMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationDTO createApplication(Long projectId, ApplicationReqVO applicationReqVO) {
        ApplicationDTO applicationInsert = loadApplicationInsertInfo(projectId, applicationReqVO);
        if (applicationMapper.insert(applicationInsert) != 1) {
            throw new InsertException("error.application.insert");
        }
        ApplicationDTO resApp = applicationMapper.selectByPrimaryKey(applicationInsert);
        batchInsertAppSvcRef(applicationReqVO, resApp);
        return resApp;
    }

    @Override
    public Boolean checkName(Long projectId, String name) {
        ApplicationDTO applicationQuery = new ApplicationDTO();
        applicationQuery.setProjectId(projectId);
        applicationQuery.setName(name);
        return applicationMapper.select(applicationQuery).isEmpty();
    }

    @Override
    public ApplicationDTO updateApplication(ApplicationReqVO applicationReqVO) {
        ApplicationDTO applicationUpdate = new ApplicationDTO();
        applicationUpdate.setId(applicationReqVO.getId());
        applicationUpdate.setName(applicationReqVO.getName());
        applicationUpdate.setDescription(applicationReqVO.getDescription());
        return updateAppWithOVN(applicationReqVO.getObjectVersionNumber(), applicationUpdate);
    }

    @Override
    public ApplicationDTO updateApplicationFeedbackToken(Long id, Long objectVersionNumber) {
        ApplicationDTO applicationUpdate = new ApplicationDTO();
        applicationUpdate.setId(id);
        applicationUpdate.setFeedbackToken(UUID.randomUUID().toString());
        return updateAppWithOVN(objectVersionNumber, applicationUpdate);
    }

    @Override
    public PageInfo<ApplicationRespVO> pagingProjectAppByOptions(Long projectId, String name, String description,
                                                                 String projectName, String creatorRealName, String[] params, Pageable pageable) {
        PageInfo<ApplicationRespVO> pageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(
                        () -> applicationMapper.fulltextSearchInProject(projectId, name, description, projectName, creatorRealName, params));
        pageInfo.getList().forEach(v -> v.setAmendable(checkAppVersionIsAmendable(v.getId())));
        return pageInfo;
    }

    @Override
    public PageInfo<ApplicationRespVO> pagingOrgAppByOptions(Long orgId, String type, String name, String description,
                                                             String projectName, String creatorRealName, Long createBy,
                                                             Long participant, Boolean all, String[] params, Pageable pageable) {
        List<ApplicationRespVO> respVOS = new ArrayList<>();
        List<ApplicationRespVO> marketRespVOS = new ArrayList<>();

        // all == null 表示查询我创建的和我参与的
        // // all表示 查询 来自应用市场的和我参与的 - 并集
        if (all == null && type == null || all != null && all) {
            respVOS = applicationMapper.fulltextSearchInOrganization(
                    orgId, name, description, projectName, creatorRealName, createBy, participant, params);
        }

        // 如果类型不为空 则查询类型
        if (type != null) {
            marketRespVOS = applicationMapper.fulltextSearchMarketAppInOrganization(
                    orgId, type, name, description, creatorRealName, createBy, params);
        }
        respVOS.addAll(marketRespVOS);
        return PageUtils.createPageFromList(
                respVOS
                        .stream()
                        .sorted(Comparator.comparing(ApplicationRespVO::getId).reversed())
                        .collect(Collectors.toList()),
                pageable);
    }

    @Override
    public ApplicationDTO getAppById(Long id) {
        return applicationMapper.selectByPrimaryKey(id);
    }

    @Override
    public ProjectAndAppVO getProjectAndAppByToken(String token) {
        ApplicationDTO resApp = getAppByFeedbackToken(token);
        return new ProjectAndAppVO(getProject(resApp.getProjectId()), resApp);
    }

    @Override
    public List<ApplicationDTO> getAppBriefInfo(Long projectId, Boolean hasGenerated) {
        List<ApplicationDTO> briefInfo = applicationMapper.getBriefInfo(new ApplicationDTO()
                .setProjectId(projectId)
                .setHasGenerated(hasGenerated)
                .setType(ApplicationType.CUSTOM.getValue()));
        briefInfo.sort((a1, a2) -> {
            Boolean b1 = CollectionUtils.isEmpty(applicationVersionMapper.select(new ApplicationVersionDTO().setApplicationId(a1.getId())));
            Boolean b2 = CollectionUtils.isEmpty(applicationVersionMapper.select(new ApplicationVersionDTO().setApplicationId(a2.getId())));
            if (b1.equals(b2)) {
                return -1;
            }
            if (b1) {
                return 1;
            } else {
                return -1;
            }
        });
        return briefInfo;
    }

    @Override
    public ApplicationDTO checkExist(Long id) {
        ApplicationDTO applicationDTO = applicationMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(applicationDTO)) {
            throw new CommonException("error.application.does.not.exist");
        }
        return applicationDTO;
    }

    @Override
    public List<ApplicationVO> listApplicationInfoByServiceIds(Long projectID, Set<Long> serviceIds) {
        List<ApplicationServiceRefDTO> selectRefDTOs = new ArrayList<>();
        serviceIds.forEach(v -> {
            List<ApplicationServiceRefDTO> selectRefs = serviceRefMapper.select(new ApplicationServiceRefDTO().setServiceId(v));
            if (!CollectionUtils.isEmpty(selectRefs)) {
                selectRefDTOs.addAll(selectRefs);
            }
        });
        List<ApplicationVO> selectAppDTOs = new ArrayList<>();
        selectRefDTOs.forEach(v -> {
            ApplicationDTO applicationDTO = applicationMapper.selectByPrimaryKey(v.getApplicationId());
            ApplicationVO applicationVO = new ApplicationVO();
            BeanUtils.copyProperties(applicationDTO, applicationVO);
            if (applicationDTO.getId() != null) {
                selectAppDTOs.add(applicationVO);
            }
        });
        Set<ApplicationVO> applicationVOSet = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
        applicationVOSet.addAll(selectAppDTOs);
        return new ArrayList<>(applicationVOSet);
    }

    @Override
    public void deleteApplication(Long applicationId) {
        if (!checkAppVersionIsAmendable(applicationId)) {
            throw new CommonException("error.app.delete.for.app.version.exist");
        }
        applicationMapper.deleteByPrimaryKey(applicationId);
    }

    @Override
    public ApplicationDTO selectByPublishAppId(Long publishApplicationId) {
        return applicationMapper.selectByPublishAppId(publishApplicationId);
    }

    private Boolean checkAppVersionIsAmendable(Long applicationId) {
        ApplicationVersionDTO appVersionQuery = new ApplicationVersionDTO();
        appVersionQuery.setApplicationId(applicationId);
        return applicationVersionMapper.select(appVersionQuery).isEmpty();
    }

    private ApplicationDTO getAppByFeedbackToken(String token) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setFeedbackToken(token);
        ApplicationDTO resApp = applicationMapper.selectOne(applicationDTO);
        if (ObjectUtils.isEmpty(resApp)) {
            throw new CommonException("error.application.not.exist");
        }
        return resApp;
    }

    private void batchInsertAppSvcRef(ApplicationReqVO applicationReqVO, ApplicationDTO resApp) {
        applicationReqVO.getServiceIds().forEach(v -> {
            ApplicationServiceRefDTO serviceRefInsert = new ApplicationServiceRefDTO();
            serviceRefInsert.setApplicationId(resApp.getId());
            serviceRefInsert.setServiceId(v);
            if (serviceRefMapper.insert(serviceRefInsert) != 1) {
                throw new InsertException("error.application.service.ref.insert");
            }
        });
    }

    private ApplicationDTO loadApplicationInsertInfo(Long projectId, ApplicationReqVO applicationReqVO) {
        ApplicationDTO applicationInsert = new ApplicationDTO();
        applicationInsert.setName(applicationReqVO.getName());
        applicationInsert.setDescription(applicationReqVO.getDescription());
        applicationInsert.setProjectId(projectId);
        applicationInsert.setCode(UUID.randomUUID().toString());
        applicationInsert.setType(ApplicationType.CUSTOM.getValue());
        applicationInsert.setFeedbackToken(UUID.randomUUID().toString());
        applicationInsert.setHasGenerated(false);
        return applicationInsert;
    }

    private ApplicationDTO updateAppWithOVN(Long objectVersionNumber, ApplicationDTO applicationUpdate) {
        applicationUpdate.setObjectVersionNumber(objectVersionNumber);
        if (applicationMapper.updateByPrimaryKeySelective(applicationUpdate) != 1) {
            throw new UpdateException(APPLICATION_UPDATE_EXCEPTION);
        }
        return applicationMapper.selectByPrimaryKey(applicationUpdate);
    }

    private ProjectDTO getProject(Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        if (ObjectUtils.isEmpty(projectDTO)) {
            throw new CommonException("error.application.project.not.exist", projectId);
        }
        return projectDTO;
    }
}
