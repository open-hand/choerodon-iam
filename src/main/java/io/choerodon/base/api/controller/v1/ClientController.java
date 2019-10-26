package io.choerodon.base.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.ClientVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.ClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.ClientDTO;
import io.choerodon.core.base.BaseController;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/clients")
public class ClientController extends BaseController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建客户端")
    @PostMapping
    public ResponseEntity<ClientDTO> create(@PathVariable("organization_id") Long organizationId,
                                            @RequestBody @Valid ClientDTO clientDTO) {
        return new ResponseEntity<>(clientService.create(organizationId, clientDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "随机的客户端创建信息生成")
    @GetMapping(value = "/createInfo")
    public ResponseEntity<ClientDTO> createInfo(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(clientService.getDefaultCreateData(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改客户端")
    @PostMapping(value = "/{client_id}")
    public ResponseEntity<ClientDTO> update(@PathVariable("organization_id") Long organizationId,
                                            @PathVariable("client_id") Long clientId,
                                            @RequestBody ClientDTO clientDTO) {
        clientDTO.setId(clientId);
        clientDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(clientService.update(clientDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除客户端")
    @DeleteMapping(value = "/{client_id}")
    public ResponseEntity delete(@PathVariable("organization_id") Long organizationId, @PathVariable("client_id") Long clientId) {
        clientService.delete(organizationId, clientId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "通过id查询客户端")
    @GetMapping(value = "/{client_id}")
    public ResponseEntity<ClientDTO> query(@PathVariable("organization_id") Long organizationId, @PathVariable("client_id") Long clientId) {
        return new ResponseEntity<>(clientService.query(organizationId, clientId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "通过名称查询客户端")
    @GetMapping("/query_by_name")
    public ResponseEntity<ClientDTO> queryByName(@PathVariable("organization_id") Long organizationId, @RequestParam(value = "client_name") String clientName) {
        return new ResponseEntity<>(clientService.queryByName(organizationId, clientName), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "分页模糊查询客户端")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<ClientDTO>> list(@PathVariable("organization_id") Long organizationId,
                                                    @ApiIgnore
                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String params) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setOrganizationId(organizationId);
        clientDTO.setName(name);
        return new ResponseEntity<>(clientService.list(clientDTO, Pageable, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "客户端信息校验(新建校验不传id,更新校验传id)")
    @PostMapping(value = "/check")
    public void check(@PathVariable(name = "organization_id") Long organizationId,
                      @RequestBody ClientDTO client) {
        clientService.check(client);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "客户端分配角色")
    @PostMapping(value = "/{client_id}/assign_roles")
    public ResponseEntity<ClientDTO> assignRoles(@PathVariable("organization_id") Long organizationId,
                                                 @PathVariable("client_id") Long clientId,
                                                 @RequestBody List<Long> roleIds) {
        return ResponseEntity.ok(clientService.assignRoles(organizationId, clientId, roleIds));
    }
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据类型创建客户端")
    @PostMapping("/wih_type")
    public ResponseEntity<ClientDTO> createClientWithType(@PathVariable("organization_id") Long organizationId,
                                                          @RequestBody @Valid ClientVO clientVO) {
        return new ResponseEntity<>(clientService.createClientWithType(organizationId, clientVO), HttpStatus.OK);
    }

}
