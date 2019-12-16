package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.RouteRuleVO;
import io.choerodon.base.infra.dto.RouteRuleDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RouteRuleMapper
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
public interface RouteRuleMapper extends Mapper<RouteRuleDTO> {

    List<RouteRuleVO> listRouteRules(@Param("code") String code);
}
