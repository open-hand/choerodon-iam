package io.choerodon.iam.infra.mapper;

import java.util.List;

import io.choerodon.iam.api.vo.UserGuideStepVO;
import io.choerodon.iam.infra.dto.UserGuideStepDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:13
 */
public interface UserGuideStepMapper extends BaseMapper<UserGuideStepDTO> {

    List<UserGuideStepVO> listStepByGuideId(Long guideId);
}
