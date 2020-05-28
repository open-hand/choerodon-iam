package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.User;

import io.choerodon.iam.infra.dto.ProjectDTO;

public interface MessageSendService {

    /**
     * 发送平台层添加用户消息
     * @param user
     * @param roleName
     */
    void sendSiteAddUserMsg(User user, String roleName);

    /**
     * 发送停用用户消息
     * @param user
     * @param tenantId
     */
    void sendDisableUserMsg(User user, Long tenantId);

    /**
     * 发送添加团队成员消息
     * @param projectDTO
     * @param roleName
     * @param userList
     */
    void sendProjectAddUserMsg(ProjectDTO projectDTO, String roleName, List<User> userList);
}
