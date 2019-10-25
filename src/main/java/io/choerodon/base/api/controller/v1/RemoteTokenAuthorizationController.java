package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.app.service.RemoteTokenAuthorizationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/5
 */
@RestController
@RequestMapping(value = "/v1/remote_token/authorization")
public class RemoteTokenAuthorizationController {

    private RemoteTokenAuthorizationService remoteTokenAuthorizationService;

    public RemoteTokenAuthorizationController(RemoteTokenAuthorizationService remoteTokenAuthorizationService) {
        this.remoteTokenAuthorizationService = remoteTokenAuthorizationService;
    }

    @ApiOperation(value = "分页查询校验远程TOKEN的历史记录")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<RemoteTokenAuthorizationVO>> pagingAllAuthorizations(@ApiIgnore
                                                                                        @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                        @RequestParam(required = false) String name,
                                                                                        @RequestParam(required = false) String email,
                                                                                        @RequestParam(required = false) String status,
                                                                                        @RequestParam(required = false, value = "organization_name") String organizationName,
                                                                                        @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(remoteTokenAuthorizationService.pagingAuthorizations(name, email, status,organizationName, params, Pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "存储远程连接令牌并校验是否有效")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @PostMapping(value = "/storeAndCheck")
    public ResponseEntity<RemoteTokenAuthorizationVO> storeAndCheckToken(@RequestBody String tokenBase64) {
        return new ResponseEntity<>(remoteTokenAuthorizationService.storeAndCheckToken(tokenBase64), HttpStatus.OK);
    }

    @ApiOperation(value = "校验最新token是否有效")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @GetMapping(value = "/check/latest")
    public ResponseEntity<RemoteTokenAuthorizationVO> checkLatestToken() {
        return new ResponseEntity<>(remoteTokenAuthorizationService.checkLatestToken(), HttpStatus.OK);
    }


    @ApiOperation(value = "断开token连接")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @PutMapping(value = "/latest/status/break")
    public ResponseEntity<RemoteTokenAuthorizationVO> breakLatestToken() {
        return new ResponseEntity<>(remoteTokenAuthorizationService.breakLatestToken(), HttpStatus.OK);
    }

    @ApiOperation(value = "token重新连接")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @PutMapping(value = "/latest/status/reconnect")
    public ResponseEntity<RemoteTokenAuthorizationVO> reconnectLatestToken() {
        return new ResponseEntity<>(remoteTokenAuthorizationService.reconnectLatestToken(), HttpStatus.OK);
    }
}
