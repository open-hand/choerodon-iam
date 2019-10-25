package io.choerodon.base.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.base.app.service.NotifyService;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.feign.NotifyFeignClient;
import io.choerodon.core.notify.NoticeSendDTO;

@Component
public class NotifyServiceImpl implements NotifyService {

    private NotifyFeignClient notifyFeignClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    public NotifyServiceImpl(NotifyFeignClient notifyFeignClient) {
        this.notifyFeignClient = notifyFeignClient;
    }

    private static final String PARAM_REGISTRANT = "registrant";


    @Override
    public void sendMtkAppNotice(List<UserDTO> userIds, Map<String, Object> paramsMap, String code) {
        LOGGER.info("ready : send Notice to {} users", userIds.size());
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        List<NoticeSendDTO.User> users = new ArrayList<>();
        if (!org.springframework.util.CollectionUtils.isEmpty(userIds)) {
            userIds.forEach(userDTO -> {
                NoticeSendDTO.User user = new NoticeSendDTO.User();
                user.setId(userDTO.getId());

                user.setEmail(userDTO.getEmail());

                users.add(user);
            });
            noticeSendDTO.setCode(code);
            noticeSendDTO.setParams(paramsMap);
            noticeSendDTO.setTargetUsers(users);
            LOGGER.info("start : send Notice to {} users", userIds.size());
            notifyFeignClient.postNotice(noticeSendDTO);
            LOGGER.info("end : send Notice to {} users", userIds.size());

        }
    }
}
