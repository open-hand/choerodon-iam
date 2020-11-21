package io.choerodon.iam.app.service.impl;

import java.util.HashMap;
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
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(MemberRoleConstants.MEMBER_OLD_ROLE, MemberRoleConstants.MEMBER_OLD_ROLE);
        for (Long memberId : listMap.keySet()) {
            List<Long> oldTenantRoleIds = memberRoleC7nMapper.listRoleByUserIdAndLevel(memberId, ResourceLevel.ORGANIZATION.value()).stream().map(Role::getId).collect(Collectors.toList());
            for (MemberRole memberRole : listMap.get(memberId)) {
                if (oldTenantRoleIds.contains(memberRole.getRoleId())) {
                    memberRole.getAdditionalParams().putAll(additionalParams);
                    continue;
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
     * 重写批量移除角色
     * 不能在hzero界面移除项目层角色
     *
     * @param memberRoleList
     * @param checkAuth
     */
    @Override
    public void revokeMemberRole(List<MemberRole> memberRoleList, boolean checkAuth) {
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return;
        }
        for (MemberRole memberRole : memberRoleList) {
            List<String> labelList = roleC7nMapper.listRoleLabels(memberRole.getRoleId()).stream().map(Label::getName).collect(Collectors.toList());
            if (labelList.contains(RoleLabelEnum.PROJECT_ROLE.value())
                    && (CollectionUtils.isEmpty(memberRole.getAdditionalParams())
                    || ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE))
                    || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE)))) {
                throw new CommonException("error.role.type");
            }
        }
        super.revokeMemberRole(memberRoleList, checkAuth);
    }

}
