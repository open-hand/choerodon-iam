package io.choerodon.base.api.dto.payload;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/22
 */
public class AppServiceVersionDownloadPayload {
    @ApiModelProperty("应用服务版本")
    private String version;

    @ApiModelProperty("镜像地址")
    private String image;

    @ApiModelProperty("源码文件地址")
    private String repoFilePath;

    @ApiModelProperty("chart文件地址")
    private String chartFilePath;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRepoFilePath() {
        return repoFilePath;
    }

    public void setRepoFilePath(String repoFilePath) {
        this.repoFilePath = repoFilePath;
    }

    public String getChartFilePath() {
        return chartFilePath;
    }

    public void setChartFilePath(String chartFilePath) {
        this.chartFilePath = chartFilePath;
    }

    public AppServiceVersionDownloadPayload() {
    }

    public AppServiceVersionDownloadPayload(String version, String image, String repoFilePath, String chartFilePath) {
        this.version = version;
        this.image = image;
        this.repoFilePath = repoFilePath;
        this.chartFilePath = chartFilePath;
    }
}
