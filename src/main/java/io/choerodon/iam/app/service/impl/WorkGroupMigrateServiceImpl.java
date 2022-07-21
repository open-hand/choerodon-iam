package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.api.vo.agile.MigrateWorkGroupDataVO;
import io.choerodon.iam.app.service.WorkGroupMigrateService;
import io.choerodon.iam.infra.dto.WorkGroupDTO;
import io.choerodon.iam.infra.dto.WorkGroupProjectRelDTO;
import io.choerodon.iam.infra.dto.WorkGroupTreeClosureDTO;
import io.choerodon.iam.infra.dto.WorkGroupUserRelDTO;
import io.choerodon.iam.infra.mapper.WorkGroupMapper;
import io.choerodon.iam.infra.mapper.WorkGroupProjectRelMapper;
import io.choerodon.iam.infra.mapper.WorkGroupTreeClosureMapper;
import io.choerodon.iam.infra.mapper.WorkGroupUserRelMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author superlee
 * @since 2022-07-21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkGroupMigrateServiceImpl implements WorkGroupMigrateService {

    @Autowired
    private WorkGroupMapper workGroupMapper;
    @Autowired
    private WorkGroupProjectRelMapper workGroupProjectRelMapper;
    @Autowired
    private WorkGroupTreeClosureMapper workGroupTreeClosureMapper;
    @Autowired
    private WorkGroupUserRelMapper workGroupUserRelMapper;

    @Override
    public void migrate(MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        if (ObjectUtils.isEmpty(migrateWorkGroupDataVO)) {
            return;
        }
        syncWorkGroup(migrateWorkGroupDataVO);
        syncWorkGroupProjectRel(migrateWorkGroupDataVO);
        syncWorkGroupTreeClosures(migrateWorkGroupDataVO);
        syncWorkGroupUserRel(migrateWorkGroupDataVO);
    }

    private void syncWorkGroupUserRel(MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        List<WorkGroupUserRelDTO> workGroupUserRel = migrateWorkGroupDataVO.getWorkGroupUserRel();
        if (ObjectUtils.isEmpty(workGroupUserRel)) {
            return;
        }
        Set<Long> ids = workGroupUserRel.stream().map(WorkGroupUserRelDTO::getId).collect(Collectors.toSet());
        Set<Long> existedIds =
                workGroupUserRelMapper.selectByIds(StringUtils.join(ids, ",")).stream().map(WorkGroupUserRelDTO::getId).collect(Collectors.toSet());
        List<WorkGroupUserRelDTO> insertList =
                workGroupUserRel.stream().filter(x -> !existedIds.contains(x.getId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(insertList)) {
            return;
        }
        workGroupUserRelMapper.insertSyncData(insertList);
    }

    private void syncWorkGroupTreeClosures(MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        List<WorkGroupTreeClosureDTO> workGroupTreeClosures = migrateWorkGroupDataVO.getWorkGroupTreeClosures();
        if (ObjectUtils.isEmpty(workGroupTreeClosures)) {
            return;
        }
        Set<Long> ids = workGroupTreeClosures.stream().map(WorkGroupTreeClosureDTO::getId).collect(Collectors.toSet());
        Set<Long> existedIds =
                workGroupTreeClosureMapper.selectByIds(StringUtils.join(ids, ",")).stream().map(WorkGroupTreeClosureDTO::getId).collect(Collectors.toSet());
        List<WorkGroupTreeClosureDTO> insertList =
                workGroupTreeClosures.stream().filter(x -> !existedIds.contains(x.getId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(insertList)) {
            return;
        }
        workGroupTreeClosureMapper.insertSyncData(insertList);
    }

    private void syncWorkGroupProjectRel(MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        List<WorkGroupProjectRelDTO> workGroupProjectRel = migrateWorkGroupDataVO.getWorkGroupProjectRel();
        if (ObjectUtils.isEmpty(workGroupProjectRel)) {
            return;
        }
        Set<Long> ids = workGroupProjectRel.stream().map(WorkGroupProjectRelDTO::getId).collect(Collectors.toSet());
        Set<Long> existedIds =
                workGroupProjectRelMapper.selectByIds(StringUtils.join(ids, ",")).stream().map(WorkGroupProjectRelDTO::getId).collect(Collectors.toSet());
        List<WorkGroupProjectRelDTO> insertList =
                workGroupProjectRel.stream().filter(x -> !existedIds.contains(x.getId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(insertList)) {
            return;
        }
        workGroupProjectRelMapper.insertSyncData(insertList);
    }

    private void syncWorkGroup(MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        List<WorkGroupDTO> workGroups = migrateWorkGroupDataVO.getWorkGroups();
        if (ObjectUtils.isEmpty(workGroups)) {
            return;
        }
        Set<Long> ids = workGroups.stream().map(WorkGroupDTO::getId).collect(Collectors.toSet());
        Set<Long> existedIds =
                workGroupMapper.selectByIds(StringUtils.join(ids, ",")).stream().map(WorkGroupDTO::getId).collect(Collectors.toSet());
        List<WorkGroupDTO> insertList =
                workGroups.stream().filter(x -> !existedIds.contains(x.getId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(insertList)) {
            return;
        }
        workGroupMapper.insertSyncData(insertList);
    }
}
