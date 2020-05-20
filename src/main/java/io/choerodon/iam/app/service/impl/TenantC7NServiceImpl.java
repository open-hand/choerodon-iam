package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.ORG_DISABLE;
import static io.choerodon.iam.infra.utils.SagaTopic.Organization.ORG_ENABLE;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.iam.app.service.TenantService;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.TenantC7nMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.ConvertUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/4/21
 * @description
 */
@Service
public class TenantC7NServiceImpl implements TenantC7nService {
    public static final String ORGANIZATION_DOES_NOT_EXIST_EXCEPTION = "error.organization.does.not.exist";
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
    private OrganizationAssertHelper organizationAssertHelper;
    @Autowired
    private AsgardFeignClient asgardFeignClient;
    @Autowired
    private DevopsFeignClient devopsFeignClient;
    // 注入messageClient
    @Autowired
    protected MessageClient messageClient;

    // TODO 重写tenant逻辑
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(Long tenantId, TenantVO tenantVO) {
//        Tenant tenant = getTenant(tenantVO);
//
//        TenantConfigVO configVO = JSON.parseObject(tenantService.queryTenant(tenantVO.getTenantId()).getExtInfo(), TenantConfigVO.class);
//        configVO.setAddress(tenantVO.getTenantConfigVO().getAddress());
//        configVO.setImageUrl(tenantVO.getTenantConfigVO().getImageUrl());
//        configVO.setHomePage(tenantVO.getTenantConfigVO().getHomePage());
//        tenant.setExtInfo(JSON.toJSONString(configVO));
//
//        tenantService.updateTenant(tenantId, tenant);
    }

    // TODO 重写tenant逻辑
    @Override
    public TenantVO queryTenantById(Long tenantId) {
//        Tenant tenant = tenantService.queryTenant(tenantId);
//        TenantVO tenantVO = ConvertUtils.convertObject(tenant, TenantVO.class);
//        TenantConfigVO configVO = JSON.parseObject(tenant.getExtInfo(), TenantConfigVO.class);
//        tenantVO.setTenantConfigVO(configVO);

        return new TenantVO();
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
        TenantVO dto = ConvertUtils.convertObject(tenantService.queryTenant(tenantId), TenantVO.class);
        long userId = customUserDetails.getUserId();

        List<ProjectDTO> projects = projectMapper.selectUserProjectsUnderOrg(userId, tenantId, null);
        dto.setProjects(projects);
        dto.setProjectCount(projects.size());

        List<Role> roles = roleC7nMapper.queryRolesInfoByUser(ResourceLevel.ORGANIZATION.value(), tenantId, userId);
        dto.setRoles(roles);
        return dto;
    }

    // TODO 重写tenant逻辑
    @Override
    public Page<TenantVO> pagingQuery(PageRequest pageRequest, String name, String code, String ownerRealName, Boolean enabled, String params) {
//        Page<TenantVO> tenantVOS = PageHelper.doPageAndSort(pageRequest, () -> tenantC7nMapper.fulltextSearch(name, code, enabled, params));
//        if (!CollectionUtils.isEmpty(tenantVOS.getContent())) {
//            List<TenantVO> list = tenantVOS.getContent().stream().peek(t -> {
//                t.setTenantConfigVO(JSON.parseObject(t.getExtInfo(), TenantConfigVO.class));
//                // todo 用户查询
//            }).collect(Collectors.toList());
//            tenantVOS.setContent(list);
//        }
        return new Page<>();
    }

    // TODO 重写tenant逻辑
    @Override
    public Page<TenantVO> getAllTenants(PageRequest pageRequest) {
//        return PageHelper.doPageAndSort(pageRequest, () -> tenantC7nMapper.selectAllTenants());
        return new Page<>();
    }

    @Override
    public Tenant enableOrganization(Long organizationId, Long userId) {
        Tenant organization = organizationAssertHelper.notExisted(organizationId);
        organization.setEnabledFlag(1);
        return updateAndSendEvent(organization, ORG_ENABLE, userId);
    }

    @Override
    public Tenant disableOrganization(Long organizationId, Long userId) {
        Tenant organizationDTO = organizationAssertHelper.notExisted(organizationId);
        organizationDTO.setEnabledFlag(0);
        return updateAndSendEvent(organizationDTO, ORG_DISABLE, userId);
    }

    @Override
    public void check(TenantVO tenantVO) {
        Boolean checkCode = !StringUtils.isEmpty(tenantVO.getTenantNum());
        if (!checkCode) {
            throw new CommonException("error.organization.code.empty");
        } else {
            checkCode(tenantVO);
        }
    }

