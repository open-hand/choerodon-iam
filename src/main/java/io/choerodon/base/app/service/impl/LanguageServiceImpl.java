package io.choerodon.base.app.service.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.base.app.service.LanguageService;
import io.choerodon.base.infra.asserts.AssertHelper;
import io.choerodon.base.infra.dto.LanguageDTO;
import io.choerodon.base.infra.mapper.LanguageMapper;
import io.choerodon.core.exception.CommonException;


/**
 * @author superlee
 */
@Service
public class LanguageServiceImpl implements LanguageService {

    private LanguageMapper languageMapper;

    private AssertHelper assertHelper;

    public LanguageServiceImpl(LanguageMapper languageMapper,
                               AssertHelper assertHelper) {
        this.languageMapper = languageMapper;
        this.assertHelper = assertHelper;
    }

    @Override
    public PageInfo<LanguageDTO> pagingQuery(Pageable pageable, LanguageDTO languageDTO, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> languageMapper.fulltextSearch(languageDTO, param));
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public LanguageDTO update(LanguageDTO languageDTO) {
        assertHelper.objectVersionNumberNotNull(languageDTO.getObjectVersionNumber());
        if (languageMapper.updateByPrimaryKeySelective(languageDTO) != 1) {
            throw new CommonException("error.language.update");
        }
        return languageMapper.selectByPrimaryKey(languageDTO.getId());
    }

    @Override
    public LanguageDTO queryByCode(String code) {
        LanguageDTO dto = new LanguageDTO();
        dto.setCode(code);
        return languageMapper.selectOne(dto);
    }

    @Override
    public List<LanguageDTO> listAll() {
        return languageMapper.selectAll();
    }

}
