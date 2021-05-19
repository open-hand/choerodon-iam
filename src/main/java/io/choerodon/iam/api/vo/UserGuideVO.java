package io.choerodon.iam.api.vo;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:34
 */
public class UserGuideVO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty("指引编码")
    private String code;

    @ApiModelProperty("指引标题")
    private String title;

    private List<UserGuideStepVO> userGuideStepVOList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UserGuideStepVO> getUserGuideStepVOList() {
        return userGuideStepVOList;
    }

    public void setUserGuideStepVOList(List<UserGuideStepVO> userGuideStepVOList) {
        this.userGuideStepVOList = userGuideStepVOList;
    }
}
