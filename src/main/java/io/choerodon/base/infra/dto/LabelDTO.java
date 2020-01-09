package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

/**
 * @author superlee
 * @since 2019-04-23
 */
@Table(name = "iam_label")
public class LabelDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "层级")
    @Column(name = "fd_level")
    private String level;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "是否获取gitlab相关标签")
    @Transient
    private Boolean gitlabLabel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getGitlabLabel() {
        return gitlabLabel;
    }

    public void setGitlabLabel(Boolean gitlabLabel) {
        this.gitlabLabel = gitlabLabel;
    }
}
