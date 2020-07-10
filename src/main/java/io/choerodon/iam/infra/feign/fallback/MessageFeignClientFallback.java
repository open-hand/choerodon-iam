package io.choerodon.iam.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.MessageFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageFeignClientFallback implements MessageFeignClient {
    @Override
    public ResponseEntity<List<Long>> getOnlineUserIds() {
        throw new CommonException("error.get.online.user.ids");
    }
}
