package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author wanghao
 * @Date 2019/8/21 17:15
 */
public class MarketServiceVO {

    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "应用编码")
    private String marketAppCode;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "服务编码")
    private String code;

    private List<MarketServiceVersionVO> marketServiceVersionVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketAppCode() {
        return marketAppCode;
    }

    public void setMarketAppCode(String marketAppCode) {
        this.marketAppCode = marketAppCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MarketServiceVersionVO> getMarketServiceVersionVOS() {
        return marketServiceVersionVOS;
    }

    public void setMarketServiceVersionVOS(List<MarketServiceVersionVO> marketServiceVersionVOS) {
        this.marketServiceVersionVOS = marketServiceVersionVOS;
    }
}
