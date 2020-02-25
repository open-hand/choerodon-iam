package io.choerodon.base.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.RoleAssignmentDeleteDTO;
import io.choerodon.base.api.validator.RoleAssignmentViewValidator;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
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

    private static final String BUSINESS_TYPE_CODE = "addMember";

    private UserMapper userMapper;

    private MemberRoleMapper memberRoleMapper;

    private RoleAssertHelper roleAssertHelper;

    private UserService userService;

    private OrganizationMapper organizationMapper;

    private RoleMemberService roleMemberService;

    private TransactionalProducer producer;

    private LabelMapper labelMapper;


    public OrgAdministratorServiceImpl(UserMapper userMapper,
                                       MemberRoleMapper memberRoleMapper,
                                       RoleAssertHelper roleAssertHelper,
                                       UserService userService,
                                       OrganizationMapper organizationMapper,
                                       RoleMemberService roleMemberService,
                                       TransactionalProducer producer,
                                       LabelMapper labelMapper) {
        this.userMapper = userMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.roleAssertHelper = roleAssertHelper;
        this.userService = userService;
        this.organizationMapper = organizationMapper;
        this.roleMemberService = roleMemberService;
        this.producer = producer;
        this.labelMapper = labelMapper;
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
        //查询用户的角色标签名称
        Set<String> lableNames = labelMapper.selectLableNameByUserId(userId).stream().map(labelDTO -> labelDTO.getName()).collect(Collectors.toSet());
        if (memberRoleMapper.delete(memberRoleDTO) != 1) {
            throw new CommonException("error.memberRole.delete");
        }
        //删除组织管理员成功后也要发saga删除gitlab相应的权限。
        roleMemberService.deleteOrgAdmin(organizationId, userId, Arrays.asList(memberRoleDTO), ResourceLevel.ORGANIZATION.value(), lableNames);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrgAdministrator(List<Long> userIds, Long organizationId) {
        RoleDTO roleDTO = roleAssertHelper.roleNotExisted(InitRoleCode.ORGANIZATION_ADMINISTRATOR);
        Long roleId = roleDTO.getId();
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(organizationId);
        Set<Long> notifyUserIds = new HashSet<>();
        Map<Long, List<MemberRoleDTO>> listMap = new HashMap<>();
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
            notifyUserIds.add(id);
            listMap.put(id, Arrays.asList(memberRoleDTO));
        });

        listMap.forEach((userId, memberRoleDTOS) -> {
            //添加组织管理员的角色后,用户需要有gitlab下三个组的owner权限
            roleMemberService.insertOrUpdateRolesOfUserByMemberId(false, organizationId, userId, memberRoleDTOS, ResourceLevel.ORGANIZATION.value());
        });
        //添加组织管理员发送消息通知被添加者,异步发送消息
        Map<String, Object> params = new HashMap<>();
        params.put("organizationName", organizationDTO.getName());
        params.put("roleName", roleDTO.getName());
        userService.sendNotice(customUserDetails.getUserId(), new ArrayList<>(notifyUserIds), BUSINESS_TYPE_CODE, params, organizationId);
        return true;
    }
}
