package io.choerodon.iam.app.service.impl;

import com.zaxxer.hikari.util.UtilityElf;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.iam.app.service.IamCheckLogService;
import io.choerodon.iam.app.service.StarProjectService;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.StarProjectUserRelDTO;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.StarProjectMapper;

/**
 * Created by wangxiang on 2020/10/14
 */
@Service
public class IamCheckLogServiceImpl implements IamCheckLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IamCheckLogService.class);

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("message-upgrade", false));


    @Autowired
    private StarProjectService starProjectService;

    @Autowired
    private StarProjectMapper starProjectMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public void checkLog(String version) {
        LOGGER.info("start upgrade task");
        executorService.submit(new UpgradeTask(version),)
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
                if (StringUtils.equalsIgnoreCase("0.24.0", version.trim())) {
                    handData();
                }
            } catch (Exception ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex.getMessage());
            }
        }

    }

    private void handData() {
        //查询所有的星标项目的数据
        List<StarProjectUserRelDTO> starProjectUserRelDTOS = starProjectMapper.selectAll();
        if (CollectionUtils.isEmpty(starProjectUserRelDTOS)) {
            return;
        }
        //填充组织id
        starProjectUserRelDTOS.forEach(starProjectUserRelDTO -> {
            Long projectId = starProjectUserRelDTO.getProjectId();
            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
            starProjectUserRelDTO.setOrganizationId(projectDTO.getOrganizationId());
        });
        //填充序号
        //先按照userId分组
        Map<Long, List<StarProjectUserRelDTO>> userIdMap = starProjectUserRelDTOS.stream().collect(Collectors.groupingBy(StarProjectUserRelDTO::getUserId));
        for (Map.Entry<Long, List<StarProjectUserRelDTO>> longListEntry : userIdMap.entrySet()) {
            List<StarProjectUserRelDTO> userRelDTOS = longListEntry.getValue();
            //再按照组织id分组
            Map<Long, List<StarProjectUserRelDTO>> longListMap = userRelDTOS.stream().collect(Collectors.groupingBy(StarProjectUserRelDTO::getOrganizationId));
            for (Map.Entry<Long, List<StarProjectUserRelDTO>> listEntry : longListMap.entrySet()) {
                AtomicLong index = new AtomicLong(1L);
                List<StarProjectUserRelDTO> listEntryValue = listEntry.getValue();
                //按照id的升序而升序 从0开始
                List<StarProjectUserRelDTO> collect = listEntryValue.stream().sorted(Comparator.comparing(StarProjectUserRelDTO::getId)).collect(Collectors.toList());
                collect.forEach(starProjectUserRelDTO -> {
                    starProjectUserRelDTO.setSort(index.getAndIncrement());
                    //更新到数据库
                    starProjectMapper.updateByPrimaryKey(starProjectUserRelDTO);
                });
            }
        }
    }
}
