package io.choerodon.iam.infra.feign;

import io.choerodon.asgard.saga.dto.SagaTaskInstanceDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDetail;
import io.choerodon.iam.infra.feign.fallback.AsgardFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author dengyouquan
 **/
@FeignClient(value = "asgard-service",
        fallback = AsgardFeignClientFallback.class)
public interface AsgardFeignClient {
    @PutMapping("/v1/schedules/organizations/{organization_id}/tasks/disable")
    void disableOrg(@PathVariable("organization_id") long orgId);

    @PutMapping("/v1/schedules/projects/{project_id}/tasks/disable")
    void disableProj(@PathVariable("project_id") long projectId);
//
//    @PostMapping("/v1/schedules/organizations/{organization_id}/tasks")
//    ResponseEntity<QuartzTask> createOrgTask(@PathVariable("organization_id") long organizationId,
//                                             @RequestBody ScheduleTaskDTO scheduleTaskDTO);


    @DeleteMapping("/v1/schedules/organizations/{organization_id}/tasks/{id}")
    void deleteOrgTask(@PathVariable("organization_id") long orgId,
                       @PathVariable("id") long id);

    @PutMapping("/v1/schedules/organizations/{organization_id}/tasks/{id}/enable")
    void enableOrgTask(@PathVariable("organization_id") long orgId,
                       @PathVariable("id") long id, @RequestParam("objectVersionNumber") long objectVersionNumber);

    @PutMapping("/v1/schedules/organizations/{organization_id}/tasks/{id}/disable")
    void disableOrgTask(@PathVariable("organization_id") long orgId,
                        @PathVariable("id") long id, @RequestParam("objectVersionNumber") long objectVersionNumber);

    @GetMapping("/v1/schedules/organizations/{organization_id}/tasks/{id}")
    ResponseEntity<ScheduleTaskDetail> getTaskDetail(@PathVariable("organization_id") long orgId,
                                                     @PathVariable("id") long id);
//
//    @GetMapping("/v1/schedules/organizations/{organization_id}/methods/service")
//    ResponseEntity<List<ScheduleMethodDTO>> getMethodByService(@PathVariable("organization_id") long orgId,
//                                                               @RequestParam(value = "service") String service);

    @PutMapping("/v1/sagas/tasks/instances/{id}/retry")
    void retry(@PathVariable("id") long id);

    @GetMapping("/v1/sagas/tasks/instances/{id}")
    ResponseEntity<SagaTaskInstanceDTO> query(@PathVariable("id") Long id);

}