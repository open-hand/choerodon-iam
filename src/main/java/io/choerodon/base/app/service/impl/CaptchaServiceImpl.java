package io.choerodon.base.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.base.api.vo.ResendEmailMsgDTO;
import io.choerodon.base.app.service.CaptchaService;
import io.choerodon.base.infra.feign.NotifyFeignClient;
import io.choerodon.base.infra.utils.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CaptchaServiceImpl implements CaptchaService {
    private final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Value("${choerodon.captcha.timeoutSecond:600}")
    private Long timeout;
    @Value("${choerodon.captcha.resendSecond:60}")
    private Long resend;

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public void setResend(Long resend) {
        this.resend = resend;
    }


    private NotifyFeignClient notifyFeignClient;
    private RedisHelper redisHelper;

    public CaptchaServiceImpl(RedisHelper redisHelper, NotifyFeignClient notifyFeignClient) {
        this.notifyFeignClient = notifyFeignClient;
        this.redisHelper = redisHelper;
    }

    @Override
    public void validateCaptcha(String email, String captcha) {
        String captchaKey = "captcha:" + email;
        if (!captcha.equals(redisHelper.get(captchaKey))) {
            throw new CommonException("error.captcha");
        }
        redisHelper.delete(captchaKey);
        String resendKey = "resend:" + email;
        redisHelper.delete(resendKey);
    }

    @Override
    public ResendEmailMsgDTO sendEmailCaptcha(String email) {
        ResendEmailMsgDTO msg = new ResendEmailMsgDTO();
        msg.setEmail(email);
        String resendKey = "resend:" + email;
        if (redisHelper.hasKey(resendKey)) {
            msg.setCanResend(false);
            Long remainingSecond = resend - (System.currentTimeMillis() - Long.valueOf(redisHelper.get(resendKey))) / 1000;
            msg.setRemainingSecond(remainingSecond);
            return msg;
        }
        redisHelper.set(resendKey, System.currentTimeMillis() + "", resend);

        String captcha = String.valueOf(new Random().nextInt(899999) + 100000);
        String captchaKey = "captcha:" + email;
        redisHelper.set(captchaKey, captcha, timeout);

        sendEmail(email, msg, resendKey, captcha, captchaKey);
        return msg;
    }

    private void sendEmail(String email, ResendEmailMsgDTO msg, String resendKey, String captcha, String captchaKey) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("captcha", captcha);
        variables.put("timeout", timeout / 60);
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        NoticeSendDTO.User user = new NoticeSendDTO.User();
        user.setEmail(email);
        List<NoticeSendDTO.User> users = new ArrayList<>();
        users.add(user);
        noticeSendDTO.setTargetUsers(users);
        noticeSendDTO.setCode("captcha-registrantOrganization");
        noticeSendDTO.setParams(variables);
        try {
            notifyFeignClient.postNotice(noticeSendDTO);
            msg.setSuccessful(true);
            msg.setCanResend(false);
            msg.setRemainingSecond(resend);
        } catch (Exception e) {
            logger.error("invoke notify-service to send email failed, exception : {}", e);
            msg.setSuccessful(false);
            msg.setFailedCause("error.invoke.notifyService");
            msg.setCanResend(true);
            redisHelper.delete(resendKey);
            redisHelper.delete(captchaKey);
        }
    }
}
