package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;

import io.choerodon.iam.infra.dto.ProjectDTO;

public interface MessageSendService {

    /**
     * 发送平台层添加用户消息
     *
     * @param user
     * @param roleName
     */
    void sendSiteAddUserMsg(User user, String roleName);

    /**
     * 发送停用用户消息
     *
     * @param user
     * @param tenantId
     */
    void sendDisableUserMsg(User user, Long tenantId);

    /**
     * 发送添加团队成员消息
     *
     * @param projectDTO
     * @param roleName
     * @param userList
     */
    void sendProjectAddUserMsg(ProjectDTO projectDTO, String roleName, List<User> userList, Long operatorId);


    /**
     * 组织层添加角色
     *
     * @param tenant
     * @param userList
     * @param roleName
     */
    void sendAddMemberMsg(Tenant tenant, String roleName, List<User> userList, Long operatorId);

    /**
     * 启用或通用项目时发送通知
     *
     * @param projectDTO
     * @param consumerType
     * @param enabled
     * @param userId
     */
    void sendDisableOrEnableProject(ProjectDTO projectDTO, String consumerType, boolean enabled, Long userId);

    /**
     * 启用或停用组织
     *
     * @param tenant
     * @param consumerType
     * @param userId
     */
    void sendDisableOrEnableTenant(Tenant tenant, String consumerType, Long userId);

    /**
     * 平台添加root用户
     *
     * @param rootBusinessTypeCode
     * @param userId
     */
    void sendSiteAddRoot(String rootBusinessTypeCode, Long userId);

    /**
     * 组织层添加用户
     *
     * @param tenant
     * @param params
     * @param businessTypeCode
     */
    void sendAddMemberMsg(Tenant tenant, Map<String, String> params, String businessTypeCode, Long userId);

    /**
     * 项目层添加用户
     *
     * @param projectDTO
     * @param params
     * @param projectAddUser
     * @param userId
     */
    void sendProjectAddUserMsg(ProjectDTO projectDTO, Map<String, String> params, String projectAddUser, Long userId);
}
