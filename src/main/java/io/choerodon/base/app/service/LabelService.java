package io.choerodon.base.app.service;


import io.choerodon.base.infra.dto.LabelDTO;

import java.util.List;

/**
 * @author superlee
 */
public interface LabelService {
    List<LabelDTO> listByOption(LabelDTO label);
}
