package io.choerodon.iam.api.vo;

import io.choerodon.iam.infra.utils.StringUtil;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/16
 */
public class TimeZoneWorkCalendarRefCreateVO extends AuditDomain {

    private Long calendarId;

    @ApiModelProperty(value = "日期")
    private String workDay;

    @ApiModelProperty(value = "状态，0为放假，1为补班")
    private Integer status;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeZoneWorkCalendarRefCreateVO)) {
            return false;
        }
        TimeZoneWorkCalendarRefCreateVO that = (TimeZoneWorkCalendarRefCreateVO) o;
        return Objects.equals(getWorkDay(), that.getWorkDay()) &&
                Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getWorkDay(), getStatus());
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
