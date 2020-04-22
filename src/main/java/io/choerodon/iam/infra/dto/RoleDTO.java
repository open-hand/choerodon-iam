package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.Role;

/**
 * 补充Role缺失的字段
 *
 * @author zmf
 * @since 20-4-22
 */
public class RoleDTO extends Role {
    @ApiModelProperty("组织id")
    private Long organizationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
