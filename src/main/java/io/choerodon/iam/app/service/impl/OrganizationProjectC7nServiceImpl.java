package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.LabelMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.BarLabelRotationItemVO;
import io.choerodon.iam.api.vo.BarLabelRotationVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.DetailsHelperAssert;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectTypeDTO;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.enums.ProjectCategory;
import io.choerodon.iam.infra.enums.SendSettingBaseEnum;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.ProjectTypeMapper;
import io.choerodon.iam.infra.valitador.ProjectValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
@Service
public class OrganizationProjectC7nServiceImpl implements OrganizationProjectC7nService {

    private static final String ERROR_PROJECT_NOT_EXIST = "error.project.not.exist";
    private static final String ERROR_PROJECT_CATEGORY_EMPTY = "error.project.category.empty";
    public static final String PROJECT = "project";
    public static final String ERROR_ORGANIZATION_PROJECT_NUM_MAX = "error.organization.project.num.max";

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    @Value("${spring.application.name:default}")
    private String serviceName;

    @Value("${choerodon.category.enabled:false}")
    private Boolean categoryEnable;

    private SagaClient sagaClient;

    private UserService userService;
    private UserC7nService userC7nService;

//    private AsgardFeignClient asgardFeignClient;

    private DevopsFeignClient devopsFeignClient;

    private ProjectMapCategoryMapper projectMapCategoryMapper;

    private ProjectMapper projectMapper;

    private ProjectTypeMapper projectTypeMapper;

    private RoleMapper roleMapper;

    private LabelMapper labelMapper;

    private ProjectAssertHelper projectAssertHelper;

    private OrganizationAssertHelper organizationAssertHelper;

    private UserAssertHelper userAssertHelper;

    private MemberRoleService memberRoleService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ProjectValidator projectValidator;

    private TransactionalProducer producer;
    private TenantC7nService tenantC7nService;
    private C7nTenantConfigService c7nTenantConfigService;

    private OrganizationResourceLimitService organizationResourceLimitService;


    public OrganizationProjectC7nServiceImpl(SagaClient sagaClient,
                                             UserService userService,
                                             ProjectMapCategoryMapper projectMapCategoryMapper,
                                             ProjectMapper projectMapper,
                                             ProjectAssertHelper projectAssertHelper,
                                             ProjectTypeMapper projectTypeMapper,
                                             OrganizationAssertHelper organizationAssertHelper,
                                             UserAssertHelper userAssertHelper,
                                             RoleMapper roleMapper,
                                             LabelMapper labelMapper,
                                             MemberRoleService memberRoleService,
                                             ProjectValidator projectValidator,
                                             TransactionalProducer producer,
                                             DevopsFeignClient devopsFeignClient,
                                             TenantC7nService tenantC7nService,
                                             UserC7nService userC7nService,
                                             C7nTenantConfigService c7nTenantConfigService,
                                             OrganizationResourceLimitService organizationResourceLimitService) {
        this.sagaClient = sagaClient;
        this.userService = userService;
        this.userC7nService = userC7nService;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectTypeMapper = projectTypeMapper;
        this.userAssertHelper = userAssertHelper;
        this.roleMapper = roleMapper;
        this.labelMapper = labelMapper;
        this.memberRoleService = memberRoleService;
        this.projectValidator = projectValidator;
        this.producer = producer;
        this.devopsFeignClient = devopsFeignClient;
        this.tenantC7nService = tenantC7nService;
        this.organizationResourceLimitService = organizationResourceLimitService;
        this.c7nTenantConfigService = c7nTenantConfigService;
    }


    @Override
    @Saga(code = PROJECT_CREATE, description = "iam创建项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO createProject(Long organizationId, ProjectDTO projectDTO) {
        organizationResourceLimitService.checkEnableCreateProjectOrThrowE(organizationId);
        ProjectCategoryDTO projectCategoryDTO = projectValidator.validateProjectCategory(projectDTO.getCategory());
        Boolean enabled = projectDTO.getEnabled();
        projectDTO.setEnabled(enabled == null ? true : enabled);
        ProjectDTO res;
        if (devopsMessage) {
            res = sendCreateProjectEvent(projectDTO);
        } else {
            res = create(projectDTO);
            // TODO by wanghao
//            initMemberRole(projectDTO);
        }
        insertProjectMapCategory(projectCategoryDTO.getId(), projectDTO.getId());
        //创建项目成功发送webhook
        Map<String, String> params = new HashMap<>();
        params.put("projectId", String.valueOf(res.getId()));
        params.put("name", res.getName());
        params.put("code", res.getCode());
        params.put("organizationId", String.valueOf(res.getOrganizationId()));
        params.put("enabled", String.valueOf(res.getEnabled()));
        params.put("category", res.getCategory());
        userC7nService.sendNotice(Arrays.asList(res.getCreatedBy()), SendSettingBaseEnum.CREATE_PROJECT.value(), params, res.getOrganizationId(), ResourceLevel.ORGANIZATION);
        return res;
    }


