package io.choerodon.iam.infra.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zmf
 * @since 20-4-23
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserDTO extends User {

    public static final String EMAIL_REG = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    public static final String PHONE_REG = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$";

    @Transient
    @ApiModelProperty("组织code")
    private String organizationCode;
    @Transient
    @ApiModelProperty(value = "组织名称/非必填")
    private String organizationName;
    @JsonIgnore
    @Transient
    private String originalPassword;

    @Transient
    @ApiModelProperty(value = "用户角色编码,多个用英文逗号隔开")
    private String roleCodes;

    @Transient
    @ApiModelProperty(value = "用户角色标签,多个用英文逗号隔开")
    private String roleLabels;

    @Transient
    @ApiModelProperty(value = "事务实例id")
    @Encrypt
    private Long sagaInstanceId;

    @ApiModelProperty("手机号是否绑定")
    private Boolean phoneBind;

    public Boolean getPhoneBind() {
        return phoneBind;
    }

    public void setPhoneBind(Boolean phoneBind) {
        this.phoneBind = phoneBind;
    }

    public Long getSagaInstanceId() {
        return sagaInstanceId;
    }

    public void setSagaInstanceId(Long sagaInstanceId) {
        this.sagaInstanceId = sagaInstanceId;
    }

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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
