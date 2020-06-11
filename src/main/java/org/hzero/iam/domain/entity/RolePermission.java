//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hzero.iam.domain.entity;

import java.util.Date;
import java.util.Set;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.infra.constant.RolePermissionType;

import io.choerodon.mybatis.annotation.ModifyAudit;

@ApiModel("角色权限关系")
@ModifyAudit
@Table(
        name = "iam_role_permission"
)
public class RolePermission {
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_CREATE_FLAG = "createFlag";
    public static final String FIELD_INHERIT_FLAG = "inheritFlag";
    public static final RolePermissionType DEFAULT_TYPE;
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty("角色ID")
    private Long roleId;
    @Column(
            name = "permission_id"
    )
    @ApiModelProperty("权限ID")
    private Long permissionSetId;
    @Column(
            name = "type"
    )
    @ApiModelProperty("权限类型")
    private String type;
    @Column(
            name = "h_create_flag"
    )
    @ApiModelProperty("创建标识")
    private String createFlag;
    @Column(
            name = "h_inherit_flag"
    )
    @ApiModelProperty("继承标识")
    private String inheritFlag;
    @Transient
    @ApiModelProperty("角色编码")
    private String roleCode;
    @Transient
    @ApiModelProperty("角色唯一路径")
    private String levelPath;
    @Transient
    @ApiModelProperty("权限集合")
    private Set<Long> permissionSetIds;
    @Transient
    @ApiModelProperty("权限层级")
    private String level;
    @Transient
    private boolean bothCreateAndInheritFlag;

    private Date creationDate;

    private Long createdBy;

    private Date lastUpdateDate;

    private Long lastUpdatedBy;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public RolePermission() {
    }

    public RolePermission(Long roleId, Long permissionSetId, String inheritFlag, String createFlag, String type) {
        this.roleId = roleId;
        this.permissionSetId = permissionSetId;
        this.inheritFlag = inheritFlag;
        this.createFlag = createFlag;
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public RolePermission setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public RolePermission setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Long getPermissionSetId() {
        return this.permissionSetId;
    }

    public RolePermission setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public RolePermission setType(String type) {
        this.type = type;
        return this;
    }

    public String getCreateFlag() {
        return this.createFlag;
    }

    public RolePermission setCreateFlag(String createFlag) {
        this.createFlag = createFlag;
        return this;
    }

    public String getInheritFlag() {
        return this.inheritFlag;
    }

    public RolePermission setInheritFlag(String inheritFlag) {
        this.inheritFlag = inheritFlag;
        return this;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public RolePermission setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getLevelPath() {
        return this.levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public Set<Long> getPermissionSetIds() {
        return this.permissionSetIds;
    }

    public RolePermission setPermissionSetIds(Set<Long> permissionSetIds) {
        this.permissionSetIds = permissionSetIds;
        return this;
    }

    public String getLevel() {
        return this.level;
    }

    public RolePermission setLevel(String level) {
        this.level = level;
        return this;
    }

    public boolean isBothCreateAndInheritFlag() {
        return this.bothCreateAndInheritFlag;
    }

    public RolePermission setBothCreateAndInheritFlag(boolean bothCreateAndInheritFlag) {
        this.bothCreateAndInheritFlag = bothCreateAndInheritFlag;
        return this;
    }

    static {
        DEFAULT_TYPE = RolePermissionType.PS;
    }
}
