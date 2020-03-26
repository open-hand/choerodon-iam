package io.choerodon.base.app.service;


import io.choerodon.base.infra.dto.LabelDTO;

import java.util.List;
import java.util.Set;

/**
 * @author superlee
 */
public interface LabelService {
    List<LabelDTO> listByOption(LabelDTO label);

    List<LabelDTO> listByRoleId(Long roleId);

    Set<String> selectLabelNamesInRoleIds(List<Long> ownRoleIds);
}
