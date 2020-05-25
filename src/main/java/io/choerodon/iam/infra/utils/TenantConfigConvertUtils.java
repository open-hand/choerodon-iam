package io.choerodon.iam.infra.utils;

import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import org.hzero.iam.domain.entity.TenantConfig;

import java.util.List;

public class TenantConfigConvertUtils {
    public static TenantConfigVO configDTOToVO(List<TenantConfig> configs) {
        TenantConfigVO tenantConfigVO = new TenantConfigVO();
        configs.forEach(t -> {
            switch (TenantConfigEnum.forValue(t.getConfigKey())) {
                case ADDRESS:
                    tenantConfigVO.setAddress(t.getConfigValue());
                    break;
                case SCALE:
                    tenantConfigVO.setScale(t.getConfigValue());
                    break;
                case HOME_PAGE:
                    tenantConfigVO.setHomePage(t.getConfigValue());
                    break;
                case IMAGE_URL:
                    tenantConfigVO.setImageUrl(t.getConfigValue());
                    break;
                case BUSINESS_TYPE:
                    tenantConfigVO.setBusinessType(t.getConfigValue());
                    break;
                case EMAIL_SUFFIX:
                    tenantConfigVO.setEmailSuffix(t.getConfigValue());
                    break;
                case IS_REGISTER:
                    tenantConfigVO.setRegister(Boolean.getBoolean(t.getConfigValue()));
                    break;
                case USER_ID:
                    tenantConfigVO.setUserId(Long.parseLong(t.getConfigValue()));
                    break;
            }
        });
        return tenantConfigVO;
    }
}
