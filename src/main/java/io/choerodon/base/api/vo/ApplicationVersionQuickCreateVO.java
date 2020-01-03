package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

import static io.choerodon.base.infra.utils.RegularExpression.ALPHANUMERIC_AND_SYMBOLS;

/**
 * @author Eugen
 * <p>
 * 此VO用于应用版本的快捷创建
 */
public class ApplicationVersionQuickCreateVO {

    @ApiModelProperty(value = "应用版本名称")
    @NotEmpty(message = "error.application.version.create.version.can.not.be.empty")
    @Pattern(regexp = ALPHANUMERIC_AND_SYMBOLS, message = "error.application.version.create.version.invalid")
    private String version;

    @ApiModelProperty(value = "应用版本下服务版本主键列表")
    @NotEmpty(message = "error.application.version.create.service.version.ref.can.not.be.empty")
    private Set<Long> serviceVersionIds;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<Long> getServiceVersionIds() {
        return serviceVersionIds;
    }

    public void setServiceVersionIds(Set<Long> serviceVersionIds) {
        this.serviceVersionIds = serviceVersionIds;
    }
}