    @Override
    public ProjectDTO create(ProjectDTO projectDTO) {
        Long organizationId = projectDTO.getOrganizationId();
        organizationAssertHelper.notExisted(organizationId);
        projectAssertHelper.codeExisted(projectDTO.getCode(), organizationId);

        if (projectMapper.insertSelective(projectDTO) != 1) {
            throw new CommonException("error.project.create");
        }
        return projectMapper.selectByPrimaryKey(projectDTO);
    }


    private void insertProjectMapCategory(Long categoryId, Long projectId) {
        ProjectMapCategoryDTO example = new ProjectMapCategoryDTO();
        example.setCategoryId(categoryId);
        example.setProjectId(projectId);
        if (projectMapCategoryMapper.insertSelective(example) != 1) {
            throw new InsertException("error.project.map.category.insert");
        }
    }

    private ProjectDTO sendCreateProjectEvent(ProjectDTO project) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType(PROJECT)
                        .withSagaCode(PROJECT_CREATE),
                builder -> {
                    ProjectDTO projectDTO = create(project);
                    // TODO by wanghao
//                    Set<String> roleLabels = initMemberRole(projectDTO);
//                    ProjectEventPayload projectEventPayload = generateProjectEventMsg(projectDTO, roleLabels);
                    ProjectEventPayload projectEventPayload = generateProjectEventMsg(projectDTO, null);
                    builder
                            .withPayloadAndSerialize(projectEventPayload)
                            .withRefId(String.valueOf(projectDTO.getId()))
                            .withSourceId(projectDTO.getOrganizationId());
                    return projectDTO;
                });
    }

    private ProjectEventPayload generateProjectEventMsg(ProjectDTO projectDTO, Set<String> roleLabels) {
        ProjectEventPayload projectEventMsg = new ProjectEventPayload();
        CustomUserDetails details = DetailsHelper.getUserDetails();
        Tenant tenant = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
        if (details != null && details.getUserId() != 0) {
            projectEventMsg.setUserName(details.getUsername());
            projectEventMsg.setUserId(details.getUserId());
        } else {
            Long userId = Long.valueOf(c7nTenantConfigService.queryNonNullCertainConfigValue(projectDTO.getId(), TenantConfigEnum.USER_ID));
            User userDTO = userAssertHelper.userNotExisted(userId);
            projectEventMsg.setUserId(userId);
            projectEventMsg.setUserName(userDTO.getLoginName());
        }
        projectEventMsg.setRoleLabels(roleLabels);
        projectEventMsg.setProjectId(projectDTO.getId());
        projectEventMsg.setProjectCode(projectDTO.getCode());
        projectEventMsg.setProjectCategory(projectDTO.getCategory());
        projectEventMsg.setProjectName(projectDTO.getName());
        projectEventMsg.setImageUrl(projectDTO.getImageUrl());
        projectEventMsg.setOrganizationCode(tenant.getTenantNum());
        projectEventMsg.setOrganizationName(tenant.getTenantName());
        projectEventMsg.setOrganizationId(tenant.getTenantId());
        return projectEventMsg;
    }
