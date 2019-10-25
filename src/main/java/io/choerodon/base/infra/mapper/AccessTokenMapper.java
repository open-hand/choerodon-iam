package io.choerodon.base.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.infra.dto.AccessTokenDTO;
import io.choerodon.mybatis.common.Mapper;


/**
 * @author Eugen
 */
public interface AccessTokenMapper extends Mapper<AccessTokenDTO> {

    List<AccessTokenDTO> selectTokens(@Param("userName") String userName,
                                      @Param("clientId") String clientId,
                                      @Param("params") String params);

    List<AccessTokenDTO> selectTokenList(@Param("tokenIds") List<String> tokenIds);
}
