package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.api.vo.WorkGroupVO;
import io.choerodon.iam.app.service.WorkGroupProjectRelService;
import io.choerodon.iam.infra.dto.WorkGroupDTO;
import io.choerodon.iam.infra.dto.WorkGroupProjectRelDTO;
import io.choerodon.iam.infra.mapper.WorkGroupMapper;
import io.choerodon.iam.infra.mapper.WorkGroupProjectRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkGroupProjectRelServiceImpl implements WorkGroupProjectRelService {
    @Autowired
    private WorkGroupProjectRelMapper workGroupProjectRelMapper;
    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertOrUpdateOrDeleteRelation(Long organizationId, Long projectId, Long workgroupId) {
        WorkGroupProjectRelDTO workGroupProjectRelSearchDTO = new WorkGroupProjectRelDTO();
        workGroupProjectRelSearchDTO.setOrganizationId(organizationId);
        workGroupProjectRelSearchDTO.setProjectId(projectId);

        WorkGroupProjectRelDTO result = workGroupProjectRelMapper.selectOne(workGroupProjectRelSearchDTO);
        // 关联关系不存在，但是workGroupId不为null，表示插入
        if (result == null) {
            if (workgroupId != null) {
                insert(organizationId, projectId, workgroupId);
            }
        } else {
            // 表示移除关联关系
            if (workgroupId == null) {
                workGroupProjectRelMapper.deleteByPrimaryKey(result.getId());
            } else if (result.getWorkGroupId().equals(workgroupId)) {
                // 表示关联关系未发生变化，不做处理
                return;
            } else {
                // 表示需要更新关联关系
                workGroupProjectRelMapper.deleteByPrimaryKey(result.getId());
                insert(organizationId, projectId, workgroupId);
            }
        }
    }

    private void insert(Long organizationId, Long projectId, Long workGroupId) {
        WorkGroupProjectRelDTO workGroupProjectRelDTOToInsert = new WorkGroupProjectRelDTO();
        workGroupProjectRelDTOToInsert.setProjectId(projectId);
        workGroupProjectRelDTOToInsert.setOrganizationId(organizationId);
        workGroupProjectRelDTOToInsert.setWorkGroupId(workGroupId);

        workGroupProjectRelMapper.insert(workGroupProjectRelDTOToInsert);
    }

    @Override
    public Map<Long, String> listByProjectIds(Long organizationId, Set<Long> projectIds) {
        // 查出组织下的所有工作组
        List<WorkGroupDTO> allWorkGroupDTO = workGroupMapper.selectByOrganiztionId(organizationId);
        Map<Long, WorkGroupDTO> workGroupDTOMappedById = allWorkGroupDTO.stream().collect(Collectors.toMap(WorkGroupDTO::getId, Function.identity()));

        // 查询项目所属工作组
        List<WorkGroupVO> workGroupVOSByProjectIdsIds = workGroupProjectRelMapper.selectWorkGroupByProjectIds(organizationId, projectIds);
        Map<Long, WorkGroupVO> workGroupVOSMappedByProjectId = workGroupVOSByProjectIdsIds.stream().collect(Collectors.toMap(WorkGroupVO::getProjectId, Function.identity()));

        Map<Long, String> workGroupNameMap = new HashMap<>();
        Map<Long, String> projectWorkGroupMap = new HashMap<>();
        projectIds.forEach(id -> {
            WorkGroupVO workGroupVO = workGroupVOSMappedByProjectId.get(id);
            if (workGroupVO != null) {
                String workGroupName = workGroupNameMap.get(workGroupVO.getId());
                if (workGroupName == null) {
                    workGroupName = WorkGroupServiceImpl.calculateWorkGroup(workGroupVO, workGroupDTOMappedById, "-");
                    workGroupNameMap.put(workGroupVO.getId(), workGroupName);
                }
                projectWorkGroupMap.put(id, workGroupName);
            }
        });
        return projectWorkGroupMap;
    }

    @Override
    public List<Long> listProjectIdsByWorkId(List<Long> workGroupIds) {
        if (CollectionUtils.isEmpty(workGroupIds)) {
            return new ArrayList<>();
        }
        return workGroupProjectRelMapper.listProjectIdsByWorkGroupId(workGroupIds);
    }
}
