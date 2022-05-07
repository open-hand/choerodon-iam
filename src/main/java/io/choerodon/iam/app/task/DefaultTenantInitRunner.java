package io.choerodon.iam.app.task;

import static org.hzero.iam.app.service.IDocumentService.NULL_VERSION;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.infra.constant.TenantConstants;

/**
 * 初始化默认组织
 */
@Component
public class DefaultTenantInitRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTenantInitRunner.class);


    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private TenantC7nService tenantC7nService;
    @Autowired
    private UserMapper userMapper;
    private static final String ADMIN_LOGIN_NAME = "admin";
    @Autowired
    private IDocumentService documentService;
    @Value("${hzero.service.iam.name:choerodon-iam}")
    private String serviceName;

    @Override
    public void run(String... strings) {
        try {
            documentService.refreshPermissionAsync(serviceName, NULL_VERSION, true);
        } catch (Exception e) {
            LOGGER.error("error.sync.permission.service:{}", serviceName);
        }
        try {
            LOGGER.info(">>>>>>>>>>>>>>>>>> check default tenant is created <<<<<<<<<<<<<<<<<<<<<<<");
            Tenant tenant = tenantMapper.selectByPrimaryKey(TenantConstants.DEFAULT_C7N_TENANT_TD);
            if (tenant == null) {
                LOGGER.info(">>>>>>>>>>>>>>>>>> Default tenant is not created, create it now <<<<<<<<<<<<<<<<<<<<<<<");
                // 默认组织不存在则创建
                // 查询默认管理员账户，并且设置上下文
                User user = new User();
                user.setLoginName(ADMIN_LOGIN_NAME);
                User admin = userMapper.selectOne(user);
                DetailsHelper.setCustomUserDetails(admin.getId(), BaseConstants.DEFAULT_LOCALE_STR);
                DetailsHelper.getUserDetails().setOrganizationId(BaseConstants.DEFAULT_TENANT_ID);
                tenantC7nService.createDefaultTenant(TenantConstants.DEFAULT_TENANT_NAME, TenantConstants.DEFAULT_TENANT_NUM);
            } else {
                LOGGER.info(">>>>>>>>>>>>>>>>>> Default tenant is created <<<<<<<<<<<<<<<<<<<<<<<");
            }
        } catch (Exception e) {
            throw new CommonException("error.init.default.tenant", e);
        }

    }

}
