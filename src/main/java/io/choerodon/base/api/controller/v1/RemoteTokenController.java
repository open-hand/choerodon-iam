package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.api.dto.RemoteTokenBase64VO;
import io.choerodon.base.app.service.RemoteTokenService;
import io.choerodon.base.infra.dto.RemoteTokenDTO;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Eugen
 **/
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/remote_tokens")
public class RemoteTokenController {

    private RemoteTokenService remoteTokenService;

    public RemoteTokenController(RemoteTokenService remoteTokenService) {
        this.remoteTokenService = remoteTokenService;
    }

    public void setRemoteTokenService(RemoteTokenService remoteTokenService) {
        this.remoteTokenService = remoteTokenService;
    }

    @ApiOperation(value = "获取组织最新的远程连接令牌")
    @Permission(type = ResourceType.ORGANIZATION)
    @GetMapping(value = "/latest")
    public ResponseEntity<RemoteTokenBase64VO> getTheLatest(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(remoteTokenService.getTheLatest(organizationId), HttpStatus.OK);
    }

    @ApiOperation(value = "分页查询组织远程连接TOKEN的历史记录（不包括最新一条记录，不论最新记录的状态）")
    @Permission(type = ResourceType.ORGANIZATION)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<RemoteTokenBase64VO>> pagingTheHistoryList(@PathVariable(name = "organization_id") Long organizationId,
                                                                              @ApiIgnore
                                                                              @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                              @RequestParam(value = "name", required = false) String name,
                                                                              @RequestParam(value = "params", required = false) String params) {
        RemoteTokenDTO filterDTO = new RemoteTokenDTO();
        if (name != null) {
            filterDTO.setName(name);
        }
        return new ResponseEntity<>(remoteTokenService.pagingTheHistoryList(organizationId, Pageable, filterDTO, params), HttpStatus.OK);
    }

    @ApiOperation(value = "创建组织的远程连接令牌")
    @Permission(type = ResourceType.ORGANIZATION)
    @PostMapping
    public ResponseEntity<RemoteTokenBase64VO> create(@PathVariable(name = "organization_id") Long organizationId,
                                                      @RequestBody @Validated RemoteTokenDTO createDTO) {
        createDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(remoteTokenService.createNewOne(organizationId, createDTO), HttpStatus.OK);
    }

    @ApiOperation(value = "将远程应用令牌置为失效")
    @Permission(type = ResourceType.ORGANIZATION)
    @PutMapping(value = "/{id}/expired")
    public ResponseEntity<RemoteTokenBase64VO> expired(@PathVariable(name = "organization_id") Long organizationId,
                                                       @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(remoteTokenService.expired(organizationId, id), HttpStatus.OK);
    }

    @ApiOperation(value = "使远程应用令牌重新生效")
    @Permission(type = ResourceType.ORGANIZATION)
    @PutMapping(value = "/{id}/renewal")
    public ResponseEntity<RemoteTokenBase64VO> renewal(@PathVariable(name = "organization_id") Long organizationId,
                                                       @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(remoteTokenService.renewal(organizationId, id), HttpStatus.OK);
    }
}
