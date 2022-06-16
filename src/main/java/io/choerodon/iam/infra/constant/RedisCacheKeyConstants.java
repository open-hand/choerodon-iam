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
    public static final String PROJECT_INFO = KEY_PREFIX + "immutable-project-info:%s";

    /**
     * 用户是否是gitlab owner，过期时间 10 秒
     * 值是 true 或者 false
     * 参数是项目id，用户id
     */
    public static final String IS_GITLAB_OWNER = KEY_PREFIX + "is_gtlb_owner:%s-%s";

    /**
     * 是否正在修复菜单层级
     */
    public static final String FIX_MENU_LEVEL_PATH_FLAG = KEY_PREFIX + "fix_menu_level_path_flag";
    /**
     * 组织访问人数记录
     */
    public static final String TENANT_VISITORS_FORMAT = "tenant-visitors:%s";
    /**
     * 登录页面缓存
     */
    public static final String REDIS_KEY_LOGIN = "c7n-iam:settingLogin";

}
