package io.choerodon.iam.api.vo;

import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.Role;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/22 14:09
 */
public class RoleVO extends Role {

    @ApiModelProperty("角色层级organization，project")
    private String roleLevel;
    @ApiModelProperty("角色拥有的菜单id集合")
    private Set<Long> menuIdList;

    public Set<Long> getMenuIdList() {
        return menuIdList;
    }

    public void setMenuIdList(Set<Long> menuIdList) {
        this.menuIdList = menuIdList;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }
}
