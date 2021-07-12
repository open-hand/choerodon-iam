package io.choerodon.iam.api.vo;

import io.choerodon.iam.infra.dto.DashboardLayoutDTO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

public class DashboardVO {
    @ApiModelProperty(value = "视图ID", required = true)
    @Encrypt
    private Long dashboardId;
    @ApiModelProperty(value = "视图名称")
    private String dashboardName;
    @ApiModelProperty(value = "是否更新布局")
    private Integer updateLayoutFlag;
    @ApiModelProperty(value = "布局信息")
    @Encrypt
    private List<DashboardLayoutDTO> dashboardLayoutS;

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Integer getUpdateLayoutFlag() {
        return updateLayoutFlag;
    }

    public void setUpdateLayoutFlag(Integer updateLayoutFlag) {
        this.updateLayoutFlag = updateLayoutFlag;
    }

    public List<DashboardLayoutDTO> getDashboardLayoutS() {
        return dashboardLayoutS;
    }

    public void setDashboardLayoutS(List<DashboardLayoutDTO> dashboardLayoutS) {
        this.dashboardLayoutS = dashboardLayoutS;
    }

    public String getDashboardName() {
        return dashboardName;
    }

    public void setDashboardName(String dashboardName) {
        this.dashboardName = dashboardName;
    }
}
