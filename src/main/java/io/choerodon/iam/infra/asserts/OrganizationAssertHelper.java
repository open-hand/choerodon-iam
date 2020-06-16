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
    // TODO 等待hzero添加字段，再实现这些方法
//    public Tenant organizationNotExisted(WhichColumn whichColumn, String value) {
//        return organizationNotExisted(whichColumn, value, "error.organization.not.exist");
//    }

//    public Tenant organizationNotExisted(WhichColumn whichColumn, String value, String message) {
//        switch (whichColumn) {
//            case CODE:
//                return codeNotExisted(value, message);
//            case EMAIL_SUFFIX:
//                return emailSuffixNotExisted(value, message);
//            default:
//                throw new IllegalArgumentException("error.organization.assert.illegal.param");
//        }
//    }

//    private Tenant emailSuffixNotExisted(String value, String message) {
//        Tenant dto = new Tenant();
//        dto.setEmailSuffix(value);
//        return Optional.ofNullable(tenantMapper.selectOne(dto)).orElseThrow(() -> new CommonException(message));
//    }


    private Tenant numNotExisted(String num, String message) {
        Tenant dto = new Tenant();
        dto.setTenantNum(num);
        return Optional.ofNullable(tenantMapper.selectOne(dto)).orElseThrow(() -> new CommonException(message));
    }

    public void numExisted(String num) {
        Tenant tenant = new Tenant();
        tenant.setTenantNum(num);
        if (tenantMapper.selectOne(tenant) != null) {
            throw new AlreadyExistedException("error.organization.code.duplicate");
        }
    }

    public enum WhichColumn {
        CODE, EMAIL_SUFFIX
    }
}
