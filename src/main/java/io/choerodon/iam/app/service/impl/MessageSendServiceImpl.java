package io.choerodon.iam.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.TenantC7nMapper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.iam.app.service.MessageSendService;
import io.choerodon.iam.infra.constant.MessageCodeConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.payload.WebHookUser;
import org.springframework.util.CollectionUtils;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.*;
import static io.choerodon.iam.infra.utils.SagaTopic.Organization.*;

@Service
public class MessageSendServiceImpl implements MessageSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendServiceImpl.class);

    @Autowired
    private MessageClient messageClient;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserC7nService userC7nService;

    @Autowired
    private TenantC7nMapper tenantC7nMapper;

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
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>Send Add site user Msg failed. userId : {}, loginName : {}", user.getId(), user.getLoginName());
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

            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>Stop User failed. userId : {}, loginName : {}", user.getId(), user.getLoginName());
        }
    }

    @Override
    public void sendProjectAddUserMsg(ProjectDTO projectDTO, String roleName, List<User> userList) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(MessageCodeConstants.PROJECT_ADD_USER);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);

            List<WebHookUser> webHookUsers = new ArrayList<>();
            // 接收者
            List<Receiver> receiverList = new ArrayList<>();
            userList.forEach(user -> {
                WebHookUser webHookUser = new WebHookUser();
                webHookUser.setLoginName(user.getLoginName());
                webHookUser.setUserName(user.getRealName());
                webHookUsers.add(webHookUser);

                Receiver receiver = new Receiver();
                receiver.setUserId(user.getId());
                // 发送邮件消息时 必填
                receiver.setEmail(user.getEmail());
                // 发送短信消息 必填
                receiver.setPhone(user.getPhone());
                // 必填
                receiver.setTargetUserTenantId(user.getOrganizationId());
                receiverList.add(receiver);

            });

            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("projectName", projectDTO.getName());
            argsMap.put("roleName", roleName);
            argsMap.put("organizationId", projectDTO.getOrganizationId().toString());
            argsMap.put("addCount", String.valueOf(userList.size()));
            argsMap.put("userList", JSON.toJSONString(webHookUsers));
            messageSender.setArgs(argsMap);
            messageSender.setReceiverAddressList(receiverList);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>Send Add project user failed. userList : {}", JSON.toJSONString(userList));
        }
    }

    @Override
    public void sendAddMemberMsg(Tenant tenant, String roleName, List<User> userList) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(MessageCodeConstants.ADD_MEMBER);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);

            // 接收者
            List<Receiver> receiverList = new ArrayList<>();
            List<WebHookUser> webHookUserList = new ArrayList<>();
            userList.forEach(user -> {

                WebHookUser webHookUser = new WebHookUser();
                webHookUser.setLoginName(user.getLoginName());
                webHookUser.setUserName(user.getRealName());
                webHookUserList.add(webHookUser);

                Receiver receiver = new Receiver();
                receiver.setUserId(user.getId());
                // 发送邮件消息时 必填
                receiver.setEmail(user.getEmail());
                // 发送短信消息 必填
                receiver.setPhone(user.getPhone());
                // 必填
                receiver.setTargetUserTenantId(tenant.getTenantId());
                receiverList.add(receiver);
            });
            messageSender.setReceiverAddressList(receiverList);

            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("organizationName", tenant.getTenantName());
            argsMap.put("roleName", roleName);
            argsMap.put("organizationId", tenant.getTenantId().toString());
            argsMap.put("addCount", String.valueOf(userList.size()));
            argsMap.put("userList", JSON.toJSONString(webHookUserList));
            messageSender.setArgs(argsMap);

            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), tenant.getTenantId());
            messageSender.setAdditionalInformation(objectMap);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>send add member msg failed. roleName : {}, userList : {}", roleName, JSON.toJSONString(userList));
        }
    }

    @Override
    public void sendDisableOrEnableProject(ProjectDTO projectDTO, String consumerType, boolean enabled, Long userId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            if (PROJECT_DISABLE.equals(consumerType)) {
                messageSender.setMessageCode(MessageCodeConstants.DISABLEPROJECT);
            } else if (PROJECT_ENABLE.equals(consumerType)) {
                messageSender.setMessageCode(MessageCodeConstants.ENABLEPROJECT);
            } else {
                LOGGER.info("disable or enable is null");
                return;
            }
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);
            // 接收者为所有的项目成员
            List<Receiver> receiverList = new ArrayList<>();
            List<WebHookUser> webHookUserList = new ArrayList<>();
            List<Long> userIds = projectMapper.listUserIds(projectDTO.getId());
            if (!CollectionUtils.isEmpty(userIds)) {
                Long[] ids = userIds.toArray(new Long[]{});
                List<User> userList = userC7nService.listUsersByIds(ids, Boolean.TRUE);
                userList.forEach(user -> {
                    Receiver receiver = new Receiver();
                    receiver.setUserId(user.getId());
                    // 发送邮件消息时 必填
                    receiver.setEmail(user.getEmail());
                    // 发送短信消息 必填
                    receiver.setPhone(user.getPhone());
                    // 必填
                    receiver.setTargetUserTenantId(projectDTO.getOrganizationId());
                    receiverList.add(receiver);
                });
            }
            messageSender.setReceiverAddressList(receiverList);
            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("projectName", projectDTO.getName());
            messageSender.setArgs(argsMap);

            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), projectDTO.getOrganizationId());
            messageSender.setAdditionalInformation(objectMap);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>send Disable Or Enable Project projectDTO: {}, consumerType:{},userId:{}", JSON.toJSONString(projectDTO), consumerType, userId);
        }
    }

    @Override
    public void sendDisableOrEnableTenant(Tenant tenant, String consumerType, Long userId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            if (ORG_ENABLE.equals(consumerType)) {
                messageSender.setMessageCode(MessageCodeConstants.ENABLEORGANIZATION);
            } else if (ORG_DISABLE.equals(consumerType)) {
                messageSender.setMessageCode(MessageCodeConstants.DISABLE_ORGANIZATION);
            } else {
                LOGGER.info("disable or enable is null");
                return;
            }
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);
            // 接收者为组织下所有成员
            List<Receiver> receiverList = new ArrayList<>();
            List<WebHookUser> webHookUserList = new ArrayList<>();
            List<User> users = tenantC7nMapper.listMemberIds(tenant.getTenantId());
            if (!CollectionUtils.isEmpty(users)) {
                Long[] ids = users.toArray(new Long[]{});
                List<User> userList = userC7nService.listUsersByIds(ids, Boolean.TRUE);
                userList.forEach(user -> {
                    Receiver receiver = new Receiver();
                    receiver.setUserId(user.getId());
                    // 发送邮件消息时 必填
                    receiver.setEmail(user.getEmail());
                    // 发送短信消息 必填
                    receiver.setPhone(user.getPhone());
                    // 必填
                    receiver.setTargetUserTenantId(tenant.getTenantId());
                    receiverList.add(receiver);
                });
            }
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put("organizationName", tenant.getTenantName());
            argsMap.put("organizationId", String.valueOf(tenant.getTenantId()));
            argsMap.put("code", tenant.getTenantNum());
            argsMap.put("name", tenant.getTenantNum());
            argsMap.put("enabled", ORG_ENABLE.equals(consumerType) ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
            messageSender.setArgs(argsMap);

            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), tenant.getTenantId());
            messageSender.setAdditionalInformation(objectMap);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>send Disable Or Enable Tenant: {}, consumerType:{},userId:{}", JSON.toJSONString(tenant), consumerType, userId);
        }
    }

    @Override
    public void sendSiteAddRoot(String rootBusinessTypeCode, Long userId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(rootBusinessTypeCode);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);
            // 接收者为组织下所有成员
            List<Receiver> receiverList = new ArrayList<>();
            User user = userC7nService.queryInfo(userId);
            Receiver receiver = new Receiver();
            receiver.setUserId(user.getId());
            // 发送邮件消息时 必填
            receiver.setEmail(user.getEmail());
            // 发送短信消息 必填
            receiver.setPhone(user.getPhone());
            // 必填
            receiver.setTargetUserTenantId(user.getOrganizationId());
            receiverList.add(receiver);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>send Site Add Root rootBusinessTypeCode: {}, userId:{}", rootBusinessTypeCode, userId);
        }
    }

    @Override
    public void sendAddMemberMsg(Tenant tenant, Map<String, String> argsMap, String businessTypeCode, Long userId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            messageSender.setMessageCode(businessTypeCode);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);
            // 接收者为组织下所有成员
            User user = userC7nService.queryInfo(userId);
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

            messageSender.setArgs(argsMap);
            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), tenant.getTenantId());
            messageSender.setAdditionalInformation(objectMap);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>send Add Member Msg tenant: {}, userId:{}", JSON.toJSONString(tenant), userId);
        }
    }

    @Override
    public void sendProjectAddUserMsg(ProjectDTO projectDTO, Map<String, String> params, String projectAddUser, Long userId) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            messageSender.setMessageCode(projectAddUser);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);
            // 接收者为组织下所有成员
            User user = userC7nService.queryInfo(userId);
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

            messageSender.setArgs(params);
            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), projectDTO.getOrganizationId());
            messageSender.setAdditionalInformation(objectMap);
            messageClient.async().sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>send Project Add User Msg projectDTO: {},params:{},projectAddUser:{} userId:{}", JSON.toJSONString(projectDTO), params, projectAddUser, userId);
        }
    }
}
