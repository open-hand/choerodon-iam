package io.choerodon.iam.api.vo;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.mybatis.domian.SecurityToken;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/22 14:09
 */
public class RoleVO extends Role {

    private Boolean projectAdminFlag = false;

    private Boolean projectMemberFlag = false;

    @ApiModelProperty("角色层级organization，project")
    private String roleLevel;
    @ApiModelProperty("角色拥有的菜单id集合")
    private Set<Long> menuIdList;

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

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Role.class;
    }

    public Boolean getProjectAdminFlag() {
        return projectAdminFlag;
    }

    public void setProjectAdminFlag(Boolean projectAdminFlag) {
        this.projectAdminFlag = projectAdminFlag;
    }

    public Boolean getProjectMemberFlag() {
        return projectMemberFlag;
    }

    public void setProjectMemberFlag(Boolean projectMemberFlag) {
        this.projectMemberFlag = projectMemberFlag;
    }
}
