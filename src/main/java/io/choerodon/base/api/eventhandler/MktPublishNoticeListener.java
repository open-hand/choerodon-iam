package io.choerodon.base.api.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.base.api.dto.payload.MarketAppPayload;
import io.choerodon.base.app.service.NotifyService;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.base.infra.template.MarketAppNoticeEmailTemplate;
import io.choerodon.base.infra.utils.NoticeUtils;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.choerodon.base.infra.utils.SagaTopic.PublishApp.PUBLISH_APP_FAIL;
import static io.choerodon.base.infra.utils.SagaTopic.PublishApp.TASK_PUBLISH_APP_FAIL;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_PUBLISH_FAIL_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_SVC_UPDATE_FAIL_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_PUBLISH_FAIL_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_SVC_UPDATE_FAIL_TITLE;

/**
 * @author jiameng.cao
 * @date 2019/9/20
 */
@Component
public class MktPublishNoticeListener {
    private final ObjectMapper mapper = new ObjectMapper();
    private UserMapper userMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    private NotifyService notifyService;

    public MktPublishNoticeListener(UserMapper userMapper, NotifyService notifyService) {
        this.userMapper = userMapper;
        this.notifyService = notifyService;
    }

    @SagaTask(code = TASK_PUBLISH_APP_FAIL, sagaCode = PUBLISH_APP_FAIL, seq = 1, description = "应用发布/更新修复版本失败")
    public MarketAppPayload publishFail(String message) throws IOException {
        MarketAppPayload payloads = mapper.readValue(message, MarketAppPayload.class);
        if (payloads == null) {
            throw new CommonException("error.sagaTask.sendPm.payloadsIsEmpty");
        }
        Long[] ids = new Long[1];
        ids[0] = payloads.getActionId();
        LOGGER.info("actionId" + payloads.getActionId());
        List<UserDTO> userDTOS = userMapper.listUsersByIds(ids, false);
        if (CollectionUtils.isEmpty(userDTOS)) {
            LOGGER.info("没有用户");
        }
        Boolean fixFlag = payloads.getFixFlag();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("title", (fixFlag ? APP_SVC_UPDATE_FAIL_TITLE : APP_PUBLISH_FAIL_TITLE) + " ");
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("operator", userDTOS.get(0).getRealName());
        messageMap.put("version", payloads.getVersion());
        messageMap.put("applicationName", payloads.getAppName());

        String content = fixFlag ? APP_SVC_UPDATE_FAIL_MSG : APP_PUBLISH_FAIL_MSG;
        content = NoticeUtils.renderMessageMapToString(content, messageMap);
        paramsMap.put("message", content);
        notifyService.sendMtkAppNotice(userDTOS, paramsMap, MarketAppNoticeEmailTemplate.BUSINESS_TYPE_CODE);
//        notifyService.sendMtkAppNotice(userDTOS, paramsMap, MarketAppNoticePmTemplate.BUSINESS_TYPE_CODE);
        return payloads;
    }
}
