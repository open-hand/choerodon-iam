package io.choerodon.base.app.service;

import java.util.List;
import java.util.Set;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.UserDTO;

/**
 * @author flyleft
 */
public interface ProjectService {

    ProjectDTO queryProjectById(Long projectId);

    PageInfo<UserDTO> pagingQueryTheUsersOfProject(Long id, Long userId, String email, Pageable Pageable, String param);

    ProjectDTO update(ProjectDTO projectDTO);

    ProjectDTO disableProject(Long id);

    Boolean checkProjCode(String code);

    List<Long> listUserIds(Long projectId);

    List<ProjectDTO> queryByIds(Set<Long> ids);

    List<Long> getProListByName(String name);

    /**
     * 批量更新敏捷项目code, 如果项目id对应的agileProjectCode已经不为空则不更新
     *
     * @param projects 项目信息中包含项目id和敏捷项目code
     */
    void batchUpdateAgileProjectCode(List<ProjectDTO> projects);

}
