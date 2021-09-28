package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.hzero.iam.domain.entity.TenantConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.UserWizardStatusVO;
import io.choerodon.iam.app.service.UserWizardService;
import io.choerodon.iam.infra.dto.UserWizardDTO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.feign.operator.AdminFeignClientOperator;
import io.choerodon.iam.infra.mapper.TenantConfigC7nMapper;
import io.choerodon.iam.infra.mapper.UserWizardMapper;
import io.choerodon.iam.infra.mapper.UserWizardTenantMapper;
import io.choerodon.iam.infra.utils.TypeUtil;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
@Service
public class UserWizardServiceImpl implements UserWizardService {
    @Autowired
    private UserWizardMapper userWizardMapper;
    @Autowired
    private AdminFeignClientOperator adminFeignClientOperator;
    @Autowired
    private TenantConfigC7nMapper tenantConfigC7nMapper;
    @Autowired
    private UserWizardTenantMapper userWizardTenantMapper;

    @Override
    public List<UserWizardDTO> listUserWizards(Long organizationId) {
        // 判断是否是组织所有者
        if (!checkTenantOwner(organizationId)) {
            return null;
        }
        // 判断是否已经完成 创建项目第一步

        List<UserWizardDTO> list = userWizardMapper.selectAll();
        if (!adminFeignClientOperator.haveAgileModel()) {
            //使用迭代器的移除敏捷向导
            list.removeIf(s -> s.getCode().equals("openSprint"));
        }
        return list;
    }

    @Override
    public List<UserWizardStatusVO> listUserWizardsStatus(Long organizationId) {
        if (!checkTenantOwner(organizationId)) {
            return null;
        }
        List<UserWizardStatusVO> list=userWizardTenantMapper.queryUserWizardStatusByOrgId(organizationId);
        return null;
    }

    /**
     * 校验是否是组织 所有者（tenantConfig表的userId用户）
     * 不是组织所有者角色用户
     *
     * @return
     */
    private Boolean checkTenantOwner(Long organizationId) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails == null) {
            return false;
        }
        TenantConfig config = tenantConfigC7nMapper.queryTenantConfigByTenantIdAndKey(organizationId, TenantConfigEnum.USER_ID.value());
        return userDetails.getUserId().equals(TypeUtil.objToLong(config.getConfigValue()));
    }



}
