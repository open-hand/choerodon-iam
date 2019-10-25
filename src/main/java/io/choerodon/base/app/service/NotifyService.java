package io.choerodon.base.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.base.infra.dto.UserDTO;

/**
 * @author Eugen
 */
public interface NotifyService {

    void sendMtkAppNotice(List<UserDTO> userDTOS, Map<String, Object> paramsMap, String businessTypeCode);
}
