package io.choerodon.base.infra.enums;

/**
 * 应用发布的类型
 *
 * @author Eugen
 */
public enum PublishTypeEnum {
    /**
     * 仅可部署（默认）
     */
    DEPLOY_ONLY("mkt_deploy_only"),
    /**
     * 仅可下载
     */
    DOWNLOAD_ONLY("mkt_code_only"),
    /**
     * 可部署，可下载
     */
    ALL("mkt_code_deploy");

    private final String value;

    PublishTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }


    public static boolean isInclude(String key) {
        boolean include = false;
        for (PublishTypeEnum e : PublishTypeEnum.values()) {
            if (e.value().equalsIgnoreCase(key)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
