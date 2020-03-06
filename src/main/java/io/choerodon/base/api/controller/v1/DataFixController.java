package io.choerodon.base.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.app.service.DataFixService;
import io.choerodon.base.app.service.OrganizationService;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author: 25499
 * @date: 2020/3/3 19:38
 * @description:
 */
@RestController
@RequestMapping(value = "/v1/fix")
public class DataFixController {
    private DataFixService dataFixService;

    public DataFixController(DataFixService dataFixService) {
        this.dataFixService = dataFixService;
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "分页查询所有组织基本信息（修数据用）")
    @GetMapping(value = "/organizations/all")
    @CustomPageRequest
    public ResponseEntity<List<OrganizationSimplifyDTO>> getAllOrgsList() {
        return new ResponseEntity<>(dataFixService.getAllOrgsList(), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "分页查询所有项目基本信息（修数据用）")
    @GetMapping(value = "/projects/all")
    @CustomPageRequest
    public ResponseEntity<List<ProjectDTO>> getAllProList() {
        return new ResponseEntity<>(dataFixService.getAllproList(), HttpStatus.OK);
    }
}

