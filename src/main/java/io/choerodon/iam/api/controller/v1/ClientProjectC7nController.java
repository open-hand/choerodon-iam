package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.domain.repository.ClientRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
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

    private final ClientService clientService;
    private final ClientC7nService clientC7nService;
    private final ClientRepository clientRepository;
    private final TenantRepository tenantRepository;


    public ClientProjectC7nController(ClientService clientService,
                               ClientRepository clientRepository,
                               ClientC7nService clientC7nService,
                               TenantRepository tenantRepository) {
        this.clientService = clientService;
        this.clientC7nService = clientC7nService;
        this.clientRepository = clientRepository;
        this.tenantRepository = tenantRepository;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建客户端，该接口会保存客户端关系")
    @PostMapping
    public ResponseEntity<ClientVO> create(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("project_id") Long projectId,
            @RequestBody ClientVO clientVO) {
        clientVO.setOrganizationId(organizationId);
        return Results.success(clientC7nService.create(clientVO));
    }

}
