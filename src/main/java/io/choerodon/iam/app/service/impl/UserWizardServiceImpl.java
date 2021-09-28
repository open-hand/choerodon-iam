package io.choerodon.iam.app.service.impl;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.hzero.iam.domain.entity.TenantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.UserWizardStatusVO;
import io.choerodon.iam.app.service.UserWizardService;
import io.choerodon.iam.infra.dto.UserWizardDTO;
import io.choerodon.iam.infra.dto.UserWizardTenantDTO;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.enums.UserWizardStatusEnum;
import io.choerodon.iam.infra.enums.UserWizardStepEnum;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserWizardServiceImpl.class);

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
        // 判断组织是要需要向导
        // 之前的旧组织不进行数据迁移，新组织加属性
        TenantConfig config = tenantConfigC7nMapper.queryTenantConfigByTenantIdAndKey(organizationId, TenantConfigEnum.USER_WIZARD.value());
        if (config == null) {
            return null;
        }
        // 判断是否已经完成 创建项目第一步
        if (checkCompletedFirstStep(organizationId)) {
            return null;
        }
        List<UserWizardDTO> list = userWizardMapper.selectAll();
        if (!adminFeignClientOperator.haveAgileModel()) {
            //使用迭代器的移除敏捷向导
            list.removeIf(s -> s.getCode().equals(UserWizardStepEnum.OPEN_SPRINT.value()));
        }
        return list;
    }

    @Override
    public List<UserWizardStatusVO> listUserWizardsStatus(Long organizationId) {
        if (!checkTenantOwner(organizationId)) {
            return null;
        }
        TenantConfig config = tenantConfigC7nMapper.queryTenantConfigByTenantIdAndKey(organizationId, TenantConfigEnum.USER_WIZARD.value());
        if (config == null) {
            return null;
        }
        List<UserWizardStatusVO> list = userWizardTenantMapper.queryUserWizardStatusByOrgId(organizationId);
        // 如果不存在数据 进行初始化
        if (CollectionUtils.isEmpty(list)) {
            initUserWizardByTenantId(organizationId);
            list = userWizardTenantMapper.queryUserWizardStatusByOrgId(organizationId);
        }
        // 判断是否已经完成 创建项目第一步
        // 没有完整不可点击第三步
        if (!checkCompletedFirstStep(organizationId)) {
            list.forEach(t -> {
                if (t.getCode().equals(UserWizardStepEnum.OPEN_SPRINT.value())) {
                    t.setEnableClick(false);
                }
            });
        }
        return list;
    }

    @Override
    public void initUserWizardByTenantId(Long tenantId) {
        try {
            List<UserWizardDTO> list = userWizardMapper.selectAll();
            if (!adminFeignClientOperator.haveAgileModel()) {
                //使用迭代器的移除敏捷向导
                list.removeIf(s -> s.getCode().equals(UserWizardStepEnum.OPEN_SPRINT.value()));
            }
            list.forEach(t -> {
                UserWizardTenantDTO wizardTenantDTO = new UserWizardTenantDTO();
                wizardTenantDTO.setTenantId(tenantId);
                wizardTenantDTO.setWizardId(t.getId());
                // 不存在则初始化
                if (userWizardTenantMapper.selectOne(wizardTenantDTO) == null) {
                    wizardTenantDTO.setStatus(UserWizardStatusEnum.UNCOMPLETED.value());
                    if (userWizardTenantMapper.insertSelective(wizardTenantDTO) != 1) {
                        throw new CommonException("error.insert.user.wizard.tenant");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("error.init.user.wizard", e);
        }
    }

    @Override
    public void updateUserWizardCompleted(@NotNull Long tenantId, String code) {
        try {
            UserWizardTenantDTO wizardTenantDTO = userWizardTenantMapper.queryByTenantIdAndCode(tenantId, code);
            // 如果不存在数据 进行初始化
            if (wizardTenantDTO == null) {
                initUserWizardByTenantId(tenantId);
                wizardTenantDTO = userWizardTenantMapper.queryByTenantIdAndCode(tenantId, code);
            }
            // 已经成功的状态不重复更新
            if (wizardTenantDTO.getStatus().equals(UserWizardStatusEnum.UNCOMPLETED.value())) {
                wizardTenantDTO.setStatus(UserWizardStatusEnum.COMPLETED.value());
                if (userWizardTenantMapper.updateByPrimaryKeySelective(wizardTenantDTO) != 1) {
                    throw new CommonException("error.update.user.wizard.status");
                }
            }
        } catch (Exception e) {
            LOGGER.error("error.update.user.wizard", e);
        }
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

    /**
     * 查询第一步的状态
     *
     * @param organizationId
     * @return
     */
    private Boolean checkCompletedFirstStep(Long organizationId) {
        String status = userWizardTenantMapper.queryCompletedFirstStep(organizationId);
        return status != null && !status.equals(UserWizardStatusEnum.UNCOMPLETED.value());
    }

}
