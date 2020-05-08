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

    List<Menu> listPermissionSetTree(Long organizationId, String menuLevel);

    List<Menu> listNavMenuTree(Long roleId, Set<String> labels, Long projectId);

}
