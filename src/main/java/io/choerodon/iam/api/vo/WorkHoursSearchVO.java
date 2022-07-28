package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhaotianxin
 * @date 2021-10-15 16:26
 */
public class WorkHoursSearchVO {

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "用户id集合")
    @Encrypt
    private List<Long> userIds;

    @ApiModelProperty(value = "项目id集合")
    private List<Long> projectIds;

    @ApiModelProperty(value = "是否导出工作组月报")
    private Boolean exportMonthlyReport;

    @ApiModelProperty(value = "工作组id集合")
    @Encrypt(ignoreValue = {"0"})
    private List<Long> workGroupIds;

    @ApiModelProperty("用户标签")
    private List<String> userLabels;

    @ApiModelProperty("登记过工时的用户id")
    private Set<Long> workLogUserIds;

    public Set<Long> getWorkLogUserIds() {
        return workLogUserIds;
    }

    public void setWorkLogUserIds(Set<Long> workLogUserIds) {
        this.workLogUserIds = workLogUserIds;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Boolean getExportMonthlyReport() {
        return exportMonthlyReport;
    }

    public void setExportMonthlyReport(Boolean exportMonthlyReport) {
        this.exportMonthlyReport = exportMonthlyReport;
    }

    public List<Long> getWorkGroupIds() {
        return workGroupIds;
    }

    public void setWorkGroupIds(List<Long> workGroupIds) {
        this.workGroupIds = workGroupIds;
    }

    public List<String> getUserLabels() {
        return userLabels;
    }

    public void setUserLabels(List<String> userLabels) {
        this.userLabels = userLabels;
    }


}
