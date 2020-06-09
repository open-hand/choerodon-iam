package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.User;
import org.hzero.iam.saas.domain.entity.Tenant;

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
    void sendProjectAddUserMsg(ProjectDTO projectDTO, String roleName, List<User> userList);


    /**
     * 组织层添加角色
     *
     * @param tenant
     * @param userList
     * @param roleName
     */
    void sendAddMemberMsg(Tenant tenant, String roleName, List<User> userList);

    /**
     * 启用或通用项目时发送通知
     * @param projectDTO
     * @param consumerType
     * @param enabled
     * @param userId
     */
    void sendDisableOrEnableProject(ProjectDTO projectDTO, String consumerType, boolean enabled, Long userId);
}
