package io.choerodon.iam.app.service;

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
     * 查询菜单下的指引步骤， meunId不为空则根据menuId查询，否则根据guideCode查询
     * @param menuId
     * @param tabCode
     * @param guideCode
     * @param projectId
     * @param organizationId
     * @return
     */
    UserGuideVO listUserGuideByMenuId(Long menuId, String tabCode, String guideCode, Long projectId, Long organizationId);
}
