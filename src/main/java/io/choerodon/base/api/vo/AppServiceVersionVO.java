package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author wanghao
 * @Date 2019/9/13 12:34
 */
public class AppServiceVersionVO {
    @ApiModelProperty(value = "服务版本主键")
    private Long id;
    @ApiModelProperty(value = "服务版本名称")
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
