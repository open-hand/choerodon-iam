package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.choerodon.iam.app.service.OrganizationProjectC7nService;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author scp
 * @since 2022-1-06
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations")
public class AgileOnOrganizationController {

    @Autowired
    private UserC7nService userC7nService;
    @Autowired
    private OrganizationProjectC7nService organizationProjectC7nService;

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询当前组织下用户的项目列表")
    @GetMapping(value = "/{organization_id}/users/{user_id}/projects_simple")
    public ResponseEntity<List<ProjectDTO>> listProjectsByUserIdForSimple(@PathVariable(name = "organization_id") Long organizationId,
                                                                          @Encrypt @PathVariable(name = "user_id") Long userId,
                                                                          @RequestParam(required = false) String name,
                                                                          @RequestParam(required = false) String code,
                                                                          @RequestParam(required = false) String category,
                                                                          @RequestParam(required = false) Boolean enabled,
                                                                          @RequestParam(required = false) String params) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(name);
        projectDTO.setCode(code);
        projectDTO.setCategory(category);
        projectDTO.setEnabled(enabled);
        return new ResponseEntity<>(userC7nService.listProjectsByUserIdForSimple(organizationId, userId, projectDTO, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询项目，并将传入的id置前")
    @PostMapping(value = "/{organization_id}/projects/list_and_top")
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> pagingQueryAndTop(@PathVariable(name = "organization_id") Long organizationId,
                                                              @ApiIgnore
                                                              @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                              @RequestBody ProjectDTO project) {
        return new ResponseEntity<>(organizationProjectC7nService.pagingQueryAndTop(organizationId, pageRequest, project),
                HttpStatus.OK);
    }

}
