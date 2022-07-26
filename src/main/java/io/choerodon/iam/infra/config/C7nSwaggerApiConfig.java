package io.choerodon.iam.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * <p>
 * Swagger Api 描述配置
 * </p>
 *
 * @author qingsheng.chen 2018/7/30 星期一 14:26
 */
@Configuration
public class C7nSwaggerApiConfig {
    public static final String CHOERODON_SYSTEM_SETTING = "Choerodon System Setting";
    public static final String CHOERODON_CLIENT = "Choerodon Client";
    public static final String CHOERODON_CLIENT_PROJECT = "Choerodon Client Project";
    public static final String CHOERODON_LABEL = "Choerodon Lable";
    public static final String CHOERODON_MENU = "Choerodon Menu";
    public static final String CHOERODON_MENU_ROLE = "Choerodon Menu Role";
    public static final String CHOERODON_TENANT_USER = "Choerodon Tenant User";
    public static final String CHOERODON_PERMISSION = "Choerodon Permission";
    public static final String CHOERODON_PROJECT = "Choerodon Project";
    public static final String CHOERODON_PROJECT_TYPE = "Choerodon Project Type";
    public static final String CHOERODON_PROJECT_USER = "Choerodon Project User";
    public static final String CHOERODON_AGILE_PROJECT = "Choerodon Agile Project";
    public static final String CHOERODON_PROD = "Choerodon prod";
    public static final String CHOERODON_ORGANIZATION_PROJECT = "Choerodon Organization Project";
    public static final String CHOERODON_REPORT = "Choerodon Report";
    public static final String CHOERODON_TENANT = "Choerodon Tenant";
    public static final String CHOERODON_USER = "Choerodon User";
    public static final String ORGANIZATION_USER = "Organization User";
    public static final String CHOERODON_TENANT_PROJECT = "Choerodon Tenant Project";
    public static final String CHOERODON_USER_SELF = "Choerodon User Self";
    public static final String CHOERODON_ROLE_MEMBER = "Choerodon Role Member";
    public static final String CHOERODON_DATA_FIX = "Choerodon Data Fix";
    public static final String CHOERODON_WORK_CALENDAR = "Choerodon Work Calendar";
    public static final String CHOERODON_WORK_CALENDAR_PROJECT = "Choerodon Work Calendar Project";
    public static final String CHOERODON_WORK_CALENDAR_HOLIDAY_REF = "Choerodon Work Calendar Holiday Ref";
    public static final String CHOERODON_ORGANIZATION_ADMIN = "Choerodon Organization Admin";
    public static final String CHOERODON_ORGANIZATION_PROJECT_CATEGORY = "Choerodon Organization Project Category";
    public static final String CHOERODON_COMPATIBLE = "Choerodon Compatible";
    public static final String CHOERODON_STAR_PROJECT = "Choerodon Star Project";
    public static final String CHOERODON_QUICK_LINK = "Choerodon Quick Link";
    public static final String CHOERODON_CUSTOM_LAYOUT_CONFIG = "Custom Layout Config";
    public static final String CHOERODON_PROJECT_SAGA = "Choerodon project saga";
    public static final String CHOERODON_RESOURCE_LIMIT = "Choerodon reource limit";
    public static final String CHOERODON_USER_WIZARD = "Choerodon user wizard";
    public static final String CHOERODON_WORK_GROUP = "Choerodon work group";


    @Autowired
    public C7nSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(CHOERODON_SYSTEM_SETTING, "Choerodon系统配置"),
                new Tag(CHOERODON_CLIENT, "Choerodon客户端"),
                new Tag(CHOERODON_LABEL, "Choerodon标签"),
                new Tag(CHOERODON_MENU, "choerodon菜单"),
                new Tag(CHOERODON_MENU_ROLE, "choerodon菜单角色"),
                new Tag(CHOERODON_TENANT_USER, "choerodon组织用户"),
                new Tag(CHOERODON_PERMISSION, "choerodon权限"),
                new Tag(CHOERODON_PROJECT, "choerodon项目"),
                new Tag(CHOERODON_CLIENT_PROJECT, "Choerodon客户端-项目层"),
                new Tag(CHOERODON_PROJECT_TYPE, "choerodon项目类型"),
                new Tag(CHOERODON_PROJECT_USER, "choerodon项目用户"),
                new Tag(CHOERODON_REPORT, "choerodon报表"),
                new Tag(CHOERODON_TENANT, "choerodon组织"),
                new Tag(CHOERODON_USER, "choerodon用户"),
                new Tag(CHOERODON_TENANT_PROJECT, "choerodon组织项目"),
                new Tag(CHOERODON_USER_SELF, "choerodon登录用户接口 "),
                new Tag(ORGANIZATION_USER, "choerodon组织用户"),
                new Tag(CHOERODON_ORGANIZATION_ADMIN, "choerodon组织管理员"),
                new Tag(CHOERODON_COMPATIBLE, "choerodon对外组件向后兼容接口"),
                new Tag(CHOERODON_CUSTOM_LAYOUT_CONFIG, "choerodon自定义工作台、项目概览组件配置接口"),
                new Tag(CHOERODON_PROJECT_SAGA, "choerodon项目事务"),
                new Tag(CHOERODON_RESOURCE_LIMIT, "Choerodon资源限制"),
                new Tag(CHOERODON_USER_WIZARD, "choerodon新用户向导"),
                new Tag(CHOERODON_WORK_GROUP, "choerodon工作组")
        );
    }
}
