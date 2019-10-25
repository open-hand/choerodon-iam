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
public class ProjectCategoryEDTO {

    public static final String CODE_REGULAR_EXPRESSION
            = "^[a-z]([-.a-z0-9]*[a-z0-9])$";

    private Long id;

    @ApiModelProperty("项目类型名/必填")
    @NotEmpty(message = "error.project.category.name.empty", groups = {Insert.class})
    @Size(min = 1, max = 32, message = "error.project.category.name.length", groups = {Insert.class})
    private String name;
    private String description;

    @ApiModelProperty("项目类型编码/必填")
    @NotEmpty(message = "error.project.category.code.empty", groups = {Insert.class, Check.class})
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.project.category.code.illegal", groups = {Insert.class, Check.class})
    @Size(min = 1, max = 15, message = "error.project.category.code.length", groups = {Insert.class, Check.class})
    private String code;
    private Long organizationId;
    private Boolean displayFlag;
    private Boolean builtInFlag;
    private Long objectVersionNumber;
    private List<MenuCodeDTO> menuCodes;

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Boolean displayFlag) {
        this.displayFlag = displayFlag;
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

    public List<MenuCodeDTO> getMenuCodes() {
        return menuCodes;
    }

    public void setMenuCodes(List<MenuCodeDTO> menuCodes) {
        this.menuCodes = menuCodes;
    }
}
