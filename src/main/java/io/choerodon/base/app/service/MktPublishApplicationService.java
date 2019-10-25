package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.*;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * @author Eugen
 **/
public interface MktPublishApplicationService {
    /**
     * 分页过滤查询项目下的市场应用信息
     *
     * @param projectId   项目主键
     * @param Pageable 分页信息
     * @param filterDTO   过滤DTO
     * @param version     版本（过滤信息）
     * @param params      全局搜索信息
     * @return 分页结果
     */
    PageInfo<PublishAppPageVO> pageSearchPublishApps(Long projectId, Pageable Pageable, MarketPublishApplicationVO filterDTO, String version, String status, String[] params);

    /**
     * 查询市场发布应用详情
     *
     * @param appId 应用ID
     * @return 市场发布应用详情
     */
    MarketPublishApplicationVO queryMktPublishAppDetail(Long projectId, Long appId);

    /**
     * 修改市场发布应用信息
     *
     * @param appId
     * @return
     */
    MktPublishApplicationDTO updateMktPublishAppInfo(Long appId, MktPublishApplicationDTO updateVO, Boolean released);

    /**
     * 校验市场应用名称
     *
     * @param id   市场应用Id
     * @param name 名称
     * @return 校验结果
     */
    Boolean checkName(Long id, String name);

    /*----------------------------------申请发布新应用 >>> -------------------------------------*/


    /**
     * 获取申请发布信息时的初始化信息（贡献者/通知邮箱）
     *
     * @param organizationId 组织主键
     * @return （贡献者/通知邮箱）等信息
     */
    MktPublishApplicationDTO getInitialInfo(Long organizationId);

    /**
     * 创建市场发布信息（可申请）
     *
     * @param organizationId 组织主键
     * @param apply          是否申请
     * @param createVO       创建信息
     * @return 创建结果
     */
    MktPublishApplicationDTO createAndApply(Long organizationId,
                                            Boolean apply,
                                            MktPublishApplicationVO createVO);


    /**
     * 创建市场发布信息
     *
     * @param createVO 创建信息
     * @return 创建结果
     */
    MktPublishApplicationDTO create(MktPublishApplicationVO createVO);


    /*----------------------------------<<<  申请发布新应用-------------------------------------*/

    /**
     * 根据服务ID查询应用信息
     *
     * @param appIds 服务IDs
     * @return 应用信息集合
     */
    List<MktPublishApplicationDTO> listApplicationInfoByIds(Set<Long> appIds);

    /**
     * 市场发布一键申请
     *
     * @param organizationId 组织主键
     * @param id             申请市场发布信息主键
     * @return 申请结果
     */
    Boolean oneClickApply(Long organizationId,
                          Long id,
                          Long versionInfoId);


    /**
     * @param Pageable 分页
     * @return 可用应用类型
     */
    PageInfo<AppCategoryDTO> getEnableCategoryList(Pageable Pageable);

    /**
     * 应用类型校验
     *
     * @param categoryName
     * @return 校验结果
     */
    Boolean categoriesCheck(String categoryName);


    /*----------------------------------发布新版本 >>> -------------------------------------*/

    /**
     * 发布新版本 - 查询默认信息
     * <p>
     *
     * @param id 申请市场发布信息主键
     * @return 默认信息
     */
    MktNewVersionVO getBeforeNewVersion(Long id);

    /**
     * 发布新版本(带申请)
     *
     * @param organizationId 组织主键
     * @param id             市场发布信息主键
     * @param apply          是否申请
     * @param newVersionVO   发布新版本信息
     * @return 新版本信息
     */
    MktNewVersionVO createNewVersionAndApply(Long organizationId,
                                             Long id,
                                             Boolean apply,
                                             MktNewVersionVO newVersionVO);


    /**
     * 发布新版本
     *
     * @param id           市场发布信息主键
     * @param newVersionVO 发布新版本信息
     * @return 新版本信息
     */
    MktNewVersionVO createNewVersion(Long id,
                                     MktNewVersionVO newVersionVO);

    /*----------------------------------<<< 发布新版本 -------------------------------------*/


    /*----------------------------------确认信息 >>> -------------------------------------*/

    /**
     * 确认信息 - 查询默认信息
     * <p>
     *
     * @param id            申请市场发布信息主键
     * @param versionInfoId 市场发布版本信息主键
     * @return 默认信息
     */
    MktConfirmVO getBeforeConfirm(Long id,
                                  Long versionInfoId);

    /**
     * 确认信息(带发布)
     * <p>
     *
     * @param organizationId 组织主键
     * @param id             市场发布信息主键
     * @param versionInfoId  市场发布版本信息主键
     * @param publish        是否发布
     * @param confirmVO      确认信息
     * @return 确认信息
     */
    MktConfirmVO confirmAndPublish(Long organizationId,
                                   Long id,
                                   Long versionInfoId,
                                   Boolean publish,
                                   MktConfirmVO confirmVO);

    /**
     * 确认信息
     * <p>
     *
     * @param id            市场发布信息主键
     * @param versionInfoId 市场发布版本信息主键
     * @param confirmVO     确认信息
     * @return 确认信息
     */
    MktConfirmVO confirm(Long id,
                         Long versionInfoId,
                         MktConfirmVO confirmVO);


    /**
     * 重新发布
     *
     * @param organizationId 组织主键
     * @param id             市场发布信息主键
     * @param versionInfoId  市场发布版本信息主键
     * @return 发布成功
     */
    Boolean republish(Long organizationId,
                      Long id,
                      Long versionInfoId);

    /*----------------------------------<<< 确认信息 -------------------------------------*/

    /**
     * 撤销发布
     *
     * @param id            市场发布主键
     * @param versionInfoId 市场发布版本信息主键
     */
    Boolean revocation(Long id,
                       Long versionInfoId);

    /**
     * 删除市场发布信息 及 版本发布信息
     *
     * @param id            市场发布主键
     * @param versionInfoId 版本发布信息主键
     */
    void delete(Long id,
                Long versionInfoId);


    /**
     * 上传图片到SaaS平台
     *
     * @param file   图片
     * @param rotate 顺时针旋转的角度
     * @param axisX  裁剪的X轴
     * @param axisY  裁剪的Y轴
     * @param width  裁剪的宽度
     * @param height 裁剪的高度
     * @return 图片地址
     */
    String cutImage(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

}
