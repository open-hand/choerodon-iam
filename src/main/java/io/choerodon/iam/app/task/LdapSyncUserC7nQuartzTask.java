package io.choerodon.iam.app.task;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.service.ldap.LdapConnectService;
import org.hzero.iam.domain.service.ldap.LdapSyncReport;
import org.hzero.iam.domain.service.ldap.LdapSyncUserTask;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.IamCheckLogService;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.app.service.StarProjectService;
import io.choerodon.iam.app.service.impl.LdapC7nServiceImpl;
import io.choerodon.iam.infra.enums.LdapType;

/**
 * @author dengyouquan
 **/
@Component
public class LdapSyncUserC7nQuartzTask {

    private static final String TENANT_CODE = "tenantNum";
    private static final String SYNC_TYPE = "syncType";

    private final Logger logger = LoggerFactory.getLogger(LdapSyncUserC7nQuartzTask.class);
    private LdapConnectService ldapConnectService;
    private LdapC7nService ldapC7nService;
    private TenantMapper tenantMapper;
    private LdapSyncUserTask ldapSyncUserTask;
    private LdapHistoryRepository ldapHistoryRepository;

    @Autowired
    private IamCheckLogService iamCheckLogService;

    public LdapSyncUserC7nQuartzTask(LdapConnectService ldapConnectService,
                                     LdapC7nService ldapC7nService,
                                     TenantMapper tenantMapper,
                                     LdapSyncUserTask ldapSyncUserTask,
                                     LdapHistoryRepository ldapHistoryRepository) {
        this.ldapConnectService = ldapConnectService;
        this.ldapC7nService = ldapC7nService;
        this.tenantMapper = tenantMapper;
        this.ldapSyncUserTask = ldapSyncUserTask;
        this.ldapHistoryRepository = ldapHistoryRepository;
    }

    @JobTask(maxRetryCount = 2, code = "syncLdapUserSite",
            params = {
                    @JobParam(name = TENANT_CODE, defaultValue = "hand", description = "组织编码"),
                    @JobParam(name = SYNC_TYPE, defaultValue = "manual", description = "同步类型")
            }, description = "全局层同步LDAP用户")
    public void syncLdapUserSite(Map<String, Object> map) {
        long startTime = System.currentTimeMillis();
        String syncType = StringUtils.isEmpty(map.get(SYNC_TYPE)) ? LdapType.MANUAL.value() : map.get(SYNC_TYPE).toString();
        syncLdapUser(map, "", syncType);
        long entTime = System.currentTimeMillis();
        logger.info("Timed Task for syncing users has been completed, total time: {} millisecond", (entTime - startTime));
    }

    @JobTask(maxRetryCount = 2, code = "syncLdapUserOrganization", level = ResourceLevel.ORGANIZATION,
            params = {
                    @JobParam(name = TENANT_CODE, description = "组织编码"),
                    @JobParam(name = SYNC_TYPE, defaultValue = "manual", description = "同步类型")
            }, description = "组织层同步LDAP用户")
    public void syncLdapUserOrganization(Map<String, Object> map) {
        syncLdapUserSite(map);
    }

    @JobTask(maxRetryCount = 2, code = "syncDisabledLdapUserSite",
            params = {
                    @JobParam(name = TENANT_CODE, defaultValue = "hand", description = "组织编码"),
                    @JobParam(name = "filterStr", defaultValue = "(employeeType=1)", description = "ldap过滤条件"),
                    @JobParam(name = SYNC_TYPE, defaultValue = "manual", description = "同步类型")
            },
            description = "全局层过滤并停用LDAP用户")
    public void syncDisabledLdapUserSite(Map<String, Object> map) {
        String filter =
                Optional
                        .ofNullable((String) map.get("filterStr"))
                        .orElseThrow(() -> new CommonException("error.syncLdapUser.filterStrEmpty"));
        long startTime = System.currentTimeMillis();
        String syncType = StringUtils.isEmpty(map.get(SYNC_TYPE)) ? LdapType.MANUAL.value() : map.get(SYNC_TYPE).toString();
        syncDisabledLDAPUser(map, filter, syncType);
        long entTime = System.currentTimeMillis();
        logger.info("Timed Task for disabling users has been completed, total time: {} millisecond", (entTime - startTime));
    }

    @JobTask(maxRetryCount = 2, code = "syncDisabledLdapUserOrg", level = ResourceLevel.ORGANIZATION,
            params = {
                    @JobParam(name = TENANT_CODE, description = "组织编码"),
                    @JobParam(name = "filterStr", defaultValue = "(employeeType=1)", description = "ldap过滤条件"),
                    @JobParam(name = SYNC_TYPE, defaultValue = "manual", description = "同步类型")
            }, description = "组织层过滤并停用LDAP用户")
    public void syncDisabledLdapUserOrg(Map<String, Object> map) {
        syncDisabledLdapUserSite(map);
    }


