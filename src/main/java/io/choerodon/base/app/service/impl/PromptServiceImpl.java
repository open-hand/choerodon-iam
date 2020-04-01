package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.app.service.PromptService;
import io.choerodon.base.infra.asserts.AssertHelper;
import io.choerodon.base.infra.dto.PromptDTO;
import io.choerodon.base.infra.enums.Language;
import io.choerodon.base.infra.mapper.PromptMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.web.util.PageableHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


/**
 * @author wkj
 * @since 2019/10/30
 **/
@Service
public class PromptServiceImpl implements PromptService {
    private PromptMapper promptMapper;
    private AssertHelper assertHelper;

    public PromptServiceImpl(PromptMapper promptMapper, AssertHelper assertHelper) {
        this.promptMapper = promptMapper;
        this.assertHelper = assertHelper;
    }


    @Override
    public PromptDTO create(PromptDTO promptDTO) {
        if (!Language.contains(promptDTO.getLang())) {
            throw new CommonException("error.language.not.match");
        }
        promptDTO.setId(null);
        PromptDTO prompt = new PromptDTO();
        prompt.setPromptCode(promptDTO.getPromptCode());
        prompt.setLang(promptDTO.getLang());
        if (promptMapper.selectOne(prompt) != null) {
            throw new InsertException("error.prompt.exist");
        }
        if (promptMapper.insertSelective(promptDTO) != 1) {
            throw new InsertException("error.prompt.insert");
        }
        return promptDTO;
    }

    @Override
    public PromptDTO update(Long id, PromptDTO promptDTO) {
        if (promptDTO.getLang() != null && !Language.contains(promptDTO.getLang())) {
            throw new CommonException("error.language.not.match");
        }
        assertHelper.objectVersionNumberNotNull(promptDTO.getObjectVersionNumber());
        PromptDTO prompt;
        if ((prompt = promptMapper.selectByPrimaryKey(id)) == null) {
            throw new UpdateException("errror.prompt.not.exist");
        }
        if (!prompt.getObjectVersionNumber().equals(promptDTO.getObjectVersionNumber())) {
            throw new UpdateException("error.update.dataObject.objectVersionNumber.not.equal");
        }
        if (!(prompt.getPromptCode().equals(promptDTO.getPromptCode()) && prompt.getLang().equals(promptDTO.getLang()))) {
            prompt = new PromptDTO();
            prompt.setPromptCode(promptDTO.getPromptCode());
            prompt.setLang(promptDTO.getLang());
            if (promptMapper.selectOne(prompt) != null) {
                throw new UpdateException("error.prompt.constraint.not.obey");
            }
        }
        if (promptMapper.updateByPrimaryKeySelective(promptDTO) != 1) {
            throw new UpdateException("error.prompt.update");
        }
        return promptDTO;
    }

    @Override
    public PageInfo<PromptDTO> queryByOptions(String promptCode, String lang, String serviceCode, String description, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> promptMapper.fulltextSearch(promptCode, lang, serviceCode, description, param));
    }

    @Override
    public PromptDTO queryById(Long id) {
        PromptDTO promptDTO;
        if ((promptDTO = promptMapper.selectByPrimaryKey(id)) == null) {
            throw new CommonException("error.prompt.not.exist");
        }
        return promptDTO;
    }

    @Override
    public void delete(Long id) {
        if (promptMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.prompt.delete");
        }
    }

    @Override
    public PromptDTO queryByCode(String code, String lang) {
        if (ObjectUtils.isEmpty(lang)) {
            lang = Language.Chinese.getValue();
        }
        if (!Language.contains(lang)) {
            throw new CommonException("error.language.invalid");
        }
        PromptDTO promptDTO = promptMapper.selectOne(new PromptDTO().setPromptCode(code).setLang(lang));
        if (ObjectUtils.isEmpty(promptDTO)) {
            throw new NotExistedException("error.prompt.not.exist");
        }
        return promptDTO;
    }
}
