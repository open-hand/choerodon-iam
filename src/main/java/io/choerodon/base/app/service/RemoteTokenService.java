package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.RemoteTokenBase64VO;
import io.choerodon.base.api.dto.CommonCheckResultVO;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.RemoteTokenDTO;

/**
 * @author Eugen
 **/
public interface RemoteTokenService {

    /**
     * 创建新的组织的远程连接令牌（并置旧的令牌为失效）
     *
     * @param createDTO 更新信息（organizationId,name,email）
     * @return 更新结果
     */
    RemoteTokenBase64VO createNewOne(Long organizationId, RemoteTokenDTO createDTO);


    /**
     * 分页过滤组织远程连接令牌的历史记录（不包括最新记录，最新纪录将单独展示）
     *
     * @param organizationId 组织主键
     * @param filterDTO      字段过滤信息
     * @param params         全局过滤信息
     * @return 历史令牌信息列表
     */
    PageInfo<RemoteTokenBase64VO> pagingTheHistoryList(Long organizationId, Pageable Pageable, RemoteTokenDTO filterDTO, String params);


    /**
     * 获取组织最新的远程连接令牌
     *
     * @param organizationId 组织主键
     * @return 最新令牌信息
     */
    RemoteTokenBase64VO getTheLatest(Long organizationId);


    /**
     * 将远程应用令牌置为失效
     *
     * @param organizationId 组织主键
     * @param id             远程连接令牌主键
     * @return 失效信息
     */
    RemoteTokenBase64VO expired(Long organizationId, Long id);


    /**
     * 使远程应用令牌重新生效
     *
     * @param organizationId 组织主键
     * @param id             远程连接令牌主键
     * @return 生效信息
     */
    RemoteTokenBase64VO renewal(Long organizationId, Long id);


    /**
     * 校验Token
     *
     * @param remoteToken token
     * @return 校验结果
     * 1. 校验通过
     * 2. token不存在
     * 3. token已失效
     */
    CommonCheckResultVO checkToken(String remoteToken, String operation);
}