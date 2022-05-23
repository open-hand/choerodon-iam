package io.choerodon.iam.infra.feign.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.iam.api.vo.SagaInstanceDetails;
import io.choerodon.iam.infra.dto.asgard.QuartzTask;
import io.choerodon.iam.infra.dto.asgard.ScheduleMethodDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.iam.infra.feign.AsgardFeignClient;

@Component
public class AsgardServiceClientOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsgardServiceClientOperator.class);


    @Autowired
    private AsgardFeignClient asgardFeignClient;

    public ScheduleMethodDTO getMethodDTO(Long organizationId, String methodName, String service) {
        ResponseEntity<List<ScheduleMethodDTO>> methodsResponseEntity = null;
        try {
            methodsResponseEntity = asgardFeignClient.getMethodByService(organizationId, service);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        List<ScheduleMethodDTO> methodDTOList = methodsResponseEntity.getBody();
        if (methodDTOList == null || methodDTOList.size() == 0) {
            throw new CommonException("error.list.methods");
        }
        Optional<ScheduleMethodDTO> methodDTO = methodDTOList.stream().filter(t -> t.getCode().equals(methodName)).findFirst();
        if (!methodDTO.isPresent()) {
            throw new CommonException("error.ldap.sync.method.get");
        }

        return methodDTO.get();
    }

    public ScheduleMethodDTO getMethodDTOSite(String methodName, String service) {
        ResponseEntity<List<ScheduleMethodDTO>> methodsResponseEntity = null;
        try {
            methodsResponseEntity = asgardFeignClient.getMethodByServiceSite(service);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        List<ScheduleMethodDTO> methodDTOList = methodsResponseEntity.getBody();
        if (methodDTOList == null || methodDTOList.size() == 0) {
            throw new CommonException("error.list.methods");
        }
        Optional<ScheduleMethodDTO> methodDTO = methodDTOList.stream().filter(t -> t.getCode().equals(methodName)).findFirst();
        if (!methodDTO.isPresent()) {
            throw new CommonException("error.ldap.sync.method.get");
        }

        return methodDTO.get();
    }

    public QuartzTask createQuartzTask(Long organizationId, ScheduleTaskDTO scheduleTaskDTO) {
        ResponseEntity<QuartzTask> quartzTaskResponseEntity;
        try {
            quartzTaskResponseEntity = asgardFeignClient.createOrgTask(organizationId, scheduleTaskDTO);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        QuartzTask result = quartzTaskResponseEntity.getBody();
        if (result == null || result.getId() == null) {
            throw new CommonException("error.create.quartz.task");
        }
        return result;
    }

    public QuartzTask createQuartzTaskSite(ScheduleTaskDTO scheduleTaskDTO) {
        ResponseEntity<QuartzTask> quartzTaskResponseEntity;
        try {
            quartzTaskResponseEntity = asgardFeignClient.createSiteTask(scheduleTaskDTO);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
        QuartzTask result = quartzTaskResponseEntity.getBody();
        if (result == null || result.getId() == null) {
            throw new CommonException("error.create.quartz.task");
        }
        return result;
    }

    public List<SagaInstanceDetails> queryByRefTypeAndRefIds(String refType, List<String> refIds, String sagaCode) {
        ResponseEntity<List<SagaInstanceDetails>> listResponseEntity;
        // 查询事务状态流程出错 不影响整体查询
        try {
            listResponseEntity = asgardFeignClient.queryByRefTypeAndRefIds(refType, refIds, sagaCode);
            if (listResponseEntity == null) {
                throw new CommonException("error.query.saga");
            }
            return listResponseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

}
