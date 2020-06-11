package io.choerodon.iam.infra.dto.payload;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netflix.appinfo.InstanceInfo;

public class EurekaEventPayload {
    private static final String VERSION_STR = "version";
    private static final String DEFAULT_VERSION_NAME = "unknown";
    private String id;
    private String status;
    private String appName;
    private String version;
    private String instanceAddress;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            locale = "zh",
            timezone = "GMT+8"
    )
    private Date createTime;
    private String apiData;

    public EurekaEventPayload(InstanceInfo instanceInfo) {
        this.id = instanceInfo.getId();
        this.status = instanceInfo.getStatus().name();
        this.appName = instanceInfo.getAppName().toLowerCase();
        this.version = instanceInfo.getMetadata().get("version");
        this.version = this.version == null ? "unknown" : this.version;
        this.instanceAddress = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
        this.createTime = new Date();
    }

    public EurekaEventPayload() {
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstanceAddress() {
        return this.instanceAddress;
    }

    public void setInstanceAddress(String instanceAddress) {
        this.instanceAddress = instanceAddress;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getApiData() {
        return this.apiData;
    }

    public void setApiData(String apiData) {
        this.apiData = apiData;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            EurekaEventPayload that = (EurekaEventPayload) o;
            return Objects.equals(this.id, that.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id});
    }

    public String toString() {
        return "EurekaEventPayload{id='" + this.id + '\'' + ", status='" + this.status + '\'' + ", appName='" + this.appName + '\'' + ", version='" + this.version + '\'' + ", instanceAddress='" + this.instanceAddress + '\'' + ", createTime=" + this.createTime + '}';
    }
}
