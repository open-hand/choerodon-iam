package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.LabelDTO;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Label;

import java.util.List;
import java.util.Set;

/**
 * @author superlee
 */
public interface LabelC7nMapper {

    List<LabelDTO> selectByRoleId(Long roleId);

    List<LabelDTO> selectByUserId(Long id);

    Set<String> selectLabelNamesInRoleIds(List<Long> roleIds);

    List<LabelDTO> listByOption(@Param("label") LabelDTO labelDTO);

    List<LabelDTO> selectLableNameByUserId(@Param("userId") Long userId);

    List<Label> listByNames(@Param("names") Set<String> names);
}
