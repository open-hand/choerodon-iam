package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 应用以及服务关联VO devops使用
 *
 * @author pengyuhua
 * @date 2019/9/17
 */
public class ApplicationServiceRefVO {
    @ApiModelProperty(value = "服务ID")
    private Long AppServiceId;
    @ApiModelProperty(value = "市场发布应用ID")
    private Long Id;
    @ApiModelProperty(value = "市场发布应用名称")
    private String Name;

    public Long getAppServiceId() {
        return AppServiceId;
    }

    public ApplicationServiceRefVO setAppServiceId(Long appServiceId) {
        AppServiceId = appServiceId;
        return this;
    }

    public Long getId() {
        return Id;
    }

    public ApplicationServiceRefVO setId(Long id) {
        Id = id;
        return this;
    }

    public String getName() {
        return Name;
    }

    public ApplicationServiceRefVO setName(String name) {
        Name = name;
        return this;
    }
}
