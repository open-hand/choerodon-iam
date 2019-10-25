package io.choerodon.base.app.service;


import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.LanguageDTO;

import java.util.List;

/**
 * @author superlee
 */
public interface LanguageService {

    PageInfo<LanguageDTO> pagingQuery(Pageable Pageable, LanguageDTO languageDTO, String param);

    LanguageDTO update(LanguageDTO languageDTO);

    LanguageDTO queryByCode(String code);

    List<LanguageDTO> listAll();
}
