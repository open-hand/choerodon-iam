package io.choerodon.base.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.AccessTokenDTO;

/**
 * @author Eugen
 **/
public interface AccessTokenService {

    /**
     * 在用户Id下查询用户下所有生效的客户端名称符合条件的token
     *
     * @param Pageable  分页对象
     * @param clientName   客户端名称
     * @param currentToken 当前Token
     * @param params       模糊匹配参数
     * @return Token列表
     */
    PageInfo<AccessTokenDTO> pagedSearch(Pageable Pageable, String clientName, String currentToken, String params);

    /**
     * 手动失效用户已存在的token
     *
     * @param tokenId      tokenId
     * @param currentToken 当前token
     */
    void delete(String tokenId, String currentToken);

    /**
     * 批量失效用户的token
     *
     * @param tokenIds     token列表
     * @param currentToken 当前token
     */
    void deleteList(List<String> tokenIds, String currentToken);

    /**
     * 删除所有过期的token
     */
    void deleteAllExpiredToken(Map<String, Object> map);
}
