package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;
import java.util.Set;

/**
 * @author zhaotianxin
 * @date 2021-11-08 15:31
 */
public class WorkGroupVO {
    @ApiModelProperty(value = "工作组id")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "工作组名称")
    private String name;

    @ApiModelProperty(value = "父级id")
    @Encrypt(ignoreValue = {"0"})
    private Long parentId;

    @ApiModelProperty(value = "rank")
    private String rank;

    @ApiModelProperty(value = "组织id")
    private Long organizationId;

    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "子级id集合")
    @Encrypt
    private List<Long> children;

    @ApiModelProperty(value = "用户id集合")
    @Encrypt
    private Set<Long> userIds;

    @ApiModelProperty(value = "用户数")
    private Integer userCount;

    @ApiModelProperty(value = "用户id")
    @Encrypt
    private Long userId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty("parent 三方id")
    private String parentOpenObjectId;
    @ApiModelProperty("第三方对象id")
    private String openObjectId;
    @ApiModelProperty("第三方对象id")
    private String openType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getParentOpenObjectId() {
        return parentOpenObjectId;
    }

    public void setParentOpenObjectId(String parentOpenObjectId) {
        this.parentOpenObjectId = parentOpenObjectId;
    }

    public String getOpenObjectId() {
        return openObjectId;
    }

    public void setOpenObjectId(String openObjectId) {
        this.openObjectId = openObjectId;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
