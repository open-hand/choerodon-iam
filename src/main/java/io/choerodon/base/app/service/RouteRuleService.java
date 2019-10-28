package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.RouteRuleVO;
import io.choerodon.base.infra.dto.RouteRuleDTO;
import org.springframework.data.domain.Pageable;

/**
 * RouteRuleService
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
public interface RouteRuleService {
    /**
     * 分页查询路由规则信息
     *
     * @param pageable        分页参数
     * @param name            路由名称
     * @param description     路由描述
     * @param hostNumber      主机数
     * @param userNumber      用户数
     * @param params          全局搜索参数
     * @return                路由信息列表
     */
    PageInfo<RouteRuleVO> listRouteRules(Pageable pageable, String name, String description, Long hostNumber, Long userNumber, String[] params);

    /**
     * 根据ID查询路由的详细信息
     *
     * @param id
     * @return
     */
    RouteRuleVO queryRouteRuleDetailById(Long id);

    /**
     * 添加路由规则信息
     *
     * @param routeRuleVO    路由规则DTO
     * @return                添加成功返回添加成功的routeRuleDTO
     */
    RouteRuleVO routeRuleInsert(RouteRuleVO routeRuleVO);

    /**
     * 根据路由ID删除路由信息
     *
     * @param id   路由ID
     * @return     操作结果 bool值
     */
    Boolean deleteRouteRuleById(Long id);

    /**
     * 路由规则信息更新
     *
     * @param routeRuleVO    更新路由信息
     * @return                更新完成路由规则信息
     */
    RouteRuleVO routeRuleUpdate(RouteRuleVO routeRuleVO, Long ObjectVersionNumber);

    /**
     * 路由名称重复校验
     *
     * @param routeRuleDTO  校验信息
     * @return             校验结果
     */
    Boolean checkName(RouteRuleDTO routeRuleDTO);

    /**
     * 检验路由是否存在
     *
     * @param routeRuleDTO 查询路由信息
     * @return             查询结果
     */
    RouteRuleDTO checkRouteRuleExist(RouteRuleDTO routeRuleDTO);
}
