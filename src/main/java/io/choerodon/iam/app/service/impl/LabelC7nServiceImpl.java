package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.infra.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.LabelC7nService;

/**
 * @author scp
 * @date 2020/3/31
 * @description
 */
@Service
public class LabelC7nServiceImpl implements LabelC7nService {
    @Autowired
    private LabelMapper labelMapper;

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
}
