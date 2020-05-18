package io.choerodon.iam.app.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.choerodon.iam.app.service.OrganizationProjectService;
import io.choerodon.iam.app.service.OrganizationService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.annotation.OperateLog;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.dto.*;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.enums.ProjectCategory;
import io.choerodon.iam.infra.enums.RoleLabel;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.valitador.ProjectValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.infra.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.*;

/**
 * @author flyleft
 * @since 2018/3/26
 */
@Service
@RefreshScope
public class OrganizationProjectServiceImpl implements OrganizationProjectService {
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

    @Value("${choerodon.organization.resourceLimit.projectMaxNumber:20}")
    private Integer projectMaxNumber;

    private SagaClient sagaClient;

    private UserService userService;

    private AsgardFeignClient asgardFeignClient;

    private DevopsFeignClient devopsFeignClient;

    private ProjectMapCategoryMapper projectMapCategoryMapper;

    private ProjectMapper projectMapper;

    private ProjectTypeMapper projectTypeMapper;

    private RoleC7nMapper roleC7nMapper;

    private LabelMapper labelMapper;

    private LabelC7nMapper labelC7nMapper;

    private ProjectAssertHelper projectAssertHelper;

    private OrganizationAssertHelper organizationAssertHelper;

    private UserAssertHelper userAssertHelper;

    private RoleMemberService roleMemberService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ProjectValidator projectValidator;

    private TransactionalProducer producer;
    private OrganizationService organizationService;


    public OrganizationProjectServiceImpl(SagaClient sagaClient,
                                          UserService userService,
                                          AsgardFeignClient asgardFeignClient,
                                          ProjectMapCategoryMapper projectMapCategoryMapper,
                                          ProjectMapper projectMapper,
                                          ProjectAssertHelper projectAssertHelper,
                                          ProjectTypeMapper projectTypeMapper,
                                          OrganizationAssertHelper organizationAssertHelper,
                                          UserAssertHelper userAssertHelper,
                                          RoleC7nMapper roleC7nMapper,
                                          LabelMapper labelMapper,
                                          LabelC7nMapper labelC7nMapper,
                                          RoleMemberService roleMemberService,
                                          ProjectValidator projectValidator,
                                          TransactionalProducer producer,
                                          DevopsFeignClient devopsFeignClient,
                                          OrganizationService organizationService) {
        this.sagaClient = sagaClient;
        this.userService = userService;
        this.asgardFeignClient = asgardFeignClient;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectTypeMapper = projectTypeMapper;
        this.userAssertHelper = userAssertHelper;
        this.roleC7nMapper = roleC7nMapper;
        this.labelMapper = labelMapper;
        this.labelC7nMapper = labelC7nMapper;
        this.roleMemberService = roleMemberService;
        this.projectValidator = projectValidator;
        this.producer = producer;
        this.devopsFeignClient = devopsFeignClient;
        this.organizationService = organizationService;
    }


