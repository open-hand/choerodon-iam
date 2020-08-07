package io.choerodon.iam.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.QuickLinkVO;
import io.choerodon.iam.app.service.QuickLinkService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.QuickLinkDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 16:13
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_QUICK_LINK)
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/quick_links")
public class QuickLinkController {

    @Autowired
    private QuickLinkService quickLinkService;

    @ApiOperation("新增快速链接")
    @Permission(permissionLogin = true)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid QuickLinkDTO quickLinkDTO) {
        quickLinkService.create(quickLinkDTO);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("修改快速链接")
    @Permission(permissionLogin = true)
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt @PathVariable(value = "id") Long id,
            @RequestBody @Valid QuickLinkDTO quickLinkDTO) {
        quickLinkService.update(organizationId, id, quickLinkDTO);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("删除快速链接")
    @Permission(permissionLogin = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt @PathVariable(value = "id") Long id) {
        quickLinkService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("分页查询快速链接")
    @Permission(permissionLogin = true)
    @GetMapping
    public ResponseEntity<Page<QuickLinkVO>> query(@PathVariable(value = "organization_id") Long organizationId,
                                                   @RequestParam(value = "project_id", required = false) Long projectId,
                                                   @ApiIgnore
                                                   @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable) {
        return ResponseEntity.ok(quickLinkService.query(organizationId, projectId, pageable));
    }


    @ApiOperation("分页查询项目快速链接")
    @Permission(permissionLogin = true)
    @GetMapping("/scope/project")
    public ResponseEntity<Page<QuickLinkVO>> queryProject(@PathVariable(value = "organization_id") Long organizationId,
                                                          @RequestParam(value = "project_id", required = false) Long projectId,
                                                          @ApiIgnore
                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable) {
        return ResponseEntity.ok(quickLinkService.queryProject(organizationId, projectId, pageable));
    }

    @ApiOperation("分页查询个人快速链接")
    @Permission(permissionLogin = true)
    @GetMapping("/scope/self")
    public ResponseEntity<Page<QuickLinkVO>> querySelf(@PathVariable(value = "organization_id") Long organizationId,
                                                       @ApiIgnore
                                                       @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable) {
        return ResponseEntity.ok(quickLinkService.querySelf(organizationId, pageable));
    }

    @ApiOperation("添加置顶")
    @Permission(permissionLogin = true)
    @PutMapping("/{quick_link_id}/add_top")
    public ResponseEntity<Page<QuickLinkVO>> addTop(@PathVariable(value = "organization_id") Long organizationId,
                                                    @Encrypt @PathVariable(value = "quick_link_id") Long id) {
        quickLinkService.addTop(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("取消置顶")
    @Permission(permissionLogin = true)
    @PutMapping("/{quick_link_id}/delete_top")
    public ResponseEntity<Page<QuickLinkVO>> deleteTop(@PathVariable(value = "organization_id") Long organizationId,
                                                       @Encrypt @PathVariable(value = "quick_link_id") Long id) {
        quickLinkService.deleteTop(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
