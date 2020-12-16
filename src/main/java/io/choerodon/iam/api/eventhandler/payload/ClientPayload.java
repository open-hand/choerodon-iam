package io.choerodon.iam.api.eventhandler.payload;

/**
 * @author scp
 * @date 2020/12/16
 * @description
 */
public class ClientPayload {
    private Long clientId;
    private Long tenantId;

    public ClientPayload(Long clientId, Long tenantId) {
        this.clientId = clientId;
        this.tenantId = tenantId;
    }

    public ClientPayload() {
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
