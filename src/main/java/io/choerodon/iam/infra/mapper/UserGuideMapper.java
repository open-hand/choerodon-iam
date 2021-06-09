package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.api.vo.UserGuideVO;
import io.choerodon.iam.infra.dto.UserGuideDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:12
 */
public interface UserGuideMapper extends BaseMapper<UserGuideDTO> {

    UserGuideVO queryUserGuideByMenuIdAndCode(@Param("menuId") Long menuId, @Param("tabCode") String tabCode);

    UserGuideVO queryUserGuideByCode(@Param("guideCode") String menuCode);

    List<UserGuideVO> queryUserGuideByMenuId(@Param("menuId") Long menuId);
}
