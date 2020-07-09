package io.choerodon.iam.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.MessageFeignClient;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MessageFeignClientFallback implements MessageFeignClient {
    @Override
    public ResponseEntity<List<Long>> getOnlineUserIds() {
        throw new CommonException("error.get.online.user.ids");
    }
}
