package io.choerodon.base.api.controller.v1;

import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.base.infra.dto.AuditDTO;
import org.springframework.data.web.SortDefault;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.base.app.service.AuditService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Eugen
 **/
@RestController
@RequestMapping(value = "/v1/audit")
public class AuditController {

    private AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "创建审计记录")
    @PostMapping(value = "/insert")
    public ResponseEntity<AuditDTO> create(@RequestBody @Valid AuditDTO auditDTO) {
        return new ResponseEntity<>(auditService.create(auditDTO), HttpStatus.OK);
    }


    @Permission(permissionWithin = true)
    @ApiOperation(value = "分页查询审计记录")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<AuditDTO>> pagingQuery(@ApiIgnore
                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                          @RequestParam(name = "userId", required = false) Long userId,
                                                          @RequestParam(value = "dataType", required = false) String dataType,
                                                          @RequestParam(value = "businessType", required = false) String businessType) {
        return new ResponseEntity<>(auditService.pagingQuery(userId, businessType, dataType,Pageable), HttpStatus.OK);
    }
}
