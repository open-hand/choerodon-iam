package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.app.service.LanguageSpaceService;
import io.choerodon.base.infra.asserts.AssertHelper;
import io.choerodon.base.infra.dto.LanguageSpaceDTO;
import io.choerodon.base.infra.dto.LdapDTO;
import io.choerodon.base.infra.mapper.LanguageSpaceMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author wkj
 * @since 2019/10/30
 **/
@Service
public class LanguageSpaceServiceImpl implements LanguageSpaceService {
    private LanguageSpaceMapper languageSpaceMapper;
    private AssertHelper assertHelper;
    public LanguageSpaceServiceImpl(LanguageSpaceMapper languageSpaceMapper, AssertHelper assertHelper) {
        this.languageSpaceMapper = languageSpaceMapper;
        this.assertHelper = assertHelper;
    }


    @Override
    public LanguageSpaceDTO create(LanguageSpaceDTO languageSpaceDTO) {
        languageSpaceDTO.setId(null);
        LanguageSpaceDTO language = new LanguageSpaceDTO();
        language.setPromptCode(languageSpaceDTO.getPromptCode());
        language.setLang(languageSpaceDTO.getLang());
        if(languageSpaceMapper.selectOne(language) != null) {
            throw new InsertException("error.language.exist");
        }
        if(languageSpaceMapper.insertSelective(languageSpaceDTO) != 1) {
            throw new InsertException("error.language.insert");
        }
        return languageSpaceDTO;
    }

    @Override
    public LanguageSpaceDTO update(Long id, LanguageSpaceDTO languageSpaceDTO) {
        assertHelper.objectVersionNumberNotNull(languageSpaceDTO.getObjectVersionNumber());
        if(languageSpaceMapper.selectByPrimaryKey(id) == null) {
            throw new UpdateException("errror.language.not.exist");
        }
        LanguageSpaceDTO language = new LanguageSpaceDTO();
        language.setPromptCode(languageSpaceDTO.getPromptCode());
        language.setLang(languageSpaceDTO.getLang());
        if(languageSpaceMapper.selectOne(language) != null) {
            throw new UpdateException("error.constraint.not.obey");
        }
        if(languageSpaceMapper.updateByPrimaryKey(language) != 1) {
            throw new UpdateException("error.language.update");
        }
        return languageSpaceDTO;
    }

    @Override
    public PageInfo<LanguageSpaceDTO> queryByOptions(LanguageSpaceDTO languageSpaceDTO, PageRequest pageRequest, String param) {
        return PageMethod.startPage(pageRequest.getPageNumber(),pageRequest.getPageSize())
                .doSelectPageInfo(() -> languageSpaceMapper.fulltextSearch(languageSpaceDTO, param));
    }

    @Override
    public LanguageSpaceDTO queryById(Long id) {
        LanguageSpaceDTO languageSpaceDTO;
        if((languageSpaceDTO = languageSpaceMapper.selectByPrimaryKey(id)) == null) {
            throw new CommonException("error.language.not.exist");
        }
        return languageSpaceDTO;
    }

    @Override
    public void delete(Long id) {
        if(languageSpaceMapper.deleteByPrimaryKey(id) != 1 ) {
            throw new CommonException("error.language.delete");
        }
    }
}
