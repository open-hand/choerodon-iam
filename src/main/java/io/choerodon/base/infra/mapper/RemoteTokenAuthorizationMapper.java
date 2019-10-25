package io.choerodon.base.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.infra.dto.RemoteTokenAuthorizationDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/05
 */
public interface RemoteTokenAuthorizationMapper extends Mapper<RemoteTokenAuthorizationDTO> {

    /**
     * 查询最新的token
     *
     * @return 令牌信息
     */
    RemoteTokenAuthorizationVO selectLatestToken();

    /**
     * 查询所有远程连接token记录
     *
     * @param name             服务名称
     * @param email            服务编码
     * @param status           服务状态
     * @param organizationName 组织名称
     * @param params           全局过滤参数
     * @return 应用版本DTO
     */
    List<RemoteTokenAuthorizationVO> fulltextSearch(@Param("name") String name,
                                                    @Param("email") String email,
                                                    @Param("status") String status,
                                                    @Param("organizationName") String organizationName,
                                                    @Param("params") String[] params);
}
