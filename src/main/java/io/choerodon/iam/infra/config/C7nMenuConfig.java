package io.choerodon.iam.infra.config;

import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.mapper.MenuMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import io.choerodon.iam.app.service.*;
import io.choerodon.iam.app.service.impl.MenuC7nServiceImpl;
import io.choerodon.iam.app.service.impl.ReportC7nServiceImpl;
import io.choerodon.iam.app.service.impl.StarProjectServiceImpl;
import io.choerodon.iam.app.service.impl.TenantC7NServiceImpl;
import io.choerodon.iam.infra.mapper.*;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/4/23
 * @Modified By:
 */
@Configuration
public class C7nMenuConfig {

    @Bean
    @ConditionalOnMissingBean(MenuC7nService.class)
    public MenuC7nService menuC7nService(MenuC7nMapper menuC7nMapper,
                                         ProjectMapCategoryMapper projectMapCategoryMapper,
                                         MenuRepository menuRepository,
                                         MenuMapper menuMapper,
                                         UserRepository userRepository,
                                         MemberRoleC7nMapper memberRoleC7nMapper,
                                         RoleC7nMapper roleC7nMapper,
                                         ProjectPermissionMapper projectPermissionMapper,
                                         ProjectC7nService projectC7nService,
                                         UserC7nMapper userC7nMapper,
                                         RedisTemplate<String, String> redisTemplate,
                                         ProjectMapper projectMapper,
                                         UserMapper userMapper,
                                         StarProjectService starProjectService) {
        return new MenuC7nServiceImpl(menuC7nMapper, projectMapCategoryMapper, menuRepository, menuMapper,
                userRepository, memberRoleC7nMapper, roleC7nMapper, projectPermissionMapper,
                projectC7nService, userC7nMapper, redisTemplate, projectMapper, userMapper, starProjectService);
    }

    @Bean
    @ConditionalOnMissingBean(TenantC7nService.class)
    public TenantC7nService tenantC7nService() {
        return new TenantC7NServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ReportC7nService.class)
    public ReportC7nService reportC7nService() {
        return new ReportC7nServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(StarProjectService.class)
    public StarProjectService starProjectService() {
        return new StarProjectServiceImpl();
    }
}
