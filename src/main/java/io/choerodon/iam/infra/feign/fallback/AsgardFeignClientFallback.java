package io.choerodon.iam.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.SagaInstanceDetails;
import io.choerodon.iam.infra.dto.asgard.QuartzTask;
import io.choerodon.iam.infra.dto.asgard.ScheduleMethodDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDetail;
import io.choerodon.iam.infra.feign.AsgardFeignClient;

/**
 * @author dengyouquan
 **/
@Component
public class AsgardFeignClientFallback implements AsgardFeignClient {
    @Override
    public void disableOrg(long orgId) {
        throw new CommonException("error.asgard.quartzTask.disableOrg");
    }

    @Override
    public void disableProj(long projectId) {
        throw new CommonException("error.asgard.quartzTask.disableProject");
    }

    @Override
    public ResponseEntity<QuartzTask> createOrgTask(long organizationId, ScheduleTaskDTO scheduleTaskDTO) {
        throw new CommonException("error.asgard.quartzTask.createOrganization");
    }

    @Override
    public ResponseEntity<QuartzTask> createSiteTask(ScheduleTaskDTO scheduleTaskDTO) {
        throw new CommonException("error.asgard.quartzTask.createSite");
    }

    @Override
    public ResponseEntity<QuartzTask> deleteSiteTask(String name) {
        throw new CommonException("error.asgard.quartzTask.deleteSite");
    }

    @Override
    public void deleteOrgTask(long orgId, long id) {
        throw new CommonException("error.asgard.quartzTask.deleteOrganization");
    }

    @Override
    public void enableOrgTask(long orgId, long id, long objectVersionNumber) {
        throw new CommonException("error.asgard.quartzTask.enableOrganization");
    }

    @Override
    public ResponseEntity<List<ScheduleMethodDTO>> getMethodByService(long orgId, String service) {
        throw new CommonException("error.asgard.method.query");
    }

    @Override
    public ResponseEntity<List<ScheduleMethodDTO>> getMethodByServiceSite(String service) {
        throw new CommonException("error.asgard.method.query.site");
    }

    @Override
    public void retry(long id) {
        throw new CommonException("error.asgard.retry");
    }

    @Override
    public void disableOrgTask(long orgId, long id, long objectVersionNumber) {
        throw new CommonException("error.asgard.orgTask.disable");
    }

    @Override
    public ResponseEntity<ScheduleTaskDetail> getTaskDetail(long orgId, long id) {
        throw new CommonException("error.asgard.get.taskDetail");
    }

    @Override
    public ResponseEntity<SagaTaskInstanceDTO> query(Long id) {
        throw new CommonException("error.asgard.instance.detail");
    }

    @Override
    public ResponseEntity<List<SagaInstanceDetails>> queryByRefTypeAndRefIds(String refType, List<String> refIds, String sagaCode) {
        throw new CommonException("error.query.instance.detail");
    }
}