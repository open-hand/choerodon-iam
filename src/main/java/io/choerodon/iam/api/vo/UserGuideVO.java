package io.choerodon.iam.api.vo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:34
 */
public class UserGuideVO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty("步骤名")
    private String stepName;
    @ApiModelProperty("步骤描述")
    private String description;
    @ApiModelProperty("指引文档地址")
    private String docUrl;
    @ApiModelProperty("页面地址")
    private String pageUrl;

    @ApiModelProperty("步骤顺序")
    private Long stepOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Long getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Long stepOrder) {
        this.stepOrder = stepOrder;
    }
}
