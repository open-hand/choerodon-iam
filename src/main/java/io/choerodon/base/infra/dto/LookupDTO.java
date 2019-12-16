package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import io.choerodon.mybatis.annotation.*;
import io.choerodon.mybatis.entity.*;

import static io.choerodon.base.infra.utils.RegularExpression.*;

/**
 * @author superlee
 * @since 2019-04-23
 */
@MultiLanguage
@Table(name = "fd_lookup")
public class LookupDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "快码id", hidden = true)
    private Long id;

    @ApiModelProperty(value = "快码code")
    @NotEmpty(message = "error.lookup.code.empty")
    @Size(max = 32, min = 1, message = "error.code.length")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.lookup.code.format.incorrect")
    private String code;

    @MultiLanguageField
    @NotEmpty(message = "error.lookup.description.empty")
    @ApiModelProperty(value = "描述")
    private String description;

    @Transient
    @Valid
    @ApiModelProperty(value = "快码值")
    private List<LookupValueDTO> lookupValues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LookupValueDTO> getLookupValues() {
        return lookupValues;
    }

    public void setLookupValues(List<LookupValueDTO> lookupValues) {
        this.lookupValues = lookupValues;
    }
}
