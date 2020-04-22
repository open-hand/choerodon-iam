package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.app.service.MemberRoleC7nService;
import io.choerodon.iam.infra.asserts.ClientAssertHelper;
import io.choerodon.iam.infra.mapper.TenantC7nMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

import org.apache.commons.lang.RandomStringUtils;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.hzero.iam.infra.mapper.MemberRoleMapper;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private MemberRoleMapper memberRoleMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TenantC7nMapper tenantC7nMapper;
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    private MemberRoleC7nService memberRoleC7nService;

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
        memberRoleC7nService.createOrUpdateRolesByMemberIdOnOrganizationLevel(true, organizationId, Collections.singletonList(clientId), memberRoles, Constants.MemberType.CLIENT);
        return clientDTO;
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
                throw new CommonException("error.client.illegal.role.id", id);
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

    public List<MemberRole> insertOrUpdateRolesOfClientByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Client client = clientMapper.selectByPrimaryKey(memberId);
        if (client == null) {
            throw new CommonException("error.client.not.exist");
        }
        List<MemberRole> returnList = new ArrayList<>();
        insertOrUpdateRolesByMemberIdExecute(userId, isEdit,
                sourceId,
                memberId,
                sourceType,
                memberRoles,
                returnList, Constants.MemberType.CLIENT);
        return returnList;
    }

    public List<Long> insertOrUpdateRolesByMemberIdExecute(Long fromUserId, Boolean isEdit, Long sourceId,
                                                           Long memberId, String sourceType,
                                                           List<MemberRole> memberRoleList,
                                                           List<MemberRole> returnList, String memberType) {
        MemberRole memberRole = new MemberRole();
        memberRole.setMemberId(memberId);
        memberRole.setMemberType(memberType);
        memberRole.setSourceId(sourceId);
        memberRole.setSourceType(sourceType);
        List<MemberRole> existingMemberRoleList = memberRoleMapper.select(memberRole);
        List<Long> existingRoleIds =
                existingMemberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        List<Long> newRoleIds = memberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        //交集，传入的roleId与数据库里存在的roleId相交
        List<Long> intersection = existingRoleIds.stream().filter(newRoleIds::contains).collect(Collectors.toList());
        //传入的roleId与交集的差集为要插入的roleId
        List<Long> insertList = newRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        //数据库存在的roleId与交集的差集为要删除的roleId
        List<Long> deleteList = existingRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        returnList.addAll(existingMemberRoleList);
        List<MemberRole> memberRoleDTOS = new ArrayList<>();
        insertList.forEach(roleId -> {
            MemberRole mr = new MemberRole();
            mr.setRoleId(roleId);
            mr.setMemberId(memberId);
            mr.setMemberType(memberType);
            mr.setSourceType(sourceType);
            mr.setSourceId(sourceId);
            MemberRole memberRoleDTO = insertSelective(mr);
            returnList.add(memberRoleDTO);
            memberRoleDTOS.add(memberRoleDTO);
        });
        //批量添加，导入成功发送消息
        memberRoleDTOS.stream().forEach(memberRoleDTO -> {
            sendMsg(sourceType, fromUserId, memberRoleDTO, sourceId, memberRoleDTOS);
        });

        if (isEdit != null && isEdit && !deleteList.isEmpty()) {
            memberRoleMapper.selectDeleteList(memberId, sourceId, memberType, sourceType, deleteList)
                    .forEach(t -> {
                        if (t != null) {
                            memberRoleMapper.deleteByPrimaryKey(t);
                            returnList.removeIf(memberRoleDTO -> memberRoleDTO.getId().equals(t));
                        }
                    });
        }
        //查当前用户/客户端有那些角色
        return memberRoleMapper.select(memberRole)
                .stream().map(MemberRole::getRoleId).collect(Collectors.toList());
    }

    public MemberRole insertSelective(MemberRole memberRoleDTO) {
        if (memberRoleDTO.getMemberType() == null) {
            memberRoleDTO.setMemberType("user");
        }
        Role roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
        if (roleDTO == null) {
            throw new CommonException("error.member_role.insert.role.not.exist");
        }
        if (ResourceLevel.PROJECT.value().equals(memberRoleDTO.getSourceType())
                && projectMapper.selectByPrimaryKey(memberRoleDTO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.project.not.exist");
        }
        if (ResourceLevel.ORGANIZATION.value().equals(memberRoleDTO.getSourceType())
                && tenantC7nMapper.selectByPrimaryKey(memberRoleDTO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.organization.not.exist");
        }
        if (memberRoleMapper.selectOne(memberRoleDTO) != null) {
            throw new CommonException("error.member_role.has.existed");
        }
        if (memberRoleMapper.insertSelective(memberRoleDTO) != 1) {
            throw new CommonException("error.member_role.create");
        }
        //如果是平台root更新user表
        // todo 平台管理员判断需更改
//        if (SITE_ROOT.equals(roleDTO.getCode())) {
//            User userDTO = userMapper.selectByPrimaryKey(memberRoleDTO.getMemberId());
//            userDTO.setAdmin(true);
//            userMapper.updateByPrimaryKey(userDTO);
//        }
        return memberRoleMapper.selectByPrimaryKey(memberRoleDTO.getId());
    }

    private void sendMsg(String sourceType, Long fromUserId, MemberRole memberRoleDTO, Long sourceId, List<MemberRole> memberRoleDTOS) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Role roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
        Map<String, Object> params = new HashMap<>();
        // todo 消息发送
//        if (ResourceLevel.SITE.value().equals(sourceType)) {
//            // todo 平台管理员判断需更改
////            if (SITE_ROOT.equals(roleDTO.getCode())) {
////                userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), ROOT_BUSINESS_TYPE_CODE, Collections.EMPTY_MAP, sourceId);
////            } else {
////                params.put("roleName", roleDTO.getName());
////                userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), USER_BUSINESS_TYPE_CODE, params, sourceId);
////            }
//        }
//        if (ResourceType.ORGANIZATION.value().equals(sourceType)) {
//            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(sourceId);
//            params.put("organizationName", organizationDTO.getName());
//            params.put("roleName", roleDTO.getName());
//            //webhook json
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("organizationId", organizationDTO.getId());
//            jsonObject.put("addCount", 1);
//            WebHookJsonSendDTO.User webHookUser = userService.getWebHookUser(memberRoleDTO.getMemberId());
//            jsonObject.put("userList", JSON.toJSONString(Arrays.asList(webHookUser)));
//
////            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
////                    SendSettingBaseEnum.ADD_MEMBER.value(),
////                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.ADD_MEMBER.value()),
////                    jsonObject,
////                    new Date(),
////                    userService.getWebHookUser(fromUserId)
////            );
////            userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), BUSINESS_TYPE_CODE, params, sourceId, webHookJsonSendDTO);
//        }
//        if (ResourceType.PROJECT.value().equals(sourceType)) {
//            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(sourceId);
//            params.put("projectName", projectDTO);
//            params.put("roleName", roleDTO.getName());
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("organizationId", projectDTO.getOrganizationId());
//            jsonObject.put("addCount", 1);
//            WebHookJsonSendDTO.User webHookUser = userService.getWebHookUser(memberRoleDTO.getMemberId());
//            jsonObject.put("userList", JSON.toJSONString(Arrays.asList(webHookUser)));
//
////            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
////                    SendSettingBaseEnum.PROJECT_ADDUSER.value(),
////                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.PROJECT_ADDUSER.value()),
////                    jsonObject,
////                    new Date(),
////                    userService.getWebHookUser(fromUserId)
////            );
////            userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), PROJECT_ADD_USER, params, sourceId, webHookJsonSendDTO);
//        }
    }

}
