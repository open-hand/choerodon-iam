package io.choerodon.iam.api.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 〈功能简述〉
 * 〈用户数统计VO〉
 *
 * @author wanghao
 * @Date 2020/2/26 14:35
 */
public class UserNumberVO {
    private List<String> dateList = new ArrayList<>();
    @ApiModelProperty("当前总人数集合")
    private List<Long> totalUserNumberList = new ArrayList<>();
    @ApiModelProperty("新增人数集合")
    private List<Long> newUserNumberList = new ArrayList<>();

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<Long> getTotalUserNumberList() {
        return totalUserNumberList;
    }

    public void setTotalUserNumberList(List<Long> totalUserNumberList) {
        this.totalUserNumberList = totalUserNumberList;
    }

    public List<Long> getNewUserNumberList() {
        return newUserNumberList;
    }

    public void setNewUserNumberList(List<Long> newUserNumberList) {
        this.newUserNumberList = newUserNumberList;
    }
}
