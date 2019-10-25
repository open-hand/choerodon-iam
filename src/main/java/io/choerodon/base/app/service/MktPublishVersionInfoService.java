package io.choerodon.base.app.service;

import io.choerodon.base.api.vo.MktPublishAppVersionVO;
import io.choerodon.base.api.vo.MktUnPublishVersionInfoVO;
import io.choerodon.base.api.vo.MktVersionUpdateVO;
import io.choerodon.base.infra.dto.MktPublishVersionInfoDTO;

import java.util.List;

/**
 * @author Eugen
 **/
public interface MktPublishVersionInfoService {
    /**
     * 市场发布页查询应用版本列表
     *
     * @param appId 应用ID
     * @return 版本信息集合
     */
    List<MktPublishAppVersionVO> listMktAppVersions(Long appId);

    /**
     * 创建市场发布版本信息
     *
     * @param createDTO 创建信息
     */
    MktPublishVersionInfoDTO create(MktPublishVersionInfoDTO createDTO);


    /**
     * 更新市场发布版本信息
     *
     * @param updateDTO 更新信息
     */
    MktPublishVersionInfoDTO update(MktPublishVersionInfoDTO updateDTO);

    /**
     * 校验市场发布版本信息是否存在
     * 查询市场发布应用版本详情
     *
     * @param versionId 版本ID
     * @return 版本详情
     */
    MktPublishAppVersionVO queryMktPublishAppVersionDetail(Long versionId);

    /**
     * 校验市场发布版本信息是否存在
     *
     * @param applicationVersionId 应用版本主键
     * @return 市场发布版本信息
     */
    MktPublishVersionInfoDTO checkExistByAppVersionId(Long applicationVersionId);

    /**
     * 更新市场应用信息及版本信息
     *
     * @param projectId 项目id
     * @param updateVO  更新信息
     * @return
     */
    MktPublishAppVersionVO updateUnPublished(Long organizationId, Long projectId, MktUnPublishVersionInfoVO updateVO, Boolean apply);

    /**
     * 检验未发布版本信息是否存在
     *
     * @param id versionId
     * @return MktPublishVersionInfoDTO
     */
    MktPublishVersionInfoDTO checkMktPublishVersionInfoExit(Long id);

    /**
     * 发布失败接口
     *
     * @param id
     * @param errorCode
     * @param fixFlag
     * @return
     */
    Boolean publishFail(Long id, String errorCode, Boolean fixFlag, Long projectId);

    /**
     * 删除市场发布版本信息
     *
     * @param id 市场发布版本信息主键
     */
    void delete(Long id);

    /**
     * 撤销版本申请
     *
     * @param id 市场发布版本信息主键
     * @return 更新后信息
     */
    MktPublishVersionInfoDTO revocation(Long id);


    /**
     * 查询更新已发布应用版本
     *
     * @param id 市场发布版本信息主键
     * @return 查询信息
     */
    MktVersionUpdateVO getBeforeUpdateAndFix(Long id);

    /**
     * 更新已发布应用版本
     *
     * @param organizationId 组织主键
     * @param publishAppId   市场发布信息主键
     * @param id             市场发布版本信息主键
     * @param updateVO       更新信息
     * @return 更新结果
     */
    MktVersionUpdateVO updateAndFix(Long organizationId,
                                    Long publishAppId,
                                    Long id,
                                    MktVersionUpdateVO updateVO);


    /**
     * 更新已发布应用版本
     *
     * @param publishAppId 市场发布信息主键
     * @param id           市场发布版本信息主键
     * @return 更新结果
     */
    MktVersionUpdateVO refix(Long publishAppId,
                             Long id);
}
