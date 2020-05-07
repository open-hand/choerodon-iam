package io.choerodon.iam.api.vo;

public class PermissionVO {
    private String[] roles = new String[0];
    private String type;
    private boolean permissionLogin;
    private boolean permissionPublic;
    private boolean permissionWithin;

    public PermissionVO() {
    }

    public String[] getRoles() {
        return this.roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPermissionLogin() {
        return this.permissionLogin;
    }

    public void setPermissionLogin(boolean permissionLogin) {
        this.permissionLogin = permissionLogin;
    }

    public boolean isPermissionPublic() {
        return this.permissionPublic;
    }

    public void setPermissionPublic(boolean permissionPublic) {
        this.permissionPublic = permissionPublic;
    }

    public boolean isPermissionWithin() {
        return this.permissionWithin;
    }

    public void setPermissionWithin(boolean permissionWithin) {
        this.permissionWithin = permissionWithin;
    }
}
