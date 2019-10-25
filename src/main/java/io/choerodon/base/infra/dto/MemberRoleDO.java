package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author superlee
 */
@Table(name = "iam_member_role")
public class MemberRoleDO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roleId;

    private Long memberId;

    private String memberType;

    private Long sourceId;

    private String sourceType;

    public Long getId() {
        return id;
    }

    public MemberRoleDO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public MemberRoleDO setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getMemberType() {
        return memberType;
    }

    public MemberRoleDO setMemberType(String memberType) {
        this.memberType = memberType;
        return this;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public MemberRoleDO setSourceId(Long sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public MemberRoleDO setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public Long getMemberId() {
        return memberId;
    }

    public MemberRoleDO setMemberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    @Override
    public String toString() {
        return "MemberRoleDO{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", memberId=" + memberId +
                ", memberType='" + memberType + '\'' +
                ", sourceId=" + sourceId +
                ", sourceType='" + sourceType + '\'' +
                '}';
    }
}
