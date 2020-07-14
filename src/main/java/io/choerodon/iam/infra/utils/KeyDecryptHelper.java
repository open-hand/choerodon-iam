package io.choerodon.iam.infra.utils;

import com.google.common.base.Joiner;
import io.choerodon.core.convertor.ApplicationContextHelper;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.springframework.util.Assert;

import static org.hzero.core.util.StringPool.EMPTY;

/**
 * @author zmf
 * @since 2020/7/9
 */
public final class KeyDecryptHelper {

    public static final String COMMA = ",";

    private KeyDecryptHelper() {
    }

    /**
     * 用于解密逗号分隔的加密id (用于加密时未提供key的解密过程)
     *
     * @param commaSeparatedEncryptedIds 逗号分隔的加密id
     * @return 逗号分隔的原值id
     */
    public static String decryptCommaSeparatedIds(String commaSeparatedEncryptedIds) {
        IEncryptionService iEncryptionService = ApplicationContextHelper.getContext().getBean(IEncryptionService.class);
        Assert.notNull(commaSeparatedEncryptedIds, "ids should not be null");
        String[] encryptedIds = commaSeparatedEncryptedIds.split(COMMA);
        for (int i = 0; i < encryptedIds.length; i++) {
            encryptedIds[i] = iEncryptionService.decrypt(encryptedIds[i], EMPTY);
        }
        return Joiner.on(COMMA).join(encryptedIds);
    }

    public static Long decryptId(String encryptId) {
        IEncryptionService iEncryptionService = ApplicationContextHelper.getContext().getBean(IEncryptionService.class);
        Assert.notNull(encryptId, "ids should not be null");
        return Long.valueOf(iEncryptionService.decrypt(encryptId, EMPTY));
    }
}
