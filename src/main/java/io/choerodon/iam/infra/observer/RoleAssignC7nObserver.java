package io.choerodon.iam.infra.observer;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.service.role.observer.RoleAssignObserver;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.MessageSendService;
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
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private LabelC7nMapper labelC7nMapper;
    @Autowired
    @Lazy
    private RoleMemberService roleMemberService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MessageSendService messageSendService;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public void assignMemberRole(List<MemberRole> memberRoleList) {
        Long operatorId = DetailsHelper.getUserDetails() == null ? 0L : DetailsHelper.getUserDetails().getUserId();
        if (isHzeroMemberRole(memberRoleList)) {
            if (!CollectionUtils.isEmpty(memberRoleList)) {
                Map<Long, List<MemberRole>> sourceMemberMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getSourceId));
                sourceMemberMap.forEach((sourceId, sourceMemberList) -> {
                    Map<Long, Set<String>> userRoleLabelsMap = getUserRoleLabelsMap(sourceMemberList);
                    Map<Long, Set<String>> userOldRoleLabelsMap = getUserOldRoleLabelsMap(sourceMemberList);
                    assignUsersRolesEvent(sourceId, ResourceLevel.ORGANIZATION, userRoleLabelsMap, userOldRoleLabelsMap);
                    sendTenantAssignMessage(sourceId, sourceMemberList, operatorId);
                });
            }
        } else if (isHzeroMemberSiteRole(memberRoleList)) {
            sendSiteAssignMessage(memberRoleList);
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
                && (memberRoleList.get(0).getMemberType() == null || memberRoleList.get(0).getMemberType().equals(MemberType.USER.value()))
                && (StringUtils.isEmpty(memberRoleList.get(0).getSourceType()) || memberRoleList.get(0).getSourceType().contains(ResourceLevel.ORGANIZATION.value()));
    }

    private Boolean isHzeroMemberSiteRole(List<MemberRole> memberRoleList) {
        Map<String, Object> objectMap = memberRoleList.get(0).getAdditionalParams();
        return (CollectionUtils.isEmpty(objectMap)
                || ObjectUtils.isEmpty(objectMap.get(MemberRoleConstants.MEMBER_TYPE))
                || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(objectMap.get(MemberRoleConstants.MEMBER_TYPE)))
                && (memberRoleList.get(0).getMemberType() == null || memberRoleList.get(0).getMemberType().equals(MemberType.USER.value()))
                && (memberRoleList.get(0).getSourceType().contains(ResourceLevel.SITE.value()));
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

    private Map<Long, Set<String>> getUserOldRoleLabelsMap(List<MemberRole> memberRoleList) {
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return new HashMap<>();
        }
        Map<Long, Set<String>> userRoleLabelsMap = new HashMap<>();
        for (MemberRole memberRole : memberRoleList) {
            if (!ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_LABEL))) {
                userRoleLabelsMap.put(memberRole.getMemberId(), castSet(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_LABEL), String.class));
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
                if (!CollectionUtils.isEmpty(userOldRoleLabelsMap.get(k))) {
                    userMemberEventPayload.setPreviousRoleLabels(userOldRoleLabelsMap.get(k));
                } else {
                    userMemberEventPayload.setPreviousRoleLabels(new HashSet<>());
                }
                userMemberEventPayload.setResourceId(sourceId);
                userMemberEventPayload.setResourceType(level.value());
                userMemberEventPayloads.add(userMemberEventPayload);
                roleMemberService.updateMemberRole(k, userMemberEventPayloads, level, sourceId);
            });
        }
    }

    private void sendTenantAssignMessage(Long sourceId, List<MemberRole> sourceMemberList, Long operatorId) {
        try {
            Tenant tenant = tenantMapper.selectByPrimaryKey(sourceId);
            sourceMemberList.forEach(memberRole -> {
                if (!ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_ID))) {
                    ArrayList<Long> oldRoleIds = castList(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_ID), Long.class);
                    if (!oldRoleIds.contains(memberRole.getRole().getId())) {
                        User user = userMapper.selectByPrimaryKey(memberRole.getMemberId());
                        ArrayList<User> list = new ArrayList<>();
                        list.add(user);
                        messageSendService.sendAddMemberMsg(tenant, memberRole.getRole().getName(), list, operatorId);
                    }
                } else {
                    User user = userMapper.selectByPrimaryKey(memberRole.getMemberId());
                    ArrayList<User> list = new ArrayList<>();
                    list.add(user);
                    messageSendService.sendAddMemberMsg(tenant, memberRole.getRole().getName(), list, operatorId);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to send message while hzero tenant assign role : ", e);
        }
    }

    private void sendSiteAssignMessage(List<MemberRole> memberRoleList) {
        try {
            memberRoleList.forEach(memberRole -> {
                if (!ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_ID))) {
                    List<Long> oldRoleIds = castList(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_OLD_ROLE_ID), Long.class);
                    if (!oldRoleIds.contains(memberRole.getRole().getId())) {
                        User user = userMapper.selectByPrimaryKey(memberRole.getMemberId());
                        messageSendService.sendSiteAddUserMsg(user, memberRole.getRole().getName());
                    }
                } else {
                    User user = userMapper.selectByPrimaryKey(memberRole.getMemberId());
                    messageSendService.sendSiteAddUserMsg(user, memberRole.getRole().getName());
                }
            });
        } catch (Exception e) {
            logger.error("Failed to send message while hzero site assign role : ", e);
        }
    }

    private <T> Set<T> castSet(Object obj, Class<T> clazz) {
        HashSet<T> result = new HashSet<T>();
        if (obj instanceof HashSet<?>) {
            for (Object o : (HashSet<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    private <T> ArrayList<T> castList(Object obj, Class<T> clazz) {
        ArrayList<T> result = new ArrayList<T>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (ArrayList<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

}
