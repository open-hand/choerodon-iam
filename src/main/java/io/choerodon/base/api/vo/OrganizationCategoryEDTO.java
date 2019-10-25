package io.choerodon.base.api.vo;

import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/6/5
 */
public class OrganizationCategoryEDTO {
    public static final String CODE_REGULAR_EXPRESSION
            = "^[a-z]([-.a-z0-9]*[a-z0-9])$";

    private Long id;

    @ApiModelProperty("组织类型名/必填")
    @NotEmpty(message = "error.organization.category.name.empty", groups = {Insert.class})
    @Size(min = 1, max = 32, message = "error.organization.category.name.length", groups = {Insert.class})
    private String name;
    private String description;

    @ApiModelProperty("组织类型编码/必填")
    @NotEmpty(message = "error.organization.category.code.empty", groups = {Insert.class, Check.class})
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.organization.category.code.illegal", groups = {Insert.class, Check.class})
    @Size(min = 1, max = 15, message = "error.organization.category.code.length", groups = {Insert.class, Check.class})
    private String code;
    private Boolean builtInFlag;
    private Long objectVersionNumber;
    private List<MenuCodeDTO> menuProCodes;
    private List<MenuCodeDTO> menuOrgCodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getBuiltInFlag() {
        return builtInFlag;
    }

    public void setBuiltInFlag(Boolean builtInFlag) {
        this.builtInFlag = builtInFlag;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<MenuCodeDTO> getMenuProCodes() {
        return menuProCodes;
    }

    public void setMenuProCodes(List<MenuCodeDTO> menuProCodes) {
        this.menuProCodes = menuProCodes;
    }

    public List<MenuCodeDTO> getMenuOrgCodes() {
        return menuOrgCodes;
    }

    public void setMenuOrgCodes(List<MenuCodeDTO> menuOrgCodes) {
        this.menuOrgCodes = menuOrgCodes;
    }
}
