package io.choerodon.iam.infra.interceptor;

import static io.choerodon.iam.app.service.impl.OrganizationUserServiceImpl.role2MemberRole;

import java.util.List;
import java.util.stream.Collectors;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author scp
 * @date 2020/8/4
 * @description
 */
@Component
public class LdapUserPreInterceptor implements HandlerInterceptor<User> {
    private final RoleRepository roleRepository;

    @Autowired
    public LdapUserPreInterceptor(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void interceptor(User user) {
        if (user.getLdap() == null || !user.getLdap()) {
            return;
        }
        if (CollectionUtils.isEmpty(user.getMemberRoleList())) {
            List<Role> roleList = roleRepository.selectByCondition(Condition.builder(Role.class)
                    .where(Sqls.custom()
                            .andEqualTo(Role.FIELD_TENANT_ID, user.getOrganizationId())
                    )
                    .build()).stream().filter(e -> "member".equals(e.getCode())).collect(Collectors.toList());
            user.setMemberRoleList(role2MemberRole(user.getOrganizationId(), (Long) null, roleList));
        }
    }
}
