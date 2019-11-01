package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.Update;
import io.choerodon.base.api.vo.RouteRuleVO;
import io.choerodon.base.app.service.RouteRuleService;
import io.choerodon.base.infra.dto.RouteRuleDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * RouteRuleController
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@RestController
@RequestMapping(value = "/v1/route_rules")
public class RouteRuleController {
    private RouteRuleService routeRuleService;

    public RouteRuleController(RouteRuleService routeRuleService) {
        this.routeRuleService = routeRuleService;
    }

    @GetMapping
    @ApiOperation(value = "查询所有路由规则信息")
    @Permission(permissionWithin = true)
    public ResponseEntity<PageInfo<RouteRuleVO>> listRouteRules(@SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @RequestParam(value = "code", required = false) String code) {
        System.out.println("size:" + pageable.getPageSize() + "pageNum:" + pageable.getPageNumber() + "sort:" + pageable.getSort().toString());
        return new ResponseEntity<>(routeRuleService.listRouteRules(pageable, code), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "根据id查询路由详细信息")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.queryRouteRuleDetailById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/insert")
    @ApiOperation(value = "添加路由规则")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO> insertRouteRule(@RequestBody @Validated({Insert.class}) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.insertRouteRule(routeRuleVO), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation("根据ID删除路由规则")
    @Permission(permissionWithin = true)
    public ResponseEntity<Boolean> routeRuleDeleteById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.deleteRouteRuleById(id), HttpStatus.OK);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新路由规则信息")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO>updateRouteRule(@RequestBody @Validated(Update.class) RouteRuleVO routeRuleVO,
                                                        @RequestParam(value = "object_version_number") Long objectVersionNumber) {
        return new ResponseEntity<>(routeRuleService.updateRouteRule(routeRuleVO, objectVersionNumber), HttpStatus.OK);
    }

    @PostMapping("/check")
    @ApiOperation(value = "路由编码重复性校验")
    @Permission(permissionWithin = true)
    public ResponseEntity<Boolean> checkCode(@RequestBody @Validated({Check.class}) RouteRuleDTO routeRuleDTO) {
        return new ResponseEntity<>(routeRuleService.checkCode(routeRuleDTO), HttpStatus.OK);
    }
}
