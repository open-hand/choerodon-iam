package io.choerodon.base.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zaxxer.hikari.util.UtilityElf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.SyncDateService;
import io.choerodon.base.infra.dto.LabelDTO;
import io.choerodon.base.infra.mapper.LabelMapper;

@Service
public class SyncDateServiceImpl implements SyncDateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDateServiceImpl.class);

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("base-upgrade", false));

    @Autowired
    private LabelMapper labelMapper;

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
                    LOGGER.info("修复数据完成");
                } else {
                    LOGGER.info("version not matched");
                }

            } catch (Throwable ex) {
                printRetryNotice();
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex);
            }
        }
    }

    private void syncRole() {
        List<String> labels = new ArrayList<>();
        labels.add("project.deploy.admin");
        labels.add("project.deploy.admin");
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setName("project.deploy.admin");
        labelMapper.delete(labelDTO);

    }
}
