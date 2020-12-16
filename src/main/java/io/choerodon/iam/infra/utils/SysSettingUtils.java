package io.choerodon.iam.infra.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;

public class SysSettingUtils {
    private static final Integer DEFAULT_CLEAN_NUM = 180;


    /**
     * 系统配置列表转为系统配置Map
     *
     * @param settingDTOS 系统配置列表
     * @return 返回null或者系统配置Map
     */
    public static Map<String, String> listToMap(List<SysSettingDTO> settingDTOS) {
        if (CollectionUtils.isEmpty(settingDTOS)) {
            return null;
        }
        Map<String, String> settingDTOMap = new HashMap<>();
        settingDTOS.forEach(settingDTO -> settingDTOMap.put(settingDTO.getSettingKey(), settingDTO.getSettingValue()));
        return settingDTOMap;
    }

    /**
     * 系统配置列表转为系统配置VO
     *
     * @param settingDTOS 系统配置列表
     * @return 返回null或者系统配置VO
     */
    public static SysSettingVO listToSysSettingVo(List<SysSettingDTO> settingDTOS) {
        Map<String, String> settingDTOMap = listToMap(settingDTOS);
        if (ObjectUtils.isEmpty(settingDTOMap)) {
            return null;
        }
        // 基本信息
        SysSettingVO sysSettingVO = new SysSettingVO();
        sysSettingVO.setFavicon(settingDTOMap.get(SysSettingEnum.FAVICON.value()));
        sysSettingVO.setSystemLogo(settingDTOMap.get(SysSettingEnum.SYSTEM_LOGO.value()));
        sysSettingVO.setSystemTitle(settingDTOMap.get(SysSettingEnum.SYSTEM_TITLE.value()));
        sysSettingVO.setSystemName(settingDTOMap.get(SysSettingEnum.SYSTEM_NAME.value()));
        sysSettingVO.setDefaultLanguage(settingDTOMap.get(SysSettingEnum.DEFAULT_LANGUAGE.value()));
        sysSettingVO.setRegisterUrl(settingDTOMap.get(SysSettingEnum.REGISTER_URL.value()));
        sysSettingVO.setResetGitlabPasswordUrl(settingDTOMap.get(SysSettingEnum.RESET_GITLAB_PASSWORD_URL.value()));
        sysSettingVO.setThemeColor(settingDTOMap.get(SysSettingEnum.THEME_COLOR.value()));
        sysSettingVO.setAutoCleanEmailRecord(Boolean.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD.value())));
        sysSettingVO.setAutoCleanEmailRecordInterval(Integer.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD_INTERVAL.value())));
        sysSettingVO.setAutoCleanWebhookRecord(Boolean.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD.value())));
        sysSettingVO.setAutoCleanWebhookRecordInterval(Integer.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD_INTERVAL.value())));
        sysSettingVO.setAutoCleanSagaInstance(Boolean.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE.value())));
        if (!StringUtils.isEmpty(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE_INTERVAL.value()))) {
            sysSettingVO.setAutoCleanSagaInstanceInterval(Integer.valueOf(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE_INTERVAL.value())));
        }
        if (!StringUtils.isEmpty(settingDTOMap.get(SysSettingEnum.RETAIN_FAILED_SAGA_INSTANCE.value()))) {
            sysSettingVO.setRetainFailedSagaInstance(Boolean.valueOf(settingDTOMap.get(SysSettingEnum.RETAIN_FAILED_SAGA_INSTANCE.value())));
        }
        String registerEnabled = settingDTOMap.get(SysSettingEnum.REGISTER_ENABLED.value());
        if (!ObjectUtils.isEmpty(registerEnabled)) {
            sysSettingVO.setRegisterEnabled(Boolean.valueOf(registerEnabled));
        }
        // 密码策略
        sysSettingVO.setDefaultPassword(settingDTOMap.get(SysSettingEnum.DEFAULT_PASSWORD.value()));
        String minPwd = settingDTOMap.get(SysSettingEnum.MIN_PASSWORD_LENGTH.value());
        String maxPwd = settingDTOMap.get(SysSettingEnum.MAX_PASSWORD_LENGTH.value());
        if (!ObjectUtils.isEmpty(minPwd)) {
            sysSettingVO.setMinPasswordLength(Integer.valueOf(minPwd));
        }
        if (!ObjectUtils.isEmpty(maxPwd)) {
            sysSettingVO.setMaxPasswordLength(Integer.valueOf(maxPwd));
        }
        return sysSettingVO;
    }


    /**
     * 是否为平台密码策略的配置项
     *
     * @param settingKey 配置属性
     * @return true:是;false:不是
     */
    public static boolean isPasswordPolicy(String settingKey) {
        return SysSettingEnum.DEFAULT_PASSWORD.value().equals(settingKey)
                || SysSettingEnum.MIN_PASSWORD_LENGTH.value().equals(settingKey)
                || SysSettingEnum.MAX_PASSWORD_LENGTH.value().equals(settingKey);
    }

    /**
     * 平台密码策略是否存在.
     *
     * @param originSettingDTOMap 系统配置Map
     * @return 返回 true:存在;false:不存在
     */
    public static boolean passwordPolicyIsExisted(Map<String, String> originSettingDTOMap) {
        if (originSettingDTOMap == null) {
            return false;
        }
        return originSettingDTOMap.containsKey(SysSettingEnum.DEFAULT_PASSWORD.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.MIN_PASSWORD_LENGTH.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.MAX_PASSWORD_LENGTH.value());
    }

    /**
     * 系统配置VO转为平台基本信息Map
     *
     * @param sysSettingVO 系统配置VO
     * @return 平台基本信息Map
     */
    public static Map<String, String> sysSettingVoToGeneralInfoMap(SysSettingVO sysSettingVO) {
        Map<String, String> settingDTOMap = new HashMap<>();
        settingDTOMap.put(SysSettingEnum.FAVICON.value(), sysSettingVO.getFavicon());
        settingDTOMap.put(SysSettingEnum.SYSTEM_LOGO.value(), sysSettingVO.getSystemLogo());
        settingDTOMap.put(SysSettingEnum.SYSTEM_TITLE.value(), sysSettingVO.getSystemTitle());
        settingDTOMap.put(SysSettingEnum.SYSTEM_NAME.value(), sysSettingVO.getSystemName());
        settingDTOMap.put(SysSettingEnum.DEFAULT_LANGUAGE.value(), sysSettingVO.getDefaultLanguage());
        settingDTOMap.put(SysSettingEnum.REGISTER_URL.value(), sysSettingVO.getRegisterUrl());
        settingDTOMap.put(SysSettingEnum.RESET_GITLAB_PASSWORD_URL.value(), sysSettingVO.getResetGitlabPasswordUrl());
        settingDTOMap.put(SysSettingEnum.THEME_COLOR.value(), sysSettingVO.getThemeColor());
        settingDTOMap.put(SysSettingEnum.REGISTER_ENABLED.value(), String.valueOf(sysSettingVO.getRegisterEnabled()));
        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD.value(), String.valueOf(sysSettingVO.getAutoCleanEmailRecord()));
        if (sysSettingVO.getAutoCleanEmailRecordInterval() == null) {
            sysSettingVO.setAutoCleanEmailRecordInterval(DEFAULT_CLEAN_NUM);
        }
        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD_INTERVAL.value(), String.valueOf(sysSettingVO.getAutoCleanEmailRecordInterval()));
        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD.value(), String.valueOf(sysSettingVO.getAutoCleanWebhookRecord()));
        if (sysSettingVO.getAutoCleanWebhookRecordInterval() == null) {
            sysSettingVO.setAutoCleanWebhookRecordInterval(DEFAULT_CLEAN_NUM);
        }
        if (sysSettingVO.getAutoCleanSagaInstanceInterval() == null) {
            sysSettingVO.setAutoCleanSagaInstanceInterval(DEFAULT_CLEAN_NUM);
        }
        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE.value(), String.valueOf(sysSettingVO.getAutoCleanSagaInstance()));
        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE_INTERVAL.value(), String.valueOf(sysSettingVO.getAutoCleanSagaInstanceInterval()));
        settingDTOMap.put(SysSettingEnum.RETAIN_FAILED_SAGA_INSTANCE.value(), String.valueOf(sysSettingVO.getRetainFailedSagaInstance()));

        settingDTOMap.put(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD_INTERVAL.value(), String.valueOf(sysSettingVO.getAutoCleanWebhookRecordInterval()));
        return settingDTOMap;
    }

    /**
     * 系统配置VO转为平台密码策略Map
     *
     * @param sysSettingVO 系统配置VO
     * @return 平台密码策略Map
     */
    public static Map<String, String> sysSettingVoToPasswordPolicyMap(SysSettingVO sysSettingVO) {
        Map<String, String> settingDTOMap = new HashMap<>();
        settingDTOMap.put(SysSettingEnum.DEFAULT_PASSWORD.value(), sysSettingVO.getDefaultPassword());
        settingDTOMap.put(SysSettingEnum.MIN_PASSWORD_LENGTH.value(), String.valueOf(sysSettingVO.getMinPasswordLength()));
        settingDTOMap.put(SysSettingEnum.MAX_PASSWORD_LENGTH.value(), String.valueOf(sysSettingVO.getMaxPasswordLength()));
        return settingDTOMap;
    }

}
