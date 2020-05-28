package io.choerodon.iam.infra.dto.payload;

public class WebHookUser {
    private String loginName;
    private String userName;

    public WebHookUser() {
    }

    public WebHookUser(String loginName, String userName) {
        this.loginName = loginName;
        this.userName = userName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}