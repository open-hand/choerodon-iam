package io.choerodon.iam.infra.utils;


import io.choerodon.iam.api.vo.TenantConfigVO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import org.hzero.iam.domain.entity.TenantConfig;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * User: Mr.Wang
 * Date: 2020/5/26
 */
public class TenantUtils {
    private static final Integer CONSTANT_ONE = 1;
    private static final String DEFAULT = "DEFAULT";

    public static TenantConfigVO tenantConfigListVOToTenantConfigVO(List<TenantConfig> configList) {
        TenantConfigVO tenantConfigVO = new TenantConfigVO();
        if (!CollectionUtils.isEmpty(configList)) {
            configList.forEach(tenantConfig -> {
                if (TenantConfigEnum.USER_ID.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setUserId(Long.valueOf(tenantConfig.getConfigValue()));
                }
                if (TenantConfigEnum.REMOTE_TOKEN_ENABLED.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setRemoteTokenEnabled(tenantConfig.getConfigValue().equals(String.valueOf(CONSTANT_ONE)) ? true : false);
                }
                if (TenantConfigEnum.CATEGORY.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setCategory(tenantConfig.getConfigValue());
                }
                if (TenantConfigEnum.HOME_PAGE.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setHomePage(tenantConfig.getConfigValue());
                }
                if (TenantConfigEnum.ADDRESS.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setAddress(tenantConfig.getConfigValue());
                }
                if (TenantConfigEnum.IS_REGISTER.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setRegister(tenantConfig.getConfigValue().equals(String.valueOf(CONSTANT_ONE)) ? true : false);
                }
                if (TenantConfigEnum.BUSINESS_TYPE.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setBusinessType(tenantConfig.getConfigValue());
                }
                if (TenantConfigEnum.SCALE.value().equals(tenantConfig.getConfigValue())) {
                    tenantConfigVO.setScale(tenantConfig.getConfigValue());
                }
                if (TenantConfigEnum.EMAIL_SUFFIX.value().equals(tenantConfig.getConfigKey())) {
                    tenantConfigVO.setEmailSuffix(tenantConfig.getConfigValue());
                }
            });
            return tenantConfigVO;
        }
        return null;
    }

    public static List<TenantConfig> tenantConfigVOToTenantConfigList(Long tenantId, TenantConfigVO tenantConfigVO) {
        if (Objects.isNull(tenantConfigVO)) {
            return Collections.emptyList();
        }
        List<TenantConfig> tenantConfigs = new ArrayList<>();
        if (!Objects.isNull(tenantConfigVO.getAddress())) {
            TenantConfig addres = new TenantConfig();
            addres.setConfigKey(TenantConfigEnum.ADDRESS.value());
            addres.setConfigValue(tenantConfigVO.getAddress());
            addres.setTenantId(tenantId);
            tenantConfigs.add(addres);
        }
        if (!Objects.isNull(tenantConfigVO.getHomePage())) {
            TenantConfig homePage = new TenantConfig();
            homePage.setConfigKey(TenantConfigEnum.HOME_PAGE.value());
            homePage.setConfigValue(tenantConfigVO.getHomePage());
            homePage.setTenantId(tenantId);
            tenantConfigs.add(homePage);
        }
        if (!Objects.isNull(tenantConfigVO.getUserId())) {
            TenantConfig userId = new TenantConfig();
            userId.setConfigKey(TenantConfigEnum.USER_ID.value());
            userId.setConfigValue(String.valueOf(tenantConfigVO.getUserId()));
            userId.setTenantId(tenantId);
            tenantConfigs.add(userId);
        }
        if (!Objects.isNull(tenantConfigVO.getEmailSuffix())) {
            TenantConfig emailSuffix = new TenantConfig();
            emailSuffix.setConfigKey(TenantConfigEnum.EMAIL_SUFFIX.value());
            emailSuffix.setConfigValue(String.valueOf(tenantConfigVO.getEmailSuffix()));
            emailSuffix.setTenantId(tenantId);
            tenantConfigs.add(emailSuffix);
        }
        if (!Objects.isNull(tenantConfigVO.getBusinessType())) {
            TenantConfig businessType = new TenantConfig();
            businessType.setConfigKey(TenantConfigEnum.BUSINESS_TYPE.value());
            businessType.setConfigValue(String.valueOf(tenantConfigVO.getBusinessType()));
            businessType.setTenantId(tenantId);
            tenantConfigs.add(businessType);
        }
        if (!Objects.isNull(tenantConfigVO.getScale())) {
            TenantConfig scale = new TenantConfig();
            scale.setConfigKey(TenantConfigEnum.SCALE.value());
            scale.setConfigValue(String.valueOf(tenantConfigVO.getScale()));
            scale.setTenantId(tenantId);
            tenantConfigs.add(scale);
        }
        if (!Objects.isNull(tenantConfigVO.getRegister())){
            TenantConfig register = new TenantConfig();
            register.setConfigKey(TenantConfigEnum.IS_REGISTER.value());
            register.setConfigValue(String.valueOf(0));
            register.setTenantId(tenantId);
            tenantConfigs.add(register);
        }
        if (!Objects.isNull(tenantConfigVO.getCategory())){
            TenantConfig category = new TenantConfig();
            category.setConfigKey(TenantConfigEnum.CATEGORY.value());
            category.setConfigValue(DEFAULT);
            category.setTenantId(tenantId);
            tenantConfigs.add(category);
        }

        if (!Objects.isNull(tenantConfigVO.getCategory())){
            TenantConfig token = new TenantConfig();
            token.setConfigKey(TenantConfigEnum.REMOTE_TOKEN_ENABLED.value());
            token.setConfigValue(String.valueOf(CONSTANT_ONE));
            token.setTenantId(tenantId);
            tenantConfigs.add(token);
        }
        return tenantConfigs;
    }
}
