package io.choerodon.base.api.vo;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/8/6
 */
public class MarketAppServiceVO {
    private Long id;

    private String marketAppCode;

    private String name;

    private String code;

    private List<MarketServiceVersionDTO> marketServiceVersionDTOS;
    private MarketServiceVersionDTO marketServiceVersionCreateDTO;
    private List<ServiceVersionVO> serviceVersionVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<MarketServiceVersionDTO> getMarketServiceVersionDTOS() {
        return marketServiceVersionDTOS;
    }

    public void setMarketServiceVersionDTOS(List<MarketServiceVersionDTO> marketServiceVersionDTOS) {
        this.marketServiceVersionDTOS = marketServiceVersionDTOS;
    }

    public String getMarketAppCode() {
        return marketAppCode;
    }

    public void setMarketAppCode(String marketAppCode) {
        this.marketAppCode = marketAppCode;
    }

    public MarketServiceVersionDTO getMarketServiceVersionCreateDTO() {
        return marketServiceVersionCreateDTO;
    }

    public void setMarketServiceVersionCreateDTO(MarketServiceVersionDTO marketServiceVersionCreateDTO) {
        this.marketServiceVersionCreateDTO = marketServiceVersionCreateDTO;
    }

    public List<ServiceVersionVO> getServiceVersionVOS() {
        return serviceVersionVOS;
    }

    public void setServiceVersionVOS(List<ServiceVersionVO> serviceVersionVOS) {
        this.serviceVersionVOS = serviceVersionVOS;
    }
}
