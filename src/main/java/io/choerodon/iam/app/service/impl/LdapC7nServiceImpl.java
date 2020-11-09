package io.choerodon.iam.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.validator.LdapValidator;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.infra.asserts.LdapAssertHelper;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.constant.MisConstants;
import io.choerodon.iam.infra.dto.LdapAutoDTO;
import io.choerodon.iam.infra.dto.asgard.QuartzTask;
import io.choerodon.iam.infra.dto.asgard.ScheduleMethodDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDetail;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.iam.infra.enums.LdapAutoFrequencyType;
import io.choerodon.iam.infra.enums.LdapType;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.mapper.LdapAutoMapper;
import io.choerodon.iam.infra.mapper.TenantC7nMapper;
import io.choerodon.iam.infra.utils.CommonExAssertUtil;

import org.hzero.common.HZeroService;

import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;

/**
 * @author wuguokai
 */
@Service
public class LdapC7nServiceImpl implements LdapC7nService {

    private static final String LDAP_ERROR_USER_MESSAGE_DIR = "classpath:messages/messages";
    private static final String REGEX = "\\(.*\\)";
    private static final String CRON_FORMAT = "%s %s %s %s %s %s";
    private static final String TASK_FORMAT = "%s组织LDAP自动同步用户任务";
    private static final String TASK_DESCRIPTION = "组织下自动同步LDAP用户任务";
    private static final String CRON_TRIGGER = "cron-trigger";
    private static final String STOP = "STOP";
    private static final String TENANT_CODE = "tenantNum";
    private static final String SYNC_TYPE = "syncType";
    private static final String EXECUTE_METHOD = "syncLdapUserOrganization";

    public static final String LDAP_CONNECTION_DTO = "ldapConnectionDTO";

    public static final String LDAP_TEMPLATE = "ldapTemplate";

    private static final String OBJECT_CLASS = "objectclass";

    @Value("${spring.application.name}")
    private String APPLICATION_NAME;

    @Autowired
    private LdapAutoMapper ldapAutoMapper;

    @Autowired
    private AsgardFeignClient asgardFeignClient;

    @Autowired
    private OrganizationAssertHelper organizationAssertHelper;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private TransactionalProducer producer;

