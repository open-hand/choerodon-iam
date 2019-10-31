package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.PromptDTO;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * @author wkj
 * @since 2019/10/30
 **/
public interface PromptMapper extends Mapper<PromptDTO> {
    List<PromptDTO> fulltextSearch(PromptDTO promptDTO, String param);
}
