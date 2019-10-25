package io.choerodon.base.api.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.base.api.dto.payload.MarketAppPayload;
import io.choerodon.base.app.service.NotifyService;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.base.infra.template.MarketAppNoticeEmailTemplate;
import io.choerodon.base.infra.utils.NoticeUtils;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.choerodon.base.infra.utils.SagaTopic.Application.APPLICATION_DOWNLOAD_COMPLETED;
import static io.choerodon.base.infra.utils.SagaTopic.Application.APPLICATION_DOWNLOAD_FAILED;
import static io.choerodon.base.infra.utils.SagaTopic.Application.APPLICATION_DOWNLOAD_PROCESSING;
import static io.choerodon.base.infra.utils.SagaTopic.Application.TASK_APPLICATION_DOWNLOAD_COMPLETED;
import static io.choerodon.base.infra.utils.SagaTopic.Application.TASK_APPLICATION_DOWNLOAD_FAILED;
import static io.choerodon.base.infra.utils.SagaTopic.Application.TASK_APPLICATION_DOWNLOAD_PROCESSING;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_COMPLETED_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_FAILED_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Message.APP_VERSION_DOWNLOAD_PROCESSING_MSG;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_COMPLETED_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_FAILED_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_TITLE;
import static io.choerodon.base.infra.utils.TemplateTitleAndMessage.Title.APP_VERSION_DOWNLOAD_PROCESSING_TITLE;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/23
 */
@Component
public class AppDownloadNoticeListener {
    private final ObjectMapper mapper = new ObjectMapper();

    private UserMapper userMapper;

    private NotifyService notifyService;

    public AppDownloadNoticeListener(UserMapper userMapper, NotifyService notifyService) {
        this.userMapper = userMapper;
        this.notifyService = notifyService;
    }

    @SagaTask(code = TASK_APPLICATION_DOWNLOAD_PROCESSING, sagaCode = APPLICATION_DOWNLOAD_PROCESSING, seq = 1, description = "开始应用版本下载")
    public MarketAppPayload downloadingAppVersion(String message) throws IOException {
        MarketAppPayload payload = praseMeesageToMarketAppPayload(message);
        String templateTitle = (payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_TITLE : APP_VERSION_DOWNLOAD_PROCESSING_TITLE) + " ";
        String templateMessage = payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_MSG : APP_VERSION_DOWNLOAD_PROCESSING_MSG;
        return sendNotifyAndEmailNotice(payload, templateTitle, templateMessage);
    }

    @SagaTask(code = TASK_APPLICATION_DOWNLOAD_COMPLETED, sagaCode = APPLICATION_DOWNLOAD_COMPLETED, seq = 1, description = "应用版本下载完成")
    public MarketAppPayload completeDownloadAppVersion(String message) throws IOException {
        MarketAppPayload payload = praseMeesageToMarketAppPayload(message);
        String templateTitle = (payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_TITLE : APP_VERSION_DOWNLOAD_COMPLETED_TITLE) + " ";
        String templateMessage = payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_MSG : APP_VERSION_DOWNLOAD_COMPLETED_MSG;
        return sendNotifyAndEmailNotice(payload, templateTitle, templateMessage);
    }

    @SagaTask(code = TASK_APPLICATION_DOWNLOAD_FAILED, sagaCode = APPLICATION_DOWNLOAD_FAILED, seq = 1, description = "应用版本下载失败")
    public MarketAppPayload failDownloadAppVersion(String message) throws IOException {
        MarketAppPayload payload = praseMeesageToMarketAppPayload(message);
        String templateTitle = (payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_TITLE : APP_VERSION_DOWNLOAD_FAILED_TITLE) + " ";
        String templateMessage = payload.getFixFlag() ? APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_MSG : APP_VERSION_DOWNLOAD_FAILED_MSG;
        return sendNotifyAndEmailNotice(payload, templateTitle, templateMessage);
    }

    private MarketAppPayload praseMeesageToMarketAppPayload(String message) throws IOException {
        MarketAppPayload payloads = mapper.readValue(message, MarketAppPayload.class);
        if (payloads == null) {
            throw new CommonException("error.sagaTask.sendPm.payloadsIsEmpty");
        }
        return payloads;
    }

    private MarketAppPayload sendNotifyAndEmailNotice(MarketAppPayload payloads, String templateTitle, String templateMessage) {
        UserDTO user = userMapper.selectByPrimaryKey(payloads.getActionId());

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("title", templateTitle);
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("operator", user.getRealName());
        messageMap.put("version", payloads.getVersion());
        messageMap.put("applicationName", payloads.getAppName());
        messageMap.put("downloader", user.getRealName());

        String content = templateMessage;
        content = NoticeUtils.renderMessageMapToString(content, messageMap);
        paramsMap.put("message", content);
        notifyService.sendMtkAppNotice(ImmutableList.of(user), paramsMap, MarketAppNoticeEmailTemplate.BUSINESS_TYPE_CODE);
        return payloads;
    }

}
