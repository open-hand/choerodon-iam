package io.choerodon.base.app.service.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import io.choerodon.base.app.service.LookupService;
import io.choerodon.base.infra.asserts.AssertHelper;
import io.choerodon.base.infra.dto.LookupDTO;
import io.choerodon.base.infra.dto.LookupValueDTO;
import io.choerodon.base.infra.mapper.LookupMapper;
import io.choerodon.base.infra.mapper.LookupValueMapper;
import io.choerodon.core.exception.*;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;

/**
 * @author superlee
 */
@Service
public class LookupServiceImpl implements LookupService {

    private LookupMapper lookupMapper;

    private LookupValueMapper lookupValueMapper;

    private AssertHelper assertHelper;

    public LookupServiceImpl(LookupMapper lookupMapper,
                             LookupValueMapper lookupValueMapper,
                             AssertHelper assertHelper) {
        this.lookupMapper = lookupMapper;
        this.lookupValueMapper = lookupValueMapper;
        this.assertHelper = assertHelper;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LookupDTO create(LookupDTO lookupDTO) {
        lookupDTO.setId(null);
        List<LookupValueDTO> values = lookupDTO.getLookupValues();
        if (lookupMapper.insertSelective(lookupDTO) != 1) {
            throw new InsertException("error.repo.lookup.insert");
        }
        if (!ObjectUtils.isEmpty(values)) {
            values.forEach(v -> {
                v.setId(null);
                v.setLookupId(lookupDTO.getId());
                if (lookupValueMapper.insertSelective(v) != 1) {
                    throw new InsertException("error.lookupValue.insert");
                }
            });
        }
        return lookupDTO;
    }

    @Override
    public PageInfo<LookupDTO> pagingQuery(Pageable pageable, String code, String description, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> lookupMapper.fulltextSearch(code, description, param));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        lookupMapper.deleteByPrimaryKey(id);
        //删除lookup级联删除lookupValue
        LookupValueDTO lookupValue = new LookupValueDTO();
        lookupValue.setLookupId(id);
        lookupValueMapper.delete(lookupValue);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LookupDTO update(LookupDTO lookupDTO) {

        LookupDTO dbLookupDTO = lookupMapper.selectByPrimaryKey(lookupDTO);
        if (ObjectUtils.isEmpty(dbLookupDTO)) {
            throw new CommonException("error.lookup.not.exist");
        }
        lookupDTO.setObjectVersionNumber(dbLookupDTO.getObjectVersionNumber());
        List<LookupValueDTO> values = lookupDTO.getLookupValues();
        if (lookupMapper.updateByPrimaryKeySelective(lookupDTO) != 1) {
            throw new UpdateException("error.repo.lookup.update");
        }

        LookupValueDTO dto = new LookupValueDTO();
        dto.setLookupId(lookupDTO.getId());
        List<LookupValueDTO> list = lookupValueMapper.select(dto);

        if (!ObjectUtils.isEmpty(values)) {
            values.forEach(v -> {
                if (v.getId() == null) {
                    v.setLookupId(lookupDTO.getId());
                    if (lookupValueMapper.insertSelective(v) != 1) {
                        throw new CommonException("error.lookupValue.insert");
                    }
                }
                list.forEach(d -> {
                    if (d.getId().equals(v.getId())) {
                        d.setCode(v.getCode());
                        d.setDescription(v.getDescription());
                        d.setDisplayOrder(v.getDisplayOrder());
                        lookupValueMapper.updateByPrimaryKeySelective(d);
                    }
                });

            });
        }
        return lookupDTO;
    }

    @Override
    public LookupDTO queryByDes(String des) {
        return lookupMapper.queryByDes(des);
    }

    @Override
    public LookupDTO queryByCode(String code) {
        return lookupMapper.queryByCode(code);
    }

    @Override
    public void check(Long lookupId, String code) {
        Long count = lookupMapper.check(lookupId, code);
        if (count >= 1) {
            throw new CommonException("error.lookup.code.duplication");
        }
    }
}
