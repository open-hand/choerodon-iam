package io.choerodon.iam.app.service.impl;

import java.util.Optional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.iam.app.service.ClientC7nService;
import io.choerodon.iam.infra.asserts.ClientAssertHelper;
import io.choerodon.iam.infra.mapper.ClientC7nMapper;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.page.PageMethod;

import org.apache.commons.lang.RandomStringUtils;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
