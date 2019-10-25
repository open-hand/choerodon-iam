package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.LovService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.LovDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/lov")
public class LovController {
    private LovService lovService;

    public LovController(LovService lovService) {
        this.lovService = lovService;
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过code查询LOV")
    @GetMapping(value = "/code")
    public ResponseEntity<LovDTO> queryByCode(@RequestParam(name = "code") String code) {
        return new ResponseEntity<>(lovService.queryLovByCode(code), HttpStatus.OK);
    }
}
