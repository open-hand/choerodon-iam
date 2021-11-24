package io.choerodon.iam.infra.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.hzero.iam.domain.entity.TenantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;

public class TenantConfigConvertUtils {

    private static final Integer CONSTANT_ONE = 1;
    private static final String DEFAULT = "DEFAULT";
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantConfigConvertUtils.class);


    public static TenantConfigVO configDTOToVO(List<TenantConfig> configs) {
        TenantConfigVO tenantConfigVO = new TenantConfigVO();
        try {
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
                        tenantConfigVO.setRegister(Boolean.valueOf(t.getConfigValue()));
                        break;
                    case USER_ID:
                        tenantConfigVO.setUserId(Long.parseLong(t.getConfigValue()));
                        break;
                    case REMOTE_TOKEN_ENABLED:
                        tenantConfigVO.setRemoteTokenEnabled(Boolean.valueOf(t.getConfigValue()));
                        break;
                    case CATEGORY:
                        tenantConfigVO.setCategory(t.getConfigValue());
                        break;
                    case REMARK:
                        tenantConfigVO.setRemark(t.getConfigValue());
                        break;
                    case SUCCESS_MANAGER:
                        tenantConfigVO.setSuccessManager(TypeUtil.objToLong(t.getConfigValue()));
                        break;
                    case MARKETING_MANAGER:
                        tenantConfigVO.setMarketingManager(TypeUtil.objToLong(t.getConfigValue()));
                        break;
                    case VISITORS:
                        Long visitors = 0L;
                        if (!StringUtils.isEmpty(t.getConfigValue())) {
                            visitors = TypeUtil.objToLong(t.getConfigValue());
                        }
                        tenantConfigVO.setVisitors(visitors);
                        break;
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Compatible dirty data:{}", e.getMessage());
        }
        return tenantConfigVO;
    }
}
