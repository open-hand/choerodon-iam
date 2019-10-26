package io.choerodon.base.app.service.impl;


import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.base.api.dto.SimplifiedClientDTO;
import io.choerodon.base.api.query.ClientRoleQuery;
import io.choerodon.base.api.vo.ClientVO;
import io.choerodon.base.app.service.ClientService;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.infra.enums.ClientTypeEnum;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.asserts.ClientAssertHelper;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.base.infra.dto.ClientDTO;
import io.choerodon.base.infra.dto.MemberRoleDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.enums.MemberType;
import io.choerodon.base.infra.mapper.ClientMapper;
import io.choerodon.base.infra.mapper.MemberRoleMapper;
import io.choerodon.base.infra.mapper.RoleMapper;
import io.choerodon.base.infra.utils.JsonUtils;
import io.choerodon.base.infra.utils.ParamUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.IllegalArgumentException;
import io.choerodon.core.exception.ext.*;

/**
 * @author wuguokai
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final String ORGANIZATION_ID_NOT_EQUAL_EXCEPTION = "error.organizationId.not.same";
    private static final String SOURCETYPE_INVALID_EXCEPTION = "error.sourceType.invalid";

    private OrganizationAssertHelper organizationAssertHelper;
    private ClientAssertHelper clientAssertHelper;
    private ClientMapper clientMapper;
    private MemberRoleMapper memberRoleMapper;
    private RoleMemberService roleMemberService;
    private ModelMapper modelMapper = new ModelMapper();
    private RoleMapper roleMapper;

    public ClientServiceImpl(OrganizationAssertHelper organizationAssertHelper,
                             ClientAssertHelper clientAssertHelper,
                             ClientMapper clientMapper,
                             MemberRoleMapper memberRoleMapper,
                             RoleMapper roleMapper,
                             RoleMemberService roleMemberService) {
        this.organizationAssertHelper = organizationAssertHelper;
        this.clientMapper = clientMapper;
        this.clientAssertHelper = clientAssertHelper;
        this.memberRoleMapper = memberRoleMapper;
        this.roleMapper = roleMapper;
        this.roleMemberService = roleMemberService;
    }

    @Override
    public ClientDTO create(Long orgId, ClientDTO clientDTO) {
        organizationAssertHelper.notExisted(orgId);
        validateAdditionalInfo(clientDTO);
        clientDTO.setId(null);
        clientDTO.setOrganizationId(orgId);

        if (clientMapper.insertSelective(clientDTO) != 1) {
            throw new InsertException("error.client.create");
        }
        return clientMapper.selectByPrimaryKey(clientDTO.getId());
    }

    /**
     * 创建客户端时生成随机的clientId和secret
     */
    @Override
    public ClientDTO getDefaultCreateData(Long orgId) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName(generateUniqueName());
        clientDTO.setSecret(RandomStringUtils.randomAlphanumeric(16));
        return clientDTO;
    }

    @Override
    public ClientDTO update(ClientDTO clientDTO) {
        preUpdate(clientDTO);

        if (clientMapper.updateByPrimaryKey(clientDTO) != 1) {
            throw new UpdateException("error.client.update");
        }
        return clientMapper.selectByPrimaryKey(clientDTO.getId());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long orgId, Long clientId) {
        ClientDTO dto = clientAssertHelper.clientNotExisted(clientId);
        if (!dto.getOrganizationId().equals(orgId)) {
            throw new CommonException(ORGANIZATION_ID_NOT_EQUAL_EXCEPTION);
        }
        memberRoleMapper.deleteMemberRoleByMemberIdAndMemberType(clientId, "client");
        clientMapper.deleteByPrimaryKey(clientId);
    }

    @Override
    public ClientDTO query(Long orgId, Long clientId) {
        ClientDTO dto = clientAssertHelper.clientNotExisted(clientId);
        if (!orgId.equals(dto.getOrganizationId())) {
            throw new CommonException(ORGANIZATION_ID_NOT_EQUAL_EXCEPTION);
        }
        dto.setRoles(roleMapper.queryRoleByMember(dto.getId(), "client", "organization", orgId));
        return dto;
    }

    @Override
    public ClientDTO queryByName(Long orgId, String clientName) {
        ClientDTO dto = clientAssertHelper.clientNotExisted(clientName);
        if (!orgId.equals(dto.getOrganizationId())) {
            throw new CommonException(ORGANIZATION_ID_NOT_EQUAL_EXCEPTION);
        }
        return dto;
    }

    @Override
    public PageInfo<ClientDTO> list(ClientDTO clientDTO, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> clientMapper.fulltextSearch(clientDTO, param));
    }

    @Override
    public void check(ClientDTO client) {
        String name = client.getName();
        if (StringUtils.isEmpty(name)) {
            throw new EmptyParamException(("error.clientName.null"));
        }
        checkName(client);
    }

    @Override
    public PageInfo<ClientDTO> pagingQueryUsersByRoleId(Pageable pageable, ResourceType resourceType, Long sourceId, ClientRoleQuery clientRoleSearchDTO, Long roleId) {
        String param = Optional.ofNullable(clientRoleSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> clientMapper.selectClientsByRoleIdAndOptions(roleId, sourceId, resourceType.value(), clientRoleSearchDTO, param));
    }

    @Override
    public PageInfo<SimplifiedClientDTO> pagingQueryAllClients(Pageable pageable, String params) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() -> clientMapper.selectAllClientSimplifiedInfo(params));
    }

    @Override
    public ClientDTO assignRoles(Long organizationId, Long clientId, List<Long> roleIds) {
        ClientDTO clientDTO = clientAssertHelper.clientNotExisted(clientId);
        if (roleIds == null) {
            roleIds = new ArrayList<>();
        }
        List<MemberRoleDTO> memberRoles = validateRoles(organizationId, clientId, roleIds);
        roleMemberService.createOrUpdateRolesByMemberIdOnOrganizationLevel(true, organizationId, Collections.singletonList(clientId), memberRoles, MemberType.CLIENT.value());
        return clientDTO;
    }
    @Override
    public ClientDTO createClientWithType(Long organizationId, ClientVO clientVO) {
        // 校验sourceType
        if (Arrays.stream(ClientTypeEnum.values()).noneMatch(t -> t.value().equals(clientVO.getSourceType()))) {
            throw new CommonException(SOURCETYPE_INVALID_EXCEPTION);
        }
        ClientDTO clientDTO = modelMapper.map(clientVO, ClientDTO.class);
        return create(organizationId,clientDTO);
    }


    private List<MemberRoleDTO> validateRoles(Long organizationId, Long clientId, List<Long> roleIds) {
        //查询当前组织下允许分配的所有角色
        List<RoleDTO> roles = roleMapper.pagingQueryOrgRoles(organizationId, null, null, null, null, true, null);
        List<Long> allowedRoleIds = roles.stream().map(RoleDTO::getId).collect(Collectors.toList());
        List<MemberRoleDTO> memberRoles = new ArrayList<>();
        roleIds.forEach(id -> {
            if (!allowedRoleIds.contains(id)) {
                throw new IllegalArgumentException("error.client.illegal.role.id", id);
            }
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            memberRoleDTO.setMemberType(MemberType.CLIENT.value());
            memberRoleDTO.setSourceType(ResourceType.ORGANIZATION.value());
            memberRoleDTO.setSourceId(organizationId);
            memberRoleDTO.setRoleId(id);
            memberRoleDTO.setMemberId(clientId);
            memberRoles.add(memberRoleDTO);
        });
        return memberRoles;
    }

    private String generateUniqueName() {
        String uniqueName;
        ClientDTO dto = new ClientDTO();
        while (true) {
            uniqueName = RandomStringUtils.randomAlphanumeric(12);
            dto.setName(uniqueName);
            if (clientMapper.selectOne(dto) == null) {
                break;
            }
        }
        return uniqueName;
    }

    private void preUpdate(ClientDTO clientDTO) {
        if (StringUtils.isEmpty(clientDTO.getName())) {
            throw new EmptyParamException("error.clientName.empty");
        }
        Long id = clientDTO.getId();
        ClientDTO dto = clientAssertHelper.clientNotExisted(id);
        //组织id不可修改
        clientDTO.setOrganizationId(dto.getOrganizationId());
        validateAdditionalInfo(clientDTO);
    }

    private void validateAdditionalInfo(ClientDTO clientDTO) {
        String additionalInfo = clientDTO.getAdditionalInformation();
        if (StringUtils.isEmpty(additionalInfo)) {
            clientDTO.setAdditionalInformation("{}");
        } else if (!JsonUtils.isJSONValid(additionalInfo)) {
            throw new CommonException("error.client.additionalInfo.notJson");
        }
    }

    private void checkName(ClientDTO client) {
        Boolean createCheck = StringUtils.isEmpty(client.getId());
        String name = client.getName();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName(name);
        if (createCheck) {
            Boolean existed = clientMapper.selectOne(clientDTO) != null;
            if (existed) {
                throw new AlreadyExistedException("error.clientName.exist");
            }
        } else {
            Long id = client.getId();
            ClientDTO dto = clientMapper.selectOne(clientDTO);
            Boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new AlreadyExistedException("error.clientName.exist");
            }
        }

    }

}
