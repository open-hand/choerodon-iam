package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.OrgRemoteTokenConnRecordVO;
import io.choerodon.base.api.vo.RemoteConnectionRecordVO;
import io.choerodon.base.infra.dto.RemoteConnectionRecordDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author Eugen
 */
public interface RemoteConnectionRecordMapper extends Mapper<RemoteConnectionRecordDTO> {
    /**
     * 获取全部远程连接记录
     *
     * @param filterVO 过滤信息
     * @param params   参数
     * @return 远程连接记录
     */
    List<RemoteConnectionRecordVO> searchByParams(@Param("filterVO") RemoteConnectionRecordVO filterVO,
                                                  @Param("params") String[] params);

    /**
     * 查询指定operation的组织配置远程连接记录的相关信息
     *
     * @param filter 筛选条件
     * @return 组织配置远程连接记录的相关信息
     */
    List<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecords(OrgRemoteTokenConnRecordVO filter);

    /**
     * 根据组织ID查询组织配置远程连接记录的相关信息
     *
     * @param filter 筛选条件
     * @return 组织配置远程连接记录相的关信息
     */
    List<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecordsByOrgId(OrgRemoteTokenConnRecordVO filter);
}
