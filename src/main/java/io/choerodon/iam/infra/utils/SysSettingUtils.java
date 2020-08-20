package io.choerodon.iam.infra.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;

public class SysSettingUtils {

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
        sysSettingVO.setForceModifyPassword(Boolean.valueOf(settingDTOMap.get(SysSettingEnum.FORCE_MODIFY_PASSWORD.value())));
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
     * 平台基本信息是否存在.
     *
     * @param originSettingDTOMap 系统配置Map
     * @return 返回 true:存在;false:不存在
     */
    public static boolean generalInfoIsExisted(Map<String, String> originSettingDTOMap) {
        if (originSettingDTOMap == null) {
            return false;
        }
        return originSettingDTOMap.containsKey(SysSettingEnum.FAVICON.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.SYSTEM_LOGO.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.SYSTEM_TITLE.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.SYSTEM_NAME.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.DEFAULT_LANGUAGE.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.REGISTER_ENABLED.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.REGISTER_URL.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.RESET_GITLAB_PASSWORD_URL.value()) &&
                originSettingDTOMap.containsKey(SysSettingEnum.THEME_COLOR.value());
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
        settingDTOMap.put(SysSettingEnum.FORCE_MODIFY_PASSWORD.value(), String.valueOf(sysSettingVO.getForceModifyPassword()));
        return settingDTOMap;
    }

}
