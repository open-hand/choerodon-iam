package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.ApplicationRespVO;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
public interface ApplicationMapper extends Mapper<ApplicationDTO> {

    List<ApplicationRespVO> fulltextSearchInProject(@Param("projectId") Long projectId,
                                                    @Param("name") String name,
                                                    @Param("description") String description,
                                                    @Param("projectName") String projectName,
                                                    @Param("creatorRealName") String creatorRealName,
                                                    @Param("params") String[] params);

    List<ApplicationRespVO> fulltextSearchInOrganization(@Param("organizationId") Long organizationId,
                                                         @Param("name") String name,
                                                         @Param("description") String description,
                                                         @Param("projectName") String projectName,
                                                         @Param("creatorRealName") String creatorRealName,
                                                         @Param("createBy") Long createBy,
                                                         @Param("participant") Long participant,
                                                         @Param("params") String[] params);

    List<ApplicationRespVO> fulltextSearchMarketAppInOrganization(@Param("organizationId") Long organizationId,
                                                                  @Param("type") String type,
                                                                  @Param("name") String name,
                                                                  @Param("description") String description,
                                                                  @Param("creatorRealName") String creatorRealName,
                                                                  @Param("createBy") Long createBy,
                                                                  @Param("params") String[] params);

    /**
     * 获取应用的简要信息
     *
     * @param filterDTO 过滤信息
     * @return 简要信息
     */
    List<ApplicationDTO> getBriefInfo(@Param("filterDTO") ApplicationDTO filterDTO);

    /**
     * 根据已发布应用id,查询应用信息
     *
     * @param publishApplicationId
     * @return
     */
    ApplicationDTO selectByPublishAppId(@Param("publishApplicationId") Long publishApplicationId);
}
