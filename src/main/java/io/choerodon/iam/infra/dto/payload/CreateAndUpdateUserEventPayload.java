package io.choerodon.iam.infra.dto.payload;

import java.util.List;


/**
 * User: Mr.Wang
 * Date: 2020/2/10
 */
public class CreateAndUpdateUserEventPayload {

    private UserEventPayload userEventPayload;
    private List<UserMemberEventPayload> userMemberEventPayloads;

    public UserEventPayload getUserEventPayload() {
        return userEventPayload;
    }

    public void setUserEventPayload(UserEventPayload userEventPayload) {
        this.userEventPayload = userEventPayload;
    }

    public List<UserMemberEventPayload> getUserMemberEventPayloads() {
        return userMemberEventPayloads;
    }

    public void setUserMemberEventPayloads(List<UserMemberEventPayload> userMemberEventPayloads) {
        this.userMemberEventPayloads = userMemberEventPayloads;
    }
}
