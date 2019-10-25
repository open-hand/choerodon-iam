package io.choerodon.base.infra.enums;

/**
 * token验证不通过的类型
 *
 * @author Eugen
 */
public enum RemoteTokenCheckFailedType {
    /**
     * token不存在
     */
    NOTEXIST("not exist"),
    /**
     * token已失效
     */
    EXPIRED("expired"),
    /**
     * 远程token连接功能已关闭
     */
    FUNCTION_CLOSED("function_closed");

    private final String value;

    RemoteTokenCheckFailedType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
