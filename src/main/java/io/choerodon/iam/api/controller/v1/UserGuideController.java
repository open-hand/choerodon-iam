package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
    @ApiOperation(value = "查询指引步骤(menuId和guideCode不能同时为空)")
    @GetMapping
    public ResponseEntity<UserGuideVO> listUserGuideByMenuId(@RequestParam(value = "menu_id", required = false) @Encrypt Long menuId,
                                                             @RequestParam(value = "guide_code", required = false) String guideCode,
                                                             @RequestParam(value = "project_id", required = false) Long projectId,
                                                             @RequestParam(value = "organization_id", required = false) Long organizationId) {
        return ResponseEntity.ok(userGuideService.listUserGuideByMenuId(menuId, guideCode, projectId, organizationId));
    }
}
