package io.choerodon.iam.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.iam.app.service.CustomLayoutConfigService;
import io.choerodon.iam.infra.dto.CustomLayoutConfigDTO;
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
    public CustomLayoutConfigDTO saveCustomWorkBeachConfig(CustomLayoutConfigDTO customLayoutConfigDTO) {
        CustomLayoutConfigDTO customLayoutConfigRecord = customLayoutConfigMapper.selectByPrimaryKey(customLayoutConfigDTO.getId());
        if (customLayoutConfigRecord == null) {
            customLayoutConfigMapper.insertSelective(customLayoutConfigDTO);
        } else {
            customLayoutConfigMapper.updateByPrimaryKeySelective(customLayoutConfigDTO);
        }
        return customLayoutConfigMapper.selectByPrimaryKey(customLayoutConfigDTO.getId());
    }
}
