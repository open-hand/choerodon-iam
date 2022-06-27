package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.domain.entity.Client;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;


/**
 * @author wuguokai
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_CLIENT)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/clients")
public class ClientC7nController extends BaseController {

    private final ClientC7nService clientC7nService;


    public ClientC7nController(ClientC7nService clientC7nService) {
        this.clientC7nService = clientC7nService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "随机的客户端创建信息生成")
    @GetMapping(value = "/createInfo")
    public ResponseEntity<Client> createInfo(
            @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(clientC7nService.getDefaultCreateData(organizationId), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除客户端")
    @DeleteMapping(value = "/{client_id}")
    public ResponseEntity<Void> delete(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt @PathVariable("client_id") Long clientId) {
        clientC7nService.delete(organizationId, clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "通过名称查询客户端")
    @GetMapping("/query_by_name")
    public ResponseEntity<Client> queryByName(
            @PathVariable("organization_id") Long organizationId,
            @RequestParam(value = "client_name") String clientName) {
        return new ResponseEntity<>(clientC7nService.queryByName(organizationId, clientName), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端分配角色")
    @PostMapping(value = "/{client_id}/assign_roles")
    public ResponseEntity<Void> assignRoles(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt @PathVariable("client_id") Long clientId,
            @Encrypt @RequestBody List<Long> roleIds) {
        clientC7nService.assignRoles(organizationId, clientId, roleIds);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建客户端，该接口会保存客户端关系")
    @PostMapping
    public ResponseEntity<ClientVO> create(
            @PathVariable("organization_id") Long organizationId, @RequestBody ClientVO clientVO) {
        clientVO.setOrganizationId(organizationId);
        this.validObject(clientVO);
        return Results.success(clientC7nService.create(clientVO));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("分页模糊查询客户端")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<Client>> list(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "params", required = false) String params,
                                             @ApiIgnore
                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(clientC7nService.pageClient(organizationId, name, params, pageRequest));
    }
}
