package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.OrgRemoteTokenConnRecordVO;
import io.choerodon.base.api.vo.RemoteConnectionRecordVO;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.RemoteConnectionRecordDTO;

/**
 * @author Eugen
 **/
public interface RemoteConnectionRecordService {

    /**
     * 远程连接令牌测试记录
     *
     * @param remoteTokenId 远程Id
     * @return 记录结果
     */
    RemoteConnectionRecordDTO successRecord(Long remoteTokenId, String operation);

    /**
     * 分页过滤远程连接记录
     *
     * @param filterVO    过滤信息
     * @param Pageable 请求
     * @param params      全局过滤参数
     * @return 分页结果
     */
    PageInfo<RemoteConnectionRecordVO> pageSearch(RemoteConnectionRecordVO filterVO, Pageable Pageable, String[] params);

    /**
     * 查询指定operation的组织、远程令牌、连接记录相关信息
     *
     * @param filter 帅选条件
     * @return 配置过远程令牌的组织相关信息
     */
    PageInfo<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecords(Pageable Pageable, OrgRemoteTokenConnRecordVO filter);

    /**
     * 根据组织ID查询组织配置远程连接记录的相关信息
     *
     * @param filter 帅选条件
     * @return 组织配置远程连接记录相的关信息
     */
    PageInfo<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecordsByOrgId(Pageable Pageable, OrgRemoteTokenConnRecordVO filter);
}