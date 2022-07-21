package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SysSettingUtils.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.app.service.SysSettingHandler;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;

@Service
public class SysSettingHandlerImpl implements SysSettingHandler {
    /**
     * 系统配置VO转为平台基本信息Map
     *
     * @param sysSettingVO 系统配置VO
     * @return 平台基本信息Map
     */
    @Override
    public void sysSettingVoToGeneralInfoMap(SysSettingVO sysSettingVO, Map<String, String> settingDTOMap) {
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
    }

    /**
     * 系统配置列表转为系统配置VO
     *
     * @param settingDTOS 系统配置列表
     * @return 返回null或者系统配置VO
     */
    @Override
    public void listToSysSettingVo(List<SysSettingDTO> settingDTOS, SysSettingVO sysSettingVO) {
        Map<String, String> settingDTOMap = listToMap(settingDTOS);
        if (ObjectUtils.isEmpty(settingDTOMap)) {
            return;
        }
        // 基本信息
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
    }
}
