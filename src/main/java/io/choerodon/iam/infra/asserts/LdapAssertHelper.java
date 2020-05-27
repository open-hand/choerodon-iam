package io.choerodon.iam.infra.asserts;

import io.choerodon.core.exception.ext.IllegalArgumentException;
import io.choerodon.core.exception.ext.NotExistedException;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.repository.LdapRepository;
import org.springframework.stereotype.Component;

/**
 * ldap断言帮助类
 *
 * @author superlee
 * @since 2019-07-11
 */
@Component
public class LdapAssertHelper extends AssertHelper {

    private LdapRepository ldapRepository;

    public LdapAssertHelper(LdapRepository ldapRepository) {
        this.ldapRepository = ldapRepository;
    }

    public Ldap ldapNotExisted(WhichColumn whichColumn, Long id) {
        return ldapNotExisted(whichColumn, id, "error.ldap.not.exist");
    }

    public Ldap ldapNotExisted(WhichColumn whichColumn, Long id, String message) {
        switch (whichColumn) {
            case ID:
                return idNotExisted(id, message);
            case ORGANIZATION_ID:
                return organizationIdNotExisted(id, message);
            default:
                throw new IllegalArgumentException("error.illegal.whichColumn", whichColumn.value);
        }
    }

    private Ldap organizationIdNotExisted(Long id, String message) {
        Ldap dto = new Ldap();
        dto.setOrganizationId(id);
        Ldap result = ldapRepository.selectOne(dto);
        if (result == null) {
            throw new NotExistedException(message);
        }
        return result;
    }

    private Ldap idNotExisted(Long id, String message) {
        Ldap dto = ldapRepository.selectByPrimaryKey(id);
        if (dto == null) {
            throw new NotExistedException(message);
        }
        return dto;
    }

    public enum WhichColumn {
        /**
         * id
         */
        ID("id"),

        /**
         * organization_id
         */
        ORGANIZATION_ID("organization_id");

        private String value;

        WhichColumn(String value) {
            this.value = value;
        }
    }
}