package io.choerodon.iam.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.api.vo.ClientVO;
import io.choerodon.base.app.service.ClientC7nService;
import io.choerodon.base.infra.asserts.ClientAssertHelper;
import io.choerodon.base.infra.enums.ClientTypeEnum;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;

/**
 * @author scp
 * @date 2020/3/27
 * @description
 */
@Service
public class ClientC7nServiceImpl implements ClientC7nService {
    private static final String ORGANIZATION_ID_NOT_EQUAL_EXCEPTION = "error.organizationId.not.same";
    private static final String SOURCETYPE_INVALID_EXCEPTION = "error.sourceType.invalid";

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientAssertHelper clientAssertHelper;

    private ModelMapper modelMapper = new ModelMapper();


    @Override
    public Client getDefaultCreateData(Long orgId) {
        Client client = new Client();
        client.setName(generateUniqueName());
        client.setSecret(RandomStringUtils.randomAlphanumeric(16));
        return client;
    }

    @Override
    public Client assignRoles(Long organizationId, Long clientId, List<Long> roleIds) {
        Client clientDTO = clientAssertHelper.clientNotExisted(clientId);
        if (roleIds == null) {
            roleIds = new ArrayList<>();
        }
        List<MemberRole> memberRoles = validateRoles(organizationId, clientId, roleIds);
        roleMemberService.createOrUpdateRolesByMemberIdOnOrganizationLevel(true, organizationId, Collections.singletonList(clientId), memberRoles, Constants.MemberType.CLIENT.value());
        return clientDTO;
    }

    @Override
    public Client createClientWithType(Long organizationId, ClientVO clientVO) {
        // 校验sourceType
        if (Arrays.stream(ClientTypeEnum.values()).noneMatch(t -> t.value().equals(clientVO.getSourceType()))) {
            throw new CommonException(SOURCETYPE_INVALID_EXCEPTION);
        }
        Client client = modelMapper.map(clientVO, Client.class);
        client.setOrganizationId(organizationId);
        return clientService.create(client);
    }

    @Override
    public Client queryClientBySourceId(Long organizationId, Long sourceId) {
        Client record = new Client();
        record.setOrganizationId(organizationId);
        record.setSourceId(sourceId);
        return clientMapper.selectOne(record);
    }

    @Override
    public Client queryByName(Long orgId, String clientName) {
        Client dto = clientAssertHelper.clientNotExisted(clientName);
        if (!orgId.equals(dto.getOrganizationId())) {
            throw new CommonException(ORGANIZATION_ID_NOT_EQUAL_EXCEPTION);
        }
        return dto;
    }

    private String generateUniqueName() {
        String uniqueName;
        Client dto = new Client();
        while (true) {
            uniqueName = RandomStringUtils.randomAlphanumeric(12);
            dto.setName(uniqueName);
            if (clientMapper.selectOne(dto) == null) {
                break;
            }
        }
        return uniqueName;
    }

    private List<MemberRole> validateRoles(Long organizationId, Long clientId, List<Long> roleIds) {
        //查询当前组织下允许分配的所有角色
        Role role = new Role();
        role.setTenantId(organizationId);
        role.setEnabled(true);
        List<Role> roles = roleMapper.select(role);

        List<Long> allowedRoleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        List<MemberRole> memberRoles = new ArrayList<>();
        roleIds.forEach(id -> {
            if (!allowedRoleIds.contains(id)) {
                throw new IllegalArgumentException("error.client.illegal.role.id", id);
            }
            MemberRole memberRoleDTO = new MemberRole();
            memberRoleDTO.setMemberType(Constants.MemberType.CLIENT);
            memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRoleDTO.setSourceId(organizationId);
            memberRoleDTO.setRoleId(id);
            memberRoleDTO.setMemberId(clientId);
            memberRoles.add(memberRoleDTO);
        });
        return memberRoles;
    }
}
