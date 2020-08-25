package io.choerodon.iam.api.vo;

import io.choerodon.core.domain.Page;

public class OnlineUserStatistics {
    private Integer totalOnlineUser;
    private Page<UserVO> onlineUserList;

    public Integer getTotalOnlineUser() {
        return totalOnlineUser;
    }

    public void setTotalOnlineUser(Integer totalOnlineUser) {
        this.totalOnlineUser = totalOnlineUser;
    }

    public Page<UserVO> getOnlineUserList() {
        return onlineUserList;
    }

    public void setOnlineUserList(Page<UserVO> onlineUserList) {
        this.onlineUserList = onlineUserList;
    }
}
