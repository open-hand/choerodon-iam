package io.choerodon.iam.infra.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.app.service.SysSettingHandler;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;

public class SysSettingUtils {
    public static final Integer DEFAULT_CLEAN_NUM = 180;


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

    public static Map<String, String> listToMapTl(List<SysSettingDTO> settingDTOS) {
        if (CollectionUtils.isEmpty(settingDTOS)) {
            return null;
        }
        Map<String, String> settingDTOMap = new HashMap<>();
        settingDTOS.forEach(settingDTO -> {
            settingDTOMap.put(settingDTO.getSettingKey(), JsonHelper.marshalByJackson(settingDTO));
        });
        return settingDTOMap;
    }

    /**
     * 系统配置列表转为系统配置VO
     *
     * @param settingDTOS 系统配置列表
     * @return 返回null或者系统配置VO
     */
    public static void listToSysSettingVo(List<SysSettingHandler> settingHandlers, List<SysSettingDTO> settingDTOS, SysSettingVO sysSettingVO) {
        settingHandlers.forEach(handler -> {
            handler.listToSysSettingVo(settingDTOS, sysSettingVO);
        });
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
    public static void sysSettingVoToGeneralInfoMap(List<SysSettingHandler> settingHandlers, SysSettingVO sysSettingVO, Map<String, String> settingDTOMap) {
        settingHandlers.forEach(sysSettingHandler -> {
            sysSettingHandler.sysSettingVoToGeneralInfoMap(sysSettingVO, settingDTOMap);
        });
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
