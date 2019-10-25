package io.choerodon.base.api.controller.v1;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.OrgAdministratorVO;
import io.choerodon.base.app.service.OrgAdministratorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author jiameng.cao
 * @since 2019/8/1
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/org_administrator")
public class OrganizationAdministratorController {

    private OrgAdministratorService orgAdministratorService;

    public OrganizationAdministratorController(OrgAdministratorService orgAdministratorService) {
        this.orgAdministratorService = orgAdministratorService;
    }

    @GetMapping
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "查询本组织下的所有组织管理者")
    public ResponseEntity<PageInfo<OrgAdministratorVO>> pagingQueryOrgAdministrator(@PathVariable(name = "organization_id") Long organizationId,
                                                                                    @ApiIgnore
                                                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                    @RequestParam(required = false) String realName,
                                                                                    @RequestParam(required = false) String loginName,
                                                                                    @RequestParam(required = false) String params) {
        return new ResponseEntity<>(orgAdministratorService.pagingQueryOrgAdministrator(Pageable, organizationId, realName, loginName, params), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "删除该User在本组织的组织管理员角色")
    public ResponseEntity<Boolean> deleteOrgAdministrator(@PathVariable(name = "organization_id") Long organizationId,
                                                          @PathVariable(name = "id") Long userId) {
        return new ResponseEntity<>(orgAdministratorService.deleteOrgAdministrator(userId, organizationId), HttpStatus.OK);

    }

    @PostMapping
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "添加User在本组织的组织管理员角色")
    public ResponseEntity<Boolean> createOrgAdministrator(@PathVariable(name = "organization_id") Long organizationId,
                                                          @RequestParam(name = "id") List<Long> userIds) {
        return new ResponseEntity<>(orgAdministratorService.createOrgAdministrator(userIds, organizationId), HttpStatus.OK);

    }
}
