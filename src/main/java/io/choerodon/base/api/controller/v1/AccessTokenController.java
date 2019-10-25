package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.app.service.AccessTokenService;
import io.choerodon.base.infra.dto.AccessTokenDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author Eugen
 **/
@RestController
@RequestMapping(value = "/v1/token")
public class AccessTokenController extends BaseController {

    private AccessTokenService accessTokenService;

    public AccessTokenController(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Permission(permissionLogin = true, type = ResourceType.SITE)
    @ApiOperation(value = "分页查询当前用户token")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<AccessTokenDTO>> list(@ApiIgnore
                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                         @RequestParam String currentToken,
                                                         @RequestParam(required = false) String clientName,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(accessTokenService.pagedSearch(Pageable, clientName, currentToken, params), HttpStatus.OK);
    }


    @Permission(permissionLogin = true, type = ResourceType.SITE)
    @ApiOperation(value = "根据tokenId删除token")
    @DeleteMapping
    public void delete(@RequestParam(name = "tokenId") String tokenId,
                       @RequestParam(value = "currentToken") String currentToken) {
        accessTokenService.delete(tokenId, currentToken);
    }

    @Permission(permissionLogin = true, type = ResourceType.SITE)
    @ApiOperation(value = "根据tokenId列表批量删除token")
    @DeleteMapping("/batch")
    public void deleteList(@RequestBody List<String> tokenIds,
                           @RequestParam(value = "currentToken") String currentToken) {
        accessTokenService.deleteList(tokenIds, currentToken);
    }
}
