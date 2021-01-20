package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ProjectSagaVO;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by wangxiang on 2021/1/19
 */
@Api(tags = C7nSwaggerApiConfig.ORGANIZATION_USER)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}")
public class SagaProjectController {

    @Autowired
    private ProjectC7nService projectC7nService;


    @GetMapping("/saga/{project_id}")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("创建或者修改项目之后，根据项目的id查询")
    public ResponseEntity<ProjectSagaVO> queryProjectSaga(@PathVariable(value = "organization_id") Long organizationId,
                                                          @PathVariable(value = "project_id") Long projectId,
                                                          @RequestParam("operateType") String operateType) {

        return ResponseEntity.ok(projectC7nService.queryProjectSaga(organizationId, projectId, operateType));
    }

}
