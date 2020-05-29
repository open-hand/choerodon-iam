package io.choerodon.iam.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.infra.asserts.ClientAssertHelper;
import io.choerodon.iam.infra.mapper.ClientC7nMapper;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.iam.infra.utils.SagaTopic;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.page.PageMethod;

import org.apache.commons.lang.RandomStringUtils;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.hzero.iam.infra.mapper.MemberRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * @author scp
 * @date 2020/3/27
 * @description
 */
@Service
public class ClientC7nServiceImpl implements ClientC7nService {
    private static final String ORGANIZATION_ID_NOT_EQUAL_EXCEPTION = "error.organizationId.not.same";

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientC7nMapper clientC7nMapper;
    @Autowired
    private ClientAssertHelper clientAssertHelper;
    @Autowired
    private MemberRoleMapper memberRoleMapper;
    @Autowired
    private MemberRoleService memberRoleService;


    @Override
    public Client getDefaultCreateData(Long orgId) {
        Client client = new Client();
        client.setName(generateUniqueName());
        client.setSecret(RandomStringUtils.randomAlphanumeric(16));
        return client;
    }

    @Override
    public Client queryByName(Long orgId, String clientName) {
        Client dto = clientAssertHelper.clientNotExisted(clientName);
        if (!orgId.equals(dto.getOrganizationId())) {
            throw new CommonException(ORGANIZATION_ID_NOT_EQUAL_EXCEPTION);
        }
        return dto;
    }

    @Override
    public Page<Client> pagingQueryUsersByRoleId(PageRequest pageRequest, ResourceLevel resourceType, Long sourceId, ClientRoleQueryVO clientRoleQueryVO, Long roleId) {
        String param = Optional.ofNullable(clientRoleQueryVO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        return PageHelper.doPage(pageRequest,() -> clientC7nMapper.selectClientsByRoleIdAndOptions(roleId, sourceId, resourceType.value(), clientRoleQueryVO, param));
    }

    @Override
    public void assignRoles(Long organizationId, Long clientId, List<Long> newRoleIds) {
        MemberRole queryDTO=new MemberRole();
        queryDTO.setSourceType(Constants.MemberType.CLIENT);
        queryDTO.setSourceId(clientId);
        List<Long> existingRoleIds =memberRoleMapper.select(queryDTO).stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        //交集，传入的roleId与数据库里存在的roleId相交
        List<Long> intersection = existingRoleIds.stream().filter(newRoleIds::contains).collect(Collectors.toList());
        //传入的roleId与交集的差集为要插入的roleId
        List<Long> insertList = newRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        //数据库存在的roleId与交集的差集为要删除的roleId
        List<Long> deleteList = existingRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());

        List<MemberRole> addMemberRoles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(insertList)) {
            insertList.forEach(t -> addMemberRoles.add(getMemberRole(clientId,t,organizationId)));
            memberRoleService.batchAssignMemberRoleInternal(addMemberRoles);
        }
        List<MemberRole> delMemberRoles = new ArrayList<>();

        if (!CollectionUtils.isEmpty(deleteList)) {
            deleteList.forEach(t -> delMemberRoles.add(getMemberRole(clientId,t,organizationId)));
            memberRoleService.batchDeleteMemberRole(organizationId,delMemberRoles);
        }
    }

    private MemberRole getMemberRole(Long clientId, Long roleId, Long organizationId) {
        MemberRole memberRole = new MemberRole();
        memberRole.setMemberId(clientId);
        memberRole.setMemberType(Constants.MemberType.CLIENT);
        memberRole.setRoleId(roleId);
        memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
        memberRole.setSourceId(organizationId);
        return memberRole;
    }

    private String generateUniqueName() {
        String uniqueName;
        Client dto = new Client();
        while (true) {
            uniqueName = RandomStringUtils.randomAlphanumeric(12);
            dto.setName(uniqueName);
            if (clientMapper.selectOne(dto) == null) {
                break;
            }
        }
        return uniqueName;
    }

}
