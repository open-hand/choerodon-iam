package io.choerodon.base.app.service;


import io.choerodon.base.api.vo.ResendEmailMsgDTO;

/**
 * @author Eugen
 */
public interface CaptchaService {
    /**
     * 验证码校验
     *
     * @param email
     * @param captcha
     */
    void validateCaptcha(String email, String captcha);

    /**
     * 发送验证码
     *
     * @param email
     * @return
     */
    ResendEmailMsgDTO sendEmailCaptcha(String email);
}