    @Autowired
    private LdapAssertHelper ldapAssertHelper;

    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapC7nServiceImpl.class);


    @Override
    @Saga(code = CREATE_LDAP_AUTO,
            description = "base创建ldap自动同步任务", inputSchema = "{}")
    @Transactional
    public LdapAutoDTO createLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO) {
        if (queryLdapAutoByOrgId(organizationId) != null) {
            throw new CommonException("organization.already.exist.ldap.auto");
        }
        ldapAutoDTO.setOrganizationId(organizationId);
        if (ldapAutoMapper.insertSelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.insert.ldap.auto");
        }
        LdapAutoTaskEventPayload taskEventPayload = new LdapAutoTaskEventPayload();
        taskEventPayload.setLdapAutoId(ldapAutoDTO.getId());
        taskEventPayload.setOrganizationId(organizationId);
        taskEventPayload.setActive(ldapAutoDTO.getActive());
        sendProducerQuartzTaskEvent(taskEventPayload, organizationId, ResourceLevel.ORGANIZATION, CREATE_LDAP_AUTO);
        return ldapAutoDTO;
    }

    @Override
    @Transactional
    public LdapAutoDTO updateLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO) {
        LdapAutoDTO oldLdapAutoDTO = ldapAutoMapper.selectByPrimaryKey(ldapAutoDTO.getId());
        CommonExAssertUtil.assertTrue(organizationId.equals(oldLdapAutoDTO.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);

        Boolean isNotChange = false;
        if (ldapAutoDTO.getFrequency().equals(oldLdapAutoDTO.getFrequency()) && ldapAutoDTO.getStartTime().compareTo(oldLdapAutoDTO.getStartTime()) == 0) {
            isNotChange = true;
        }
        if (ldapAutoMapper.updateByPrimaryKeySelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.update.ldap.auto");
        }

        if (!isNotChange) {
            LdapAutoTaskEventPayload taskEventPayload = new LdapAutoTaskEventPayload();
            taskEventPayload.setLdapAutoId(ldapAutoDTO.getId());
            taskEventPayload.setDeleteQuartzTaskId(oldLdapAutoDTO.getQuartzTaskId());
            taskEventPayload.setOrganizationId(organizationId);
            taskEventPayload.setActive(ldapAutoDTO.getActive());
            sendProducerQuartzTaskEvent(taskEventPayload, organizationId, ResourceLevel.ORGANIZATION, CREATE_LDAP_AUTO);
        } else if (ldapAutoDTO.getActive().compareTo(oldLdapAutoDTO.getActive()) != 0) {
            ScheduleTaskDetail taskDetailDTO = getQuartzTaskDetail(organizationId, ldapAutoDTO.getQuartzTaskId());
            if (ldapAutoDTO.getActive()) {
                asgardFeignClient.enableOrgTask(organizationId, ldapAutoDTO.getQuartzTaskId(), taskDetailDTO.getObjectVersionNumber());
            } else {
                asgardFeignClient.disableOrgTask(organizationId, ldapAutoDTO.getQuartzTaskId(), taskDetailDTO.getObjectVersionNumber());
            }
        }
        return ldapAutoDTO;
    }

    @Override
    public Ldap queryByOrganizationId(Long organizationId) {
        organizationAssertHelper.notExisted(organizationId);
        return ldapAssertHelper.ldapNotExisted(LdapAssertHelper.WhichColumn.ORGANIZATION_ID, organizationId);
    }

    @Override
    public Ldap validateLdap(Long organizationId, Long id) {
        organizationAssertHelper.notExisted(organizationId);
        Ldap ldap = ldapAssertHelper.ldapNotExisted(LdapAssertHelper.WhichColumn.ID, id);
        LdapValidator.validate(ldap);
        return ldap;
    }

    @Override
    public LdapAutoDTO queryLdapAutoDTO(Long organizationId) {
        LdapAutoDTO queryLdapAutoDTO = new LdapAutoDTO();
        queryLdapAutoDTO.setOrganizationId(organizationId);
        return ldapAutoMapper.selectOne(queryLdapAutoDTO);
    }

    private LdapAutoDTO queryLdapAutoByOrgId(Long orgId) {
        LdapAutoDTO ldapAutoDTO = new LdapAutoDTO();
        ldapAutoDTO.setOrganizationId(orgId);
        return ldapAutoMapper.selectOne(ldapAutoDTO);
    }

    private void sendProducerQuartzTaskEvent(LdapAutoTaskEventPayload payload, Long sourceId, ResourceLevel resourceLevel, String sagaCode) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(resourceLevel)
                        .withRefType("ldapAutoTaskEvent")
                        .withSagaCode(sagaCode),
                builder -> {
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(payload.getLdapAutoId().toString())
                            .withSourceId(sourceId);
                    return payload;
                });
    }

    @Override
    public void handleLdapAutoTask(LdapAutoTaskEventPayload ldapAutoTaskEventPayload) {
        if (ldapAutoTaskEventPayload.getDeleteQuartzTaskId() != null) {
            asgardFeignClient.deleteOrgTask(ldapAutoTaskEventPayload.getOrganizationId(), ldapAutoTaskEventPayload.getDeleteQuartzTaskId());
        }
        Tenant tenant = tenantMapper.selectByPrimaryKey(ldapAutoTaskEventPayload.getOrganizationId());
        LdapAutoDTO ldapAutoDTO = ldapAutoMapper.selectByPrimaryKey(ldapAutoTaskEventPayload.getLdapAutoId());
        ScheduleTaskDTO scheduleTaskDTO = new ScheduleTaskDTO();
        scheduleTaskDTO.setName(String.format(TASK_FORMAT, tenant.getTenantName()));
        scheduleTaskDTO.setDescription(TASK_DESCRIPTION);
        scheduleTaskDTO.setCronExpression(getAutoLdapCron(ldapAutoDTO));
        scheduleTaskDTO.setTriggerType(CRON_TRIGGER);
        scheduleTaskDTO.setExecuteStrategy(STOP);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(ldapAutoDTO.getStartTime());
        scheduleTaskDTO.setStartTimeStr(dateString);

        if (ldapAutoTaskEventPayload.getActive() != null && !ldapAutoTaskEventPayload.getActive()) {
            scheduleTaskDTO.setStatus(QuartzDefinition.TaskStatus.DISABLE.name());
        }
        ScheduleTaskDTO.NotifyUser notifyUser = new ScheduleTaskDTO.NotifyUser();
        notifyUser.setAdministrator(true);
        notifyUser.setAssigner(false);
        notifyUser.setCreator(false);
        scheduleTaskDTO.setNotifyUser(notifyUser);

        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put(TENANT_CODE, tenant.getTenantNum());
        mapParams.put(SYNC_TYPE, LdapType.AUTO.value());
        scheduleTaskDTO.setParams(mapParams);

        scheduleTaskDTO.setMethodId(asgardServiceClientOperator.getMethodDTO(ldapAutoTaskEventPayload.getOrganizationId(), EXECUTE_METHOD, APPLICATION_NAME).getId());

        ldapAutoDTO.setQuartzTaskId(asgardServiceClientOperator.createQuartzTask(ldapAutoTaskEventPayload.getOrganizationId(), scheduleTaskDTO).getId());
        if (ldapAutoMapper.updateByPrimaryKeySelective(ldapAutoDTO) != 1) {
            throw new CommonException("error.update.ldap.auto");
        }
    }

    private String getAutoLdapCron(LdapAutoDTO ldapAutoDTO) {
        String[] currTime = new SimpleDateFormat("HH:mm:ss").format(ldapAutoDTO.getStartTime()).split(":");
        String cron;
        switch (LdapAutoFrequencyType.valueOf(ldapAutoDTO.getFrequency())) {
            case DAY:
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], "*", "*", "?");
                break;
            case WEEK:
                SimpleDateFormat dateFm = new SimpleDateFormat("E", Locale.ENGLISH);
                String currWeek = dateFm.format(ldapAutoDTO.getStartTime());
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], "?", "*", currWeek);
                break;
            case MONTH:
                String[] currDay = new SimpleDateFormat("yyyy-MM-dd").format(ldapAutoDTO.getStartTime()).split("-");
                String date = Integer.parseInt(currDay[2]) > 28 ? "L" : currDay[2];
                cron = String.format(CRON_FORMAT, currTime[2], currTime[1], currTime[0], date, "*", "?");
                break;
            default:
                throw new CommonException("error.frequency.type");
        }
        return cron;
    }


    private ScheduleTaskDetail getQuartzTaskDetail(Long organizationId, Long quartzTaskId) {
        ResponseEntity<ScheduleTaskDetail> quartzTaskResponseEntity = null;
        try {
            quartzTaskResponseEntity = asgardFeignClient.getTaskDetail(organizationId, quartzTaskId);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        ScheduleTaskDetail result = quartzTaskResponseEntity.getBody();
        if (result == null || result.getId() == null) {
            throw new CommonException("error.query.quartz.task");
        }
        return result;
    }

}
