package io.choerodon.base.app.service;


import com.github.pagehelper.PageInfo;
import io.choerodon.base.infra.dto.PromptDTO;
import org.springframework.data.domain.Pageable;


/**
 * @author wkj
 */
public interface PromptService {
    PromptDTO create(PromptDTO promptDTO);

    PromptDTO update(Long id, PromptDTO promptDTO);

    PageInfo<PromptDTO> queryByOptions(PromptDTO promptDTO, Pageable pageable, String params);

    PromptDTO queryById(Long id);

    void delete(Long id);
}
