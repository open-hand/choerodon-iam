package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Menu;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 17:36
 */
public interface MenuC7nService {

    List<Menu> listPermissionSetTree(Long tenantId, String menuLevel);

    List<Menu> listNavMenuTree(Set<String> labels, Long projectId);

    List<Menu> listMenuByLabel(Set<String> labels);

    /**
     * 查询个人信息菜单,仅包含type = menu的
     * @return
     */
    List<Menu> listUserInfoMenuOnlyTypeMenu();

    List<Menu> listMenuByLabelAndType(Set<String> labelNames, String type);
}
