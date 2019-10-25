package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * FD_ROUTE_MEMBER_RULE DTO
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@Table(name = "fd_route_member_rule")
public class RouteMemberRuleDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "用户ID/必填")
    private Long userId;
    @ApiModelProperty(value = "路由编码/必填")
    private String routeRuleCode;

    public Long getId() {
        return id;
    }

    public RouteMemberRuleDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RouteMemberRuleDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getRouteRuleCode() {
        return routeRuleCode;
    }

    public RouteMemberRuleDTO setRouteRuleCode(String routeRuleCode) {
        this.routeRuleCode = routeRuleCode;
        return this;
    }
}
