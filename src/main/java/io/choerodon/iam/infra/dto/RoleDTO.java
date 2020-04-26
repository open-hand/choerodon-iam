package io.choerodon.iam.infra.dto;

import java.util.List;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;

/**
 * @author scp
 * @date 2020/4/26
 * @description
 */
public class RoleDTO extends Role {
    private List<Label> labels;

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
