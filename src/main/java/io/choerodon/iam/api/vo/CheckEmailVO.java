package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 2022/3/21
 */
public class CheckEmailVO {

    @ApiModelProperty("是否是组织成员")
    private Boolean member;

    @ApiModelProperty("是否存在于组织下")
    private Boolean exist;

    public Boolean getMember() {
        return member;
    }

    public void setMember(Boolean member) {
        this.member = member;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }
}
