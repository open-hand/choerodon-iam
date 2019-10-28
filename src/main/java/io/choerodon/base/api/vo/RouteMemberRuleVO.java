package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * RouteMemberRuleVO
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
public class RouteMemberRuleVO {
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "用户ID/必填")
    private Long userId;
    @ApiModelProperty(value = "路由编码/必填")
    private String routeRuleCode;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public RouteMemberRuleVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RouteMemberRuleVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getRouteRuleCode() {
        return routeRuleCode;
    }

    public RouteMemberRuleVO setRouteRuleCode(String routeRuleCode) {
        this.routeRuleCode = routeRuleCode;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public RouteMemberRuleVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
