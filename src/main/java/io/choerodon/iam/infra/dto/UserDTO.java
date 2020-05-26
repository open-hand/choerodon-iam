package io.choerodon.iam.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;
import org.hzero.mybatis.domian.SecurityToken;

import javax.persistence.Transient;

/**
 * @author zmf
 * @since 20-4-23
 */
public class UserDTO extends User {

    public static final String EMAIL_REG = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    public static final String PHONE_REG = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$";

    @Transient
    @ApiModelProperty("组织code")
    private String organizationCode;

    @JsonIgnore
    @Transient
    private String originalPassword;

    @Transient
    @ApiModelProperty(value = "用户角色编码,多个用英文逗号隔开")
    private String roleCodes;

    @Transient
    @ApiModelProperty(value = "用户角色标签,多个用英文逗号隔开")
    private String roleLabels;

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(String roleCodes) {
        this.roleCodes = roleCodes;
    }

    public String getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(String roleLabels) {
        this.roleLabels = roleLabels;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return User.class;
    }
}
