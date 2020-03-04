package io.choerodon.base.app.service.impl;

import com.zaxxer.hikari.util.UtilityElf;
import io.choerodon.base.app.service.SyncDateService;
import io.choerodon.base.infra.dto.LabelDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.RoleLabelDTO;
import io.choerodon.base.infra.dto.RolePermissionDTO;
import io.choerodon.base.infra.mapper.LabelMapper;
import io.choerodon.base.infra.mapper.RoleLabelMapper;
import io.choerodon.base.infra.mapper.RoleMapper;
import io.choerodon.base.infra.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SyncDateServiceImpl implements SyncDateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDateServiceImpl.class);

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("base-upgrade", false));

    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private RoleLabelMapper roleLabelMapper;

    @Override
    public void syncDate(String version) {
        LOGGER.info("start upgrade task");
        executorService.execute(new UpgradeTask(version));
    }

    private static void printRetryNotice() {
        LOGGER.error("======================================================================================");
        LOGGER.error("Please retry data migration later in choerodon interface after cheorodon-front upgrade");
        LOGGER.error("======================================================================================");
    }


    class UpgradeTask implements Runnable {
        private String version;

        UpgradeTask(String version) {
            this.version = version;
        }

        @Override
        public void run() {
            try {
                if ("0.21.0".equals(version)) {
                    LOGGER.info("修复数据开始");
                    syncRole();
                    // 删除与角色层级不匹配的接口权限
                    deleteInvalidRolePermission();
                    LOGGER.info("修复数据完成");
                } else {
                    LOGGER.info("version not matched");
                }

            } catch (Throwable ex) {
                printRetryNotice();
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex);
            }
        }
        private void deleteInvalidRolePermission() {
            // 查询与角色层级不匹配的接口权限
            List<RolePermissionDTO> rolePermissionDTOS = rolePermissionMapper.selectInvalidData();
            // 删除不合法的权限
            rolePermissionDTOS.forEach(rolePermissionDTO -> rolePermissionMapper.deleteByPrimaryKey(rolePermissionDTO.getId()));

        }
    }



    private void syncRole() {
        //删除部署管理员角色
        LabelDTO deployDTO = labelMapper.selectOne(new LabelDTO("project.deploy.admin"));
        if (deployDTO != null && deployDTO.getId() != null) {
            RoleLabelDTO roleLabelDTO = new RoleLabelDTO();
            roleLabelDTO.setLabelId(deployDTO.getId());
            roleLabelMapper.delete(roleLabelDTO);
            labelMapper.deleteByPrimaryKey(deployDTO.getId());
        }

        // 删除自定义角色拥有组织管理员gitlab角色标签
        LabelDTO orgDTO = labelMapper.selectOne(new LabelDTO("organization.gitlab.owner"));
        RoleDTO roleDTO = roleMapper.selectOne(new RoleDTO("role/organization/default/administrator"));
        if (orgDTO != null && orgDTO.getId() != null && roleDTO != null && roleDTO.getId() != null) {
            roleLabelMapper.deleteByLabelId(orgDTO.getId(), roleDTO.getId());
        }
    }
}
