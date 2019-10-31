package io.choerodon.base.app.service;


import com.github.pagehelper.PageInfo;
import io.choerodon.base.infra.dto.LanguageSpaceDTO;
import io.choerodon.base.infra.dto.LdapDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;


/**
 * @author wkj
 */
public interface LanguageSpaceService {
    LanguageSpaceDTO create(LanguageSpaceDTO languageSpaceDTO);

    LanguageSpaceDTO update(Long id, LanguageSpaceDTO languageSpaceDTO);

    PageInfo<LanguageSpaceDTO> queryByOptions(LanguageSpaceDTO languageSpaceDTO, PageRequest pageRequest, String params);

    LanguageSpaceDTO queryById(Long id);

    void delete(Long id);
}
