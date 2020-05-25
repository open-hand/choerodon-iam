package io.choerodon.iam.infra.observer;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.role.observer.RoleAssignObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.dto.LabelDTO;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;

/**
 * @author scp
 * @date 2020/5/25
 * @description
 */
@Component
public class RoleAssignC7nObserver implements RoleAssignObserver {
    @Autowired
    private LabelC7nMapper labelC7nMapper;
    @Autowired
    private ProjectUserService projectUserService;
    @Autowired
    private UserAssertHelper userAssertHelper;
    @Autowired
    private RoleMemberService roleMemberService;

    @Override
    public void assignMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            Map<Long, Set<String>> userRoleLabelsMap = new HashMap<>();
            for (MemberRole memberRole : memberRoleList) {
                List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(memberRole.getRoleId());
                if (!CollectionUtils.isEmpty(labelDTOS)) {
                    Set<String> labelNames = labelDTOS.stream().map(Label::getName).collect(Collectors.toSet());
                    Set<String> roleLabels = userRoleLabelsMap.get(memberRole.getMemberId());
                    if (!CollectionUtils.isEmpty(roleLabels)) {
                        roleLabels.addAll(labelNames);
                    } else {
                        userRoleLabelsMap.put(memberRole.getMemberId(), labelNames);
                    }
                }
            }
            projectUserService.assignUsersProjectRolesEvent(memberRoleList.get(0).getSourceId(), ResourceLevel.ORGANIZATION, userRoleLabelsMap);
            // todo 发消息
        }
    }

    @Override
    public void revokeMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
            UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
            userMemberEventMsg.setResourceId(memberRoleList.get(0).getSourceId());
            userMemberEventMsg.setResourceType(ResourceLevel.PROJECT.value());
            User user = userAssertHelper.userNotExisted(DetailsHelper.getUserDetails().getUserId());
            userMemberEventMsg.setUsername(user.getLoginName());
            userMemberEventMsg.setUserId(user.getId());
            roleMemberService.deleteMemberRoleForSaga(user.getId(), userMemberEventPayloads, ResourceLevel.ORGANIZATION, memberRoleList.get(0).getSourceId());
            // todo 消息
        }
    }

    private Boolean isHzeroMemberRole(List<MemberRole> memberRoleList) {
        Map<String, Object> objectMap = memberRoleList.get(0).getAdditionalParams();
        return (CollectionUtils.isEmpty(objectMap)
                || ObjectUtils.isEmpty(objectMap.get(MemberRoleConstants.MEMBER_TYPE))
                || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(objectMap.get(MemberRoleConstants.MEMBER_TYPE)))
                && memberRoleList.get(0).getSourceType().contains(ResourceLevel.ORGANIZATION.value());
    }
}
