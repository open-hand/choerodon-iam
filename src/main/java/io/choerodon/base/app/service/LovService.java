package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.infra.dto.LovDTO;
import io.choerodon.base.infra.dto.PermissionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LovService {
    LovDTO queryLovByCode(String code);

    LovDTO queryLovById(Long id);

    LovDTO createLov(LovDTO lovDTO);

    PageInfo<List<PermissionDTO>> queryApiByLevel(Pageable pageable, String level, String params);

    PageInfo<LovDTO> queryLovList(Pageable pageable, String code, String description, String level, String param);

    LovDTO updateLov(Long id, LovDTO lovDTO);

    void deleteLov(Long id);
}
