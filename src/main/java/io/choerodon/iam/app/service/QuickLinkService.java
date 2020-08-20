package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.QuickLinkVO;
import io.choerodon.iam.infra.dto.QuickLinkDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 16:13
 */
public interface QuickLinkService {

    void create(QuickLinkDTO quickLinkDTO);

    void update(Long organizationId, Long id, QuickLinkDTO quickLinkDTO);

    void delete(Long organizationId, Long id);

    Page<QuickLinkVO> querySelf(Long organizationId, PageRequest pageable);

    Page<QuickLinkVO> queryProject(Long organizationId, Long projectId, PageRequest pageable);

    void addTop(Long organizationId, Long id);

    void deleteTop(Long organizationId, Long id);
}
