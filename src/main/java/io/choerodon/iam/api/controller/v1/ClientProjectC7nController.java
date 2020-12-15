package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.Role;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.app.service.ClientProjectC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author scp
 * @date 2020/11/3
 * @description
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_CLIENT_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/clients-project/{project_id}")
public class ClientProjectC7nController {

    private final ClientProjectC7nService clientProjectC7nService;
    private final ClientC7nService clientC7nService;
    private final ClientService clientService;

    public ClientProjectC7nController(@Lazy ClientProjectC7nService clientProjectC7nService,
                                      ClientC7nService clientC7nService,
                                      ClientService clientService) {
        this.clientProjectC7nService = clientProjectC7nService;
        this.clientC7nService = clientC7nService;
        this.clientService = clientService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "随机的客户端创建信息生成")
    @GetMapping(value = "/createInfo")
    public ResponseEntity<Client> createInfo(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(clientC7nService.getDefaultCreateData(organizationId), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建客户端，该接口会保存客户端关系")
    @PostMapping
    public ResponseEntity<ClientVO> create(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId,
            @RequestBody ClientVO clientVO) {
        clientVO.setOrganizationId(organizationId);
        clientVO.setSourceId(projectId);
        clientVO.setSourceType(ResourceLevel.PROJECT.value());
        return Results.success(clientProjectC7nService.create(clientVO));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除客户端")
    @DeleteMapping(value = "/{client_id}")
    public ResponseEntity<Void> delete(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId,
            @Encrypt @PathVariable("client_id") Long clientId) {
        clientProjectC7nService.delete(organizationId, projectId, clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改客户端")
    @PutMapping
    public ResponseEntity<Void> update(@PathVariable("organization_id") Long organizationId,
                                       @PathVariable("project_id") Long projectId, @RequestBody Client client) {
        client.setOrganizationId(organizationId);
        clientProjectC7nService.update(organizationId, projectId, client);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "通过名称查询客户端")
    @GetMapping("/query_by_name")
    public ResponseEntity<Client> queryByName(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId,
            @RequestParam(value = "client_name") String clientName) {
        return new ResponseEntity<>(clientC7nService.queryByName(organizationId, clientName), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("分页模糊查询客户端")
    @GetMapping({"/clients"})
    public ResponseEntity<Page<Client>> list(@PathVariable("organization_id") Long organizationId,
                                             @PathVariable("project_id") Long projectId,
                                             String name, Integer enabledFlag, PageRequest pageRequest) {
        return Results.success(clientProjectC7nService.pageClient(organizationId, projectId, name, enabledFlag, pageRequest));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端分配角色")
    @PostMapping(value = "/{client_id}/assign_roles")
    public ResponseEntity<Void> assignRoles(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId,
            @Encrypt @PathVariable("client_id") Long clientId,
            @Encrypt @RequestBody List<Long> roleIds) {
        clientProjectC7nService.assignRoles(organizationId, projectId, clientId, roleIds);
        return Results.success();
    }


    @ApiOperation("客户端 - 查询分配给客户端的角色")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/client-roles/{client_id}"})
    public ResponseEntity<List<Role>> listClientRoles(@PathVariable("organization_id") Long organizationId,
                                                      @PathVariable("project_id") Long projectId,
                                                      @Encrypt @PathVariable("client_id") Long clientId,
                                                      @RequestParam(value = "role_name", required = false) String roleName) {
        return Results.success(clientProjectC7nService.selectMemberRoles(organizationId, projectId, clientId, roleName));
    }

    @ApiOperation("客户端 - 查询客户端详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/{client_id}"})
    public ResponseEntity<Client> query(@PathVariable("organization_id") Long organizationId,
                                        @PathVariable("project_id") Long projectId,
                                        @Encrypt @PathVariable("client_id") Long clientId) {
        return Results.success(clientService.detailClient(organizationId, clientId));
    }

}
