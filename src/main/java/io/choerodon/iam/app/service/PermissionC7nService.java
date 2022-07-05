package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.PermissionCheckDTO;
import org.hzero.iam.domain.entity.Permission;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
public interface PermissionC7nService {

    Set<Permission> queryByRoleIds(List<Long> roleIds);

    /**
     * 查询当前用户菜单下可访问的权限集编码列表
     *
     * @param codes 权限集编码
     * @param isMobile
     * @return 可访问的权限集编码
     */
    List<PermissionCheckDTO> checkPermissionSets(List<String> codes, Long tenantId, Boolean isMobile, Long projectId);

    List<Permission> getPermission(String[] codes);

    /**
     * 根据项目id查询项目标签
     * @param projectId
     * @return
     */
    Set<String> getProjectLabels(Long projectId);

    void asyncRolePermission();

}
