package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.iam.api.vo.UserGuideVO;
import io.choerodon.iam.app.service.UserGuideService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 10:02
 */
@RestController
@RequestMapping("/choerodon/v1/guides")
public class UserGuideController {

    @Autowired
    private UserGuideService userGuideService;


    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询指引步骤")
    @GetMapping
    public ResponseEntity<List<UserGuideVO>> listUserGuideByMenuId(@RequestParam(value = "menu_id") Long menuId,
                                                                   @RequestParam(value = "project_id", required = false) Long projectId,
                                                                   @RequestParam(value = "organization_id", required = false) Long organizationId) {
        return ResponseEntity.ok(userGuideService.listUserGuideByMenuId(menuId, projectId, organizationId));
    }
}
