package io.choerodon.iam.app.task;

import static org.hzero.iam.app.service.IDocumentService.NULL_VERSION;

import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.infra.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.FixService;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.RolePermissionC7nMapper;


/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:44 2019/3/11
 * Description:
 */
@Component
public class PermissionAndMenuFixRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAndMenuFixRunner.class);
    @Value("${choerodon.fix.data.flag: true}")
    private Boolean fixDataFlag;
    @Value("${hzero.service.iam.name:choerodon-iam}")
    private String serviceName;
    @Autowired
    private IDocumentService documentService;
    @Autowired
    @Lazy
    private FixService fixService;
    @Autowired
    @Lazy
    private RoleC7nService roleC7nService;

    @Override
    public void run(String... strings) {

        try {
            if (Boolean.TRUE.equals(fixDataFlag)) {
                // 补偿iam接口刷新不进去的情况
                try {
                    documentService.refreshPermissionAsync(serviceName, NULL_VERSION, false);
                } catch (Exception e) {
                    LOGGER.error("error.sync.permission.service:{}", serviceName);
                }
                // 修复菜单层级
                fixService.fixMenuLevelPath(true);
                // 修复子角色权限（保持和模板角色权限一致）
                LOGGER.info(">>>>>>>>>>>>>>> start fix role permission >>>>>>>>>>>>>>");
                roleC7nService.fixChildPermission();
                LOGGER.info(">>>>>>>>>>>>>>>>>>> end fix role permission >>>>>>>>>>>>>>>>>>>>>>");
            }
        } catch (Exception e) {
            throw new CommonException("error.fix.role.permission.data", e);
        }

    }


}
