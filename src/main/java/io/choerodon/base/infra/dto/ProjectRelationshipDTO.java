package io.choerodon.base.infra.dto;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.base.api.vo.UserVO;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author superlee
 * @since 2019-04-23
 */
@Table(name = "fd_project_relationship")
public class ProjectRelationshipDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Long id;

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "项目组的项目Id")
    private Long parentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @Column(name = "is_enabled")
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "所属ProgramId")
    private Long programId;

    @Transient
    @ApiModelProperty(value = "项目Code")
    private String projCode;

    @Transient
    @ApiModelProperty(value = "项目Name")
    private String projName;

    @Transient
    @ApiModelProperty(value = "项目人员总数")
    private Long userCount;

    @Transient
    @ApiModelProperty(value = "项目成员")
    private List<UserVO> userVOs;

    @ApiModelProperty(value = "项目群关系创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "负责人id")
    private Long responsibilityUserId;

    @Transient
    @ApiModelProperty(value = "负责人")
    private UserVO responsibilityUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Long getProgramId() {
        return programId;
    }

    @Override
    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProjCode() {
        return projCode;
    }

    public void setProjCode(String projCode) {
        this.projCode = projCode;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public List<UserVO> getUserVOs() {
        return userVOs;
    }

    public void setUserVOs(List<UserVO> userVOs) {
        this.userVOs = userVOs;
    }

    @Override
    @JsonIgnore(value = false)
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    @JsonIgnore(value = false)
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getResponsibilityUserId() {
        return responsibilityUserId;
    }

    public void setResponsibilityUserId(Long responsibilityUserId) {
        this.responsibilityUserId = responsibilityUserId;
    }

    public UserVO getResponsibilityUser() {
        return responsibilityUser;
    }

    public void setResponsibilityUser(UserVO responsibilityUser) {
        this.responsibilityUser = responsibilityUser;
    }
}
