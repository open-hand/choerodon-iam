package io.choerodon.iam.app.service;

import java.util.List;

import io.choerodon.iam.api.vo.UserGuideVO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:11
 */
public interface UserGuideService {

    /**
     * 查询菜单下的指引步骤
     * @param menuId
     * @param projectId
     * @param organizationId
     * @return
     */
    UserGuideVO listUserGuideByMenuId(Long menuId, Long projectId, Long organizationId);
}
