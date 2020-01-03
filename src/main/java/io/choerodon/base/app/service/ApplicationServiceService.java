package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionVO;
import io.choerodon.base.infra.dto.devops.AppServiceRepVO;
import io.choerodon.base.infra.dto.devops.AppServiceVO;
import io.choerodon.base.infra.dto.devops.AppServiceVersionUploadPayload;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/7
 */
public interface ApplicationServiceService {

    /**
     * 添加应用关联服务
     *
     * @param applicationId 应用Id
     * @param serviceIds    应用服务Ids
     */
    void addAppSvcRef(Long applicationId, Set<Long> serviceIds);

    /**
     * 删除应用关联服务
     *
     * @param applicationId 应用Id
     * @param serviceIds    服务Ids
     */
    void deleteAppSvcRef(Long projectId, Long applicationId, Set<Long> serviceIds);

    /**
     * 分页查询应用下的服务
     *
     * @param applicationId 应用Id
     * @param name          服务名称
     * @param code          服务编码
     * @param type          服务类型
     * @param params        全局过滤参数
     * @return 应用服务VO
     */
    PageInfo<AppServiceDetailsVO> pagingAppSvcByOptions(Pageable Pageable, Long projectId, Long applicationId,
                                                        String name, String code, String type, String[] params);

    /**
     * 查询组织下的服务
     *
     * @param organizationId 组织Id
     * @param appType        应用类型
     * @return 服务Id集合
     */
    Set<Long> listService(Long organizationId, String appType);

    /**
     * 查询服务版本
     *
     * @param organizationId 组织Id
     * @param appType        应用类型
     * @return 服务版本Id集合
     */
    Set<Long> listSvcVersion(Long organizationId, String appType);

    /**
     * 查询应用下的服务
     *
     * @param organizationId 组织Id
     * @param applicationId  应用Id
     * @return 应用服务VO
     */
    List<AppServiceDetailsVO> listAppSvc(Long organizationId, Long applicationId);

    /**
     * 查询项目下的服务
     *
     * @param projectId    项目Id
     * @param appServiceVO 服务类型
     * @param params       全局过滤参数
     * @param filter       是否过滤掉已添加过的服务
     * @return 应用服务VO
     */
    PageInfo<AppServiceVO> pagingProServiceByOptions(Pageable Pageable, Long projectId, Long applicationId,
                                                     AppServiceVO appServiceVO, String[] params, Boolean filter);

    /**
     * 查询共享服务
     *
     * @param projectId    项目Id
     * @param appServiceVO 服务类型
     * @param params       全局过滤参数
     * @param filter       是否过滤掉已添加过的服务
     * @return 应用服务VO
     */
    PageInfo<AppServiceRepVO> pagingSharedServiceByOptions(Pageable Pageable, Long projectId, Long applicationId,
                                                           AppServiceVO appServiceVO, String[] params, Boolean filter);

    /**
     * 根据应用服务ID查询所对应的应用版本
     *
     * @param appServiceId 应用服务Id
     * @return 应用服务版本信息列表
     */
    List<AppServiceVersionUploadPayload> listVersionsByAppServiceId(Long appServiceId);

    /**
     * 根据服务id,查询服务下所有版本（支持模糊搜索）
     * @param projectId
     * @param serviceId
     * @param version
     * @return
     */
    List<AppServiceVersionVO> getAppSvcWithAllVersion(Long projectId, String serviceId, String version);
}
