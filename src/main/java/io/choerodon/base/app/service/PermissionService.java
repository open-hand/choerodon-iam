package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.api.dto.CheckPermissionDTO;
import io.choerodon.base.infra.dto.PermissionDTO;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 */
public interface PermissionService {

    PageInfo<PermissionDTO> pagingQuery(Pageable Pageable, PermissionDTO permissionDTO, String param);

    List<CheckPermissionDTO> checkPermission(List<CheckPermissionDTO> checkPermissionDTOList);

    Set<PermissionDTO> queryByRoleIds(List<Long> roleIds);

    List<PermissionDTO> query(String level, String serviceName, String code);

    void deleteByCode(String code);

    PageInfo<PermissionDTO> listPermissionsByRoleId(Pageable Pageable, Long id, String params);
}
