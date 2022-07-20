package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkGroupProjectRelService {

    /**
     * 插入、更新或者删除关联关系
     *
     * @param organizationId
     * @param projectId
     * @param workgroupId
     */
    void insertOrUpdateOrDeleteRelation(Long organizationId, Long projectId, Long workgroupId);

    /**
     * 根据项目ids查询对应的工作组
     *
     * @param organizationId
     * @param projectIds
     * @return
     */
    Map<Long, String> listByProjectIds(Long organizationId, Set<Long> projectIds);

    /**
     * 根据工作组id查询出关联的项目
     * @param workGroupIds
     * @return
     */
    List<Long> listProjectIdsByWorkId(List<Long> workGroupIds);
}
