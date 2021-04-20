package io.choerodon.iam.infra.constant;

/**
 * @author zmf
 * @since 2021/4/19
 */
public final class RedisCacheKeyConstants {
    private RedisCacheKeyConstants() {
    }
    private static final String KEY_PREFIX = "c7n-iam:";

    /**
     * 用string缓存对应的项目信息
     * 参数为项目id
     * 结构为 {@link io.choerodon.iam.api.vo.ImmutableProjectInfoVO}
     */
    public static final String PROJECT_INFO = KEY_PREFIX + "immutable-proj-info:%s";
}
