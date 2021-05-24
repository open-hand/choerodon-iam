package io.choerodon.iam.infra.enums;

/**
 * Created by wangxiang on 2021/5/24
 */
public enum  ReportModuleEnum {

    /**
     * 开发图表
     */
    DEVELOP("develop"),

    /**
     * 协作图表
     */
    COOPERATE("cooperate"),

    /**
     * 部署图表
     */
    DEPLOY("deploy");

    private final String value;

    ReportModuleEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }


}
