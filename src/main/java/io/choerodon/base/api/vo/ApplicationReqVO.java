package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.Update;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/12
 */
public class ApplicationReqVO {

    private static final String APPLICATION_NAME_REG = "^[\\u4e00-\\u9fa5._\\-a-zA-Z0-9\\s]{1,32}$";

    @ApiModelProperty(value = "应用Id/非必填")
    private Long id;

    @ApiModelProperty(value = "应用名/必填")
    @NotEmpty(message = "error.application.name.empty", groups = {Insert.class, Update.class})
    @Size(min = 1, max = 32, message = "error.application.name.size", groups = {Insert.class, Update.class})
    @Pattern(regexp = APPLICATION_NAME_REG, message = "error.application.name.illegal", groups = {Insert.class, Update.class})
    private String name;

    @ApiModelProperty(value = "应用描述")
    private String description;

    @ApiModelProperty(value = "服务Ids列表")
    private Set<Long> serviceIds;

    @ApiModelProperty(value = "乐观锁版本号")
    @NotNull(message = "error.application.object.version.number.null", groups = {Update.class})
    private Long objectVersionNumber;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(Set<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
