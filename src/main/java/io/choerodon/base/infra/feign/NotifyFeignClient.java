package io.choerodon.base.infra.feign;

import javax.validation.Valid;

import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.base.api.dto.SystemAnnouncementDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.choerodon.base.infra.feign.fallback.NotifyFeignClientFallback;

@FeignClient(value = "notify-service", path = "/v1", fallback = NotifyFeignClientFallback.class)
public interface NotifyFeignClient {

    @PostMapping("/notices")
    void postNotice(@RequestBody @Valid NoticeSendDTO dto);

    @PostMapping("/announcements")
    ResponseEntity<SystemAnnouncementDTO> create(@RequestBody @Validated SystemAnnouncementDTO dto);
}