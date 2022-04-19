package io.choerodon.iam.api.vo;

/**
 * @author superlee
 */
public class ExcelMemberRoleDTO {
    private String loginName;
    private String roleCode;
    private String cause;
    //进场时间
    private String scheduleEntryTime;
    // 撤场时间
    private String scheduleExitTime;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getScheduleEntryTime() {
        return scheduleEntryTime;
    }

    public void setScheduleEntryTime(String scheduleEntryTime) {
        this.scheduleEntryTime = scheduleEntryTime;
    }

    public String getScheduleExitTime() {
        return scheduleExitTime;
    }

    public void setScheduleExitTime(String scheduleExitTime) {
        this.scheduleExitTime = scheduleExitTime;
    }
}
