package io.choerodon.iam.app.service;

import java.util.Date;
import java.util.List;

import io.choerodon.iam.infra.dto.ProjectDTO;

/**
 * @author scp
 * @since 2022/4/19
 */
public interface BusinessService {
    /**
     * 设置用户项目下进场时间和撤场时间
     *
     * @param projectId
     * @param userId
     * @param scheduleEntryTime
     * @param scheduleExitTime
     */
    void setUserProjectDate(Long projectId, Long userId, String scheduleEntryTime, String scheduleExitTime);

    /**
     * 界面上使用
     *
     * @param projectId
     * @param userId
     * @param scheduleEntryTime
     * @param scheduleExitTime
     */
    void setUserProjectDate(Long projectId, Long userId, Date scheduleEntryTime, Date scheduleExitTime);

    /**
     * 个人接收配置界面使用
     * 查询个人能看到所有项目
     *
     * @param userId
     * @param projectDTO
     * @return
     */
    List<ProjectDTO> selectProjectsByUserId(Long userId, ProjectDTO projectDTO);

    /**
     * 处理项目的工作组和类别
     *
     * @param organizationId
     * @param projectId
     * @param classficationId
     */
    void setProjectClassfication(Long organizationId, Long projectId, Long classficationId);

    /**
     * 删除项目类别关联关系
     *
     * @param projectId
     */
    void deleteProjectClassficationRelation(Long projectId);

    /**
     * 更新工作组的关联关系
     *
     * @param organizationId
     * @param id
     * @param workGroupId
     */
    void setWorkGroup(Long organizationId, Long id, Long workGroupId);
}
