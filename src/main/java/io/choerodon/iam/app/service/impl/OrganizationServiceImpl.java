package io.choerodon.iam.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.app.service.OrganizationService;
import io.choerodon.iam.infra.annotation.OperateLog;
import io.choerodon.iam.infra.asserts.DetailsHelperAssert;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.dto.OrgSharesDTO;
import io.choerodon.iam.infra.dto.OrganizationDTO;
import io.choerodon.iam.infra.dto.OrganizationSimplifyDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.payload.OrganizationEventPayload;
import io.choerodon.iam.infra.dto.payload.OrganizationPayload;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.MemberRoleMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.common.query.Comparison;
import org.hzero.mybatis.common.query.WhereField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.*;

/**
 * @author wuguokai
 */
@Component
public class OrganizationServiceImpl implements OrganizationService {

    public static final String ORGANIZATION_DOES_NOT_EXIST_EXCEPTION = "error.organization.does.not.exist";
    public static final String ORGANIZATION_LIMIT_DATE = "2020-03-24";

    private AsgardFeignClient asgardFeignClient;

    private boolean devopsMessage;

    private SagaClient sagaClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserService userService;

    private OrganizationAssertHelper organizationAssertHelper;

    private ProjectMapper projectMapper;

    private UserMapper userMapper;

    private UserC7nMapper userC7nMapper;

    private OrganizationMapper organizationMapper;

    private RoleMapper roleMapper;

    private RoleC7nMapper roleC7nMapper;

    private MemberRoleMapper memberRoleMapper;

    private DevopsFeignClient devopsFeignClient;

    private TenantMapper tenantMapper;

    private TenantC7nMapper tenantC7nMapper;


    public OrganizationServiceImpl(@Value("${choerodon.devops.message:false}") Boolean devopsMessage,
                                   SagaClient sagaClient,
                                   UserService userService,
                                   AsgardFeignClient asgardFeignClient,
                                   OrganizationAssertHelper organizationAssertHelper,
                                   ProjectMapper projectMapper,
                                   UserMapper userMapper,
                                   UserC7nMapper userC7nMapper,
                                   OrganizationMapper organizationMapper,
                                   RoleMapper roleMapper,
                                   RoleC7nMapper roleC7nMapper,
                                   MemberRoleMapper memberRoleMapper,
                                   DevopsFeignClient devopsFeignClient,
                                   TenantMapper tenantMapper,
                                   TenantC7nMapper tenantC7nMapper) {
        this.devopsMessage = devopsMessage;
        this.sagaClient = sagaClient;
        this.userService = userService;
        this.asgardFeignClient = asgardFeignClient;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.userC7nMapper = userC7nMapper;
        this.roleC7nMapper = roleC7nMapper;
        this.organizationMapper = organizationMapper;
        this.roleMapper = roleMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.devopsFeignClient = devopsFeignClient;
        this.tenantMapper = tenantMapper;
        this.tenantC7nMapper = tenantC7nMapper;
    }

    // TODO 等待Tenant更新完成
    @Override
    public OrganizationDTO queryOrganizationById(Long organizationId) {
//        Tenant tenant = organizationAssertHelper.notExisted(organizationId);
//        ProjectDTO example = new ProjectDTO();
//        example.setOrganizationId(organizationId);
//        List<ProjectDTO> projects = projectMapper.select(example);
//        tenant.setProjects(projects);
//        tenant.setProjectCount(projects.size());
//
//        Long userId = tenant.getUserId();
//        UserDTO user = userMapper.selectByPrimaryKey(userId);
//        if (user != null) {
//            organizationDTO.setOwnerLoginName(user.getLoginName());
//            organizationDTO.setOwnerRealName(user.getRealName());
//            organizationDTO.setOwnerPhone(user.getPhone());
//            organizationDTO.setOwnerEmail(user.getEmail());
//        }
        return new OrganizationDTO();
    }

