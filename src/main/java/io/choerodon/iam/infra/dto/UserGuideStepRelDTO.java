package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:08
 */
@Table(name = "fd_user_guide_step_rel")
public class UserGuideStepRelDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty("菜单id")
    private Long userGuideId;
    @ApiModelProperty("步骤id")
    private Long stepId;
    @ApiModelProperty("步骤顺序")
    private Long stepOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserGuideId() {
        return userGuideId;
    }

    public void setUserGuideId(Long userGuideId) {
        this.userGuideId = userGuideId;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public Long getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Long stepOrder) {
        this.stepOrder = stepOrder;
    }
}
