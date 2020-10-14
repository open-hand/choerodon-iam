package io.choerodon.iam.app.service;

import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.StarProjectUserRelDTO;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 10:25
 */
public interface StarProjectService {

    /**
     * 添加星标项目
     *
     * @param organizationId
     * @param starProjectUserRelDTO
     */
    void create(Long organizationId, StarProjectUserRelDTO starProjectUserRelDTO);

    /**
     * 删除星标项目
     * @param projectId
     */
    void delete(Long projectId);

    List<ProjectDTO> query(Long organizationId, Integer size);

    void updateStarProject(List<StarProjectUserRelDTO> starProjectUserRelDTOS);
}
