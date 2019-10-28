package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.vo.RouteRuleVO;
import io.choerodon.base.app.service.RouteRuleService;
import io.choerodon.base.infra.dto.RouteRuleDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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
    @ApiOperation(value = "分页查询路由规则信息")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    public ResponseEntity<PageInfo<RouteRuleVO>> listRouteRules(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @RequestParam(value = "name", required = false) String name,
                                                                @RequestParam(value = "host_number", required = false) Long hostNumber,
                                                                @RequestParam(value = "user_number", required = false) Long userNumber,
                                                                @RequestParam(value = "description", required = false) String description,
                                                                @RequestParam(value = "params", required = false) String[] params) {
        return new ResponseEntity<>(routeRuleService.listRouteRules(pageable, name, description, hostNumber, userNumber, params), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "根据id查询路由详细信息")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO> queryRouteRuleDetailById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.queryRouteRuleDetailById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/insert")
    @ApiOperation(value = "添加路由规则")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO> routeRuleInsert(@RequestBody @Validated({Insert.class}) RouteRuleVO routeRuleVO) {
        return new ResponseEntity<>(routeRuleService.routeRuleInsert(routeRuleVO), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation("根据ID删除路由规则")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<Boolean> routeRuleDeleteById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(routeRuleService.deleteRouteRuleById(id), HttpStatus.OK);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新路由规则信息")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<RouteRuleVO> routeRuleUpdate(@RequestBody @Validated(Check.class) RouteRuleVO routeRuleVO,
                                                        @RequestParam(value = "object_version_number") Long objectVersionNumber) {
        return new ResponseEntity<>(routeRuleService.routeRuleUpdate(routeRuleVO, objectVersionNumber), HttpStatus.OK);
    }

    @PostMapping("/check")
    @ApiOperation(value = "路由名称重复性校验")
    @Permission(permissionPublic = true)
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<Boolean> checkName(@RequestBody @Validated({Check.class}) RouteRuleDTO routeRuleDTO) {
        return new ResponseEntity<>(routeRuleService.checkName(routeRuleDTO), HttpStatus.OK);
    }
}
