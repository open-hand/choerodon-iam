package io.choerodon.iam.infra.dto;


import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.choerodon.iam.infra.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@VersionAudit
@ModifyAudit
@Table(name = "iam_time_zone_work_calendar")
public class TimeZoneWorkCalendarDTO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long timeZoneId;

    private String areaCode;

    private String timeZoneCode;

    private Long organizationId;

    private Boolean useHoliday;

    private Boolean saturdayWork;

    private Boolean sundayWork;

    @Transient
    private Date startDate;
    @Transient
    private Date endDate;


    @Transient
    private List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDTOS;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<TimeZoneWorkCalendarRefDTO> getTimeZoneWorkCalendarRefDTOS() {
        return timeZoneWorkCalendarRefDTOS;
    }

    public void setTimeZoneWorkCalendarRefDTOS(List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDTOS) {
        this.timeZoneWorkCalendarRefDTOS = timeZoneWorkCalendarRefDTOS;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(Long timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTimeZoneCode() {
        return timeZoneCode;
    }

    public void setTimeZoneCode(String timeZoneCode) {
        this.timeZoneCode = timeZoneCode;
    }

    public Boolean getUseHoliday() {
        return useHoliday;
    }

    public void setUseHoliday(Boolean useHoliday) {
        this.useHoliday = useHoliday;
    }

    public Boolean getSaturdayWork() {
        return saturdayWork;
    }

    public void setSaturdayWork(Boolean saturdayWork) {
        this.saturdayWork = saturdayWork;
    }

    public Boolean getSundayWork() {
        return sundayWork;
    }

    public void setSundayWork(Boolean sundayWork) {
        this.sundayWork = sundayWork;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
