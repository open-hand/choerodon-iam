package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.app.service.ClientProjectC7nService;
import io.choerodon.iam.app.service.ProjectPermissionService;
import io.choerodon.iam.infra.dto.OauthClientResourceDTO;
import io.choerodon.iam.infra.dto.ProjectPermissionDTO;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.mapper.ClientC7nMapper;
import io.choerodon.iam.infra.mapper.OauthClientResourceMapper;
import io.choerodon.iam.infra.mapper.ProjectPermissionMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/11/3
 * @description
 */
@Service
public class ClientProjectC7nServiceImpl implements ClientProjectC7nService {
    @Autowired
    private ClientC7nService clientC7nService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientC7nMapper clientC7nMapper;
    @Autowired
    private OauthClientResourceMapper oauthClientResourceMapper;
    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;
    @Autowired
    private ProjectPermissionService projectPermissionService;
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    @Lazy
    private MemberRoleService memberRoleService;


    @Override
    public ClientVO create(ClientVO clientVO) {
        return clientC7nService.create(clientVO);
    }

    @Override
    @Transactional
    public void delete(Long tenantId, Long projectId, Long clientId) {
        clientC7nService.delete(tenantId, clientId);
        projectPermissionMapper.deleteByClientId(projectId, clientId);
    }


    @Override
    public void update(Long tenantId, Long projectId, Client client) {
        Client oldClient = clientService.detailClient(tenantId, client.getId());
        client.setOrganizationId(tenantId);
        client.setTimeZone(oldClient.getTimeZone());
        clientService.update(client);
    }

    @Override
    public Page<Client> pageClient(Long organizationId, Long projectId, String name, String params, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> clientC7nMapper.listClientBySourceId(organizationId, projectId, ResourceLevel.PROJECT.value(), name, params));
    }

    @Override
    @Transactional
    public void assignRoles(Long organizationId, Long projectId, Long clientId, List<Long> roleIds) {
        List<MemberRole> oldMemberRoleList = projectPermissionMapper.listMemberRoleByProjectIdAndClientId(projectId, clientId, null);
        Map<Long, Long> oldMemberRoleMap = oldMemberRoleList.stream().collect(Collectors.toMap(MemberRole::getRoleId, MemberRole::getId));
        Set<Long> oldRoleIds = oldMemberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        // 要删除的角色
        Set<Long> deleteRoleIds = oldRoleIds.stream().filter(v -> !roleIds.contains(v)).collect(Collectors.toSet());
        // 要新增的角色
        Set<Long> insertRoleIds = roleIds.stream().filter(v -> !oldRoleIds.contains(v)).collect(Collectors.toSet());

        List<MemberRole> addMemberRoles = new ArrayList<>();

        List<MemberRole> delMemberRoles = new ArrayList<>();

        // 删除角色
        Set<Long> deleteMemberRoleIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(deleteRoleIds)) {
            deleteRoleIds.forEach(v -> {
                Long memberRoleId = oldMemberRoleMap.get(v);
                if (memberRoleId != null) {
                    deleteMemberRoleIds.add(memberRoleId);
                }
                MemberRole delMemberRole = clientC7nService.getMemberRole(clientId, v, organizationId);
                delMemberRoles.add(delMemberRole);
            });
            projectPermissionMapper.deleteByIds(projectId, deleteMemberRoleIds);
            memberRoleService.batchDeleteMemberRole(organizationId, delMemberRoles);
        }

        // 新增角色
        if (!CollectionUtils.isEmpty(insertRoleIds)) {
            insertRoleIds.forEach(v -> addMemberRoles.add(clientC7nService.getMemberRole(clientId, v, organizationId)));
            memberRoleService.batchAssignMemberRoleInternal(addMemberRoles);
            insertRoleIds.forEach(v -> {
                ProjectPermissionDTO projectPermissionDTO = new ProjectPermissionDTO();
                projectPermissionDTO.setProjectId(projectId);
                projectPermissionDTO.setMemberRoleId(projectPermissionService.getMemberRoleId(clientId, MemberType.CLIENT.value(), v, organizationId));
                if (projectPermissionMapper.selectOne(projectPermissionDTO) == null) {
                    if (projectPermissionMapper.insertSelective(projectPermissionDTO) != 1) {
                        throw new CommonException("error.save.project.client.failed");
                    }
                }
            });
        }

    }

    @Override
    public List<Role> selectMemberRoles(Long organizationId, Long projectId, Long clientId, String roleName) {
        return roleC7nMapper.listMemberRolesForProjectClient(organizationId, clientId, projectId, roleName);
    }

    private void checkPermission(Long projectId, Long clientId) {
        OauthClientResourceDTO oauthClientResourceDTO = oauthClientResourceMapper.selectOne(new OauthClientResourceDTO().setClientId(clientId).setSourceId(projectId).setSourceType(ResourceLevel.PROJECT.value()));
        if (oauthClientResourceDTO == null) {
            throw new CommonException("no.permission.project.client");
        }
    }

}
