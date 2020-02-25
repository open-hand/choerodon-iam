package io.choerodon.base.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.api.vo.OrgAdministratorVO;
import org.springframework.data.domain.Pageable;

/**
 * @author jiameng.cao
 * @since 2019/8/1
 */
public interface OrgAdministratorService {
    PageInfo<OrgAdministratorVO> pagingQueryOrgAdministrator(Pageable Pageable, Long organizationId,
                                                             String realName, String loginName, String params);

    Boolean deleteOrgAdministrator(Long organizationId, Long userId);

    Boolean createOrgAdministrator(List<Long> userIds, Long organizationId);
}
