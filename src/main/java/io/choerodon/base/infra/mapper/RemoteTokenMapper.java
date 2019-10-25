package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.dto.RemoteTokenBase64VO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.RemoteTokenDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Eugen
 */
public interface RemoteTokenMapper extends Mapper<RemoteTokenDTO> {


    /**
     * 获取组织下最新的远程连接令牌
     *
     * @param orgId 组织ID
     * @return 令牌信息
     */
    RemoteTokenBase64VO selectLatestUnderOrg(@Param("orgId") Long orgId);


    /**
     * 过滤获取组织下历史远程连接令牌（不包括指定的主键）
     *
     * @param orgId      组织ID
     * @param excludedId 排除id
     * @param filterDTO  过滤参数信息
     * @param params     全局模糊搜索参数
     * @return 令牌信息列表
     */
    List<RemoteTokenBase64VO> filterHistoryUnderOrg(@Param("orgId") Long orgId,
                                                    @Param("excludedId") Long excludedId,
                                                    @Param("filterDTO") RemoteTokenDTO filterDTO,
                                                    @Param("params") String params);

    /**
     * @param remoteToken
     * @return 所属组织名称
     */
    OrganizationDTO selectOrganization(@Param("remote_token") String remoteToken);
}
