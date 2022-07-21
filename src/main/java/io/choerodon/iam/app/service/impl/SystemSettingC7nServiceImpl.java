package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.constant.RedisCacheKeyConstants.REDIS_KEY_LOGIN;
import static io.choerodon.iam.infra.constant.TenantConstants.BACKETNAME;
import static io.choerodon.iam.infra.utils.SysSettingUtils.listToMapTl;

import com.google.inject.internal.cglib.core.$CollectionUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.google.gson.Gson;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.PasswordPolicyService;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.app.service.SysSettingHandler;
import io.choerodon.iam.app.service.SystemSettingC7nService;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.PlatformFeignClient;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.mapper.SysSettingMapper;
import io.choerodon.iam.infra.utils.ImageUtils;
import io.choerodon.iam.infra.utils.JsonHelper;
import io.choerodon.iam.infra.utils.MockMultipartFile;
import io.choerodon.iam.infra.utils.SysSettingUtils;

/**
 * @author zmf
 * @since 2018-10-15
 */
@Service
public class SystemSettingC7nServiceImpl implements SystemSettingC7nService {
    private final Gson gson = new Gson();
    protected static final String CLEAN_EMAIL_RECORD = "cleanEmailRecord";
    protected static final String CLEAN_WEBHOOK_RECORD = "cleanWebhookRecord";
    protected static final String DEFAULT_CLEAN_NUM = "180";
    /**
     * 清理消息类型 WEBHOOK/EMAIL
     */
    protected static final String MESSAGE_TYPE = "messageType";

    /**
     * 清理多少天前的数据
     */
    protected static final String CLEAN_NUM = "cleanNum";
    protected static final String MESSAGE_TYPE_EMAIL = "EMAIL";
    private static final String MESSAGE_TYPE_WEBHOOK = "WEB_HOOK";
    public static final String REDIS_KEY_LOGIN_LANGUAGE = "c7n-iam:settingLogin:language";

    @Value("${choerodon.saga.service:choerodon-asgard}")
    private String sagaServiceName;

    private FileClient fileClient;


    private final Boolean enableCategory;

    private SysSettingMapper sysSettingMapper;
    @Autowired
    private PlatformFeignClient platformFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private AsgardFeignClient asgardFeignClient;

    private AsgardServiceClientOperator asgardServiceClientOperator;
    private PasswordPolicyRepository passwordPolicyRepository;
    private List<SysSettingHandler> sysSettingHandlers;
    @Autowired
    @Lazy
    private PasswordPolicyService passwordPolicyService;

    public SystemSettingC7nServiceImpl(FileClient fileClient,
                                       AsgardFeignClient asgardFeignClient,
                                       PasswordPolicyRepository passwordPolicyRepository,
                                       @Lazy AsgardServiceClientOperator asgardServiceClientOperator,
                                       @Value("${choerodon.category.enabled:false}")
                                               Boolean enableCategory,
                                       SysSettingMapper sysSettingMapper,
                                       List<SysSettingHandler> sysSettingHandlers) {
        this.fileClient = fileClient;
        this.asgardFeignClient = asgardFeignClient;
        this.asgardServiceClientOperator = asgardServiceClientOperator;
        this.enableCategory = enableCategory;
        this.sysSettingMapper = sysSettingMapper;
        this.passwordPolicyRepository = passwordPolicyRepository;
        this.sysSettingHandlers = sysSettingHandlers;
    }