    @Override
    public List<OrganizationDTO> queryOrganizationsByName(String organizationName) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName(organizationName);
        Criteria criteria = new Criteria(organizationDTO);
        criteria.where(new WhereField(OrganizationDTO.FIELD_NAME, Comparison.LIKE));
        return organizationMapper.selectOptional(organizationDTO, criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_UPDATE, description = "iam更新组织", inputSchemaClass = OrganizationPayload.class)
    @OperateLog(type = "updateOrganization", content = "%s修改组织【%s】的信息", level = {ResourceLevel.SITE})
    public OrganizationDTO updateOrganization(Long organizationId, OrganizationDTO organizationDTO, String resourceLevel, Long sourceId) {
        preUpdate(organizationId, organizationDTO);

        organizationDTO = doUpdate(organizationDTO);
        if (devopsMessage) {
            OrganizationPayload payload = new OrganizationPayload();
            payload
                    .setId(organizationDTO.getId())
                    .setName(organizationDTO.getName())
                    .setCode(organizationDTO.getCode())
                    .setUserId(organizationDTO.getUserId())
                    .setAddress(organizationDTO.getAddress())
                    .setImageUrl(organizationDTO.getImageUrl());
            try {
                String input = mapper.writeValueAsString(payload);
                sagaClient.startSaga(ORG_UPDATE, new StartInstanceDTO(input, "organization", organizationId + "", resourceLevel, sourceId));
            } catch (JsonProcessingException e) {
                throw new CommonException("error.organization.update.payload.to.string");
            } catch (Exception e) {
                throw new CommonException("error.organization.update.event", e);
            }
        }
        return organizationDTO;
    }

    private OrganizationDTO doUpdate(OrganizationDTO organizationDTO) {
        if (organizationMapper.updateByPrimaryKeySelective(organizationDTO) != 1) {
            throw new UpdateException("error.organization.update");
        }
        return organizationMapper.selectByPrimaryKey(organizationDTO);
    }

    // TODO 等待Tenant更新完成
    private void preUpdate(Long organizationId, OrganizationDTO organizationDTO) {
//        OrganizationDTO organization = organizationAssertHelper.notExisted(organizationId);
//        organizationDTO.setId(organizationId);
//        //code和创建人不可修改
//        organizationDTO.setUserId(organization.getUserId());
//        organizationDTO.setCode(organizati2on.getCode());
//        if (ObjectUtils.isEmpty(organizationDTO.getEnabled())) {
//            organizationDTO.setEnabled(true);
//        }
    }

    @Override
    public OrganizationDTO queryOrganizationWithRoleById(Long organizationId) {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        OrganizationDTO dto = queryOrganizationById(organizationId);
        long userId = customUserDetails.getUserId();

        List<ProjectDTO> projects = projectMapper.selectUserProjectsUnderOrg(userId, organizationId, null);
        dto.setProjects(projects);
        dto.setProjectCount(projects.size());

        List<Role> roles =
                roleC7nMapper.queryRolesInfoByUser(ResourceLevel.ORGANIZATION.value(), organizationId, userId);
        dto.setRoles(roles);
        return dto;
    }

    @Override
    public Page<OrganizationDTO> pagingQuery(PageRequest pageRequest, String name, String code, String ownerRealName, Boolean enabled, String params) {

        return PageHelper.doPageAndSort(pageRequest, () ->
                organizationMapper.fulltextSearch(name, code, ownerRealName, enabled, params));
    }

    // TODO 等待Tenant更新完成
    @Override
    @Saga(code = ORG_ENABLE, description = "iam启用组织", inputSchemaClass = OrganizationEventPayload.class)
    @OperateLog(type = "enableOrganization", content = "%s启用组织【%s】", level = {ResourceLevel.SITE})
    public OrganizationDTO enableOrganization(Long organizationId, Long userId) {
//        OrganizationDTO organization = organizationAssertHelper.notExisted(organizationId);
//        organization.setEnabled(true);
//        return updateAndSendEvent(organization, ORG_ENABLE, userId);
        return new OrganizationDTO();
    }

    // TODO 等待Tenant更新完成
    @Override
    @Saga(code = ORG_DISABLE, description = "iam停用组织", inputSchemaClass = OrganizationEventPayload.class)
    @OperateLog(type = "disableOrganization", content = "%s停用组织【%s】", level = {ResourceLevel.SITE})
    public OrganizationDTO disableOrganization(Long organizationId, Long userId) {
//        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(organizationId);
//        organizationDTO.setEnabled(false);
//        return updateAndSendEvent(organizationDTO, ORG_DISABLE, userId);
        return new OrganizationDTO();
    }

