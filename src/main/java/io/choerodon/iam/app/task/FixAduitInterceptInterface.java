package io.choerodon.iam.app.task;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.iam.app.service.IamCheckLogService;

/**
 * User: Mr.Wang
 * Date: 2020/6/24
 */
@Component
public class FixAduitInterceptInterface {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IamCheckLogService iamCheckLogService;

    public static Map<String, Long> stringLongHashMap = new HashMap<>();

    static {
        //code 与 操作拦截配置的id关联
        stringLongHashMap.put("hzero-iam.choerodon-organization-project.create", 297L);
        stringLongHashMap.put("hzero-iam.choerodon-organization-project.enableProject", 152L);
        stringLongHashMap.put("hzero-iam.choerodon-project.disableProject", 122L);
        stringLongHashMap.put("hzero-iam.organization-user.enableUser", 110L);
        stringLongHashMap.put("hzero-iam.hiam-user.unfrozenUser", 106L);
        stringLongHashMap.put("hzero-iam.hiam-user-site-level.unfrozenUser", 655L);
        stringLongHashMap.put("hzero-iam.organization-user.resetUserPassword", 168L);
        stringLongHashMap.put("hzero-iam.hiam-user.resetUserPassword", 629L);
        stringLongHashMap.put("hzero-iam.choerodon-menu-role.create", 418L);
        stringLongHashMap.put("hzero-asgard.saga-task-instance.retry", 2169L);
        stringLongHashMap.put("hzero-asgard.saga-task-instance-org.retry", 2137L);
        stringLongHashMap.put("hzero-iam.choerodon-organization-pro.create", 963L);
        stringLongHashMap.put("hzero-iam.choerodon-tenant.enableOrganization", 412L);
        stringLongHashMap.put("hzero-iam.choerodon_register_info_pro.approval", 73L);
        stringLongHashMap.put("hzero-iam.choerodon-tenant.disableOrganization", 977L);
        stringLongHashMap.put("hzero-iam.choerodon-user.addDefaultUsers", 33L);
        stringLongHashMap.put("hzero-iam.hiam-user.frozenUser", 55L);
        stringLongHashMap.put("hzero-iam.hiam-user-site-level.frozenUser", 324L);
        stringLongHashMap.put("hzero-iam.organization-user.disableUser", 597L);
    }


    /**
     *
     */
    @JobTask(maxRetryCount = 3, code = "auditUpgrade23.0", description = "修复审计接口数据")
    @TimedTask(name = "auditUpgrade23.0", description = "修复审计接口数据", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void fixInterfaceAudit(Map<String, Object> map) {
        LOGGER.info("begin to fix audit interface data.");
        iamCheckLogService.checkLog("0.23.0");
    }
}
