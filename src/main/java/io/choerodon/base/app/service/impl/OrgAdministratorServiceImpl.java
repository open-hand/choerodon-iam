package io.choerodon.base.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.base.api.vo.OrgAdministratorVO;
import io.choerodon.base.app.service.OrgAdministratorService;
import io.choerodon.base.infra.asserts.RoleAssertHelper;
import io.choerodon.base.infra.dto.MemberRoleDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.enums.MemberType;
import io.choerodon.base.infra.mapper.MemberRoleMapper;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.web.util.PageableHelper;

/**
 * @author jiameng.cao
 * @since 2019/8/1
 */
@Component
public class OrgAdministratorServiceImpl implements OrgAdministratorService {

    private UserMapper userMapper;

    private MemberRoleMapper memberRoleMapper;

    private RoleAssertHelper roleAssertHelper;


    public OrgAdministratorServiceImpl(UserMapper userMapper,
                                       MemberRoleMapper memberRoleMapper,
                                       RoleAssertHelper roleAssertHelper) {
        this.userMapper = userMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.roleAssertHelper = roleAssertHelper;
    }

    @Override
    public PageInfo<OrgAdministratorVO> pagingQueryOrgAdministrator(Pageable pageable, Long organizationId,
                                                                    String realName, String loginName, String params) {
        PageInfo<UserDTO> userDTOPageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).
                doSelectPageInfo(() -> userMapper.listOrgAdministrator(organizationId, realName, loginName, params));
        List<UserDTO> userDTOList = userDTOPageInfo.getList();
        List<OrgAdministratorVO> orgAdministratorVOS = new ArrayList<>();
        PageInfo<OrgAdministratorVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(userDTOPageInfo, pageInfo);
        if (!CollectionUtils.isEmpty(userDTOList)) {
            userDTOList.forEach(user -> {
                OrgAdministratorVO orgAdministratorVO = new OrgAdministratorVO();
                orgAdministratorVO.setEnabled(user.getEnabled());
                orgAdministratorVO.setLocked(user.getLocked());
                orgAdministratorVO.setUserName(user.getRealName());
                orgAdministratorVO.setId(user.getId());
                orgAdministratorVO.setLoginName(user.getLoginName());
                orgAdministratorVO.setCreationDate(user.getCreationDate());
                orgAdministratorVO.setExternalUser(!organizationId.equals(user.getOrganizationId()));
                orgAdministratorVOS.add(orgAdministratorVO);
            });
            pageInfo.setList(orgAdministratorVOS);
        }
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrgAdministrator(Long userId, Long organizationId) {
        RoleDTO roleDTO = roleAssertHelper.roleNotExisted(InitRoleCode.ORGANIZATION_ADMINISTRATOR);
        Long roleId = roleDTO.getId();
        MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
        memberRoleDTO.setRoleId(roleId);
        memberRoleDTO.setMemberId(userId);
        memberRoleDTO.setMemberType(MemberType.USER.value());
        memberRoleDTO.setSourceId(organizationId);
        memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
        if (CollectionUtils.isEmpty(memberRoleMapper.select(memberRoleDTO))) {
            throw new CommonException("error.memberRole.not.exist", roleId, userId);
        }
        if (memberRoleMapper.delete(memberRoleDTO) != 1) {
            throw new CommonException("error.memberRole.delete");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrgAdministrator(List<Long> userIds, Long organizationId) {
        RoleDTO roleDTO = roleAssertHelper.roleNotExisted(InitRoleCode.ORGANIZATION_ADMINISTRATOR);
        Long roleId = roleDTO.getId();
        userIds.forEach(id -> {
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            memberRoleDTO.setRoleId(roleId);
            memberRoleDTO.setMemberId(id);
            memberRoleDTO.setMemberType(MemberType.USER.value());
            memberRoleDTO.setSourceId(organizationId);
            memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
            // 如果用户已被分配组织管理员角色 直接跳过
            if (!CollectionUtils.isEmpty(memberRoleMapper.select(memberRoleDTO))) {
                return;
            }
            if (memberRoleMapper.insert(memberRoleDTO) != 1) {
                throw new CommonException("error.memberRole.create");
            }
        });
        return true;
    }
}
