package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.User;

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
}
