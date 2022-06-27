package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Label;

import io.choerodon.iam.infra.dto.LabelDTO;

/**
 * @author superlee
 */
public interface LabelC7nMapper {

    List<LabelDTO> selectByRoleId(@Param("roleId") Long roleId);

    List<LabelDTO> selectByUserId(Long id);

    Set<String> selectLabelNamesInRoleIds(@Param("roleIds") Set<Long> roleIds);

    List<LabelDTO> selectLabelNamesMapInRoleIds(@Param("roleIds") Set<Long> roleIds);

    /**
     * 查出用户在项目层和组织层的所有角色标签
     *
     * @param userId    用户id
     * @param projectId 项目id
     * @param tenantId  组织id
     * @return 角色标签
     */
    Set<String> selectRoleLabelsForUserInProjectAndOrg(@Param("userId") Long userId,
                                                       @Param("projectId") Long projectId,
                                                       @Param("tenantId") Long tenantId);

    List<LabelDTO> listByOption(@Param("label") LabelDTO labelDTO);

    List<LabelDTO> selectLableNameByUserId(@Param("userId") Long userId);

    List<Label> listByNames(@Param("names") Set<String> names);
}