    @Override
    @Saga(code = PROJECT_CREATE, description = "iam创建项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "createProject", content = "%s创建项目【%s】", level = {ResourceLevel.ORGANIZATION})
    public ProjectDTO createProject(Long organizationId, ProjectDTO projectDTO) {
        checkEnableCreateProjectOrThrowE(organizationId);
        ProjectCategoryDTO projectCategoryDTO = projectValidator.validateProjectCategory(projectDTO.getCategory());
        Boolean enabled = projectDTO.getEnabled();
        projectDTO.setEnabled(enabled == null ? true : enabled);
        ProjectDTO res;
        if (devopsMessage) {
            res = sendCreateProjectEvent(projectDTO);
        } else {
            res = create(projectDTO);
            initMemberRole(projectDTO);
        }
        insertProjectMapCategory(projectCategoryDTO.getId(), projectDTO.getId());
        //创建项目成功发送webhook
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", res.getId());
        jsonObject.put("name", res.getName());
        jsonObject.put("code", res.getCode());
        jsonObject.put("organizationId", res.getOrganizationId());
        jsonObject.put("enabled", res.getEnabled());
        jsonObject.put("category", res.getCategory());


        // // TODO notify-service
//        WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                SendSettingBaseEnum.CREATE_PROJECT.value(),
//                SendSettingBaseEnum.map.get(SendSettingBaseEnum.CREATE_PROJECT.value()),
//                jsonObject,
//                res.getCreationDate(),
//                userService.getWebHookUser(res.getCreatedBy())
//        );
//        Map<String, Object> params = new HashMap<>();
//
//        userService.sendNotice(DetailsHelper.getUserDetails().getUserId(), Arrays.asList(res.getCreatedBy()), SendSettingBaseEnum.CREATE_PROJECT.value(), params, res.getOrganizationId(), webHookJsonSendDTO);
        return res;
    }

    private void checkEnableCreateProjectOrThrowE(Long organizationId) {
        if (Boolean.FALSE.equals(checkEnableCreateProject(organizationId))) {
            throw new CommonException(ERROR_ORGANIZATION_PROJECT_NUM_MAX);
        }
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


    public Boolean checkEnableCreateProject(Long organizationId) {
        if (organizationService.checkOrganizationIsNew(organizationId)) {
            int num = organizationService.countProjectNum(organizationId);
            return num < projectMaxNumber;
        }
        return true;
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
                    Set<String> roleLabels = initMemberRole(projectDTO);
                    ProjectEventPayload projectEventPayload = generateProjectEventMsg(projectDTO, roleLabels);
                    builder
                            .withPayloadAndSerialize(projectEventPayload)
                            .withRefId(String.valueOf(projectDTO.getId()))
                            .withSourceId(projectDTO.getOrganizationId());
                    return projectDTO;
                });
    }

    // // TODO 等待Tenant更新完成
    private ProjectEventPayload generateProjectEventMsg(ProjectDTO projectDTO, Set<String> roleLabels) {
//        ProjectEventPayload projectEventMsg = new ProjectEventPayload();
//        CustomUserDetails details = DetailsHelper.getUserDetails();
//        OrganizatioTenDTO organizationDTO = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
//        if (details != null && details.getUserId() != 0) {
//            projectEventMsg.setUserName(details.getUsername());
//            projectEventMsg.setUserId(details.getUserId());
//        } else {
//            Long userId = organizationDTO.getUserId();
//            User userDTO = userAssertHelper.userNotExisted(userId);
//            projectEventMsg.setUserId(userId);
//            projectEventMsg.setUserName(userDTO.getLoginName());
//        }
//        projectEventMsg.setRoleLabels(roleLabels);
//        projectEventMsg.setProjectId(projectDTO.getId());
//        projectEventMsg.setProjectCode(projectDTO.getCode());
//        projectEventMsg.setProjectCategory(projectDTO.getCategory());
//        projectEventMsg.setProjectName(projectDTO.getName());
//        projectEventMsg.setImageUrl(projectDTO.getImageUrl());
//        projectEventMsg.setOrganizationCode(organizationDTO.getCode());
//        projectEventMsg.setOrganizationName(organizationDTO.getName());
//        projectEventMsg.setOrganizationId(organizationDTO.getId());
        return new ProjectEventPayload();
    }

