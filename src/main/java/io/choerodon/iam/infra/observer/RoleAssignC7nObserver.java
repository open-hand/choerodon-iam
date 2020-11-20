package io.choerodon.iam.infra.observer;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.service.role.observer.RoleAssignObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.dto.LabelDTO;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;

/**
 * @author scp
 * @since 2020/5/25
 */
@Component
public class RoleAssignC7nObserver implements RoleAssignObserver {
    @Autowired
    private LabelC7nMapper labelC7nMapper;
    @Autowired
    @Lazy
    private ProjectUserService projectUserService;
    @Autowired
    @Lazy
    private RoleMemberService roleMemberService;

    @Override
    public void assignMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            Map<Long, List<MemberRole>> userMemberMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
            userMemberMap.forEach((userId, userMemberList) -> {
                Map<Long, List<MemberRole>> sourceMemberMap = userMemberList.stream().collect(Collectors.groupingBy(MemberRole::getSourceId));
                sourceMemberMap.forEach((sourceId, sourceMemberList) -> {
                    Map<Long, Set<String>> userRoleLabelsMap = getUserRoleLabelsMap(sourceMemberList);
                    projectUserService.assignUsersProjectRolesEvent(sourceId, ResourceLevel.ORGANIZATION, userRoleLabelsMap);
                });
            });
        }
    }

    @Override
    public void revokeMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            Map<Long, List<MemberRole>> userMemberMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
            userMemberMap.forEach((userId, userMemberList) -> {
                Map<Long, List<MemberRole>> sourceMemberMap = userMemberList.stream().collect(Collectors.groupingBy(MemberRole::getSourceId));
                sourceMemberMap.forEach((sourceId, sourceMemberList) -> {
                    Map<Long, Set<String>> userRoleLabelsMap = getUserRoleLabelsMap(sourceMemberList);
                    userRoleLabelsMap.forEach((uid, labels) -> {
                        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
                        UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
                        userMemberEventMsg.setResourceId(memberRoleList.get(0).getSourceId());
                        userMemberEventMsg.setResourceType(ResourceLevel.ORGANIZATION.value());
                        userMemberEventMsg.setUserId(uid);
                        userMemberEventMsg.setRoleLabels(labels);
                        userMemberEventPayloads.add(userMemberEventMsg);
                        roleMemberService.deleteMemberRoleForSaga(uid, userMemberEventPayloads, ResourceLevel.ORGANIZATION, sourceId);
                    });
                });
            });

        }
    }

    private Boolean isHzeroMemberRole(List<MemberRole> memberRoleList) {
        Map<String, Object> objectMap = memberRoleList.get(0).getAdditionalParams();
        return (CollectionUtils.isEmpty(objectMap)
                || ObjectUtils.isEmpty(objectMap.get(MemberRoleConstants.MEMBER_TYPE))
                || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(objectMap.get(MemberRoleConstants.MEMBER_TYPE)))
                && (memberRoleList.get(0).getMemberType().equals(MemberType.USER.value()))
                && (StringUtils.isEmpty(memberRoleList.get(0).getSourceType()) || memberRoleList.get(0).getSourceType().contains(ResourceLevel.ORGANIZATION.value()));
    }

    private Map<Long, Set<String>> getUserRoleLabelsMap(List<MemberRole> memberRoleList) {
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
        return userRoleLabelsMap;
    }
}
