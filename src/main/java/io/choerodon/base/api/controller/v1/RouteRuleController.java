package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.Update;
import io.choerodon.base.api.vo.RouteRuleVO;
import io.choerodon.base.app.service.RouteRuleService;
import io.choerodon.core.annotation.Permission;
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
        return new ResponseEntity<>(routeRuleService.listRouteRules(pageable, code), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "根据id查询路由详细信息")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.queryRouteRuleDetailById(id), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "添加路由规则")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO> createRouteRule(@RequestBody @Validated({Insert.class}) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.createRouteRule(routeRuleVO), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation("根据ID删除路由规则")
    @Permission(permissionWithin = true)
    public ResponseEntity<Boolean> routeRuleDeleteById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.deleteRouteRuleById(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "更新路由规则信息")
    @Permission(permissionWithin = true)
    public ResponseEntity<RouteRuleVO>updateRouteRule(@PathVariable(value = "id") Long id, @RequestBody @Validated(Update.class) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.updateRouteRule(id, routeRuleVO), HttpStatus.OK);
    }

    @GetMapping("/check_code")
    @ApiOperation(value = "路由编码重复性校验")
    @Permission(permissionWithin = true)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "code") String code) {
        return new ResponseEntity<>(routeRuleService.checkCode(code), HttpStatus.OK);
    }
}
