package io.choerodon.iam.api.controller.v1;


import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.OrgAdministratorVO;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = C7nSwaggerApiConfig.CHOERODON_ORGANIZATION_ADMIN)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}")
public class OrganizationAdminC7nController {

    @Autowired
    private UserC7nService userC7nService;

    @GetMapping(value = "/org_administrator")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @ApiOperation(value = "查询本组织下的所有组织管理者")
    public ResponseEntity<Page<OrgAdministratorVO>> pagingQueryOrgAdministrator(
            @PathVariable(name = "organization_id") Long organizationId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest Pageable,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String loginName,
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(userC7nService.pagingQueryOrgAdministrator(Pageable, organizationId, realName, loginName, params), HttpStatus.OK);
    }

    @PostMapping("/org_administrator")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "添加组织管理员角色")
    public ResponseEntity<Void> createOrgAdministrator(
            @PathVariable(name = "organization_id") Long organizationId,
            @Encrypt @RequestParam(name = "id") List<Long> userIds) {
        userC7nService.createOrgAdministrator(userIds, organizationId);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/org_administrator/{id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除该User在本组织的组织管理员角色")
    public ResponseEntity<Void> deleteOrgAdministrator(
            @PathVariable(name = "organization_id") Long organizationId,
            @Encrypt @PathVariable(name = "id") Long userId) {
        userC7nService.deleteOrgAdministrator(organizationId, userId);
        return ResponseEntity.noContent().build();

    }
}
