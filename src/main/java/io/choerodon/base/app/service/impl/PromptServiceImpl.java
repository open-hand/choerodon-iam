package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.app.service.PromptService;
import io.choerodon.base.infra.asserts.AssertHelper;
import io.choerodon.base.infra.dto.PromptDTO;
import io.choerodon.base.infra.mapper.PromptMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
        promptDTO.setId(null);
        PromptDTO prompt = new PromptDTO();
        prompt.setPromptCode(promptDTO.getPromptCode());
        prompt.setLang(promptDTO.getLang());
        if(promptMapper.selectOne(prompt) != null) {
            throw new InsertException("error.prompt.exist");
        }
        if(promptMapper.insertSelective(promptDTO) != 1) {
            throw new InsertException("error.prompt.insert");
        }
        return promptDTO;
    }

    @Override
    public PromptDTO update(Long id, PromptDTO promptDTO) {
        assertHelper.objectVersionNumberNotNull(promptDTO.getObjectVersionNumber());
        if(promptMapper.selectByPrimaryKey(id) == null) {
            throw new UpdateException("errror.prompt.not.exist");
        }
        PromptDTO prompt = new PromptDTO();
        prompt.setPromptCode(promptDTO.getPromptCode());
        prompt.setLang(promptDTO.getLang());
        if(promptMapper.selectOne(prompt) != null) {
            throw new UpdateException("error.constraint.not.obey");
        }
        if(promptMapper.updateByPrimaryKey(promptDTO) != 1) {
            throw new UpdateException("error.prompt.update");
        }
        return promptDTO;
    }

    @Override
    public PageInfo<PromptDTO> queryByOptions(PromptDTO promptDTO, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(),pageable.getPageSize())
                .doSelectPageInfo(() -> promptMapper.fulltextSearch(promptDTO, param));
    }

    @Override
    public PromptDTO queryById(Long id) {
        PromptDTO promptDTO;
        if((promptDTO = promptMapper.selectByPrimaryKey(id)) == null) {
            throw new CommonException("error.prompt.not.exist");
        }
        return promptDTO;
    }

    @Override
    public void delete(Long id) {
        if(promptMapper.deleteByPrimaryKey(id) != 1 ) {
            throw new CommonException("error.prompt.delete");
        }
    }
}
