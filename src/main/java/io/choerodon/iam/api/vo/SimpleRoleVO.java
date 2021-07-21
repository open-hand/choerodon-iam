package io.choerodon.iam.api.vo;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/7/21
 * @Modified By:
 */
public class SimpleRoleVO {
    private Long id;
    private String roleName;
    private String roleCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
