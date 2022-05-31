package io.choerodon.iam.infra.enums;

/**
 * 系统配置项
 */
public enum SysSettingEnum {
    /**
     * 平台徽标
     */
    FAVICON("favicon"),
    /**
     * 平台导航栏图形标
     */
    SYSTEM_LOGO("systemLogo"),
    /**
     * 平台全称
     */
    SYSTEM_TITLE("systemTitle"),
    /**
     * 平台简称
     */
    SYSTEM_NAME("systemName"),
    /**
     * 平台默认语言
     */
    DEFAULT_LANGUAGE("defaultLanguage"),

    /**
     * 平台默认密码
     */
    DEFAULT_PASSWORD("defaultPassword"),
    /**
     * 不启用组织层密码策略时的密码最小长度
     */
    MIN_PASSWORD_LENGTH("minPasswordLength"),
    /**
     * 不启用组织层密码策略时的密码最大长度
     */
    MAX_PASSWORD_LENGTH("maxPasswordLength"),
    /**
     * 是否启用注册
     */
    REGISTER_ENABLED("registerEnabled"),
    /**
     * 注册页面链接
     */
    REGISTER_URL("registerUrl"),
    /**
     * 重置gitlab密码页面链接
     */
    RESET_GITLAB_PASSWORD_URL("resetGitlabPasswordUrl"),
    /**
     * 是否自动清理邮件方发送日志
     */
    AUTO_CLEAN_EMAIL_RECORD("autoCleanEmailRecord"),

    /**
     * 自动清理邮件方发送日志 时间间隔
     */
    AUTO_CLEAN_EMAIL_RECORD_INTERVAL("autoCleanEmailRecordInterval"),

    /**
     * 自动清理webhook方发送日志
     */
    AUTO_CLEAN_WEBHOOK_RECORD("autoCleanWebhookRecord"),

    /**
     * 自动清理webhook方发送日志 时间间隔
     */
    AUTO_CLEAN_WEBHOOK_RECORD_INTERVAL("autoCleanWebhookRecordInterval"),
    /**
     * 自动清理事务实例记录
     */
    AUTO_CLEAN_SAGA_INSTANCE("autoCleanSagaInstance"),

    /**
     * 自动清理事务实例记录 时间间隔
     */
    AUTO_CLEAN_SAGA_INSTANCE_INTERVAL("autoCleanSagaInstanceInterval"),

    RETAIN_FAILED_SAGA_INSTANCE("retainFailedSagaInstance"),
    /**
     * 平台主题色
     */
    THEME_COLOR("themeColor"),
    LOGIN_DING_TALK_APP_SECRET("loginDingTalkAppSecret"),
    LOGIN_WAY("loginWay");// 登录方式

    private final String value;

    SysSettingEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
