package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2022/5/26
 */
public class TenantWpsConfigVO {
    @Encrypt
    private Long id;

    @ApiModelProperty("组织ID")
    private Long tenantId;

    @ApiModelProperty("是否开启wps编辑")
    private Boolean enableWpsEdit;

    @ApiModelProperty("编辑连接数")
    private Integer connectionNumber;

    @ApiModelProperty("生效起始时间")
    private Date effectiveDate;

    @ApiModelProperty("过期时间")
    private Date expireDate;

    @ApiModelProperty("销售人员")
    private String salesMan;

    @ApiModelProperty("备注")
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getEnableWpsEdit() {
        return enableWpsEdit;
    }

    public void setEnableWpsEdit(Boolean enableWpsEdit) {
        this.enableWpsEdit = enableWpsEdit;
    }

    public Integer getConnectionNumber() {
        return connectionNumber;
    }

    public void setConnectionNumber(Integer connectionNumber) {
        this.connectionNumber = connectionNumber;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getSalesMan() {
        return salesMan;
    }

    public void setSalesMan(String salesMan) {
        this.salesMan = salesMan;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
