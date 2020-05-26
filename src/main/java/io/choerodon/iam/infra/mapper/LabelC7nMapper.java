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

    Set<String> selectLabelNamesInRoleIds(@Param("roleIds") List<Long> roleIds);

    List<LabelDTO> listByOption(@Param("label") LabelDTO labelDTO);

    List<LabelDTO> selectLableNameByUserId(@Param("userId") Long userId);

    List<Label> listByNames(@Param("names") Set<String> names);
}
