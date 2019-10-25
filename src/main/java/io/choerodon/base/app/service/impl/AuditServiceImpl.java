package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.AuditService;
import io.choerodon.base.infra.dto.AuditDTO;
import io.choerodon.base.infra.mapper.AuditMapper;

/**
 * @author Eugen
 * @since 01/03/2019
 */
@Service
public class AuditServiceImpl implements AuditService {
    private AuditMapper auditMapper;

    public AuditServiceImpl(AuditMapper auditMapper) {
        this.auditMapper = auditMapper;
    }

    @Override
    public AuditDTO create(AuditDTO auditDTO) {
        auditMapper.insert(auditDTO);
        return auditMapper.selectByPrimaryKey(auditDTO);
    }

    @Override
    public PageInfo<AuditDTO> pagingQuery(Long userId, String businessType, String dataType, Pageable pageable) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> auditMapper.selectByParams(userId, businessType, dataType));
    }
}
