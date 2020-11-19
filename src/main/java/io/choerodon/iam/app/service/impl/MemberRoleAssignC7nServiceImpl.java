package io.choerodon.iam.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.service.role.MemberRoleAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.MemberRoleC7nMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;

/**
 * @author scp
 * @since 2020/5/25
 */
@Component
public class MemberRoleAssignC7nServiceImpl extends MemberRoleAssignService {
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private MemberRoleC7nMapper memberRoleC7nMapper;

    /**
     * 检查有效性
     * 添加不能直接在hzero 角色管理界面上分配项目层角色
     *
     * @param memberRoleList 成员角色
     */
    protected void checkValidityAndInit(List<MemberRole> memberRoleList) {
        super.checkValidityAndInit(memberRoleList);
        Map<Long, List<MemberRole>> listMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
        for (Long memberId : listMap.keySet()) {
            List<Long> oldTenantRoleIds = memberRoleC7nMapper.listRoleByUserIdAndLevel(memberId, ResourceLevel.ORGANIZATION.value()).stream().map(Role::getId).collect(Collectors.toList());
            for (MemberRole memberRole : listMap.get(memberId)) {
                if (oldTenantRoleIds.contains(memberRole.getRoleId())) {
                    return;
                }
                List<String> labelList = roleC7nMapper.listRoleLabels(memberRole.getRoleId()).stream().map(Label::getName).collect(Collectors.toList());
                if (labelList.contains(RoleLabelEnum.PROJECT_ROLE.value())
                        && (CollectionUtils.isEmpty(memberRole.getAdditionalParams())
                        || ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE))
                        || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE)))) {
                    throw new CommonException("error.role.type");
                }
            }
        }
    }

    /**
     *  原本用于hzero界面操作 同步gitlab角色
     *  但是已经有了 {@link RoleAssignC7nObserver}
     *  测试没有问题 在下个版本删除
     */
//    protected void saveMemberRole(List<MemberRole> memberRoleList) {
//        super.saveMemberRole(memberRoleList);
//        // hzero界面分配角色 同步gitlab角色
//        if (!CollectionUtils.isEmpty(memberRoleList)
//                && memberRoleList.get(0).getMemberType().equals(MemberType.USER.value())
//                && (CollectionUtils.isEmpty(memberRoleList.get(0).getAdditionalParams())
//                || ObjectUtils.isEmpty(memberRoleList.get(0).getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE))
//                || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(memberRoleList.get(0).getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE)))) {
//            Map<Long, List<MemberRole>> listMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
//            List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
//            Long organizationId = 0L;
//            for (Long memberId : listMap.keySet()) {
//                List<MemberRole> memberRoles = listMap.get(memberId);
//                if (!CollectionUtils.isEmpty(memberRoles)) {
//                    Set<Long> roleIds = memberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
//                    Set<String> labelNames = labelC7nMapper.selectLabelNamesInRoleIds(roleIds);
//                    if (!CollectionUtils.isEmpty(roleIds)
//                            && labelNames.contains(RoleLabelEnum.TENANT_ROLE.value())) {
//                        organizationId = memberRoles.get(0).getSourceId();
//                        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
//                        userMemberEventPayload.setUserId(memberId);
//                        userMemberEventPayload.setRoleLabels(labelNames);
//                        userMemberEventPayload.setResourceId(organizationId);
//                        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
//                        userMemberEventPayloads.add(userMemberEventPayload);
//                    }
//                }
//            }
//            if (!CollectionUtils.isEmpty(userMemberEventPayloads)) {
//                Long userId = 0L;
//                if (DetailsHelper.getUserDetails() != null) {
//                    userId = DetailsHelper.getUserDetails().getUserId();
//                }
//                roleMemberService.updateMemberRole(userId, userMemberEventPayloads, ResourceLevel.ORGANIZATION, organizationId);
//            }
//        }
//    }
}
