package io.choerodon.base.app.service;


import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.LookupDTO;

/**
 * @author superlee
 */
public interface LookupService {

    LookupDTO create(LookupDTO lookupDTO);

    PageInfo<LookupDTO> pagingQuery(Pageable Pageable, LookupDTO lookupDTO, String param);

    void delete(Long id);

    LookupDTO update(LookupDTO lookupDTO);

    LookupDTO queryById(Long id);

    LookupDTO listByCodeWithLookupValues(String code);
}
