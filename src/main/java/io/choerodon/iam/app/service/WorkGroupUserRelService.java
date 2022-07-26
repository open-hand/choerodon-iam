package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.WorkGroupUserRelParamVO;
import io.choerodon.iam.api.vo.WorkGroupUserRelVO;
import io.choerodon.iam.api.vo.WorkHoursSearchVO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Map;
import java.util.Set;

/**
 * @author zhaotianxin
 * @date 2021-11-08 20:39
 */
public interface WorkGroupUserRelService {

    void batchInsertRel(Long organizationId, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    void batchDeleteRel(Long organizationId, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    Page<WorkGroupUserRelVO> pageByQuery(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    Page<WorkGroupUserRelVO> pageUnAssignee(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    Map<Long, Set<Long>> getWorkGroupMap(Long organizationId);

    Page<WorkGroupUserRelVO> pageUnlinkUser(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    Page<UserDTO> pageByGroups(Long organizationId, PageRequest pageRequest, WorkGroupUserRelParamVO workGroupUserRelParamVO);

    Set<Long> listUserIdsByWorkGroupIds(Long organizationId, WorkHoursSearchVO workHoursSearchVO);
}
