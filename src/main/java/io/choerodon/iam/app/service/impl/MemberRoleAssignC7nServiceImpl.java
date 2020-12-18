package io.choerodon.iam.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.service.role.MemberRoleAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.enums.MemberType;
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
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return;
        }
        if (isHzeroMemberRole(memberRoleList)) {
            Map<Long, List<MemberRole>> listMap = memberRoleList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
            Map<String, Object> additionalParams = new HashMap<>();
            for (Long memberId : listMap.keySet()) {
                for (MemberRole memberRole : listMap.get(memberId)) {
                    Role role = roleRepository.selectByPrimaryKey(memberRole.getRoleId());
                    Set<String> previousRoleLabels = roleC7nMapper.listLabelByTenantIdAndUserId(memberId, role.getTenantId());
                    List<Long> oldTenantRoleIds = memberRoleC7nMapper.listRoleByUserIdAndTenantId(memberId, role.getTenantId()).stream().map(Role::getId).collect(Collectors.toList());
                    additionalParams.put(MemberRoleConstants.MEMBER_OLD_ROLE, previousRoleLabels);
                    if (memberRole.getAdditionalParams() == null) {
                        memberRole.setAdditionalParams(additionalParams);
                    } else {
                        memberRole.getAdditionalParams().putAll(additionalParams);
                    }
                    if (oldTenantRoleIds.contains(memberRole.getRoleId())) {
                        continue;
                    }
                    List<String> labelList = roleC7nMapper.listRoleLabels(memberRole.getRoleId()).stream().map(Label::getName).collect(Collectors.toList());
                    if (labelList.contains(RoleLabelEnum.PROJECT_ROLE.value())) {
                        throw new CommonException("error.role.type");
                    }
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
        if (isHzeroMemberRole(memberRoleList)) {
            for (MemberRole memberRole : memberRoleList) {
                List<String> labelList = roleC7nMapper.listRoleLabels(memberRole.getRoleId()).stream().map(Label::getName).collect(Collectors.toList());
                if (labelList.contains(RoleLabelEnum.PROJECT_ROLE.value())) {
                    throw new CommonException("error.role.type");
                }
            }
        }
        super.revokeMemberRole(memberRoleList, checkAuth);
    }


    private Boolean isHzeroMemberRole(List<MemberRole> memberRoleList) {
        Map<String, Object> objectMap = memberRoleList.get(0).getAdditionalParams();
        return (CollectionUtils.isEmpty(objectMap)
                || ObjectUtils.isEmpty(objectMap.get(MemberRoleConstants.MEMBER_TYPE))
                || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(objectMap.get(MemberRoleConstants.MEMBER_TYPE)))
                && (memberRoleList.get(0).getMemberType().equals(MemberType.USER.value()))
                && (StringUtils.isEmpty(memberRoleList.get(0).getSourceType()) || memberRoleList.get(0).getSourceType().contains(ResourceLevel.ORGANIZATION.value()));
    }
}
