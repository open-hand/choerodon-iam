package io.choerodon.iam.infra.asserts;

import java.util.Optional;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;

/**
 * 组织断言帮助类
 *
 * @author superlee
 * @since 2019-05-13
 */
@Component
public class OrganizationAssertHelper extends AssertHelper {

    private TenantMapper tenantMapper;

    public OrganizationAssertHelper(TenantMapper tenantMapper) {
        this.tenantMapper = tenantMapper;
    }

    public Tenant notExisted(Long id) {
        return organizationNotExisted(id, "error.organization.not.exist");
    }

    public Tenant organizationNotExisted(Long id, String message) {
        return Optional.ofNullable(tenantMapper.selectByPrimaryKey(id)).orElseThrow(() -> new CommonException(message, id));
    }

}