    @Override
    public String uploadFavicon(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
        } catch (IOException e) {
            throw new CommonException("error.image.cut");
        }
        return uploadFile(file);
    }

    @Override
    public String uploadSystemLogo(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream()).forceSize(80, 80).toOutputStream(outputStream);
            file = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), outputStream.toByteArray());
            return uploadFile(file);
        } catch (Exception e) {
            throw new CommonException("error.setting.logo.save.failure");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysSettingVO updateGeneralInfo(SysSettingVO sysSettingVO) {
        if (sysSettingVO == null) {
            return null;
        }
        List<SysSettingDTO> oldSysSettingDTOList = sysSettingMapper.selectAll();
        SysSettingVO oldSysSettingVO = new SysSettingVO();
        SysSettingUtils.listToSysSettingVo(sysSettingHandlers, oldSysSettingDTOList, oldSysSettingVO);
        Map<String, SysSettingDTO> sysSettingDTOMap = sysSettingMapper.selectAll().stream().collect(Collectors.toMap(SysSettingDTO::getSettingKey, Function.identity()));
        Map<String, String> settingDTOMap = new HashMap<>();
        SysSettingUtils.sysSettingVoToGeneralInfoMap(sysSettingHandlers, sysSettingVO, settingDTOMap);
        Map<String, Map<String, String>> tls = sysSettingVO.get_tls();
        settingDTOMap.forEach((k, v) -> {
            SysSettingDTO settingDTO;
            if (sysSettingDTOMap.containsKey(k)) {
                settingDTO = sysSettingDTOMap.get(k);
                settingDTO.setSettingValue(v);
                if (!MapUtils.isEmpty(tls)) {
                    Map<String, String> stringStringMap = tls.get(k);
                    if (MapUtils.isNotEmpty(stringStringMap)) {
                        HashMap<String, Map<String, String>> stringMapHashMap = new HashMap<>();
                        stringMapHashMap.put(SysSettingDTO.SETTING_VALUE, stringStringMap);
                        settingDTO.set_tls(stringMapHashMap);
                    }
                }
                if (sysSettingMapper.updateByPrimaryKey(settingDTO) != 1) {
                    throw new CommonException("error.setting.update.failed");
                }
            } else {
                settingDTO = new SysSettingDTO();
                settingDTO.setSettingKey(k);
                settingDTO.setSettingValue(v);
                if (!MapUtils.isEmpty(tls)) {
                    Map<String, String> stringStringMap = tls.get(k);
                    if (MapUtils.isNotEmpty(stringStringMap)) {
                        HashMap<String, Map<String, String>> stringMapHashMap = new HashMap<>();
                        stringMapHashMap.put(SysSettingDTO.SETTING_VALUE, stringStringMap);
                        settingDTO.set_tls(stringMapHashMap);
                    }
                }
                if (sysSettingMapper.insertSelective(settingDTO) != 1) {
                    throw new CommonException("error.setting.insert.failed");
                }
            }
        });
        if (!sysSettingVO.getAutoCleanEmailRecord().equals(oldSysSettingVO.getAutoCleanEmailRecord())
                || !sysSettingVO.getAutoCleanEmailRecordInterval().equals(oldSysSettingVO.getAutoCleanEmailRecordInterval())) {
            handleScheduleTaskMessage(MESSAGE_TYPE_EMAIL, sysSettingVO.getAutoCleanEmailRecord(), sysSettingVO.getAutoCleanEmailRecordInterval());
        }
        if (!sysSettingVO.getAutoCleanWebhookRecord().equals(oldSysSettingVO.getAutoCleanWebhookRecord())
                || !sysSettingVO.getAutoCleanWebhookRecordInterval().equals(oldSysSettingVO.getAutoCleanWebhookRecordInterval())) {
            handleScheduleTaskMessage(MESSAGE_TYPE_WEBHOOK, sysSettingVO.getAutoCleanWebhookRecord(), sysSettingVO.getAutoCleanWebhookRecordInterval());
        }
        if (!sysSettingVO.getAutoCleanSagaInstanceInterval().equals(oldSysSettingVO.getAutoCleanSagaInstanceInterval())
                || !sysSettingVO.getAutoCleanSagaInstance().equals(oldSysSettingVO.getAutoCleanSagaInstance())) {
            handleScheduleTaskAutoClean(oldSysSettingVO.getAutoCleanSagaInstance(), "cleanSagaInstance", new HashMap<>(), "cleanSagaInstance", sagaServiceName);
        }
        SysSettingVO sysSettingResultVO = new SysSettingVO();
        SysSettingUtils.listToSysSettingVo(sysSettingHandlers, sysSettingMapper.selectAll(), sysSettingResultVO);
        // 更改默认语言
        if (ObjectUtils.isEmpty(oldSysSettingVO.getDefaultLanguage()) || !oldSysSettingVO.getDefaultLanguage().equals(sysSettingVO.getDefaultLanguage())) {
            updateDefaultLanguage(sysSettingVO.getDefaultLanguage());
        }
        return sysSettingResultVO;
    }

    private void updateDefaultLanguage(@NotNull String defaultLanguage) {
        platformFeignClient.updateConfig("TENANT_DEFAULT_LANGUAGE", defaultLanguage);
        stringRedisTemplate.delete(REDIS_KEY_LOGIN_LANGUAGE);
    }

    protected void handleScheduleTaskMessage(String messageType, Boolean autoClean, Integer cleanNum) {
        String taskName = messageType.equals(MESSAGE_TYPE_EMAIL) ? CLEAN_EMAIL_RECORD : CLEAN_WEBHOOK_RECORD;
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put(MESSAGE_TYPE, messageType);
        mapParams.put(CLEAN_NUM, cleanNum);
        handleScheduleTaskAutoClean(autoClean, taskName, mapParams, "cleanMessageRecord", HZeroService.Message.NAME);
    }


    protected void handleScheduleTaskAutoClean(Boolean autoClean, String taskName, Map<String, Object> mapParams, String methodName, String service) {
        asgardFeignClient.deleteSiteTask(taskName);
        if (autoClean) {
            ScheduleTaskDTO scheduleTaskDTO = new ScheduleTaskDTO();
            scheduleTaskDTO.setName(taskName);
            scheduleTaskDTO.setDescription(taskName);
            Date date = new Date();
            String cron = "0 30 1 * * ?";
            scheduleTaskDTO.setCronExpression(cron);
            scheduleTaskDTO.setTriggerType("cron-trigger");
            scheduleTaskDTO.setExecuteStrategy("STOP");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(date);
            scheduleTaskDTO.setStartTimeStr(dateString);

            ScheduleTaskDTO.NotifyUser notifyUser = new ScheduleTaskDTO.NotifyUser();
            notifyUser.setAdministrator(false);
            notifyUser.setAssigner(false);
            notifyUser.setCreator(false);
            scheduleTaskDTO.setNotifyUser(notifyUser);
            scheduleTaskDTO.setParams(mapParams);
            scheduleTaskDTO.setMethodId(asgardServiceClientOperator.getMethodDTOSite(methodName, service).getId());
            asgardServiceClientOperator.createQuartzTaskSite(scheduleTaskDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysSettingVO updatePasswordPolicy(SysSettingVO sysSettingVO) {
        if (sysSettingVO == null) {
            return null;
        }
        addDefaultLengthValue(sysSettingVO);
        validateLength(sysSettingVO);
        List<SysSettingDTO> records = sysSettingMapper.selectAll();
        Map<String, String> settingDTOMap = SysSettingUtils.sysSettingVoToPasswordPolicyMap(sysSettingVO);
        // 如果未保存过平台密码策略 执行插入;否则执行更新
        if (!SysSettingUtils.passwordPolicyIsExisted(SysSettingUtils.listToMap(records))) {
            insertSysSetting(settingDTOMap);
        } else {
            records.forEach(record -> {
                String key = record.getSettingKey();
                if (SysSettingUtils.isPasswordPolicy(key)) {
                    record.setSettingValue(settingDTOMap.get(key));
                    if (sysSettingMapper.updateByPrimaryKey(record) != 1) {
                        throw new CommonException("error.setting.update.failed");
                    }
                }
            });
        }
        PasswordPolicy passwordPolicy = passwordPolicyRepository.selectTenantPasswordPolicy(BaseConstants.DEFAULT_TENANT_ID);
        passwordPolicy.setOriginalPassword(sysSettingVO.getDefaultPassword());
        passwordPolicy.setMinLength(sysSettingVO.getMinPasswordLength());
        passwordPolicy.setMaxLength(sysSettingVO.getMaxPasswordLength());
        passwordPolicyService.updatePasswordPolicy(BaseConstants.DEFAULT_TENANT_ID, passwordPolicy);
        SysSettingVO dto = new SysSettingVO();
        SysSettingUtils.listToSysSettingVo(sysSettingHandlers, sysSettingMapper.selectAll(), dto);
        return dto;
    }

    private void insertSysSetting(Map<String, String> settingDTOMap) {
        settingDTOMap.forEach((k, v) -> {
            SysSettingDTO sysSettingDTO = new SysSettingDTO();
            sysSettingDTO.setSettingKey(k);
            sysSettingDTO.setSettingValue(v);
            if (sysSettingMapper.insertSelective(sysSettingDTO) != 1) {
                throw new CommonException("error.setting.insert.failed");
            }
        });
    }

    @Override
    @Transactional
    public void resetGeneralInfo() {
        List<SysSettingDTO> records = sysSettingMapper.selectAll();
        if (ObjectUtils.isEmpty(records)) {
            return;
        }
        records = records.stream().filter(t -> !t.getSettingKey().contains("login")).collect(Collectors.toList());
        records.forEach(record -> {
            String key = record.getSettingKey();
            // 重置跳过密码策略和 Feedback 策略
            if (SysSettingUtils.isPasswordPolicy(key)) {
                return;
            }
            record.setSettingValue(null);
            if (key.equals(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD.value())
                    || key.equals(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD.value())) {
                record.setSettingValue(Boolean.FALSE.toString());
            }
            if (key.equals(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD_INTERVAL.value())
                    || key.equals(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD_INTERVAL.value())) {
                record.setSettingValue(DEFAULT_CLEAN_NUM);
            }
            if (key.equals(SysSettingEnum.REGISTER_ENABLED.value())) {
                record.setSettingValue(Boolean.FALSE.toString());
            }
            if (key.equals(SysSettingEnum.DEFAULT_LANGUAGE.value())) {
                record.setSettingValue("zh_CN");
            }
            sysSettingMapper.updateByPrimaryKey(record);
        });
        // 删除定时任务
        asgardFeignClient.deleteSiteTask(CLEAN_EMAIL_RECORD);
        asgardFeignClient.deleteSiteTask(CLEAN_WEBHOOK_RECORD);
        updateDefaultLanguage("zh_CN");
    }

    @Override
    public SysSettingVO getSetting() {
        SysSettingVO sysSettingVO = new SysSettingVO();
        SysSettingUtils.listToSysSettingVo(sysSettingHandlers, sysSettingMapper.selectAll(), sysSettingVO);
        return sysSettingVO;
    }

    @Override
    public Boolean getEnabledStateOfTheCategory() {
        return enableCategory;
    }

    @Override
    public String getDefaultLanguage() {
        String settingStr = stringRedisTemplate.opsForValue().get(REDIS_KEY_LOGIN_LANGUAGE);
        if (ObjectUtils.isNotEmpty(settingStr)) {
            return settingStr;
        } else {
            SysSettingDTO language = sysSettingMapper.queryByKey("defaultLanguage");
            stringRedisTemplate.opsForValue().set(REDIS_KEY_LOGIN_LANGUAGE, language.getSettingValue(), 30, TimeUnit.MINUTES);
            return language.getSettingValue();
        }
    }

    @Override
    public Map<String, String> queryLogin() {
        String settingStr = stringRedisTemplate.opsForValue().get(REDIS_KEY_LOGIN);
        Map<String, String> settingDTOMap;
        if (ObjectUtils.isNotEmpty(settingStr)) {
            settingDTOMap = (Map<String, String>) gson.fromJson(settingStr, Map.class);
        } else {
            List<SysSettingDTO> settingDTOS = sysSettingMapper.listByLikeCode("login");
            settingDTOMap = settingDTOS.stream().filter(t -> ObjectUtils.isNotEmpty(t.getSettingValue())).collect(Collectors.toMap(SysSettingDTO::getSettingKey, SysSettingDTO::getSettingValue));
            settingDTOS.forEach(sysSettingDTO -> {
                settingDTOMap.put(sysSettingDTO.getSettingKey() + "Token", sysSettingDTO.get_token());
            });
            settingDTOMap.remove(SysSettingEnum.LOGIN_DING_TALK_APP_SECRET.value());
            settingStr = gson.toJson(settingDTOMap);
            stringRedisTemplate.opsForValue().set(REDIS_KEY_LOGIN, settingStr, 5, TimeUnit.MINUTES);
        }
        // 补偿机制 解决前端白屏
        settingDTOMap.putIfAbsent(SysSettingEnum.LOGIN_WAY.value(), "account,phone");
        return settingDTOMap;
    }

    @Override
    public SysSettingVO getSettingTl() {
        SysSettingVO sysSettingVO = new SysSettingVO();
        List<SysSettingDTO> sysSettingDTOS = sysSettingMapper.selectAll();
        sysSettingHandlers.forEach(handler -> {
            handSysttingTl(sysSettingVO, sysSettingDTOS);
        });
        return sysSettingVO;
    }

    private void handSysttingTl(SysSettingVO sysSettingVO, List<SysSettingDTO> sysSettingDTOS) {
        Map<String, SysSettingDTO> settingDTOMap = listToMapTl(sysSettingDTOS);
        if (org.springframework.util.ObjectUtils.isEmpty(settingDTOMap)) {
            return;
        }
        // 基本信息
        sysSettingVO.setFavicon(settingDTOMap.get(SysSettingEnum.FAVICON.value()) != null ? settingDTOMap.get(SysSettingEnum.FAVICON.value()).getSettingValue() : null);
        sysSettingVO.setSystemLogo(settingDTOMap.get(SysSettingEnum.SYSTEM_LOGO.value()) != null ? settingDTOMap.get(SysSettingEnum.SYSTEM_LOGO.value()).getSettingValue() : null);
        sysSettingVO.setSystemTitle(settingDTOMap.get(SysSettingEnum.SYSTEM_TITLE.value()) != null ? settingDTOMap.get(SysSettingEnum.SYSTEM_TITLE.value()).getSettingValue() : null);
        sysSettingVO.setSystemTitleToken(settingDTOMap.get(SysSettingEnum.SYSTEM_TITLE.value()) != null ? settingDTOMap.get(SysSettingEnum.SYSTEM_TITLE.value()).get_token() : null);
        sysSettingVO.setSystemName(settingDTOMap.get(SysSettingEnum.SYSTEM_NAME.value()) != null ? settingDTOMap.get(SysSettingEnum.SYSTEM_NAME.value()).getSettingValue() : null);
        sysSettingVO.setSystemNameToken(settingDTOMap.get(SysSettingEnum.SYSTEM_NAME.value()) != null ? settingDTOMap.get(SysSettingEnum.SYSTEM_NAME.value()).get_token() : null);

        sysSettingVO.setDefaultLanguage(settingDTOMap.get(SysSettingEnum.DEFAULT_LANGUAGE.value()) != null ? settingDTOMap.get(SysSettingEnum.DEFAULT_LANGUAGE.value()).getSettingValue() : null);
        sysSettingVO.setRegisterUrl(settingDTOMap.get(SysSettingEnum.REGISTER_URL.value()) != null ? settingDTOMap.get(SysSettingEnum.REGISTER_URL.value()).getSettingValue() : null);
        sysSettingVO.setResetGitlabPasswordUrl(settingDTOMap.get(SysSettingEnum.RESET_GITLAB_PASSWORD_URL.value()) != null ? settingDTOMap.get(SysSettingEnum.RESET_GITLAB_PASSWORD_URL.value()).getSettingValue() : null);
        sysSettingVO.setThemeColor(settingDTOMap.get(SysSettingEnum.THEME_COLOR.value()) != null ? settingDTOMap.get(SysSettingEnum.THEME_COLOR.value()).getSettingValue() : null);
        SysSettingDTO autoCleanEmail = settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_EMAIL_RECORD.value());
        if (autoCleanEmail != null) {
            sysSettingVO.setAutoCleanEmailRecord(autoCleanEmail.getSettingValue() != null ? Boolean.valueOf(autoCleanEmail.getSettingValue()) : null);
        }
        SysSettingDTO autoCleanWebhook = settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD.value());
        if (autoCleanWebhook != null) {
            sysSettingVO.setAutoCleanWebhookRecord(autoCleanWebhook.getSettingValue() != null ? Boolean.valueOf(autoCleanWebhook.getSettingValue()) : null);
        }
        SysSettingDTO autoCleanWebhookRecordInterval = settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_WEBHOOK_RECORD_INTERVAL.value());
        if (autoCleanWebhookRecordInterval != null) {
            sysSettingVO.setAutoCleanWebhookRecordInterval(autoCleanWebhookRecordInterval.getSettingValue() != null ? Integer.valueOf(autoCleanWebhookRecordInterval.getSettingValue()) : null);
        }
        SysSettingDTO autoCleanSagaInstance = settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE.value());
        if (autoCleanSagaInstance != null) {
            sysSettingVO.setAutoCleanSagaInstance(autoCleanSagaInstance.getSettingValue() != null ? Boolean.valueOf(autoCleanSagaInstance.getSettingValue()) : null);
        }


        if (!StringUtils.isEmpty(settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE_INTERVAL.value()))) {
            SysSettingDTO autoCleanSagaInstanceInterval = settingDTOMap.get(SysSettingEnum.AUTO_CLEAN_SAGA_INSTANCE_INTERVAL.value());
            if (autoCleanSagaInstanceInterval != null) {
                sysSettingVO.setAutoCleanSagaInstanceInterval(autoCleanSagaInstanceInterval.getSettingValue() != null ? Integer.valueOf(autoCleanSagaInstanceInterval.getSettingValue()) : null);
            }
        }
        if (!StringUtils.isEmpty(settingDTOMap.get(SysSettingEnum.RETAIN_FAILED_SAGA_INSTANCE.value()))) {
            SysSettingDTO retainFailedSagaInstance = settingDTOMap.get(SysSettingEnum.RETAIN_FAILED_SAGA_INSTANCE.value());
            if (retainFailedSagaInstance != null) {
                sysSettingVO.setRetainFailedSagaInstance(retainFailedSagaInstance.getSettingValue() != null ? Boolean.valueOf(retainFailedSagaInstance.getSettingValue()) : null);
            }
        }
        SysSettingDTO registerEnabled = settingDTOMap.get(SysSettingEnum.REGISTER_ENABLED.value());
        if (registerEnabled != null) {
            sysSettingVO.setRegisterEnabled(registerEnabled.getSettingValue() != null ? Boolean.valueOf(registerEnabled.getSettingValue()) : null);
        }
        // 密码策略
        SysSettingDTO defaultPassword = settingDTOMap.get(SysSettingEnum.DEFAULT_PASSWORD.value());
        sysSettingVO.setDefaultPassword(defaultPassword != null ? defaultPassword.getSettingValue() : null);
        SysSettingDTO minPwdSysSettingDTO = settingDTOMap.get(SysSettingEnum.MIN_PASSWORD_LENGTH.value());
        SysSettingDTO maxPwdSysSettingDTO = settingDTOMap.get(SysSettingEnum.MAX_PASSWORD_LENGTH.value());
        String minPwd = minPwdSysSettingDTO != null ? minPwdSysSettingDTO.getSettingValue() : null;
        String maxPwd = maxPwdSysSettingDTO != null ? maxPwdSysSettingDTO.getSettingValue() : null;
        if (!org.springframework.util.ObjectUtils.isEmpty(minPwd)) {
            sysSettingVO.setMinPasswordLength(Integer.valueOf(minPwd));
        }
        if (!org.springframework.util.ObjectUtils.isEmpty(maxPwd)) {
            sysSettingVO.setMaxPasswordLength(Integer.valueOf(maxPwd));
        }
    }

    private String uploadFile(MultipartFile file) {
        return fileClient.uploadFile(0L, BACKETNAME, null, file.getOriginalFilename(), file);
    }

    /**
     * If the value is empty, default value is to be set.
     *
     * @param sysSettingVO the dto
     */
    private void addDefaultLengthValue(SysSettingVO sysSettingVO) {
        if (sysSettingVO.getMinPasswordLength() == null) {
            sysSettingVO.setMinPasswordLength(6);
        }
        if (sysSettingVO.getMaxPasswordLength() == null) {
            sysSettingVO.setMaxPasswordLength(18);
        }
    }

    /**
     * validate the value of min length and max length
     *
     * @param sysSettingVO dto
     */
    private void validateLength(SysSettingVO sysSettingVO) {
        if (sysSettingVO.getMinPasswordLength() > sysSettingVO.getMaxPasswordLength()) {
            throw new CommonException("error.maxLength.lessThan.minLength");
        }
    }

}
