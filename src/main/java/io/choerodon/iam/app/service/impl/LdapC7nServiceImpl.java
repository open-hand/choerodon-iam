package io.choerodon.iam.app.service.impl;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.infra.dto.LdapAutoDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDetail;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.mapper.LdapAutoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.choerodon.iam.infra.utils.SagaTopic.Organization.CREATE_LDAP_AUTO;

/**
 * @author wuguokai
 */
@Service
public class LdapC7nServiceImpl implements LdapC7nService {

    @Autowired
    private LdapAutoMapper ldapAutoMapper;

    @Autowired
    private AsgardFeignClient asgardFeignClient;

    @Autowired
    private TransactionalProducer producer;

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
