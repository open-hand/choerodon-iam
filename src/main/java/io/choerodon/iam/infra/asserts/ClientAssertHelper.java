package io.choerodon.iam.infra.asserts;

import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.infra.mapper.ClientMapper;
import org.springframework.stereotype.Component;


/**
 * 客户端断言帮助类
 *
 * @author superlee
 * @since 2019-07-10
 */
@Component
public class ClientAssertHelper extends AssertHelper {

    private ClientMapper clientMapper;

    public ClientAssertHelper(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    public Client clientNotExisted(Long id) {
        return clientNotExisted(id, "error.client.not.existed");
    }

    public Client clientNotExisted(Long id, String message) {
        Client dto = clientMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new NotExistedException(message);
        }
        return dto;
    }

    public Client clientNotExisted(String name) {
        return clientNotExisted(name, "error.client.not.existed");
    }

    public Client clientNotExisted(String name, String message) {
        Client dto = new Client();
        dto.setName(name);
        Client result = clientMapper.selectOne(dto);
        if (result == null) {
            throw new NotExistedException(message);
        }
        return result;
    }
}
