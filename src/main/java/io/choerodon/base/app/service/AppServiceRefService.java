package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * @author wanghao
 * @Date 2019/9/13 12:48
 */
public interface AppServiceRefService {

    /**
     * 分页查询应用指定版本下的所有服务,以及服务版本
     * @param projectId
     * @param Pageable
     * @param applicationId
     * @param name
     * @return
     */
    PageInfo<AppServiceDetailsVO> pagingServicesWithVersionsByAppId(Long projectId, Pageable Pageable, Long applicationId, String name);

    /**
     * 根据应用ID查询包含的应用服务的ID列表
     *
     * @param appId 应用ID
     * @return 应用服务ID集合
     */
    Set<Long> listAppServiceIdsByAppId(Long appId);

}
