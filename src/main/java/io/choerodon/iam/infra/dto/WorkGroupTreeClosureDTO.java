package io.choerodon.iam.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhaotianxin
 * @date 2021-11-10 19:22
 */
@Table(name = "fd_work_group_tree_closure")
@ModifyAudit
@VersionAudit
public class WorkGroupTreeClosureDTO  extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long ancestorId;

    private Long descendantId;

    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }

    public Long getDescendantId() {
        return descendantId;
    }

    public void setDescendantId(Long descendantId) {
        this.descendantId = descendantId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
