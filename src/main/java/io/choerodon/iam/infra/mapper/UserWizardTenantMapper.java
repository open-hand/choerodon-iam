package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.api.vo.UserWizardStatusVO;
import io.choerodon.iam.infra.dto.UserWizardTenantDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
public interface UserWizardTenantMapper extends BaseMapper<UserWizardTenantDTO> {

    List<UserWizardStatusVO> queryUserWizardStatusByOrgId(@Param("organizationId") Long organizationId);
}
