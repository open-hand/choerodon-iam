package io.choerodon.base.app.service;

import io.choerodon.base.infra.dto.LovDTO;

public interface LovService {
    LovDTO queryLovByCode(String code);
}
