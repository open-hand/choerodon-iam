package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.LabelService;
import io.choerodon.base.infra.dto.LabelDTO;
import io.choerodon.base.infra.enums.RoleLabel;
import io.choerodon.base.infra.mapper.LabelMapper;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author superlee
 */
@Service
public class LabelServiceImpl implements LabelService {

    private LabelMapper labelMapper;

    public LabelServiceImpl(LabelMapper labelMapper) {
        this.labelMapper = labelMapper;
    }

    @Override
    public List<LabelDTO> listByOption(LabelDTO label) {
        List<LabelDTO> labelDTOS = labelMapper.listByOption(label);
        // 组织层过滤organization.gitlab.owner标签
        if (ResourceLevel.ORGANIZATION.value().equals(label.getLevel())) {
            labelDTOS = labelDTOS.stream()
                    .filter(labelDTO -> !RoleLabel.ORGANIZATION_GITLAB_OWNER.value().equals(labelDTO.getName()))
                    .collect(Collectors.toList());
        }
        return labelDTOS;
    }

    @Override
    public List<LabelDTO> listByRoleId(Long roleId) {
        return labelMapper.selectByRoleId(roleId);
    }

    @Override
    public Set<String> selectLabelNamesInRoleIds(List<Long> ownRoleIds) {
        return labelMapper.selectLabelNamesInRoleIds(ownRoleIds);
    }
}
