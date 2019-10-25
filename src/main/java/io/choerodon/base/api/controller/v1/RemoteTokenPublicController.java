package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.CommonCheckResultVO;
import io.choerodon.base.app.service.RemoteTokenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eugen
 **/
@RestController
@RequestMapping(value = "/v1/public/remote_tokens")
public class RemoteTokenPublicController {

    private RemoteTokenService remoteTokenService;

    public RemoteTokenPublicController(RemoteTokenService remoteTokenService) {
        this.remoteTokenService = remoteTokenService;
    }

    @ApiOperation(value = "token校验（校验是否存在，是否启用）")
    @Permission(permissionPublic = true)
    @GetMapping(value = "/check")
    public ResponseEntity<CommonCheckResultVO> checkToken(@RequestParam(name = "remote_token") String remoteToken,
                                                          @RequestParam(name = "operation", required = false) String operation) {
        return new ResponseEntity<>(remoteTokenService.checkToken(remoteToken, operation), HttpStatus.OK);
    }

    public void setRemoteTokenService(RemoteTokenService remoteTokenService) {
        this.remoteTokenService = remoteTokenService;
    }
}
