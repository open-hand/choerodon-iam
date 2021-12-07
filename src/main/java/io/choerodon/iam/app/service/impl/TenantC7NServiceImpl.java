package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.TenantConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.TenantConfigRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.hzero.iam.saas.app.service.TenantService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.app.service.MessageSendService;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.constant.TenantConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.feign.operator.DevopsFeignClientOperator;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.TenantC7nMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.ConvertUtils;
import io.choerodon.iam.infra.utils.TenantConfigConvertUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/21
 */
public class TenantC7NServiceImpl implements TenantC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantC7NServiceImpl.class);

    public static final String ERROR_TENANT_PARAM_IS_NULL = "error.tenant.param.is.null";
    public static final String ERROR_TENANT_USERID_IS_NULL = "error.tenant.user.id.is.null";
    public static final Long OPERATION_ORG_ID = 1L;

    @Autowired
    private TenantService tenantService;
    @Autowired
    private TenantC7nMapper tenantC7nMapper;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    private DevopsFeignClientOperator devopsFeignClientOperator;
    // 注入messageClient
    @Autowired
    protected MessageClient messageClient;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Override
    public TenantVO queryTenantById(Long tenantId, Boolean withMoreInfo) {
        Tenant tenant = tenantRepository.selectByPrimaryKey(tenantId);
        TenantVO tenantVO = ConvertUtils.convertObject(tenant, TenantVO.class);
        if (withMoreInfo) {
            List<TenantConfig> tenantConfigList = tenantConfigRepository.selectByCondition(Condition.builder(TenantConfig.class)
                    .where(Sqls.custom()
                            .andEqualTo(TenantConfig.FIELD_TENANT_ID, tenantId)
                    )
                    .build());
            TenantConfigVO tenantConfigVO = TenantConfigConvertUtils.configDTOToVO(tenantConfigList);
            tenantVO.setTenantConfigVO(tenantConfigVO);
            //返回组织所有者的手机号邮箱
            if (tenantConfigVO.getUserId() != null) {
                User user = userMapper.selectByPrimaryKey(tenantConfigVO.getUserId());
                if (user != null) {
                    tenantVO.setOwnerRealName(user.getRealName());
                    tenantVO.setOwnerEmail(user.getEmail());
                    tenantVO.setOwnerPhone(user.getPhone());
                    tenantVO.setOwnerLoginName(user.getLoginName());
                }
            }
        }
        return tenantVO;
    }

    @Override
    public List<TenantVO> queryTenantByName(String tenantName) {
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        return ConvertUtils.convertList(tenantRepository.select(tenant), TenantVO.class);
    }

    @Override
    public TenantVO queryTenantWithRoleById(Long tenantId) {
        CustomUserDetails customUserDetails = UserUtils.getUserDetails();
        TenantVO dto = ConvertUtils.convertObject(tenantRepository.selectByPrimaryKey(tenantId), TenantVO.class);
        long userId = customUserDetails.getUserId();
        List<TenantConfig> configList = tenantConfigRepository.select(new TenantConfig().setTenantId(tenantId));
        TenantConfigVO tenantConfigVO = TenantConfigConvertUtils.configDTOToVO((configList));
        dto.setTenantConfigVO(tenantConfigVO);
        //添加组织所有者信息
        if (!Objects.isNull(tenantConfigVO.getUserId())) {
            User user = userMapper.selectByPrimaryKey(tenantConfigVO.getUserId());
            if (!Objects.isNull(user)) {
                dto.setOwnerRealName(user.getRealName());
                dto.setOwnerLoginName(user.getLoginName());
                dto.setOwnerPhone(user.getPhone());
                dto.setOwnerEmail(user.getEmail());
            }
        }
        List<ProjectDTO> projects = projectMapper.selectUserProjectsUnderOrg(userId, tenantId, null);
        dto.setProjects(projects);
        dto.setProjectCount(projects.size());

        List<Role> roles = roleC7nMapper.queryRolesInfoByUser(ResourceLevel.ORGANIZATION.value(), tenantId, userId);
        dto.setRoles(roles);
        return dto;
    }


    @Override
    public Page<TenantVO> getAllTenants(PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> tenantRepository.selectAll());
    }

    @Override
    public Page<User> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, PageRequest pageRequest, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> userC7nMapper.selectUsersByLevelAndOptions(ResourceLevel.ORGANIZATION.value(), organizationId, userId, email, param, Collections.EMPTY_LIST));
    }

    @Override
    public Page<User> pagingQueryUsersOnOrganizationAgile(Long organizationId, Long userId, String email, PageRequest pageRequest, String param, List<Long> notSelectUserIds) {
        return PageHelper.doPageAndSort(pageRequest, () -> userC7nMapper.selectUsersByLevelAndOptions(ResourceLevel.ORGANIZATION.value(), organizationId, userId, email, param, notSelectUserIds));
    }

    @Override
    public List<Tenant> queryTenants(Set<Long> tenantIds) {
        return tenantMapper.selectByIds(org.apache.commons.lang3.StringUtils.join(tenantIds, ","));
    }

    @Override
    public Page<Tenant> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, PageRequest pageable) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new Page<>();
        }
        return PageHelper.doPageAndSort(pageable, () -> tenantC7nMapper.selectSpecified(orgIds, name, code, enabled, params));
    }

    @Override
    public ProjectOverViewVO projectOverview(Long organizationId) {
        ProjectOverViewVO projectOverViewVO = tenantC7nMapper.projectOverview(organizationId);
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
        Map<Long, Integer> map = devopsFeignClientOperator.countAppServerByProjectId(longList.get(0), longList);
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
        collect.forEach(projectOverViewVO -> {
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
    public List<Tenant> queryTenantsByIds(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tenantC7nMapper.selectByIds(ids);
    }

    @Override
    public List<TenantVO> selectSelfTenants(TenantDTO params) {
        CustomUserDetails self = UserUtils.getUserDetails();
        params.setUserId(self.getUserId());
        return listOwnedOrganizationByTenant(params);
    }

    @Override
    public List<TenantVO> listOwnedOrganizationByUserId(Long userId) {
        TenantDTO params = new TenantDTO();
        params.setUserId(userId);
        return listOwnedOrganizationByTenant(params);
    }

    @Override
    public Tenant queryDefault() {
        return tenantMapper.selectByPrimaryKey(TenantConstants.DEFAULT_C7N_TENANT_TD);
    }

    @Override
    @Transactional
    public void createDefaultTenant(String tenantName, String tenantNum) {
        Tenant defaultTenant = new Tenant();
        defaultTenant.setTenantName(tenantName);
        defaultTenant.setTenantId(TenantConstants.DEFAULT_C7N_TENANT_TD);
        defaultTenant.setTenantNum(tenantNum);
        defaultTenant.setEnabledFlag(1);
        defaultTenant.setEnableDataSecurity(0);
        initConfig(defaultTenant);
        tenantService.createTenant(defaultTenant);
        timeZoneWorkCalendarService.handleOrganizationInitTimeZone(defaultTenant.getTenantId());
    }

    @Override
    public void syncTenantTl() {
        LOGGER.info("================start sync tenant tl================");
        List<Long> tenantIds = tenantC7nMapper.querySingleTl();
        if (!CollectionUtils.isEmpty(tenantIds)) {
            List<Tenant> list = tenantC7nMapper.selectByIds(new HashSet<>(tenantIds));
            list.forEach(t -> tenantC7nMapper.insertTenantTl(t.getTenantId(), "en_US", t.getTenantName()));
        }
        LOGGER.info("================end sync tenant tl================");
    }

    @Override
    public ExternalTenantVO queryTenantByIdWithExternalInfo(Long organizationId) {
        TenantVO tenantVO = queryTenantById(organizationId, false);
        ExternalTenantVO externalTenantVO = ConvertUtils.convertObject(tenantVO, ExternalTenantVO.class);
        return externalTenantVO;
    }

    /**
     * 查询用户可访问的组织，into判断是否可进
     *
     * @param params
     * @return TenantVO列表
     */
    private List<TenantVO> listOwnedOrganizationByTenant(TenantDTO params) {
        Assert.notNull(params, ERROR_TENANT_PARAM_IS_NULL);
        Assert.notNull(params.getUserId(), ERROR_TENANT_USERID_IS_NULL);
        List<TenantDTO> tenantDTOS = tenantC7nMapper.listVisibleTentant(params);
        // 过滤hzero平台组织
        if (CollectionUtils.isEmpty(tenantDTOS)) {
            return new ArrayList<>();
        }
        tenantDTOS = tenantDTOS.stream().filter(tenantDTO -> tenantDTO.getTenantId() != 0).collect(Collectors.toList());
        User user = userMapper.selectByPrimaryKey(params.getUserId());
        List<TenantVO> ownedOrganizations = getOwnedOrganizations(user.getId(), Boolean.TRUE.equals(user.getAdmin()), tenantDTOS);
        // 如果是admin用户，没有任何组织权限，默认返回运营组织
        if (CollectionUtils.isEmpty(ownedOrganizations) && Boolean.TRUE.equals(user.getAdmin())) {
            TenantVO tenantVO = ConvertUtils.convertObject(tenantMapper.selectByPrimaryKey(OPERATION_ORG_ID), TenantVO.class);
            tenantVO.setInto(true);
            return Collections.singletonList(tenantVO);
        }
        return ownedOrganizations;
    }

    /**
     * 计算into字段
     */
    private List<TenantVO> getOwnedOrganizations(Long userId, boolean isAdmin, List<TenantDTO> tenantDTOS) {
        List<TenantVO> tenantVOS = ConvertUtils.convertList(tenantDTOS, TenantVO.class);

        if (isAdmin) {
            tenantVOS.forEach(tenantVO -> tenantVO.setInto(true));
        } else {
            Set<Long> orgIds = tenantVOS.stream().map(Tenant::getTenantId).collect(Collectors.toSet());
            Set<Long> managedOrgIds = userC7nMapper.listManagedOrgIdByUserId(userId, orgIds);
            Map<Long, TenantVO> tenantVOMap = tenantVOS.stream().collect(Collectors.toMap(Tenant::getTenantId, v -> v));
            managedOrgIds.forEach(orgId -> {
                TenantVO tenantVO = tenantVOMap.get(orgId);
                if (tenantVO != null) {
                    tenantVO.setInto(true);
                }
            });
            // 有菜单的角色也能访问
            Set<Long> hasMenuOrg = roleC7nMapper.listOrgByUserIdAndTenantIds(userId, orgIds);
            hasMenuOrg.forEach(orgId -> {
                TenantVO tenantVO = tenantVOMap.get(orgId);
                if (tenantVO != null) {
                    tenantVO.setInto(true);
                }
            });

        }
        return tenantVOS;
    }

    private void initConfig(Tenant defaultTenant) {
        List<TenantConfig> tenantConfigs = new ArrayList<>();

        TenantConfig userId = new TenantConfig();
        userId.setConfigKey(TenantConfigEnum.USER_ID.value());
        userId.setConfigValue(String.valueOf(1L));
        tenantConfigs.add(userId);

        TenantConfig register = new TenantConfig();
        register.setConfigValue("false");
        register.setConfigKey(TenantConfigEnum.IS_REGISTER.value());
        tenantConfigs.add(register);

        TenantConfig category = new TenantConfig();
        category.setConfigKey(TenantConfigEnum.CATEGORY.value());
        category.setConfigValue(TenantConstants.DEFAULT_CATEGORY);
        tenantConfigs.add(category);

        TenantConfig token = new TenantConfig();
        token.setConfigKey(TenantConfigEnum.REMOTE_TOKEN_ENABLED.value());
        token.setConfigValue("true");
        tenantConfigs.add(token);
        defaultTenant.setTenantConfigs(tenantConfigs);
    }
}
