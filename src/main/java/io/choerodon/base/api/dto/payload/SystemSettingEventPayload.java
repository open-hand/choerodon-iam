package io.choerodon.base.api.dto.payload;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.choerodon.base.api.vo.SysSettingVO;

/**
 * 用于 saga 传递消息，字段含义参照 {@link SysSettingVO}
 *
 * @author zmf
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingEventPayload implements Serializable {
    private String favicon;

    private String systemLogo;

    private String systemTitle;

    private String systemName;

    private String defaultPassword;

    private String defaultLanguage;
}
