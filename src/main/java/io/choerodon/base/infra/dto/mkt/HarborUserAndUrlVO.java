package io.choerodon.base.infra.dto.mkt;

/**
 * @author jiameng.cao
 * @date 2019/8/19
 */
public class HarborUserAndUrlVO {
    private RobotUser user;
    private String harborUrl;

    public RobotUser getUser() {
        return user;
    }

    public void setUser(RobotUser user) {
        this.user = user;
    }

    public String getHarborUrl() {
        return harborUrl;
    }

    public void setHarborUrl(String harborUrl) {
        this.harborUrl = harborUrl;
    }
}
