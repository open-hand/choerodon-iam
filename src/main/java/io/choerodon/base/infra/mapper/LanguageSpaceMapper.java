package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.LanguageSpaceDTO;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * @author wkj
 * @since 2019/10/30
 **/
public interface LanguageSpaceMapper extends Mapper<LanguageSpaceDTO> {
    List<LanguageSpaceDTO> fulltextSearch(LanguageSpaceDTO languageSpaceDTO, String param);
}
