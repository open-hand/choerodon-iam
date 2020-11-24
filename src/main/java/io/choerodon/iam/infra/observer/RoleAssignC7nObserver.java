package io.choerodon.iam.infra.observer;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.service.role.observer.RoleAssignObserver;
import org.hzero.iam.infra.mapper.RoleMapper;
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
import io.choerodon.iam.infra.mapper.RoleC7nMapper;

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
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleC7nMapper roleC7nMapper;

    @Override
    public void assignMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            if (!CollectionUtils.isEmpty(memberRoleList)) {
                Map<Long, List<MemberRole>> sourceMemberMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getSourceId));
                sourceMemberMap.forEach((sourceId, sourceMemberList) -> {
                    List<MemberRole> oldMemberRoleList = sourceMemberList.stream().filter(t -> (t.getAdditionalParams() != null && t.getAdditionalParams().containsKey(MemberRoleConstants.MEMBER_OLD_ROLE))).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(oldMemberRoleList) || oldMemberRoleList.size() != sourceMemberList.size()) {
                        Map<Long, Set<String>> userRoleLabelsMap = getUserRoleLabelsMap(sourceMemberList);
                        Map<Long, Set<String>> userOldRoleLabelsMap = getUserRoleLabelsMap(oldMemberRoleList);
                        assignUsersRolesEvent(sourceId, ResourceLevel.ORGANIZATION, userRoleLabelsMap, userOldRoleLabelsMap);
                    }
                });
            }
        }
    }

    @Override
    public void revokeMemberRole(List<MemberRole> memberRoleList) {
        if (isHzeroMemberRole(memberRoleList)) {
            memberRoleList.forEach(t -> {
                Role role = roleMapper.selectByPrimaryKey(t.getRoleId());
                t.setSourceId(role.getTenantId());
            });
            Map<Long, List<MemberRole>> sourceMemberMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getSourceId));
            sourceMemberMap.forEach((sourceId, sourceMemberList) -> {
                Map<Long, Set<String>> userRoleLabelsMap = getUserRoleLabelsMap(sourceMemberList);
                userRoleLabelsMap.forEach((userId, labels) -> {
                    List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
                    UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
                    userMemberEventMsg.setResourceId(sourceId);
                    userMemberEventMsg.setResourceType(ResourceLevel.ORGANIZATION.value());
                    userMemberEventMsg.setUserId(userId);
                    userMemberEventMsg.setRoleLabels(labels);
                    userMemberEventPayloads.add(userMemberEventMsg);
                    roleMemberService.deleteMemberRoleForSaga(userId, userMemberEventPayloads, ResourceLevel.ORGANIZATION, sourceId);
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
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return null;
        }
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

    private void assignUsersRolesEvent(Long sourceId, ResourceLevel level, Map<Long, Set<String>> userRoleLabelsMap, Map<Long, Set<String>> userOldRoleLabelsMap) {
        if (!CollectionUtils.isEmpty(userRoleLabelsMap)) {
            userRoleLabelsMap.forEach((k, v) -> {
                List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
                UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
                userMemberEventPayload.setUserId(k);
                userMemberEventPayload.setRoleLabels(v);
                if (!CollectionUtils.isEmpty(userOldRoleLabelsMap)) {
                    if (CollectionUtils.isEmpty(userOldRoleLabelsMap.get(k))) {
                        userMemberEventPayload.setPreviousRoleLabels(new HashSet<>());
                    } else {
                        userMemberEventPayload.setPreviousRoleLabels(userOldRoleLabelsMap.get(k));
                    }
                } else {
                    Set<String> oldLabels = roleC7nMapper.listLabelByTenantIdAndUserId(k, sourceId);
                    oldLabels.removeAll(v);
                    userMemberEventPayload.setPreviousRoleLabels(oldLabels);
                }
                userMemberEventPayload.setResourceId(sourceId);
                userMemberEventPayload.setResourceType(level.value());
                userMemberEventPayloads.add(userMemberEventPayload);
                roleMemberService.updateMemberRole(k, userMemberEventPayloads, level, sourceId);
            });
        }
    }

}
