package io.choerodon.base.api.dto.payload;

public class LdapAutoTaskEventPayload {
    private Long ldapAutoId;
    private Long deleteQuartzTaskId;
    private Long organizationId;
    private Boolean active;

    public Long getLdapAutoId() {
        return ldapAutoId;
    }

    public void setLdapAutoId(Long ldapAutoId) {
        this.ldapAutoId = ldapAutoId;
    }

    public Long getDeleteQuartzTaskId() {
        return deleteQuartzTaskId;
    }

    public void setDeleteQuartzTaskId(Long deleteQuartzTaskId) {
        this.deleteQuartzTaskId = deleteQuartzTaskId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
