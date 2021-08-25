package org.hzero.iam.domain.entity;

import java.util.List;
import java.util.StringJoiner;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.util.Regexs;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.helper.snowflake.annotation.SnowflakeExclude;


/**
 * 临时覆盖 下次升级hzero时删除
 * 覆盖tenantCode长度限制为30
 */
@ModifyAudit
@VersionAudit
@MultiLanguage
@Table(name = "hpfm_tenant")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tenant extends AuditDomain {

    public static final String TENANT_ID = "tenantId";
    public static final String TENANT_NAME = "tenantName";
    public static final String TENANT_NUM = "tenantNum";
    public static final String ENABLED_FLAG = "enabledFlag";

    public static final String NULL_VALUE = "";
    public static final String LIMIT_USER_QTY = "limitUserQty";

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @SnowflakeExclude
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @NotBlank
    @Length(max = 120)
    @MultiLanguageField
    @ApiModelProperty("租户名称")
    private String tenantName;
    @NotBlank
    @Length(max = 30)
    @ApiModelProperty("租户编号")
    @Pattern(regexp = Regexs.CODE)
    private String tenantNum;
    @NotNull
    @Range(max = 1, min = 0)
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("限制用户数")
    private Integer limitUserQty;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty("默认集团")
    private Group group;
    @Transient
    @ApiModelProperty("数据来源key")
    private String sourceKey;
    @Transient
    @ApiModelProperty("数据来源代码")
    private String sourceCode;

    @Transient
    @ApiModelProperty("租户配置")
    private List<TenantConfig> tenantConfigs;

    @Transient
    @ApiModelProperty("包含客制化菜单")
    private Integer customMenuFlag;

    @Transient
    @ApiModelProperty("是否开启数据加密")
    private Integer enableDataSecurity;

    @Transient
    private List<Long> excludeTenantIds;

    /**
     * 是否继承自租户管理员模板
     */
    @Transient
    private Boolean extendTemplateRole;

    public Tenant() {
    }

    public Tenant(Long tenantId, String tenantNum) {
        this.tenantId = tenantId;
        this.tenantNum = tenantNum;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Tenant setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    public Tenant setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    /**
     * @return 租户编号
     */
    public String getTenantNum() {
        return tenantNum;
    }

    public Tenant setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
        return this;
    }

    /**
     * @return 是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 传输字段: 默认集团
     */
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * @return 传输字段: 数据来源Key
     */
    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    /**
     * @return 传输字段: 数据来源代码
     */
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * @return 限制用户数，null表示不限制用户数
     */
    public Integer getLimitUserQty() {
        return limitUserQty;
    }

    public void setLimitUserQty(Integer limitUserQty) {
        this.limitUserQty = limitUserQty;
    }

    public List<TenantConfig> getTenantConfigs() {
        return tenantConfigs;
    }

    public void setTenantConfigs(List<TenantConfig> tenantConfigs) {
        this.tenantConfigs = tenantConfigs;
    }

    public Integer getCustomMenuFlag() {
        return customMenuFlag;
    }

    public Tenant setCustomMenuFlag(Integer customMenuFlag) {
        this.customMenuFlag = customMenuFlag;
        return this;
    }

    public Integer getEnableDataSecurity() {
        return enableDataSecurity;
    }

    public Tenant setEnableDataSecurity(Integer enableDataSecurity) {
        this.enableDataSecurity = enableDataSecurity;
        return this;
    }

    public List<Long> getExcludeTenantIds() {
        return excludeTenantIds;
    }

    public void setExcludeTenantIds(List<Long> excludeTenantIds) {
        this.excludeTenantIds = excludeTenantIds;
    }

    public Boolean getExtendTemplateRole() {
        return extendTemplateRole;
    }

    public void setExtendTemplateRole(Boolean extendTemplateRole) {
        this.extendTemplateRole = extendTemplateRole;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Tenant.class.getSimpleName() + "[", "]")
                .add("tenantId=" + tenantId)
                .add("tenantName='" + tenantName + "'")
                .add("tenantNum='" + tenantNum + "'")
                .add("enabledFlag=" + enabledFlag)
                .add("limitUserQty=" + limitUserQty)
                .add("group=" + group)
                .add("sourceKey='" + sourceKey + "'")
                .add("sourceCode='" + sourceCode + "'")
                .add("tenantConfigs=" + tenantConfigs)
                .add("customMenuFlag=" + customMenuFlag)
                .add("enableDataSecurity=" + enableDataSecurity)
                .add("extendTemplateRole=" + extendTemplateRole)
                .toString();
    }
}