//
//    private Set<String> initMemberRole(ProjectDTO project) {
//        List<Role> roles = roleMapper.selectRolesByLabelNameAndType(RoleLabelEnum.PROJECT_OWNER.value(), "role", null);
//        if (roles.isEmpty()) {
//            throw new CommonException("error.role.not.found.by.label", RoleLabelEnum.PROJECT_OWNER.value(), "role");
//        }
//        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
//        if (customUserDetails == null) {
//            throw new CommonException("error.user.not.login");
//        }
//        Long projectId = project.getId();
//        Long userId = customUserDetails.getUserId();
//        Set<String> labelNames = new HashSet<>();
//        roles.forEach(role -> {
//            //创建项目只分配项目层的角色
//            if (ResourceLevel.PROJECT.value().equals(role.getResourceLevel())) {
//                //查出来的符合要求的角色，要拿出来所有的label，发送给devops处理
//                List<Label> labels = labelMapper.selectByRoleId(role.getId());
//                labelNames.addAll(labels.stream().map(Label::getName).collect(Collectors.toList()));
//                MemberRole memberRole = new MemberRole();
//                memberRole.setRoleId(role.getId());
//                memberRole.setMemberType("user");
//                memberRole.setMemberId(userId);
//                memberRole.setSourceId(projectId);
//                memberRole.setSourceType(ResourceLevel.PROJECT.value());
//                memberRoleService.insertSelective(memberRole);
//            }
//        });
//        return labelNames;
//    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public ProjectDTO update(Long organizationId, ProjectDTO projectDTO) {
        updateCheck(projectDTO);
        // 项目编码、类型不可编辑
        projectDTO.setCode(null);
        projectDTO.setCategory(null);
        Tenant organizationDTO = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
        ProjectDTO dto;
        if (devopsMessage) {
            dto = new ProjectDTO();
            CustomUserDetails details = DetailsHelperAssert.userDetailNotExisted();
            User user = userAssertHelper.userNotExisted(UserAssertHelper.WhichColumn.LOGIN_NAME, details.getUsername());
            ProjectEventPayload projectEventMsg = new ProjectEventPayload();
            projectEventMsg.setUserName(details.getUsername());
            projectEventMsg.setUserId(user.getId());
            projectEventMsg.setOrganizationCode(organizationDTO.getTenantNum());
            projectEventMsg.setOrganizationName(organizationDTO.getTenantName());
            ProjectDTO newProjectDTO = updateSelective(projectDTO);
            projectEventMsg.setProjectId(newProjectDTO.getId());
            projectEventMsg.setProjectCode(newProjectDTO.getCode());
            projectEventMsg.setProjectName(newProjectDTO.getName());
            projectEventMsg.setImageUrl(newProjectDTO.getImageUrl());
            BeanUtils.copyProperties(newProjectDTO, dto);
            try {
                String input = mapper.writeValueAsString(projectEventMsg);
                sagaClient.startSaga(PROJECT_UPDATE, new StartInstanceDTO(input, PROJECT, newProjectDTO.getId() + "", ResourceLevel.ORGANIZATION.value(), organizationId));
            } catch (Exception e) {
                throw new CommonException("error.organizationProjectService.updateProject.event", e);
            }
        } else {
            dto = updateSelective(projectDTO);
        }
        return dto;
    }

    @Override
    public ProjectDTO updateSelective(ProjectDTO projectDTO) {
        if (projectMapper.updateByPrimaryKeySelective(projectDTO) != 1) {
            throw new UpdateException("error.project.update");
        }
        return projectMapper.selectByPrimaryKey(projectDTO.getId());
    }

    private void updateCheck(ProjectDTO projectDTO) {
        String name = projectDTO.getName();
        projectAssertHelper.objectVersionNumberNotNull(projectDTO.getObjectVersionNumber());
        if (StringUtils.isEmpty(name)) {
            throw new EmptyParamException("error.project.name.empty");
        }
        if (name.length() < 1 || name.length() > 32) {
            throw new IllegalArgumentException("error.project.name.size");
        }
    }

    @Override
    @Saga(code = PROJECT_ENABLE, description = "iam启用项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO enableProject(Long organizationId, Long projectId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        return updateProjectAndSendEvent(projectId, PROJECT_ENABLE, true, userId);
    }

    @Override
    @Saga(code = PROJECT_DISABLE, description = "iam停用项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO disableProject(Long organizationId, Long projectId, Long userId) {
        if (organizationId != null) {
            organizationAssertHelper.notExisted(organizationId);
        }
        return updateProjectAndSendEvent(projectId, PROJECT_DISABLE, false, userId);
    }

    /**
     * 启用、禁用项目且发送相应通知消息.
     *
     * @param projectId    项目Id
     * @param consumerType saga消息类型
     * @param enabled      是否启用
     * @param userId       用户Id
     * @return 项目信息
     */
    private ProjectDTO updateProjectAndSendEvent(Long projectId, String consumerType, boolean enabled, Long userId) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        projectDTO.setEnabled(enabled);
        // 更新项目
        projectDTO = updateSelective(projectDTO);
        String category = selectCategoryByPrimaryKey(projectId).getCategory();
        projectDTO.setCategory(category);
        if (!(ProjectCategory.AGILE.value().equalsIgnoreCase(category) || ProjectCategory.GENERAL.value().equalsIgnoreCase(category)
                || ProjectCategory.PROGRAM.value().equalsIgnoreCase(category))) {
            throw new CommonException("error.project.type");
        }
        // 发送通知消息
        sendEvent(consumerType, enabled, userId, null, projectDTO);
        return projectDTO;
    }

    /**
     * 启用、禁用项目时，发送相应通知消息.
     *
     * @param consumerType saga消息类型
     * @param enabled      是否启用
     * @param userId       用户Id
     * @param programId    项目群Id
     * @param projectDTO   项目DTO
     */
    private void sendEvent(String consumerType, boolean enabled, Long userId, Long programId, ProjectDTO projectDTO) {
        Long projectId = projectDTO.getId();
        if (devopsMessage) {
            ProjectEventPayload payload = new ProjectEventPayload();
            payload.setProjectId(projectId);
            payload.setProjectCategory(projectDTO.getCategory());
            payload.setProgramId(programId);
            //saga
            try {
                String input = mapper.writeValueAsString(payload);
                sagaClient.startSaga(consumerType, new StartInstanceDTO(input, PROJECT, "" + payload.getProjectId(), ResourceLevel.ORGANIZATION.value(), projectDTO.getOrganizationId()));
            } catch (Exception e) {
                throw new CommonException("error.organizationProjectService.enableOrDisableProject", e);
            }
            if (!enabled) {
                //给asgard发送禁用定时任务通知
//                asgardFeignClient.disableProj(projectId);
            }
            // 给项目下所有用户发送通知
//            List<Long> userIds = projectMapper.listUserIds(projectId);
//            Map<String, Object> params = new HashMap<>();
//            ProjectDTO dto = projectMapper.selectByPrimaryKey(projectId);
//            params.put("projectName", dto.getName());
//            if (PROJECT_DISABLE.equals(consumerType)) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("projectId", dto.getId());
//                jsonObject.put("enabled", dto.getEnabled());
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.DISABLE_PROJECT.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.DISABLE_PROJECT.value()),
//                        jsonObject,
//                        projectDTO.getLastUpdateDate(),
//                        userService.getWebHookUser(userId)
//                );
//                userService.sendNotice(userId, userIds, "disableProject", params, projectId, webHookJsonSendDTO);
//            } else if (PROJECT_ENABLE.equals(consumerType)) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("projectId", dto.getId());
//                jsonObject.put("enabled", dto.getEnabled());
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.ENABLE_PROJECT.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.ENABLE_PROJECT.value()),
//                        jsonObject,
//                        projectDTO.getLastUpdateDate(),
//                        userService.getWebHookUser(userId)
//                );
//                userService.sendNotice(userId, userIds, "enableProject", params, projectId, webHookJsonSendDTO);
//            }
        }
    }

    @Override
    public void check(ProjectDTO projectDTO) {
        boolean checkCode = !StringUtils.isEmpty(projectDTO.getCode());
        if (!checkCode) {
            throw new CommonException("error.project.code.empty");
        } else {
            checkCode(projectDTO);
        }
    }

    private void checkCode(ProjectDTO projectDTO) {
        boolean createCheck = StringUtils.isEmpty(projectDTO.getId());
        ProjectDTO project = new ProjectDTO();
        project.setOrganizationId(projectDTO.getOrganizationId());
        project.setCode(projectDTO.getCode());
        if (createCheck) {
            boolean existed = projectMapper.selectOne(project) != null;
            if (existed) {
                throw new CommonException("error.project.code.exist");
            }
        } else {
            Long id = projectDTO.getId();
            ProjectDTO dto = projectMapper.selectOne(project);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.project.code.exist");
            }
        }
    }

    @Override
    public Map<String, Object> getProjectsByType(Long organizationId) {
        //1.获取所有类型
        List<ProjectTypeDTO> list = projectTypeMapper.selectAll();
        List<String> legend = list.stream().map(ProjectTypeDTO::getName).collect(Collectors.toList());
        List<Map<String, Object>> data = new ArrayList<>();
        //2.获取类型下所有项目名
        list.forEach(type -> {
            List<String> projectNames = projectMapper.selectProjectNameByType(type.getCode(), organizationId);
            Map<String, Object> dataMap = new HashMap<>(5);
            dataMap.put("value", projectNames.size());
            dataMap.put("name", type.getName());
            dataMap.put("projects", projectNames);
            data.add(dataMap);
        });
        //3.获取无类型的所有项目名
        List<String> projsNoType = projectMapper.selectProjectNameNoType(organizationId);
        Map<String, Object> noTypeProjectList = new HashMap<>(5);
        noTypeProjectList.put("value", projsNoType.size());
        noTypeProjectList.put("name", "无");
        noTypeProjectList.put("projects", projsNoType);
        legend.add("无");
        data.add(noTypeProjectList);
        //4.构造返回map
        Map<String, Object> map = new HashMap<>(5);
        map.put("legend", legend);
        map.put("data", data);
        return map;
    }

    @Override
    public ProjectDTO selectCategoryByPrimaryKey(Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectCategoryByPrimaryKey(projectId);
        if (projectDTO == null) {
            throw new CommonException(ERROR_PROJECT_NOT_EXIST, projectId);
        }
        List<ProjectCategoryDTO> projectCategories = projectDTO.getCategories();
        if (CollectionUtils.isEmpty(projectCategories)) {
            throw new CommonException(ERROR_PROJECT_CATEGORY_EMPTY);
        }
        projectDTO.setCategory(projectCategories.get(0).getCode());
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> getAgileProjects(Long organizationId, String param) {
        List<ProjectDTO> projectDTOS;
        if (categoryEnable) {
            projectDTOS = projectMapper.selectByOrgIdAndCategoryEnable(organizationId, ProjectCategory.GENERAL.value(), param);
        } else {
            projectDTOS = projectMapper.selectByOrgIdAndCategory(organizationId, param);
        }
        return projectDTOS;
    }

    @Override
    public ProjectDTO getProjectByOrgIdAndCode(Long organizationId, String code) {
        ProjectDTO projectQuery = new ProjectDTO();
        projectQuery.setOrganizationId(organizationId);
        projectQuery.setCode(code);
        return projectMapper.selectOne(projectQuery);
    }

    @Override
    public List<ProjectDTO> listProjectsByOrgId(Long organizationId) {
        ProjectDTO projectQuery = new ProjectDTO();
        projectQuery.setOrganizationId(organizationId);
        return projectMapper.select(projectQuery);
    }

    @Override
    public Page<ProjectDTO> pagingQuery(Long organizationId, PageRequest pageRequest, ProjectDTO projectDTO, String params) {
        int size = pageRequest.getSize();
        boolean doPage = (size != 0);
        String sortString = getSortStringForPageQuery(pageRequest.getSort());
        if (doPage) {
            return PageHelper.doPage(pageRequest, () -> projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));

        } else {
            Page<ProjectDTO> result = new Page<>();
            result.getContent().addAll(projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));
            result.setSize(result.size());
            return result;
        }
    }

    @Override
    public BarLabelRotationVO countDeployRecords(Set<Long> projectIds, Date startTime, Date endTime) {
        BarLabelRotationVO barLabelRotationVO = new BarLabelRotationVO();

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            barLabelRotationVO.getDateList().add(date);
            startDate = startDate.plusDays(1);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        projectIds.forEach(id -> {
            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(id);
            BarLabelRotationItemVO labelRotationItemVO = devopsFeignClient.countByDate(id, simpleDateFormat.format(startTime), simpleDateFormat.format(endTime)).getBody();
            labelRotationItemVO.setName(projectDTO.getName());
            labelRotationItemVO.setId(id);
            barLabelRotationVO.getProjectDataList().add(labelRotationItemVO);
        });
        return barLabelRotationVO;
    }

    @Override
    public List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name) {
        return projectMapper.selectProjectsByOrgIdAndNameWithLimit(organizationId, name, 20);
    }

    private String getSortStringForPageQuery(Sort sort) {
        Iterator<Sort.Order> iterator = sort.iterator();

        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Sort.Order t = iterator.next();
            String field;

            if ("name".equals(t.getProperty())) {
                field = "fp.name";
            } else if ("id".equals(t.getProperty())) {
                field = "fp.id";
            } else {
                throw new FeignException("error.field.not.supported.for.sort", t.getProperty());
            }
            list.add(field + " " + t.getDirection());
        }
        return list.stream().collect(Collectors.joining(","));
    }

}
