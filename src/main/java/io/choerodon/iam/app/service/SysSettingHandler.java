package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.infra.dto.SysSettingDTO;

public interface SysSettingHandler {
    void sysSettingVoToGeneralInfoMap(SysSettingVO sysSettingVO, Map<String, String> resultMap);

    void listToSysSettingVo(List<SysSettingDTO> settingDTOS, SysSettingVO resultVo);
}
