package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Label;

/**
 * @author scp
 * @since 2020/3/31
 *
 */
public interface LabelC7nService {
    List<Label> listByOption(Label label);



    Label selectByName(String name);

    /**
     * 查询项目层gitlab标签
     * @return 标签列表
     */
    List<Label> listProjectGitlabLabels();

}
