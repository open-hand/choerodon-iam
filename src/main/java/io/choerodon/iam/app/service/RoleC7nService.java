package io.choerodon.iam.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.agile.RoleVO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/12 16:59
 */
public interface RoleC7nService {

    /**
     * 项目层查询角色列表以及该角色下的用户数量
     *
     * @param roleAssignmentSearchDTO
     * @param projectId
     * @return
     */
    List<RoleVO> listRolesWithUserCountOnProjectLevel(Long projectId, RoleAssignmentSearchDTO roleAssignmentSearchDTO);


    /**
     * 查询当前用户自己的角色
     *
     * @param organizationId
     * @param userId
     * @return
     */
    Page<RoleC7nDTO> listRole(Long organizationId, Long userId, PageRequest pageRequest);
}