    @Override
    public Page<User> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, PageRequest pageRequest, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> userC7nMapper.selectUsersByLevelAndOptions(ResourceLevel.ORGANIZATION.value(), organizationId, userId, email, param));
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
    public List<Tenant> queryTenantsByIds(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tenantC7nMapper.selectByIds(ids);
    }

    private void checkCode(TenantVO tenantVO) {
        Boolean createCheck = StringUtils.isEmpty(tenantVO.getTenantId());
        Tenant tenant = getTenant(tenantVO);
        if (createCheck) {
            Boolean existed = tenantRepository.selectOne(tenant) != null;
            if (existed) {
                throw new CommonException("error.organization.code.exist");
            }
        } else {
            Long id = tenantVO.getTenantId();
            Tenant dto = tenantRepository.selectOne(tenant);
            Boolean existed = dto != null && !id.equals(dto.getTenantId());
            if (existed) {
                throw new CommonException("error.organization.code.exist");
            }
        }
    }

    private Tenant updateAndSendEvent(Tenant tenant, String consumerType, Long userId) {
        Tenant organizationDTO = doUpdate(tenant);

        //给asgard发送禁用定时任务通知
        asgardFeignClient.disableOrg(tenant.getTenantId());

        // todo webhook消息发送
        // 给组织下所有用户发送通知
//
//        // 准备消息发送的messageSender
//        MessageSender messageSender=new MessageSender();
//        // 消息code
//        messageSender.setMessageCode(MessageCodeConstants.DISABLE_ORGANIZATION);
//        // 默认为0L,都填0L,可不填写
//        messageSender.setTenantId(0L);
//
//        // 消息参数 消息模板中${projectName}
//        Map<String,String> argsMap=new HashMap<>();
//        argsMap.put("projectName","testProject");
//        argsMap.put("orgCode","testOrganization");
//        argsMap.put("orgName","测试组织");
//        messageSender.setArgs(argsMap);
//
//        //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
//        Map<String,Object> objectMap=new HashMap<>();
//        objectMap.put(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName(),1L);
//        objectMap.put(MessageAdditionalType.PARAM_ENV_ID.getTypeName(),1L);
//        objectMap.put(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName(),"service");
//        messageSender.setAdditionalInformation(objectMap);
//
//        // 接收者
//        List<Receiver> receiverList=new ArrayList<>();
//        Receiver receiver=new Receiver();
//        receiver.setUserId(1L);
//        // 发送邮件消息时 必填
//        receiver.setEmail("xxx.qq.com");
//        // 发送短信消息 必填
//        receiver.setPhone("176666");
//        receiverList.add(receiver);
//        messageSender.setReceiverAddressList(receiverList);
//
//        messageClient.async().sendMessage(messageSender);
//        List<Long> userIds = tenantC7nMapper.listMemberIds(tenant.getTenantId(), "organization");
//        Map<String, Object> params = new HashMap<>();
//        params.put("organizationName", organizationDTO.getTenantName());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("organizationId", organizationDTO.getTenantId());
//        jsonObject.put("code", organizationDTO.getTenantNum());
//        jsonObject.put("name", organizationDTO.getTenantName());
//        jsonObject.put("enabled", organizationDTO.getEnabledFlag());
//        if (ORG_DISABLE.equals(consumerType)) {
//
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.DISABLE_ORGANIZATION.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.DISABLE_ORGANIZATION.value()),
//                        jsonObject
//                        organizationDTO.getCreationDate(),
//                        userService.getWebHookUser(organizationDTO.getCreatedBy())
//                );
//                userService.sendNotice(userId, userIds, "disableOrganization", params, organization.getId(), webHookJsonSendDTO);
//        } else if (ORG_ENABLE.equals(consumerType)) {
//
//                WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                        SendSettingBaseEnum.ENABLE_ORGANIZATION.value(),
//                        SendSettingBaseEnum.map.get(SendSettingBaseEnum.ENABLE_ORGANIZATION.value()),
//                        jsonObject,
//                        organizationDTO.getCreationDate(),
//                        userService.getWebHookUser(organizationDTO.getCreatedBy())
//                );
//                userService.sendNotice(userId, userIds, "enableOrganization", params, organization.getId(), webHookJsonSendDTO);
//        }
        return organizationDTO;
    }


    private Tenant doUpdate(Tenant tenant) {
        if (tenantRepository.updateByPrimaryKeySelective(tenant) != 1) {
            throw new UpdateException("error.organization.update");
        }
        return tenantRepository.selectByPrimaryKey(tenant);
    }

    private Tenant getTenant(TenantVO tenantVO) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(tenantVO, tenant);
        return tenant;
    }

}
