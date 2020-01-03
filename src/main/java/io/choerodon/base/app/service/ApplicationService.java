package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.ApplicationReqVO;
import io.choerodon.base.api.vo.ApplicationRespVO;
import io.choerodon.base.api.vo.ApplicationVO;
import io.choerodon.base.api.vo.ProjectAndAppVO;
import io.choerodon.base.infra.dto.ApplicationDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
public interface ApplicationService {

    /**
     * 创建应用
     *
     * @param projectId        项目Id
     * @param applicationReqVO 应用接收前端数据VO
     * @return 应用DTO
     */
    ApplicationDTO createApplication(Long projectId, ApplicationReqVO applicationReqVO);

    /**
     * 校验名称的唯一性
     *
     * @param projectId 项目Id
     * @param name      应用name
     */
    Boolean checkName(Long projectId, String name);

    /**
     * 根据主键更新应用
     * 只能更新name，description
     *
     * @param applicationReqVO 应用DTO
     * @return 应用DTO
     */
    ApplicationDTO updateApplication(ApplicationReqVO applicationReqVO);

    /**
     * 根据主键更新应用FeedbackToken
     * 只能更新name，description
     *
     * @param id                  应用id
     * @param objectVersionNumber 应用objectVersionNumber
     * @return 应用DTO
     */
    ApplicationDTO updateApplicationFeedbackToken(Long id, Long objectVersionNumber);

    /**
     * 查询用户在项目下的应用
     *
     * @param projectId       项目Id
     * @param name            应用名称
     * @param description     描述
     * @param projectName     项目名称
     * @param creatorRealName 创建人名称
     * @param params          全局过滤参数
     * @param Pageable     分页参数
     * @return PageInfo
     */
    PageInfo<ApplicationRespVO> pagingProjectAppByOptions(Long projectId, String name, String description,
                                                          String projectName, String creatorRealName, String[] params, Pageable Pageable);

    /**
     * 查询用户在组织下的应用
     *
     * @param orgId           组织Id
     * @param type            应用类型
     * @param name            应用名称
     * @param description     描述
     * @param projectName     项目名称
     * @param creatorRealName 创建人名称
     * @param createBy        创建人Id
     * @param participant     参与人Id
     * @param all             是否筛选全部应用（参与的和市场应用）
     * @param params          全局过滤参数
     * @param Pageable     分页参数
     * @return PageInfo
     */
    PageInfo<ApplicationRespVO> pagingOrgAppByOptions(Long orgId, String type, String name, String description, String projectName, String creatorRealName,
                                                      Long createBy, Long participant, Boolean all, String[] params, Pageable Pageable);

    /**
     * 根据Id查应用
     *
     * @param id 应用Id
     * @return 应用信息
     */
    ApplicationDTO getAppById(Long id);

    /**
     * 通过应用token查询应用和项目
     *
     * @param token 应用UUID
     */
    ProjectAndAppVO getProjectAndAppByToken(String token);


    /**
     * 查询可发布应用的简要信息
     * id,name
     *
     * @param projectId    项目Id
     * @param hasGenerated 是否已生成发布申请信息
     * @return
     */
    List<ApplicationDTO> getAppBriefInfo(Long projectId, Boolean hasGenerated);

    /**
     * 校验应用是否存在
     *
     * @param id 应用主键
     * @return 应用信息
     */
    ApplicationDTO checkExist(Long id);

    /**
     * 根据服务ID查询应用信息
     *
     * @param projectID  项目ID
     * @param serviceIds 服务IDs
     * @return 应用信息集合
     */
    List<ApplicationVO> listApplicationInfoByServiceIds(Long projectID, Set<Long> serviceIds);

    /**
     * 删除应用
     *
     * @param applicationId 应用ID
     * @return 应用信息集合
     */
    void deleteApplication(Long applicationId);

    /**
     * 根据已发布应用id查询,应用信息
     *
     * @param publishApplicationId
     * @return
     */
    ApplicationDTO selectByPublishAppId(Long publishApplicationId);
}
