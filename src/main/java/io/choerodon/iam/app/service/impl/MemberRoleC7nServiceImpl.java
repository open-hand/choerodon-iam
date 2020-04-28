package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.MemberRoleC7nService;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.utils.SagaTopic;

import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/4/17
 */
@Service
public class MemberRoleC7nServiceImpl implements MemberRoleC7nService {
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    @Autowired
    private UserAssertHelper userAssertHelper;


    @Override
    public List<MemberRole> insertOrUpdateRolesOfUserByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType) {
        return insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, memberId, memberRoles, sourceType, false);
    }

    @Override
    @Saga(code = MEMBER_ROLE_UPDATE, description = "iam更新用户角色", inputSchemaClass = List.class)
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRole> insertOrUpdateRolesOfUserByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType, Boolean syncAll) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        User userDTO = userAssertHelper.userNotExisted(memberId);
        List<MemberRole> returnList = new ArrayList<>();
        // todo
//        if (devopsMessage) {
//            List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
//            UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
//            userMemberEventMsg.setResourceId(sourceId);
//            userMemberEventMsg.setUserId(memberId);
//            userMemberEventMsg.setResourceType(sourceType);
//            userMemberEventMsg.setUsername(userDTO.getLoginName());
//            userMemberEventMsg.setSyncAll(syncAll);
//
////            List<Long> ownRoleIds = insertOrUpdateRolesByMemberIdExecute(userId,
////                    isEdit, sourceId, memberId, sourceType, memberRoles, returnList, Constants.MemberType.USER);
//            if (!ownRoleIds.isEmpty()) {
//                userMemberEventMsg.setRoleLabels(labelMapper.selectLabelNamesInRoleIds(ownRoleIds));
//            }
//            userMemberEventPayloads.add(userMemberEventMsg);
//            sendEvent(userMemberEventPayloads, MEMBER_ROLE_UPDATE);
//            return returnList;
//        } else {
//            insertOrUpdateRolesByMemberIdExecute(userId, isEdit,
//                    sourceId,
//                    memberId,
//                    sourceType,
//                    memberRoles,
//                    returnList, Constants.MemberType.USER);
//            return returnList;
//        }
        return null;
    }

}
