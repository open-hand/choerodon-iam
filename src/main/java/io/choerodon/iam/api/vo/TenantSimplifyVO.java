package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author Eugen
 */
public class TenantSimplifyVO {
    @ApiModelProperty(value = "主键")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "组织名")
    private String name;

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
}
