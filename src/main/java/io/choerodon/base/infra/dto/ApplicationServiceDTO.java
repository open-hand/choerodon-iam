package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/7
 */
@Table(name = "fd_application_service")
public class ApplicationServiceDTO extends BaseDTO {

    @Id
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "服务名")
    private String name;

    @ApiModelProperty(value = "服务编码")
    private String code;

    @ApiModelProperty(value = "服务状态")
    @Column(name = "is_active")
    private Boolean isActive;

    @ApiModelProperty(value = "应用Id")
    private String appId;

    @ApiModelProperty(value = "服务类型")
    private String type;

    @ApiModelProperty(value = "服务图标url")
    private String imgUrl;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
