package io.choerodon.base.infra.dto.devops;

/**
 * Creator: Runge
 * Date: 2018/5/31
 * Time: 15:48
 * Description:
 */
public class AppServiceVersionUploadPayload {
    private Long id;
    private String version;
    private String publishStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public AppServiceVersionUploadPayload() {
    }

    public AppServiceVersionUploadPayload(Long id, String version) {
        this.id = id;
        this.version = version;
    }
}
