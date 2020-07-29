package io.choerodon.iam.api.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能简述〉
 * 〈bar-label-rotation展示VO〉
 *
 * @author wanghao
 * @since 2020/2/26 9:13
 */
public class BarLabelRotationVO {

    private List<String> dateList = new ArrayList<>();

    private List<BarLabelRotationItemVO> projectDataList = new ArrayList<>();


    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<BarLabelRotationItemVO> getProjectDataList() {
        return projectDataList;
    }

    public void setProjectDataList(List<BarLabelRotationItemVO> projectDataList) {
        this.projectDataList = projectDataList;
    }

}
