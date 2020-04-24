package io.choerodon.iam.app.service;

import java.util.List;

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
}
