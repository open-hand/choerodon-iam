package io.choerodon.iam.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.CustomLayoutConfigService;
import io.choerodon.iam.infra.dto.CustomLayoutConfigDTO;
import io.choerodon.iam.infra.enums.CustomLayoutConfigTypeEnum;
import io.choerodon.iam.infra.mapper.CustomLayoutConfigMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/1/5 18:06
 */
@Service
public class CustomLayoutConfigServiceImpl implements CustomLayoutConfigService {

    @Autowired
    private CustomLayoutConfigMapper customLayoutConfigMapper;

    @Override
    @Transactional
    public CustomLayoutConfigDTO saveOrUpdateCustomWorkBeachConfig(CustomLayoutConfigDTO customLayoutConfigDTO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return saveOrUpdateCustomLayoutConfig(CustomLayoutConfigTypeEnum.WORK_BENCH, userId, customLayoutConfigDTO);
    }

    @Override
    @Transactional
    public CustomLayoutConfigDTO saveOrUpdateCustomProjectOverview(Long projectId, CustomLayoutConfigDTO customLayoutConfigDTO) {
        return saveOrUpdateCustomLayoutConfig(CustomLayoutConfigTypeEnum.PROJECT_OVERVIEW, projectId, customLayoutConfigDTO);
    }

    @Override
    public CustomLayoutConfigDTO queryCustomWorkBeachConfig() {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return queryCustomLayoutConfig(CustomLayoutConfigTypeEnum.WORK_BENCH, userId);
    }

    @Override
    public CustomLayoutConfigDTO queryCustomProjectOverview(Long projectId) {
        return queryCustomLayoutConfig(CustomLayoutConfigTypeEnum.PROJECT_OVERVIEW, projectId);
    }

    private CustomLayoutConfigDTO saveOrUpdateCustomLayoutConfig(CustomLayoutConfigTypeEnum sourceType, Long sourceId, CustomLayoutConfigDTO customLayoutConfigDTO) {
        CustomLayoutConfigDTO record = new CustomLayoutConfigDTO();
        record.setSourceType(sourceType.value());
        record.setSourceId(sourceId);
        CustomLayoutConfigDTO customLayoutConfigRecord = customLayoutConfigMapper.selectOne(record);
        if (customLayoutConfigRecord == null) {
            customLayoutConfigDTO.setSourceType(sourceType.value());
            customLayoutConfigDTO.setSourceId(sourceId);
            customLayoutConfigMapper.insertSelective(customLayoutConfigDTO);
        } else {
            customLayoutConfigDTO.setSourceType(null);
            customLayoutConfigDTO.setSourceId(null);
            customLayoutConfigMapper.updateByPrimaryKeySelective(customLayoutConfigDTO);
        }
        return customLayoutConfigMapper.selectByPrimaryKey(customLayoutConfigDTO.getId());
    }
    private CustomLayoutConfigDTO queryCustomLayoutConfig(CustomLayoutConfigTypeEnum sourceType, Long sourceId) {
        CustomLayoutConfigDTO record = new CustomLayoutConfigDTO();
        record.setSourceType(sourceType.value());
        record.setSourceId(sourceId);
        return customLayoutConfigMapper.selectOne(record);
    }
}
