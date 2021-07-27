package io.choerodon.iam.api.validator;

import java.util.List;

import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.infra.mapper.PasswordPolicyMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.app.service.SysSettingHandler;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.mapper.SysSettingMapper;
import io.choerodon.iam.infra.utils.SysSettingUtils;

/**
 * 当用户组织的密码策略未开启时，如果修改过系统设置，根据系统设置中的密码长度要求，校验用户密码
 *
 * @author zmf
 */
@Component
public class UserPasswordValidator {

    private PasswordPolicyMapper passwordPolicyMapper;

    private SysSettingMapper sysSettingMapper;

    private List<SysSettingHandler> sysSettingHandlers;

    public UserPasswordValidator(PasswordPolicyMapper passwordPolicyMapper,
                                 SysSettingMapper sysSettingMapper,
                                 List<SysSettingHandler> sysSettingHandlers) {
        this.passwordPolicyMapper = passwordPolicyMapper;
        this.sysSettingMapper = sysSettingMapper;
        this.sysSettingHandlers = sysSettingHandlers;
    }

    /**
     * 验证密码是否符合系统设置所配置的密码长度范围
     *
     * @param password           用户的密码
     * @param organizationId     用户所属组织 id
     * @param isToThrowException 当校验失败时是否抛出异常
     * @return 当符合校验时，返回true
     */
    public boolean validate(String password, Long organizationId, boolean isToThrowException) {
        PasswordPolicy dto = new PasswordPolicy();
        dto.setOrganizationId(organizationId);
        PasswordPolicy passwordPolicyDTO = passwordPolicyMapper.selectOne(dto);
        // 组织启用密码策略时，跳过验证
        if (passwordPolicyDTO != null && Boolean.TRUE.equals(passwordPolicyDTO.getEnablePassword())) {
            return true;
        }
        List<SysSettingDTO> settingDTOS = sysSettingMapper.selectAll();
        SysSettingVO setting = new SysSettingVO();
        SysSettingUtils.listToSysSettingVo(sysSettingHandlers, settingDTOS, setting);
        // 系统设置为空时，跳过
        if (setting == null || setting.getMinPasswordLength() == null || setting.getMaxPasswordLength() == null) {
            return true;
        }
        password = password.replaceAll(" ", "");
        if (password.length() < setting.getMinPasswordLength() || password.length() > setting.getMaxPasswordLength()) {
            if (isToThrowException) {
                throw new CommonException("error.password.length.out.of.setting", setting.getMinPasswordLength(), setting.getMaxPasswordLength());
            }
            return false;
        }
        return true;
    }
}
