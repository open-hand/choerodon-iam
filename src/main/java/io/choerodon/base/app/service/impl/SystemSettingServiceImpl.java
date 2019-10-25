package io.choerodon.base.app.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.base.api.dto.payload.SystemSettingEventPayload;
import io.choerodon.base.api.vo.SysSettingVO;
import io.choerodon.base.app.service.SystemSettingService;
import io.choerodon.base.infra.dto.SysSettingDTO;
import io.choerodon.base.infra.feign.FileFeignClient;
import io.choerodon.base.infra.mapper.SysSettingMapper;
import io.choerodon.base.infra.utils.ImageUtils;
import io.choerodon.base.infra.utils.MockMultipartFile;
import io.choerodon.base.infra.utils.SagaTopic;
import io.choerodon.base.infra.utils.SysSettingUtils;
import io.choerodon.core.exception.CommonException;

/**
 * @author zmf
 * @since 2018-10-15
 */
@Service
@Saga(code = SagaTopic.SystemSetting.SYSTEM_SETTING_UPDATE, description = "iam更改系统设置", inputSchemaClass = SystemSettingEventPayload.class)
public class SystemSettingServiceImpl implements SystemSettingService {
    private final FileFeignClient fileFeignClient;
    private final SagaClient sagaClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ERROR_UPDATE_SYSTEM_SETTING_EVENT_SEND = "error.system.setting.update.send.event";


    private final Boolean enableCategory;

    private SysSettingMapper sysSettingMapper;


    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;


    public SystemSettingServiceImpl(FileFeignClient fileFeignClient,
                                    SagaClient sagaClient,
                                    @Value("${choerodon.category.enabled:false}")
                                            Boolean enableCategory,
                                    SysSettingMapper sysSettingMapper) {
        this.fileFeignClient = fileFeignClient;
        this.sagaClient = sagaClient;
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
        List<SysSettingDTO> records = sysSettingMapper.selectAll();
        Map<String, String> settingDTOMap = SysSettingUtils.sysSettingVoToGeneralInfoMap(sysSettingVO);
        // 如果未保存过平台基本信息 执行插入;否则执行更新
        if (!SysSettingUtils.generalInfoIsExisted(SysSettingUtils.listToMap(records))) {
            insertSysSetting(settingDTOMap);
        } else {
            records.forEach(record -> {
                String key = record.getSettingKey();
                // 跳过密码策略和 Feedback 策略
                if (SysSettingUtils.isPasswordPolicy(key)) {
                    return;
                }
                record.setSettingValue(settingDTOMap.get(key));
                if (sysSettingMapper.updateByPrimaryKey(record) != 1) {
                    throw new CommonException("error.setting.update.failed");
                }
            });

        }
        SysSettingVO dto = SysSettingUtils.listToSysSettingVo(sysSettingMapper.selectAll());
        if (devopsMessage) {
            triggerSagaFlow(dto);
        }
        return dto;
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
        if (devopsMessage) {
            triggerSagaFlow(dto);
        }
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
            sysSettingMapper.updateByPrimaryKey(record);
        });
        if (devopsMessage) {
            triggerSagaFlow(new SysSettingVO());
        }
    }

    @Override
    public SysSettingVO getSetting() {
        return SysSettingUtils.listToSysSettingVo(sysSettingMapper.selectAll());
    }

    @Override
    public Boolean getEnabledStateOfTheCategory() {
        return enableCategory;
    }

    /**
     * 触发 saga 流程
     *
     * @param dto 系统配置VO
     */
    private void triggerSagaFlow(final SysSettingVO dto) {
        try {
            SystemSettingEventPayload payload = new SystemSettingEventPayload();
            BeanUtils.copyProperties(dto, payload);
            sagaClient.startSaga(SagaTopic.SystemSetting.SYSTEM_SETTING_UPDATE, new StartInstanceDTO(objectMapper.writeValueAsString(payload)));
        } catch (Exception e) {
            throw new CommonException(ERROR_UPDATE_SYSTEM_SETTING_EVENT_SEND, e);
        }
    }

    private String uploadFile(MultipartFile file) {
        return fileFeignClient.uploadFile("iam-service", file.getOriginalFilename(), file).getBody();
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
