package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.utils.SagaTopic.Organization.*;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.vo.ProjectOverViewVO;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.base.api.dto.OrgSharesDTO;
import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.api.dto.payload.OrganizationEventPayload;
import io.choerodon.base.api.dto.payload.OrganizationPayload;
import io.choerodon.base.app.service.OrganizationService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.asserts.DetailsHelperAssert;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.util.PageableHelper;

/**
 * @author wuguokai
 */
@Component
public class OrganizationServiceImpl implements OrganizationService {

    private final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    public static final String ORGANIZATION_DOES_NOT_EXIST_EXCEPTION = "error.organization.does.not.exist";

    private AsgardFeignClient asgardFeignClient;

    private boolean devopsMessage;

    private SagaClient sagaClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserService userService;

    private OrganizationAssertHelper organizationAssertHelper;

    private ProjectMapper projectMapper;

    private UserMapper userMapper;

    private OrganizationMapper organizationMapper;

    private RoleMapper roleMapper;

    private MemberRoleMapper memberRoleMapper;

    private DevopsFeignClient devopsFeignClient;


    public OrganizationServiceImpl(@Value("${choerodon.devops.message:false}") Boolean devopsMessage,
                                   SagaClient sagaClient,
                                   UserService userService,
                                   AsgardFeignClient asgardFeignClient,
                                   OrganizationAssertHelper organizationAssertHelper,
                                   ProjectMapper projectMapper,
                                   UserMapper userMapper,
                                   OrganizationMapper organizationMapper,
                                   RoleMapper roleMapper,
                                   MemberRoleMapper memberRoleMapper,
                                   DevopsFeignClient devopsFeignClient) {
        this.devopsMessage = devopsMessage;
        this.sagaClient = sagaClient;
        this.userService = userService;
        this.asgardFeignClient = asgardFeignClient;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
        this.organizationMapper = organizationMapper;
        this.roleMapper = roleMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public OrganizationDTO queryOrganizationById(Long organizationId) {
        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(organizationId);
        ProjectDTO example = new ProjectDTO();
        example.setOrganizationId(organizationId);
        List<ProjectDTO> projects = projectMapper.select(example);
        organizationDTO.setProjects(projects);
        organizationDTO.setProjectCount(projects.size());

        Long userId = organizationDTO.getUserId();
        UserDTO user = userMapper.selectByPrimaryKey(userId);
        if (user != null) {
            organizationDTO.setOwnerLoginName(user.getLoginName());
            organizationDTO.setOwnerRealName(user.getRealName());
            organizationDTO.setOwnerPhone(user.getPhone());
            organizationDTO.setOwnerEmail(user.getEmail());
        }
        return organizationDTO;
    }

    @Override
    public List<OrganizationDTO> queryOrganizationsByName(String organizationName) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName(organizationName);
        Criteria criteria = new Criteria(organizationDTO);
        criteria.where(new WhereField(OrganizationDTO.FIELD_NAME, Comparison.LIKE));
        return organizationMapper.selectOptions(organizationDTO, criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_UPDATE, description = "iam更新组织", inputSchemaClass = OrganizationPayload.class)
    @OperateLog(type = "updateOrganization", content = "%s修改组织【%s】的信息", level = {ResourceType.SITE})
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

    private void preUpdate(Long organizationId, OrganizationDTO organizationDTO) {
        OrganizationDTO organization = organizationAssertHelper.notExisted(organizationId);
        organizationDTO.setId(organizationId);
        //code和创建人不可修改
        organizationDTO.setUserId(organization.getUserId());
        organizationDTO.setCode(organization.getCode());
        if (ObjectUtils.isEmpty(organizationDTO.getEnabled())) {
            organizationDTO.setEnabled(true);
        }
    }

    @Override
    public OrganizationDTO queryOrganizationWithRoleById(Long organizationId) {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        OrganizationDTO dto = queryOrganizationById(organizationId);
        long userId = customUserDetails.getUserId();

        List<ProjectDTO> projects = projectMapper.selectUserProjectsUnderOrg(userId, organizationId, null);
        dto.setProjects(projects);
        dto.setProjectCount(projects.size());

        List<RoleDTO> roles =
                roleMapper.queryRolesInfoByUser(ResourceType.ORGANIZATION.value(), organizationId, userId);
        dto.setRoles(roles);
        return dto;
    }

    @Override
    public PageInfo<OrganizationDTO> pagingQuery(Pageable pageable, String name, String code, String ownerRealName, Boolean enabled, String params) {

        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(() ->
                organizationMapper.fulltextSearch(name, code, ownerRealName, enabled, params));
    }

    @Override
    @Saga(code = ORG_ENABLE, description = "iam启用组织", inputSchemaClass = OrganizationEventPayload.class)
    @OperateLog(type = "enableOrganization", content = "%s启用组织【%s】", level = {ResourceType.SITE})
    public OrganizationDTO enableOrganization(Long organizationId, Long userId) {
        OrganizationDTO organization = organizationAssertHelper.notExisted(organizationId);
        organization.setEnabled(true);
        return updateAndSendEvent(organization, ORG_ENABLE, userId);
    }

    @Override
    @Saga(code = ORG_DISABLE, description = "iam停用组织", inputSchemaClass = OrganizationEventPayload.class)
    @OperateLog(type = "disableOrganization", content = "%s停用组织【%s】", level = {ResourceType.SITE})
    public OrganizationDTO disableOrganization(Long organizationId, Long userId) {
        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(organizationId);
        organizationDTO.setEnabled(false);
        return updateAndSendEvent(organizationDTO, ORG_DISABLE, userId);
    }

    private OrganizationDTO updateAndSendEvent(OrganizationDTO organization, String consumerType, Long userId) {
        OrganizationDTO organizationDTO = doUpdate(organization);
        if (devopsMessage) {
            OrganizationEventPayload payload = new OrganizationEventPayload();
            payload.setOrganizationId(organization.getId());
            //saga
            try {
                String input = mapper.writeValueAsString(payload);
                sagaClient.startSaga(consumerType, new StartInstanceDTO(input, "organization", payload.getOrganizationId() + ""));
            } catch (Exception e) {
                throw new CommonException("error.organizationService.enableOrDisable.event", e);
            }
            //给asgard发送禁用定时任务通知
            asgardFeignClient.disableOrg(organization.getId());
            // 给组织下所有用户发送通知
            List<Long> userIds = organizationMapper.listMemberIds(organization.getId(), "organization");
            Map<String, Object> params = new HashMap<>();
            params.put("organizationName", organizationDTO.getName());
            if (ORG_DISABLE.equals(consumerType)) {
                userService.sendNotice(userId, userIds, "disableOrganization", params, organization.getId());
            } else if (ORG_ENABLE.equals(consumerType)) {
                userService.sendNotice(userId, userIds, "enableOrganization", params, organization.getId());
            }
        }
        return organizationDTO;
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
    public PageInfo<UserDTO> pagingQueryUsersInOrganization(Long organizationId, Long userId, String email, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> userMapper.selectUsersByLevelAndOptions(ResourceLevel.ORGANIZATION.value(), organizationId, userId, email, param));
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
    public PageInfo<OrganizationSimplifyDTO> getAllOrgs(Pageable pageable) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> organizationMapper.selectAllOrgIdAndName());
    }


    @Override
    public PageInfo<OrgSharesDTO> pagingSpecified(Set<Long> orgIds, String name, String code, Boolean enabled, String params, Pageable pageable) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new PageInfo<>();
        }
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(() -> organizationMapper.selectSpecified(orgIds, name, code, enabled, params));
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
        projectOverViewVO1.setProjectName("其他剩余：");
        projectOverViewVO1.setAppServerSum(sum);
        reOverViewVOS.add(projectOverViewVO1);
        return reOverViewVOS;
    }
}
