package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.agile.RoleVO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Role;
import org.springframework.data.domain.Pageable;


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
    Page<RoleC7nDTO> listRole(PageRequest pageRequest, Long tenantId, String name, String level, String params);


    Page<RoleDTO> pagingSearch(PageRequest pageRequest,Long tenantId, String name, String code, String level,
                               Boolean builtIn, Boolean enabled, String params);
}
