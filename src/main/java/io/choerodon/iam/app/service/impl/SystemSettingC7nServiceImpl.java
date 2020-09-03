package io.choerodon.iam.app.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.coobird.thumbnailator.Thumbnails;
import org.hzero.boot.file.FileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.app.service.SystemSettingC7nService;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.dto.asgard.ScheduleTaskDTO;
import io.choerodon.iam.infra.enums.SysSettingEnum;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.mapper.SysSettingMapper;
import io.choerodon.iam.infra.utils.ImageUtils;
import io.choerodon.iam.infra.utils.MockMultipartFile;
import io.choerodon.iam.infra.utils.SysSettingUtils;

/**
 * @author zmf
 * @since 2018-10-15
 */
@Service
public class SystemSettingC7nServiceImpl implements SystemSettingC7nService {
    private static final String CLEAN_EMAIL_RECORD = "cleanEmailRecord";
    private static final String CLEAN_WEBHOOK_RECORD = "cleanWebhookRecord";
    private static final String DEFAULT_CLEAN_NUM = "180";
    /**
     * 清理消息类型 WEBHOOK/EMAIL
     */
    private static final String MESSAGE_TYPE = "messageType";

    /**
     * 清理多少天前的数据
     */
    private static final String CLEAN_NUM = "cleanNum";
    private static final String MESSAGE_TYPE_EMAIL = "EMAIL";
    private static final String MESSAGE_TYPE_WEBHOOK = "WEB_HOOK";
    private FileClient fileClient;


    private final Boolean enableCategory;

    private SysSettingMapper sysSettingMapper;

    private AsgardFeignClient asgardFeignClient;

    private AsgardServiceClientOperator asgardServiceClientOperator;

    public SystemSettingC7nServiceImpl(FileClient fileClient,
                                       AsgardFeignClient asgardFeignClient,
                                       @Lazy AsgardServiceClientOperator asgardServiceClientOperator,
                                       @Value("${choerodon.category.enabled:false}")
                                               Boolean enableCategory,
                                       SysSettingMapper sysSettingMapper) {
        this.fileClient = fileClient;
        this.asgardFeignClient = asgardFeignClient;
        this.asgardServiceClientOperator = asgardServiceClientOperator;
        this.enableCategory = enableCategory;
        this.sysSettingMapper = sysSettingMapper;
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
        SysSettingVO oldSysSettingVO = SysSettingUtils.listToSysSettingVo(oldSysSettingDTOList);
        Map<String, SysSettingDTO> sysSettingDTOMap = sysSettingMapper.selectAll().stream().collect(Collectors.toMap(SysSettingDTO::getSettingKey, Function.identity()));
        Map<String, String> settingDTOMap = SysSettingUtils.sysSettingVoToGeneralInfoMap(sysSettingVO);
        settingDTOMap.forEach((k, v) -> {
            SysSettingDTO settingDTO;
            if (sysSettingDTOMap.containsKey(k)) {
                settingDTO = sysSettingDTOMap.get(k);
                settingDTO.setSettingValue(v);
                if (sysSettingMapper.updateByPrimaryKey(settingDTO) != 1) {
                    throw new CommonException("error.setting.update.failed");
                }
            } else {
                settingDTO = new SysSettingDTO();
                settingDTO.setSettingKey(k);
                settingDTO.setSettingValue(v);
                if (sysSettingMapper.insertSelective(settingDTO) != 1) {
                    throw new CommonException("error.setting.insert.failed");
                }
            }
        });
        if (!sysSettingVO.getAutoCleanEmailRecord().equals(oldSysSettingVO.getAutoCleanEmailRecord())
                || !sysSettingVO.getAutoCleanEmailRecordInterval().equals(oldSysSettingVO.getAutoCleanEmailRecordInterval())) {
            handleScheduleTask(MESSAGE_TYPE_EMAIL, sysSettingVO.getAutoCleanEmailRecord(), sysSettingVO.getAutoCleanEmailRecordInterval());
        }
        if (!sysSettingVO.getAutoCleanWebhookRecord().equals(oldSysSettingVO.getAutoCleanWebhookRecord())
                || !sysSettingVO.getAutoCleanWebhookRecordInterval().equals(oldSysSettingVO.getAutoCleanWebhookRecordInterval())) {
            handleScheduleTask(MESSAGE_TYPE_WEBHOOK, sysSettingVO.getAutoCleanWebhookRecord(), sysSettingVO.getAutoCleanWebhookRecordInterval());
        }
        return SysSettingUtils.listToSysSettingVo(sysSettingMapper.selectAll());
    }

    private void handleScheduleTask(String messageType, Boolean autoClean, Integer cleanNum) {
        String taskName = messageType.equals(MESSAGE_TYPE_EMAIL) ? CLEAN_EMAIL_RECORD : CLEAN_WEBHOOK_RECORD;
        asgardFeignClient.deleteSiteTask(taskName);
        if (autoClean) {
            ScheduleTaskDTO scheduleTaskDTO = new ScheduleTaskDTO();
            scheduleTaskDTO.setName(taskName);
            scheduleTaskDTO.setDescription(taskName);
            Date date = new Date();
            String cron = "0 0 2 * * ?";
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

            Map<String, Object> mapParams = new HashMap<>();
            mapParams.put(MESSAGE_TYPE, messageType);
            mapParams.put(CLEAN_NUM, cleanNum);
            scheduleTaskDTO.setParams(mapParams);

            scheduleTaskDTO.setMethodId(asgardServiceClientOperator.getMethodDTOSite("cleanMessageRecord", "hzero-message").getId());
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
        SysSettingVO dto = SysSettingUtils.listToSysSettingVo(sysSettingMapper.selectAll());
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
            sysSettingMapper.updateByPrimaryKey(record);
        });
        // 删除定时任务
        asgardFeignClient.deleteSiteTask(CLEAN_EMAIL_RECORD);
        asgardFeignClient.deleteSiteTask(CLEAN_WEBHOOK_RECORD);
    }

    @Override
    public SysSettingVO getSetting() {
        return SysSettingUtils.listToSysSettingVo(sysSettingMapper.selectAll());
    }

    @Override
    public Boolean getEnabledStateOfTheCategory() {
        return enableCategory;
    }

    private String uploadFile(MultipartFile file) {
        return fileClient.uploadFile(0L, "iam-service", file.getOriginalFilename(), file);
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
