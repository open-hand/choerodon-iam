package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.hzero.iam.domain.entity.Menu;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 17:36
 */
@Service
public class MenuC7nServiceImpl implements MenuC7nService {

    private MenuC7nMapper menuC7nMapper;

    public MenuC7nServiceImpl(MenuC7nMapper menuC7nMapper) {
        this.menuC7nMapper = menuC7nMapper;
    }

    @Override
    public List<Menu> listPermissionSetTree(Long organizationId, String menuLevel) {
        // todo 查询组织下的组织管理员账户

        // todo 根据层级查询组织管理员的有权限的菜单列表
        return null;
    }
}
