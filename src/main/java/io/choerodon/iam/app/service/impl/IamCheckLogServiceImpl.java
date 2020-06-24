package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.util.UtilityElf;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.IamCheckLogService;
import io.choerodon.iam.app.task.FixAduitInterceptInterface;

/**
 * User: Mr.Wang
 * Date: 2020/6/24
 */
@Service
public class IamCheckLogServiceImpl implements IamCheckLogService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PermissionRepository permissionRepository;

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("iam-upgrade", false));


    @Override
    public void checkLog(String version) {
        LOGGER.info("start fix audit interface");
        executorService.execute(new UpgradeTask(version));
    }

    class UpgradeTask implements Runnable {
        private String version;

        UpgradeTask(String version) {
            this.version = version;
        }

        @Override
        public void run() {
            try {
                if ("0.23.0".equals(version)) {
                    LOGGER.info("start fix audit data");
                    for (Map.Entry<String, Long> stringLongEntry : FixAduitInterceptInterface.stringLongHashMap.entrySet()) {
                        Permission permission = new Permission();
                        permission.setCode(stringLongEntry.getKey());
                        Permission selectOne = permissionRepository.selectOne(permission);
                        if (!Objects.isNull(selectOne)) {
                            if (selectOne.getId() != stringLongEntry.getValue()) {
                                //先删除原来的
                                Permission selectByPrimaryKey = permissionRepository.selectByPrimaryKey(stringLongEntry.getValue());
                                permissionRepository.deleteByPrimaryKey(selectByPrimaryKey.getId());
                                permissionRepository.deleteByPrimaryKey(selectOne.getId());
                                //再插入新的
                                selectByPrimaryKey.setId(null);
                                permissionRepository.insert(selectByPrimaryKey);
                                selectOne.setId(stringLongEntry.getValue());
                                permissionRepository.insert(selectOne);
                            }
                        }
                    }
                }
                LOGGER.info("end fix audit interface");
            } catch (Exception ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex);
            }
        }

    }

}
