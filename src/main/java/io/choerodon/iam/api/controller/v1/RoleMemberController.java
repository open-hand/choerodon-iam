package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.UploadHistoryService;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author superlee
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/choerodon/v1")
public class RoleMemberController extends BaseController {

    public static final String MEMBER_ROLE = "member-role";
    private UploadHistoryService uploadHistoryService;

    public RoleMemberController(UploadHistoryService uploadHistoryService) {
        this.uploadHistoryService = uploadHistoryService;
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("查项目层的历史")
    @GetMapping("/projects/{project_id}/member_role/users/{user_id}/upload/history")
    public ResponseEntity<UploadHistoryDTO> latestHistoryOnProject(@PathVariable(name = "project_id") Long projectId,
                                                                   @PathVariable(name = "user_id") Long userId) {
        return new ResponseEntity<>(uploadHistoryService.latestHistory(userId, MEMBER_ROLE, projectId, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

}
