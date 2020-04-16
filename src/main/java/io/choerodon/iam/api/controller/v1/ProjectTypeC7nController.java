package io.choerodon.iam.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.ProjectTypeC7nService;
import io.choerodon.iam.infra.dto.ProjectTypeDTO;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

@RestController
@RequestMapping("/v1/projects/types")
public class ProjectTypeC7nController {

    private ProjectTypeC7nService projectTypeService;

    public ProjectTypeC7nController(ProjectTypeC7nService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }


    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @GetMapping
    public List<ProjectTypeDTO> list() {
        return projectTypeService.list();
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页模糊查询项目类型")
    @GetMapping(value = "/paging_query")
    @CustomPageRequest
    public ResponseEntity<Page<ProjectTypeDTO>> pagingQuery(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                            @RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String code,
                                                            @RequestParam(required = false) String param) {
        return new ResponseEntity<>(projectTypeService.pagingQuery(Pageable, name, code, param), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建项目类型")
    @PostMapping
    public ResponseEntity<ProjectTypeDTO> create(@RequestBody @Valid ProjectTypeDTO projectTypeDTO) {
        return new ResponseEntity<>(projectTypeService.create(projectTypeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新项目类型")
    @PostMapping("/{id}")
    public ResponseEntity<ProjectTypeDTO> update(@PathVariable Long id,
                                                 @RequestBody @Valid ProjectTypeDTO projectTypeDTO) {
        return new ResponseEntity<>(projectTypeService.update(id, projectTypeDTO), HttpStatus.OK);
    }

    /**
     * @param projectTypeDTO
     * @return
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "重名校验")
    @PostMapping("/check")
    public ResponseEntity check(@RequestBody ProjectTypeDTO projectTypeDTO) {
        projectTypeService.check(projectTypeDTO);
        return new ResponseEntity(HttpStatus.OK);
    }


}

