package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;

/**
 * @author zmf
 * @since 20-4-23
 */
public class UserDTO extends User {
    @ApiModelProperty("组织code")
    private String organizationCode;

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
}
