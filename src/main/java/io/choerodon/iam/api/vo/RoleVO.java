package io.choerodon.iam.api.vo;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.Menu;
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

    @ApiModelProperty("是否更新角色信息/update时必须")
    private Boolean updateRoleFlag;
    @ApiModelProperty("是否更新权限/update时必须")
    private Boolean updatePermissionFlag;

    private List<Menu> menuList;
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

    public Boolean getUpdateRoleFlag() {
        return updateRoleFlag;
    }

    public void setUpdateRoleFlag(Boolean updateRoleFlag) {
        this.updateRoleFlag = updateRoleFlag;
    }

    public Boolean getUpdatePermissionFlag() {
        return updatePermissionFlag;
    }

    public void setUpdatePermissionFlag(Boolean updatePermissionFlag) {
        this.updatePermissionFlag = updatePermissionFlag;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
