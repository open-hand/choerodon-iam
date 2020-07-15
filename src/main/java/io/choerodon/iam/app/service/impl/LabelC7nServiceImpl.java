package io.choerodon.iam.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.infra.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.LabelC7nService;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;

/**
 * @author scp
 * @since 2020/3/31
 *
 */
@Service
public class LabelC7nServiceImpl implements LabelC7nService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private LabelC7nMapper labelC7nMapper;
    @Override
    public List<Label> listByOption(Label label) {
        return labelMapper.select(label);
    }

    @Override
    public Label selectByName(String name) {
        Label label = new Label();
        label.setName(name);
        return labelMapper.selectOne(label);
    }

    @Override
    public List<Label> listProjectGitlabLabels() {
        Set<String> names = new HashSet<>();
        names.add(RoleLabelEnum.GITLAB_OWNER.value());
        names.add(RoleLabelEnum.GITLAB_DEVELOPER.value());

        return labelC7nMapper.listByNames(names);
    }
}
