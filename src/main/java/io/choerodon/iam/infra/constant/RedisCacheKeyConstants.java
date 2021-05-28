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

    /**
     * 用户是否是gitlab owner，过期时间 10 秒
     * 值是 true 或者 false
     */
    public static final String IS_GITLAB_OWNER = KEY_PREFIX + "is_gtlb_owner:%s";

    /**
     * 是否正在修复菜单层级
     */
    public static final String FIX_MENU_LEVEL_PATH_FLAG = KEY_PREFIX + "fix_menu_level_path_flag";
}
