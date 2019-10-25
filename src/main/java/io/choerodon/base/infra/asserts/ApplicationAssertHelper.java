package io.choerodon.base.infra.asserts;

import org.springframework.stereotype.Component;

import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.base.infra.mapper.ApplicationMapper;

/**
 * 项目断言帮助类
 *
 * @author superlee
 * @since 2019-05-13
 */
@Component
public class ApplicationAssertHelper extends AssertHelper {

    private ApplicationMapper applicationMapper;

    public ApplicationAssertHelper(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    public void codeExisted(String code, Long organizationId) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setCode(code);
//        dto.setOrganizationId(organizationId);todo
        if (applicationMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.application.code.existed");
        }
    }
}
