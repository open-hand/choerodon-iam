package io.choerodon.iam.app.service.impl;

import java.util.List;
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
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;

/**
 * @author scp
 * @date 2020/5/25
 * @description
 */
@Component
public class MemberRoleAssignC7nServiceImpl extends MemberRoleAssignService {
    @Autowired
    private RoleC7nMapper roleC7nMapper;

    /**
     * 检查有效性
     * 添加不能直接在hzero 角色管理界面上分配项目层角色
     * @param memberRoleList 成员角色
     */
    protected void checkValidityAndInit(List<MemberRole> memberRoleList) {
        for (MemberRole memberRole : memberRoleList) {
            List<String> labelList = roleC7nMapper.listRoleLabels(memberRole.getRoleId()).stream().map(Label::getName).collect(Collectors.toList());
            if (labelList.contains(RoleLabelEnum.PROJECT_ROLE.value())
                    && (CollectionUtils.isEmpty(memberRole.getAdditionalParams())
                    || ObjectUtils.isEmpty(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE))
                    || !MemberRoleConstants.MEMBER_TYPE_CHOERODON.equals(memberRole.getAdditionalParams().get(MemberRoleConstants.MEMBER_TYPE)))) {
                throw new CommonException("error.role.type");
            }
        }
        super.checkValidityAndInit(memberRoleList);

    }
}