    // TODO 等待notify-service更新完成
    private OrganizationDTO updateAndSendEvent(OrganizationDTO organization, String consumerType, Long userId) {
//        OrganizationDTO organizationDTO = doUpdate(organization);
//        if (devopsMessage) {
//            OrganizationEventPayload payload = new OrganizationEventPayload();
//            payload.setOrganizationId(organization.getId());
//            //saga
//            try {
//                String input = mapper.writeValueAsString(payload);
//                sagaClient.startSaga(consumerType, new StartInstanceDTO(input, "organization", payload.getOrganizationId() + ""));
//            } catch (Exception e) {
//                throw new CommonException("error.organizationService.enableOrDisable.event", e);
//            }
//            //给asgard发送禁用定时任务通知
//            asgardFeignClient.disableOrg(organization.getId());
//            // 给组织下所有用户发送通知
//            List<Long> userIds = organizationMapper.listMemberIds(organization.getId(), "organization");
//            Map<String, Object> params = new HashMap<>();
//            params.put("organizationName", organizationDTO.getName());
//            if (ORG_DISABLE.equals(consumerType)) {
//                //封装web hook json的数据
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("organizationId", organizationDTO.getId());
//                jsonObject.put("code", organizationDTO.getCode());
//                jsonObject.put("name", organizationDTO.getName());
//                jsonObject.put("enabled", organizationDTO.getEnabled());
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.DISABLE_ORGANIZATION.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.DISABLE_ORGANIZATION.value()),
//                        jsonObject,
//                        organizationDTO.getCreationDate(),
//                        userService.getWebHookUser(organizationDTO.getCreatedBy())
//                );
//                userService.sendNotice(userId, userIds, "disableOrganization", params, organization.getId(), webHookJsonSendDTO);
//            } else if (ORG_ENABLE.equals(consumerType)) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("organizationId", organizationDTO.getId());
//                jsonObject.put("code", organizationDTO.getCode());
//                jsonObject.put("name", organizationDTO.getName());
//                jsonObject.put("enabled", organizationDTO.getEnabled());
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.ENABLE_ORGANIZATION.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.ENABLE_ORGANIZATION.value()),
//                        jsonObject,
//                        organizationDTO.getCreationDate(),
//                        userService.getWebHookUser(organizationDTO.getCreatedBy())
//                );
//                userService.sendNotice(userId, userIds, "enableOrganization", params, organization.getId(), webHookJsonSendDTO);
//            }
//        }
        return new OrganizationDTO();
    }

    @Override
    public void check(OrganizationDTO organization) {
        Boolean checkCode = !StringUtils.isEmpty(organization.getCode());
        if (!checkCode) {
            throw new CommonException("error.organization.code.empty");
        } else {
            checkCode(organization);
        }
    }

    @Override
    public Page<User> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, PageRequest pageRequest, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> userC7nMapper.selectUsersByLevelAndOptions(ResourceLevel.ORGANIZATION.value(), organizationId, userId, email, param));
    }

    @Override
    public List<OrganizationDTO> queryByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        } else {
            return organizationMapper.selectByIds(ids);
        }
    }

    private void checkCode(OrganizationDTO organization) {
        Boolean createCheck = StringUtils.isEmpty(organization.getId());
        String code = organization.getCode();
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setCode(code);
        if (createCheck) {
            Boolean existed = organizationMapper.selectOne(organizationDTO) != null;
            if (existed) {
                throw new CommonException("error.organization.code.exist");
            }
        } else {
            Long id = organization.getId();
            OrganizationDTO dto = organizationMapper.selectOne(organizationDTO);
            Boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.organization.code.exist");
            }
        }
    }

    @Override
    public Page<OrganizationSimplifyDTO> getAllOrgs(PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> organizationMapper.selectAllOrgIdAndName());
    }


    @Override
    public Page<OrgSharesDTO> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new Page<>();
        }
        return PageHelper.doPageAndSort(pageRequest, () -> organizationMapper.selectSpecified(orgIds, name, code, enabled, params));
    }

    @Override
    public OrganizationDTO checkNotExistAndGet(Long orgId) {
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(orgId);
        if (organizationDTO == null) {
            throw new CommonException("error.organization.not.exist");
        }
        return organizationDTO;
    }

    @Override
    public ProjectOverViewVO projectOverview(Long organizationId) {
        ProjectOverViewVO projectOverViewVO = organizationMapper.projectOverview(organizationId);
        if (projectOverViewVO == null) {
            return new ProjectOverViewVO(0, 0);
        }
        return projectOverViewVO;
    }

    @Override
    public List<ProjectOverViewVO> appServerOverview(Long organizationId) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setOrganizationId(organizationId);
        List<ProjectDTO> projectDTOS = projectMapper.select(projectDTO);
        if (org.springframework.util.CollectionUtils.isEmpty(projectDTOS)) {
            return Collections.emptyList();
        }
        List<ProjectOverViewVO> projectOverViewVOS = new ArrayList<>();
        List<Long> longList = projectDTOS.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        Map<Long, Integer> map = devopsFeignClient.countAppServerByProjectId(longList.get(0), longList).getBody();
        projectDTOS.stream().distinct().forEach(v -> {
            ProjectOverViewVO projectOverViewVO = new ProjectOverViewVO();
            projectOverViewVO.setId(v.getId());
            projectOverViewVO.setProjectName(v.getName());
            projectOverViewVO.setAppServerSum(map.get(v.getId()));
            projectOverViewVOS.add(projectOverViewVO);
        });
        List<ProjectOverViewVO> collect = projectOverViewVOS
                .stream()
                .sorted(Comparator.comparing(ProjectOverViewVO::getAppServerSum).reversed())
                .collect(Collectors.toList());
        List<ProjectOverViewVO> reOverViewVOS = new ArrayList<>();
        List<ProjectOverViewVO> temOverViewVOS = new ArrayList<>();
        collect.stream().forEach(projectOverViewVO -> {
            if (reOverViewVOS.size() < 24) {
                reOverViewVOS.add(projectOverViewVO);
            }
            if (reOverViewVOS.size() >= 24) {
                temOverViewVOS.add(projectOverViewVO);
            }
        });
        ProjectOverViewVO projectOverViewVO1 = new ProjectOverViewVO();
        int sum = temOverViewVOS.stream().mapToInt(ProjectOverViewVO::getAppServerSum).sum();
        //项目数量不足9个不要其他剩余
        if (reOverViewVOS.size() <= 9) {
            return reOverViewVOS;
        }
        projectOverViewVO1.setProjectName("其他剩余");
        projectOverViewVO1.setAppServerSum(sum);
        reOverViewVOS.add(projectOverViewVO1);
        return reOverViewVOS;
    }

    @Override
    public boolean checkOrganizationIsNew(Long organizationId) {
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(organizationId);
        if (organizationDTO == null) {
            throw new CommonException(ORGANIZATION_DOES_NOT_EXIST_EXCEPTION);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(ORGANIZATION_LIMIT_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return organizationDTO.getCreationDate().after(date);
    }

    @Override
    public int countProjectNum(Long organizationId) {
        ProjectDTO example = new ProjectDTO();
        example.setOrganizationId(organizationId);
        return projectMapper.selectCount(example);
    }

    @Override
    public int countUserNum(Long organizationId) {
        User example = new User();
        example.setOrganizationId(organizationId);
        return userMapper.selectCount(example);
    }

    @Override
    public List<Long> getoRoganizationByName(String name) {
        return organizationMapper.getoRoganizationByName(name);
    }

    @Override
    public Set<TenantVO> selectSelfTenants(TenantDTO params) {
        CustomUserDetails self = UserUtils.getUserDetails();
        params.setUserId(self.getUserId());
        List<TenantDTO> tenantDTOS = tenantMapper.selectUserTenant(params);
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        boolean isAdmin = false;
        if (customUserDetails.getAdmin() != null) {
            isAdmin = customUserDetails.getAdmin();
        }
        return getOwnedOrganizations(customUserDetails.getUserId(), isAdmin, tenantDTOS);
    }

    private Set<TenantVO> getOwnedOrganizations(Long userId, boolean isAdmin, List<TenantDTO> tenantDTOS) {
        Set<TenantVO> tenantVOS = new HashSet<>();
        if (CollectionUtils.isEmpty(tenantDTOS)) {
            return Collections.emptySet();
        }
        for (TenantDTO tenantDTO : tenantDTOS) {
            if (isTenantAdmin(userId, tenantDTO.getTenantNum())) {
                TenantVO tenantVO = new TenantVO();
                tenantVO.setTenantId(tenantDTO.getTenantId());
                tenantVO.setTenantNum(tenantDTO.getTenantNum());
                tenantVO.setTenantName(tenantDTO.getTenantName());
                tenantVO.setInto(true);
                tenantVOS.add(tenantVO);
                continue;
            }
            if (isAdmin) {
                TenantVO tenantVO = new TenantVO();
                tenantVO.setTenantId(tenantDTO.getTenantId());
                tenantVO.setTenantNum(tenantDTO.getTenantNum());
                tenantVO.setTenantName(tenantDTO.getTenantName());
                tenantVO.setInto(true);
                tenantVOS.add(tenantVO);
                continue;
            }
            else{
                TenantVO tenantVO = new TenantVO();
                tenantVO.setTenantId(tenantDTO.getTenantId());
                tenantVO.setTenantNum(tenantDTO.getTenantNum());
                tenantVO.setTenantName(tenantDTO.getTenantName());
                tenantVO.setInto(false);
                tenantVOS.add(tenantVO);
            }
        }
        return tenantVOS;
    }

    private boolean isTenantAdmin(Long userId, String tenantNum) {
        return Objects.isNull(tenantC7nMapper.tenantAdminByUserId(userId, tenantNum)) ? false : true;
    }
}
