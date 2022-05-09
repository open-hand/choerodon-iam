package io.choerodon.iam.app.task;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.iam.infra.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.FixService;
import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.RolePermissionC7nMapper;
import io.choerodon.iam.infra.utils.C7nCollectionUtils;


/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:44 2019/3/11
 * Description:
 */
@Component
public class PermissionAndMenuFixRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAndMenuFixRunner.class);

    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private RolePermissionC7nMapper rolePermissionC7nMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Value("${choerodon.fix.data.page.size:200}")
    private Integer pageSize;

    @Value("${choerodon.fix.data.page.sleep.time: 500}")
    private Integer sleepTime;
    @Value("${choerodon.fix.data.flag: true}")
    private Boolean fixDataFlag;
    @Autowired
    private PermissionC7nService permissionC7nService;
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
