package io.choerodon.iam.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.iam.app.service.MessageSendService;
import io.choerodon.iam.infra.constant.MessageCodeConstants;

@Service
public class MessageSendServiceImpl implements MessageSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendServiceImpl.class);

    @Autowired
    private MessageClient messageClient;

    @Override
    public void sendSiteAddUserMsg(User user, String roleName) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(MessageCodeConstants.SITE_ADD_USER);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);

            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("roleName", roleName);
            messageSender.setArgs(argsMap);

            // 接收者
            List<Receiver> receiverList = new ArrayList<>();
            Receiver receiver = new Receiver();
            receiver.setUserId(user.getId());
            // 发送邮件消息时 必填
            receiver.setEmail(user.getEmail());
            // 发送短信消息 必填
            receiver.setPhone(user.getPhone());
            // 必填
            receiver.setTargetUserTenantId(user.getOrganizationId());
            receiverList.add(receiver);
            messageSender.setReceiverAddressList(receiverList);
            messageClient.sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info("Send Add site user Msg failed. userId : {}, loginName : {}", user.getId(), user.getLoginName());
        }
    }

    @Override
    public void sendDisableUserMsg(User user, Long tenantId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(MessageCodeConstants.STOP_USER);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);

            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("loginName", user.getLoginName());
            argsMap.put("userName", user.getRealName());
            argsMap.put("enabled", user.getEnabled().toString());
            messageSender.setArgs(argsMap);

            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), tenantId);
            messageSender.setAdditionalInformation(objectMap);

            messageClient.sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info("Stop User failed. userId : {}, loginName : {}", user.getId(), user.getLoginName());
        }
    }
}
