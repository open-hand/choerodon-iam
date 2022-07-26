package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.MoveWorkGroupVO;
import io.choerodon.iam.api.vo.WorkGroupTreeVO;
import io.choerodon.iam.api.vo.WorkGroupVO;

import java.util.List;
import java.util.Map;

/**
 * @author zhaotianxin
 * @date 2021-11-08 15:31
 */
public interface WorkGroupService {

    WorkGroupTreeVO queryWorkGroupTree(Long organizationId);

    WorkGroupVO create(Long organizationId, WorkGroupVO workGroupVO);

    WorkGroupVO update(Long organizationId, WorkGroupVO workGroupVO);

    void delete(Long organizationId, Long workGroupId);

    WorkGroupVO queryById(Long organizationId, Long workGroupId);

    Boolean checkName(Long organizationId, Long parentId, String name);

    WorkGroupVO moveWorkGroup(Long organizationId, Long parentId, MoveWorkGroupVO moveWorkGroupVO);

    List<Long> listChildrenWorkGroup(Long organizationId, List<Long> workGroupIds);

    Map<Long, List<String>> listGroupNameByUserIds(Long organizationId, List<Long> userIds);

    /**
     * 内部创建工作组
     *
     * @param workGroupVO
     * @param organizationId
     */
    Long createWorkGroupInternal(Long organizationId, WorkGroupVO workGroupVO);

    List<WorkGroupVO> listWorkGroupByUserIds(Long organizationId, List<Long> userIds);

    List<WorkGroupVO> listWorkGroups(Long organizationId);

}
