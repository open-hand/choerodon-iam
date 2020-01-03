package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.ApplicationVersionVO;
import io.choerodon.base.api.vo.ApplicationVersionWithStatusVO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
public interface ApplicationVersionMapper extends Mapper<ApplicationVersionDTO> {

    List<ApplicationVersionVO> fulltextSearch(@Param("applicationId") Long applicationId,
                                              @Param("version") String version,
                                              @Param("description") String description,
                                              @Param("status") String status,
                                              @Param("params") String[] params);

    ApplicationVersionVO selectVersionWithPublishStatusById(@Param("id") Long id);


    /**
     * 查询应用下的版本 及 版本的发布信息状态
     *
     * @param applicationId 应用主键
     * @return 查询结果
     */
    List<ApplicationVersionWithStatusVO> selectWithStatus(@Param("application_id") Long applicationId);

}
