//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.choerodon.iam.api.vo;

import java.util.Objects;

public class PermissionDescriptionVO {
    private String path;
    private String service;
    private String method;
    private String description;
    private PermissionVO permission;

    public PermissionDescriptionVO() {
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PermissionVO getPermission() {
        return this.permission;
    }

    public void setPermission(PermissionVO permission) {
        this.permission = permission;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PermissionDescriptionVO that = (PermissionDescriptionVO)o;
            return Objects.equals(this.path, that.path) && Objects.equals(this.service, that.service) && Objects.equals(this.method, that.method) && Objects.equals(this.description, that.description) && Objects.equals(this.permission, that.permission);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.path, this.service, this.method, this.description, this.permission});
    }
}
