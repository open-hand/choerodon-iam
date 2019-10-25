package io.choerodon.base.infra.asserts;

import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 组织断言帮助类
 *
 * @author superlee
 * @since 2019-05-13
 */
@Component
public class OrganizationAssertHelper extends AssertHelper {

    private OrganizationMapper organizationMapper;

    public OrganizationAssertHelper(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    public OrganizationDTO notExisted(Long id) {
        return organizationNotExisted(id, "error.organization.not.exist");
    }

    public OrganizationDTO organizationNotExisted(Long id, String message) {
        return Optional.ofNullable(organizationMapper.selectByPrimaryKey(id)).orElseThrow(() -> new CommonException(message, id));
    }

    public OrganizationDTO organizationNotExisted(WhichColumn whichColumn, String value) {
        return organizationNotExisted(whichColumn, value, "error.organization.not.exist");
    }

    public OrganizationDTO organizationNotExisted(WhichColumn whichColumn, String value, String message) {
        switch (whichColumn) {
            case CODE:
                return codeNotExisted(value, message);
            case EMAIL_SUFFIX:
                return emailSuffixNotExisted(value, message);
            default:
                throw new IllegalArgumentException("error.organization.assert.illegal.param");
        }
    }

    private OrganizationDTO emailSuffixNotExisted(String value, String message) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setEmailSuffix(value);
        return Optional.ofNullable(organizationMapper.selectOne(dto)).orElseThrow(() -> new CommonException(message));
    }


    private OrganizationDTO codeNotExisted(String code, String message) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setCode(code);
        return Optional.ofNullable(organizationMapper.selectOne(dto)).orElseThrow(() -> new CommonException(message));
    }

    public void codeExisted(String code) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setCode(code);
        if (organizationMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.organization.code.duplicate");
        }
    }

    public enum WhichColumn {
        CODE, EMAIL_SUFFIX
    }
}
