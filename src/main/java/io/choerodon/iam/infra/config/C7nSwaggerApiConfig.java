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
    public static final String CHOERODON_LABEL = "Choerodon Lable";
    public static final String CHOERODON_MENU = "Choerodon Menu";
    public static final String CHOERODON_MENU_ROLE = "Choerodon Menu Role";
    public static final String CHOERODON_TENANT_USER = "Choerodon Tenant User";
    public static final String CHOERODON_PERMISSION = "Choerodon Permission";
    public static final String CHOERODON_PROJECT = "Choerodon Project";
    public static final String CHOERODON_PROJECT_TYPE = "Choerodon Project Type";
    public static final String CHOERODON_PROJECT_USER = "Choerodon Project User";
    public static final String CHOERODON_REPORT = "Choerodon Report";
    public static final String CHOERODON_TENANT = "Choerodon Tenant";
    public static final String CHOERODON_USER = "Choerodon User";


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
                new Tag(CHOERODON_PROJECT_TYPE, "choerodon项目类型"),
                new Tag(CHOERODON_PROJECT_USER, "choerodon项目用户"),
                new Tag(CHOERODON_REPORT, "choerodon报表"),
                new Tag(CHOERODON_TENANT, "choerodon组织"),
                new Tag(CHOERODON_USER, "choerodon用户")

        );
    }
}
