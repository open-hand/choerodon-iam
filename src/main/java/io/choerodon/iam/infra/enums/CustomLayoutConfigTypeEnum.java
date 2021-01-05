package io.choerodon.iam.infra.enums;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/1/5 19:48
 */
public enum CustomLayoutConfigTypeEnum {

    WORK_BENCH("work_bench"),
    PROJECT_OVERVIEW("project_overview");

    private String value;

    CustomLayoutConfigTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
