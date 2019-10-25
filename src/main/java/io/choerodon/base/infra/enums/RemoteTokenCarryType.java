package io.choerodon.base.infra.enums;

/**
 * token验证不通过的类型
 *
 * @author pengyuhua
 */
public enum RemoteTokenCarryType {
    /**
     * 配置并测试
     */
    CONFIGURE_AND_TEST("configure_and_test"),
    /**
     * 断开连接
     */
    DISCONNECT("disconnect"),
    /**
     * 重新连接
     */
    RECONNECTION("reconnection");

    private final String value;

    RemoteTokenCarryType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
