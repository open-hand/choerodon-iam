package io.choerodon.base.infra.enums;

/**
 * 市场应用服务版本发布状态
 *
 * @author longhe6699@icloud.com
 * @date 2019/08/29
 */
public enum ApplicationSvcVersionStatusEnum {
    /*
    未发布
     */
    UNPUBLISHED("unpublished"),
    /*
    处理中
     */
    PROCESSING("processing"),
    /*
    已完成
     */
    DONE("done"),
    /*
    失败
     */
    FAILURE("failure");

    private final String value;

    ApplicationSvcVersionStatusEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

}