    private Set<String> initMemberRole(ProjectDTO project) {
        List<Role> roles = roleC7nMapper.selectRolesByLabelNameAndType(RoleLabel.PROJECT_OWNER.value(), "role", null);
        if (roles.isEmpty()) {
            throw new CommonException("error.role.not.found.by.label", RoleLabel.PROJECT_OWNER.value(), "role");
        }
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            throw new CommonException("error.user.not.login");
        }
        Long projectId = project.getId();
        Long userId = customUserDetails.getUserId();
        Set<String> labelNames = new HashSet<>();
        roles.forEach(role -> {
            //创建项目只分配项目层的角色
            if (ResourceLevel.PROJECT.value().equals(role.getLevel())) {
                //查出来的符合要求的角色，要拿出来所有的label，发送给devops处理
                List<LabelDTO> labels = labelC7nMapper.selectByRoleId(role.getId());
                labelNames.addAll(labels.stream().map(LabelDTO::getName).collect(Collectors.toList()));
                MemberRole memberRole = new MemberRole();
                memberRole.setRoleId(role.getId());
                memberRole.setMemberType("user");
                memberRole.setMemberId(userId);
                memberRole.setSourceId(projectId);
                memberRole.setSourceType(ResourceLevel.PROJECT.value());
                roleMemberService.insertSelective(memberRole);
            }
        });
        return labelNames;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public ProjectDTO update(Long organizationId, ProjectDTO projectDTO) {
        updateCheck(projectDTO);
        // 项目编码、类型不可编辑
        projectDTO.setCode(null);
        projectDTO.setCategory(null);
        // TODO 等待Tenant更新完成
//        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
//        ProjectDTO dto;
//        if (devopsMessage) {
//            dto = new ProjectDTO();
//            CustomUserDetails details = DetailsHelperAssert.userDetailNotExisted();
//            UserDTO user = userAssertHelper.userNotExisted(WhichColumn.LOGIN_NAME, details.getUsername());
//            ProjectEventPayload projectEventMsg = new ProjectEventPayload();
//            projectEventMsg.setUserName(details.getUsername());
//            projectEventMsg.setUserId(user.getId());
//            projectEventMsg.setOrganizationCode(organizationDTO.getCode());
//            projectEventMsg.setOrganizationName(organizationDTO.getName());
//            ProjectDTO newProjectDTO = updateSelective(projectDTO);
//            projectEventMsg.setProjectId(newProjectDTO.getId());
//            projectEventMsg.setProjectCode(newProjectDTO.getCode());
//            projectEventMsg.setProjectName(newProjectDTO.getName());
//            projectEventMsg.setImageUrl(newProjectDTO.getImageUrl());
//            BeanUtils.copyProperties(newProjectDTO, dto);
//            try {
//                String input = mapper.writeValueAsString(projectEventMsg);
//                sagaClient.startSaga(PROJECT_UPDATE, new StartInstanceDTO(input, PROJECT, newProjectDTO.getId() + "", ResourceLevel.ORGANIZATION.value(), organizationId));
//            } catch (Exception e) {
//                throw new CommonException("error.organizationProjectService.updateProject.event", e);
//            }
//        } else {
//            dto = updateSelective(projectDTO);
//        }
        return new ProjectDTO();
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
    @OperateLog(type = "enableProject", content = "%s启用项目【%s】", level = {ResourceLevel.ORGANIZATION})
    public ProjectDTO enableProject(Long organizationId, Long projectId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        return updateProjectAndSendEvent(projectId, PROJECT_ENABLE, true, userId);
    }

    @Override
    @Saga(code = PROJECT_DISABLE, description = "iam停用项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "disableProject", content = "%s禁用项目【%s】", level = {ResourceLevel.ORGANIZATION})
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
                asgardFeignClient.disableProj(projectId);
            }
            // 给项目下所有用户发送通知
            List<Long> userIds = projectMapper.listUserIds(projectId);
            Map<String, Object> params = new HashMap<>();
            ProjectDTO dto = projectMapper.selectByPrimaryKey(projectId);
            params.put("projectName", dto.getName());
            // TODO notify-service
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
        boolean doPage = (pageRequest.getSize() != 0);
        String sortString = getSortStringForPageQuery(pageRequest.getSort());
        if (doPage) {
            return PageHelper.doPageAndSort(pageRequest, () -> projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));
        } else {
            Page<ProjectDTO> result = new Page<>();
            result.setContent(projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));
            result.setTotalElements(result.size());
            return result;
        }
    }

    @Override
    public List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name) {
        return projectMapper.selectProjectsByOrgIdAndNameWithLimit(organizationId, name, 20);
    }

    private String getSortStringForPageQuery(Sort sort) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order value : sort) {
            String field;
            if ("name".equals(value.getProperty())) {
                field = "fp.name";
            } else if ("id".equals(value.getProperty())) {
                field = "fp.id";
            } else {
                throw new FeignException("error.field.not.supported.for.sort", value.getProperty());
            }
            field = field + " " + value.getDirection();
            orders.add(field);
        }
        return String.join(",", orders);
    }

}
