package io.choerodon.iam.api.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.choerodon.iam.infra.valitador.SysSettingValidator;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author superlee
 * @since 2019-04-23
 */
public class SysSettingVO extends AuditDomain {

    @ApiModelProperty(value = "平台徽标，非必填字段，图片地址，大小缩放显示")
    private String favicon;

    @ApiModelProperty(value = "平台导航栏图形标，非必填字段，图片，大小缩放")
    private String systemLogo;

    @ApiModelProperty(value = "平台全称，非必填字段，如果此字段为空，则展示平台简称的信息")
    private String systemTitle;

    @ApiModelProperty(value = "平台简称，必填字段，20字符")
    @NotEmpty(message = "error.setting.name.null", groups = {SysSettingValidator.GeneralInfoGroup.class})
    @Length(max = 20, message = "error.setting.name.too.long", groups = {SysSettingValidator.GeneralInfoGroup.class})
    private String systemName;

    @ApiModelProperty(value = "平台默认密码，必填字段，至少6字符，至多15字符，数字或字母或其他特殊字符")
    @NotEmpty(message = "error.setting.default.password.null", groups = {SysSettingValidator.PasswordPolicyGroup.class})
    @Length(min = 6, max = 15, message = "error.setting.default.password.length.invalid", groups = {SysSettingValidator.PasswordPolicyGroup.class})
    @Pattern(regexp = "[a-zA-Z0-9~!@#$%^&*.:;|=+_]+", message = "error.setting.default.password.format.invalid", groups = {SysSettingValidator.PasswordPolicyGroup.class})
    private String defaultPassword;

    @ApiModelProperty(value = "平台默认语言，必填字段")
    @NotEmpty(message = "error.setting.default.language.null", groups = {SysSettingValidator.GeneralInfoGroup.class})
    private String defaultLanguage;

    @ApiModelProperty(value = "不启用组织层密码策略时的密码最小长度，非必填字段，默认6")
    @Range(min = 6, max = 15, message = "error.minLength", groups = {SysSettingValidator.PasswordPolicyGroup.class})
    private Integer minPasswordLength;

    @ApiModelProperty(value = "不启用组织层密码策略时的密码最大长度, 非必填字段，默认15")
    @Range(min = 6, max = 15, message = "error.maxLength", groups = {SysSettingValidator.PasswordPolicyGroup.class})
    private Integer maxPasswordLength;

    @ApiModelProperty(value = "是否启用注册")
    private Boolean registerEnabled;

    @ApiModelProperty(value = "注册页面链接")
    private String registerUrl;

    @ApiModelProperty(value = "重置gitlab密码页面链接")
    private String resetGitlabPasswordUrl;

    @ApiModelProperty(value = "平台主题色")
    private String themeColor;

    @ApiModelProperty(value = "是否自动清理邮件发送记录")
    private Boolean autoCleanEmailRecord;

    @ApiModelProperty(value = "自动清理邮件发送N天前记录")
    private Integer autoCleanEmailRecordInterval;

    @ApiModelProperty(value = "是否自动清理webhook发送记录")
    private Boolean autoCleanWebhookRecord;

    @ApiModelProperty(value = "自动清理webhook发送N天前记录")
    private Integer autoCleanWebhookRecordInterval;

    @ApiModelProperty(value = "自动清理事务实例记录")
    private Boolean autoCleanSagaInstance;

    @ApiModelProperty(value = "自动清理事务实例记录时间间隔")
    private Integer autoCleanSagaInstanceInterval;

    @ApiModelProperty(value = "是否保留失败的事务记录")
    private Boolean retainFailedSagaInstance;

    @ApiModelProperty(value = "systemTitle数据的token")
    private String systemTitleToken;

    @ApiModelProperty("systemName的token")
    private String systemNameToken;


    public Boolean getAutoCleanEmailRecord() {
        return autoCleanEmailRecord;
    }

    public void setAutoCleanEmailRecord(Boolean autoCleanEmailRecord) {
        this.autoCleanEmailRecord = autoCleanEmailRecord;
    }

    public Integer getAutoCleanEmailRecordInterval() {
        return autoCleanEmailRecordInterval;
    }

    public void setAutoCleanEmailRecordInterval(Integer autoCleanEmailRecordInterval) {
        this.autoCleanEmailRecordInterval = autoCleanEmailRecordInterval;
    }

    public Boolean getAutoCleanWebhookRecord() {
        return autoCleanWebhookRecord;
    }

    public void setAutoCleanWebhookRecord(Boolean autoCleanWebhookRecord) {
        this.autoCleanWebhookRecord = autoCleanWebhookRecord;
    }

    public Integer getAutoCleanWebhookRecordInterval() {
        return autoCleanWebhookRecordInterval;
    }

    public void setAutoCleanWebhookRecordInterval(Integer autoCleanWebhookRecordInterval) {
        this.autoCleanWebhookRecordInterval = autoCleanWebhookRecordInterval;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getSystemLogo() {
        return systemLogo;
    }

    public void setSystemLogo(String systemLogo) {
        this.systemLogo = systemLogo;
    }

    public String getSystemTitle() {
        return systemTitle;
    }

    public void setSystemTitle(String systemTitle) {
        this.systemTitle = systemTitle;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Integer getMinPasswordLength() {
        return minPasswordLength;
    }

    public void setMinPasswordLength(Integer minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }

    public Integer getMaxPasswordLength() {
        return maxPasswordLength;
    }

    public void setMaxPasswordLength(Integer maxPasswordLength) {
        this.maxPasswordLength = maxPasswordLength;
    }

    public Boolean getRegisterEnabled() {
        return registerEnabled;
    }

    public void setRegisterEnabled(Boolean registerEnabled) {
        this.registerEnabled = registerEnabled;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getResetGitlabPasswordUrl() {
        return resetGitlabPasswordUrl;
    }

    public void setResetGitlabPasswordUrl(String resetGitlabPasswordUrl) {
        this.resetGitlabPasswordUrl = resetGitlabPasswordUrl;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public Boolean getAutoCleanSagaInstance() {
        return autoCleanSagaInstance;
    }

    public void setAutoCleanSagaInstance(Boolean autoCleanSagaInstance) {
        this.autoCleanSagaInstance = autoCleanSagaInstance;
    }

    public Integer getAutoCleanSagaInstanceInterval() {
        return autoCleanSagaInstanceInterval;
    }

    public void setAutoCleanSagaInstanceInterval(Integer autoCleanSagaInstanceInterval) {
        this.autoCleanSagaInstanceInterval = autoCleanSagaInstanceInterval;
    }

    public Boolean getRetainFailedSagaInstance() {
        return retainFailedSagaInstance;
    }

    public void setRetainFailedSagaInstance(Boolean retainFailedSagaInstance) {
        this.retainFailedSagaInstance = retainFailedSagaInstance;
    }

    public String getSystemTitleToken() {
        return systemTitleToken;
    }

    public void setSystemTitleToken(String systemTitleToken) {
        this.systemTitleToken = systemTitleToken;
    }

    public String getSystemNameToken() {
        return systemNameToken;
    }

    public void setSystemNameToken(String systemNameToken) {
        this.systemNameToken = systemNameToken;
    }
}
