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

    PageInfo<PromptDTO> queryByOptions(String promptCode, String lang, String serviceCode, String description, Pageable pageable, String params);

    PromptDTO queryById(Long id);

    void delete(Long id);


    /**
     * 根据Code查询多语言映射
     *
     * @param code 多语言记录编码
     * @param lang 语言
     * @return
     */
    PromptDTO queryByCode(String code, String lang);
}
