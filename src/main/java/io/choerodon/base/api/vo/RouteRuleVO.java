package io.choerodon.base.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.infra.dto.RouteMemberRuleDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

import static io.choerodon.base.infra.utils.RegularExpression.CHINESE_AND_ALPHANUMERIC_AND_SPACE_SYMBOLS_30;

/**
 * RouteRuleVO
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
public class RouteRuleVO {
    @ApiModelProperty(value = "主键ID")
    @NotNull(message = "error.route.rule.update.id.can.not.be.null", groups = {Check.class})
    private Long id;
    @ApiModelProperty(value = "路由名称/必填")
    @NotEmpty(message = "error.route.rule.name.can.not.be.empty", groups = {Insert.class, Check.class})
    @Pattern(regexp = CHINESE_AND_ALPHANUMERIC_AND_SPACE_SYMBOLS_30,message = "error.route.rule.name.format.incorrect", groups = {Insert.class, Check.class})
    private String name;
    @ApiModelProperty(value = "路由编码(使用生成的UUID)/必填")
    private String code;
    @ApiModelProperty(value = "路由描述/选填")
    private String description;

    @ApiModelProperty(value = "该路由下配置的用户信息")
    @Valid
    private List<RouteMemberRuleDTO> routeMemberRuleDTOS;

    private Long objectVersionNumber;

    @JsonIgnore
    private Date creationDate;
    private Long userNumber;
    private Long hostNumber;

    public Long getId() {
        return id;
    }

    public RouteRuleVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RouteRuleVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RouteRuleVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RouteRuleVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<RouteMemberRuleDTO> getRouteMemberRuleDTOS() {
        return routeMemberRuleDTOS;
    }

    public RouteRuleVO setRouteMemberRuleDTOS(List<RouteMemberRuleDTO> routeMemberRuleDTOS) {
        this.routeMemberRuleDTOS = routeMemberRuleDTOS;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public RouteRuleVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public RouteRuleVO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Long getUserNumber() {
        return userNumber;
    }

    public RouteRuleVO setUserNumber(Long userNumber) {
        this.userNumber = userNumber;
        return this;
    }

    public Long getHostNumber() {
        return hostNumber;
    }

    public RouteRuleVO setHostNumber(Long hostNumber) {
        this.hostNumber = hostNumber;
        return this;
    }
}

