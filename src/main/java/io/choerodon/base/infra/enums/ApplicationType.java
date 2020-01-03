package io.choerodon.base.infra.enums;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/31
 */
public enum ApplicationType {

    /**
     * 自定义的应用
     */
    CUSTOM("custom"),

    /*
     * 应用市场下载包含有代码库的应用
     */
    MKT_CODE_BASE("mkt_code_only"),

    /**
     * 应用市场下载只能部署的应用
     */
    MKT_DEPLOY_ONLY("mkt_deploy_only"),

    /**
     * 应用市场下载有代码库并且能部署的应用
     */
    MKT_CODE_DEPLOY("mkt_code_deploy");

    private String value;

    ApplicationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
