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
        if (!Objects.isNull(tenantConfigVO.getRegister())) {
            TenantConfig register = new TenantConfig();
            register.setConfigKey(TenantConfigEnum.IS_REGISTER.value());
            register.setConfigValue(String.valueOf(0));
            register.setTenantId(tenantId);
            tenantConfigs.add(register);
        }
        if (!Objects.isNull(tenantConfigVO.getCategory())) {
            TenantConfig category = new TenantConfig();
            category.setConfigKey(TenantConfigEnum.CATEGORY.value());
            category.setConfigValue(DEFAULT);
            category.setTenantId(tenantId);
            tenantConfigs.add(category);
        }

        if (!Objects.isNull(tenantConfigVO.getCategory())) {
            TenantConfig token = new TenantConfig();
            token.setConfigKey(TenantConfigEnum.REMOTE_TOKEN_ENABLED.value());
            token.setConfigValue(String.valueOf(CONSTANT_ONE));
            token.setTenantId(tenantId);
            tenantConfigs.add(token);
        }
        if (!Objects.isNull(tenantConfigVO.getImageUrl())) {
            TenantConfig image = new TenantConfig();
            image.setConfigKey(TenantConfigEnum.IMAGE_URL.value());
            image.setConfigValue(tenantConfigVO.getImageUrl());
            image.setTenantId(tenantId);
            tenantConfigs.add(image);
        }
        if (!Objects.isNull(tenantConfigVO.getRemark())) {
            TenantConfig remark = new TenantConfig();
            remark.setConfigKey(TenantConfigEnum.REMARK.value());
            remark.setConfigValue(tenantConfigVO.getRemark());
            remark.setTenantId(tenantId);
            tenantConfigs.add(remark);
        }
        return tenantConfigs;
    }
}