    @JobTask(maxRetryCount = 3, code = "fixStarProjectData", description = "修复星标项目的数据")
    @TimedTask(name = "fixStarProjectData", description = "修复星标项目的数据", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void fixStarProjectData(Map<String, Object> map) {
        logger.info(">>>>>>>>>>>>>>>>>>>>begin to fix star project data<<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            iamCheckLogService.checkLog("0.24.0");
        } catch (Exception e) {
            logger.error("error.fix.star.project.data", e);
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>end fix star project data<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }


    private void syncLdapUser(Map<String, Object> map, String filter, String syncType) {
        //获取方法参数
        String orgCode =
                Optional
                        .ofNullable((String) map.get(TENANT_CODE))
                        .orElseThrow(() -> new CommonException("error.syncLdapUser.organizationCodeEmpty"));
        Ldap ldap = getLdapByOrgCode(orgCode);
        if (!StringUtils.isEmpty(filter)) {
            ldap.setCustomFilter(filter);
        }
        //获取测试连接的returnMap 及 测试连接十分成功
        Map<String, Object> returnMap = ldapConnectService.testConnect(ldap);
        validateConnection(returnMap);
        //获取ldapTemplate
        LdapTemplate ldapTemplate = (LdapTemplate) returnMap.get(LdapC7nServiceImpl.LDAP_TEMPLATE);
        CountDownLatch latch = new CountDownLatch(1);
        //开始同步
        ldapSyncUserTask.syncLDAPUser(ldapTemplate, ldap, (LdapSyncReport ldapSyncReport, LdapHistory ldapHistory) -> {
            latch.countDown();
            LdapSyncUserTask.FinishFallback fallback = new LdapSyncUserTask.FinishFallbackImpl(ldapHistoryRepository);
            return fallback.callback(ldapSyncReport, ldapHistory);
        }, syncType);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommonException("error.ldapSyncUserTask.countDownLatch", e);
        }
    }

    private void syncDisabledLDAPUser(Map<String, Object> map, String filter, String syncType) {
        //获取方法参数
        String orgCode =
                Optional
                        .ofNullable((String) map.get(TENANT_CODE))
                        .orElseThrow(() -> new CommonException("error.syncLdapUser.organizationCodeEmpty"));
        Ldap ldap = getLdapByOrgCode(orgCode);
        if (!StringUtils.isEmpty(filter)) {
            ldap.setCustomFilter(filter);
        }
        //获取测试连接的returnMap 及 测试连接十分成功
        Map<String, Object> returnMap = ldapConnectService.testConnect(ldap);
        validateConnection(returnMap);
        //获取ldapTemplate
        LdapTemplate ldapTemplate = (LdapTemplate) returnMap.get(LdapC7nServiceImpl.LDAP_TEMPLATE);
        CountDownLatch latch = new CountDownLatch(1);
        //开始同步
        ldapSyncUserTask.syncDisabledLDAPUser(ldapTemplate, ldap, (LdapSyncReport ldapSyncReport, LdapHistory ldapHistory) -> {
            latch.countDown();
            LdapSyncUserTask.FinishFallback fallback = new LdapSyncUserTask.FinishFallbackImpl(ldapHistoryRepository);
            return fallback.callback(ldapSyncReport, ldapHistory);
        }, syncType);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommonException("error.ldapSyncUserTask.countDownLatch", e);
        }
    }

    /**
     * 获取组织下ldap
     *
     * @param orgCode 组织编码
     * @return ldap
     */
    private Ldap getLdapByOrgCode(String orgCode) {
        Tenant tenant = new Tenant();
        tenant.setTenantNum(orgCode);
        tenant = tenantMapper.selectOne(tenant);
        if (tenant == null) {
            throw new CommonException("error.ldapSyncUserTask.organizationNotNull");
        }
        Long organizationId = tenant.getTenantId();
        Long ldapId = ldapC7nService.queryByOrganizationId(organizationId).getId();
        logger.info("LdapSyncUserQuartzTask starting sync ldap user,id:{},organizationId:{}", ldapId, organizationId);
        return ldapC7nService.validateLdap(organizationId, ldapId);
    }


    /**
     * 测试ldap连接十分成功
     *
     * @param returnMap ldap连接返回map
     */
    private void validateConnection(Map<String, Object> returnMap) {
        LdapConnectionDTO ldapConnectionDTO =
                (LdapConnectionDTO) returnMap.get(LdapC7nServiceImpl.LDAP_CONNECTION_DTO);
        if (!ldapConnectionDTO.getCanConnectServer()) {
            throw new CommonException("error.ldap.connect");
        }
        if (!ldapConnectionDTO.getCanLogin()) {
            throw new CommonException("error.ldap.authenticate");
        }
        if (!ldapConnectionDTO.getMatchAttribute()) {
            throw new CommonException("error.ldap.attribute.match");
        }
    }
}
