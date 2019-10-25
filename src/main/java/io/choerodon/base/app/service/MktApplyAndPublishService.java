package io.choerodon.base.app.service;

import io.choerodon.base.api.vo.PermissionVerificationResultsVO;
import io.choerodon.base.api.vo.MarketAppServiceVO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.dto.MktPublishVersionInfoDTO;
import io.choerodon.base.infra.dto.devops.AppMarketUploadPayload;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.base.infra.dto.mkt.HarborUserAndUrlVO;

import java.util.List;

/**
 * @author Eugen
 * 此服务用于市场发布信息的申请和发布业务
 */
public interface MktApplyAndPublishService {

    /**
     * 获取发布相关权限校验结果
     * <p>
     * 1.是否配置远程连接令牌
     * 2.远程连接令牌是否过期
     * 3.客户是否可用
     * 4.客户是否有发布权限
     * 5.更新状态是否成功
     *
     * @param projectId 项目主键
     * @return 校验结果
     */
    PermissionVerificationResultsVO verifyPermissions(Long projectId);


    /**
     * 更新市场发布版本状态
     *
     * @param projectId 项目主键
     * @return 更新是否成功
     */
    void updateStatus(Long projectId);

    /**
     * 更新发布状态
     *
     * @param updateVO
     */
    void updatePublishOrFixStatus(ApproveStatusVO updateVO);

    /**
     * 更新审批状态
     *
     * @param updateVO
     */
    void updateApproveStatus(ApproveStatusVO updateVO);

    /**
     * 市场发布申请
     *
     * @param organizationId          组织主键
     * @param mktPublishApplicationId 市场发布信息主键
     * @param mktPublishVersionInfoId 市场发布版本信息主键
     */
    void apply(Long organizationId,
               Long mktPublishApplicationId,
               Long mktPublishVersionInfoId);


    /**
     * 市场发布
     *
     * @param organizationId          组织主键
     * @param mktPublishApplicationId 市场发布信息主键
     * @param mktPublishVersionInfoId 市场发布版本信息主键
     */
    void publish(Long organizationId,
                 Long mktPublishApplicationId,
                 Long mktPublishVersionInfoId);


    /**
     * 市场发布修复版本
     *
     * @param organizationId          组织主键
     * @param mktPublishApplicationId 市场发布信息主键
     * @param mktPublishVersionInfoId 市场发布版本信息主键
     */
    void fixPublish(Long organizationId,
                    Long mktPublishApplicationId,
                    Long mktPublishVersionInfoId,
                    Boolean whetherToFix);


    /**
     * 构造申请数据
     *
     * @param mktPublishApplicationId 市场发布信息主键
     * @param mktPublishVersionInfoId 市场发布版本信息主键
     * @return
     */
    MarketApplicationVO constructingApplicationData(Long mktPublishApplicationId,
                                                    Long mktPublishVersionInfoId);


    /**
     * 构造确认数据
     *
     * @param mktPublishApplicationDTO 市场发布信息主键
     * @param mktPublishVersionInfoDTO 市场发布版本信息主键
     * @param applicationVersionDTO    应用版本信息
     * @return 返回确认信息
     */
    MarketApplicationVO constructingConfirmData(MktPublishApplicationDTO mktPublishApplicationDTO,
                                                MktPublishVersionInfoDTO mktPublishVersionInfoDTO,
                                                ApplicationVersionDTO applicationVersionDTO);


    /**
     * 构造发布事务数据
     *
     * @param mktPublishApplicationDTO 市场发布信息主键
     * @param mktPublishVersionInfoDTO 市场发布版本信息主键
     * @param applicationDTO           应用信息
     * @param applicationVersionDTO    应用版本信息
     * @param harborUserAndUrlVO       harbor相关信息
     * @param fixFlag                  是否是修复版本
     * @return 发布事务payload
     */
    AppMarketUploadPayload constructingPublishPayload(MktPublishApplicationDTO mktPublishApplicationDTO,
                                                      MktPublishVersionInfoDTO mktPublishVersionInfoDTO,
                                                      ApplicationDTO applicationDTO,
                                                      ApplicationVersionDTO applicationVersionDTO,
                                                      HarborUserAndUrlVO harborUserAndUrlVO,
                                                      Boolean fixFlag);

    /**
     * 构造修复服务版本数据
     *
     * @param appVersionId 应用版本主键
     * @return 需要修复的服务版本数据
     */
    List<MarketAppServiceVO> constructingSvcVersionInfo(Long appVersionId);


    /**
     * 构造修复服务版本数据
     *
     * @param appVersionId 应用版本主键
     * @return 需要修复的服务版本数据
     */
    List<MarketAppServiceVO> constructingFixSvcVersionInfo(Long appVersionId);

    /**
     * 更新应用为已生成发布信息
     *
     * @param appId 应用主键
     * @return 应用信息
     */
    ApplicationDTO determineGeneration(Long appId);

    /**
     * 更新应用为未生成发布信息
     *
     * @param appId 应用主键
     * @return 应用信息
     */
    ApplicationDTO cancelGeneration(Long appId);


    /**
     * 校验市场发布版本信息是否存在 且处于可申请状态（未发布/被驳回/已撤销）
     *
     * @param mktPublishVersionInfoId 市场发布版本信息主键
     * @return 市场发布版本信息
     */
    MktPublishVersionInfoDTO checkExistAndCanApply(Long mktPublishVersionInfoId);

    /**
     * 校验市场发布是否存在
     *
     * @param mpaId 市场发布主键
     * @return 市场发布信息
     */
    MktPublishApplicationDTO checkMPAExist(Long mpaId);

    /**
     * 校验市场发布版本信息是否存在
     *
     * @param mpviId 市场发布版本信息主键
     * @return 市场发布版本信息
     */
    MktPublishVersionInfoDTO checkMPVIExist(Long mpviId);

    /**
     * 校验应用是否存在
     *
     * @param appId 应用主键
     * @return 应用信息
     */
    ApplicationDTO checkAppExist(Long appId);

    /**
     * 校验应用版本是否存在
     *
     * @param appVersionId 应用版本主键
     * @return 应用版本信息
     */
    ApplicationVersionDTO checkAppVersionExist(Long appVersionId);
}
