package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;

/**
 * @author zmf
 * @since 20-4-21
 */
public class UserDTO extends User {
    @ApiModelProperty("组织code")
    private Long organizationCode;
    @ApiModelProperty("组织名称")
    private String organizationName;

    public Long getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(Long organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
