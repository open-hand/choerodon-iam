package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.MarketPublishApplicationVO;
import io.choerodon.base.api.vo.PublishAppPageVO;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * @author Eugen
 */
public interface MktPublishApplicationMapper extends Mapper<MktPublishApplicationDTO> {
    /**
     * 查询指定项目下应用数
     *
     * @param projectId 项目ID
     * @return 应用数
     */
    Long countProjectApps(@Param("project_id") Long projectId);

    List<PublishAppPageVO> pageSearchPublishApps(@Param("app_ids") Set<Long> appIds,
                                                 @Param("filterDTO") MarketPublishApplicationVO filterDTO,
                                                 @Param("version") String version,
                                                 @Param("status") String status,
                                                 @Param("params") String[] params);

    /**
     * 校验名称
     *
     * @param name
     * @param refAppId
     * @return
     */
    List<MktPublishApplicationDTO> checkName(@Param("name") String name,
                                             @Param("ref_app_id") Long refAppId);

    /**
     * 获取待更新状态的列表（包括状态：审批中，待确认，已发布）
     *
     * @param projectId 项目Id
     * @return 应用编码/版本名称/修复次数
     */
    List<ApproveStatusVO> getUpdateList(@Param("project_id") Long projectId);
}
