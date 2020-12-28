package io.choerodon.iam.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.TenantConfig;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.hzero.iam.saas.app.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;

import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.EnterpriseInfoVO;
import io.choerodon.iam.app.service.EnterpriseInfoService;
import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.constant.TenantConstants;
import io.choerodon.iam.infra.dto.EnterpriseInfoDTO;
import io.choerodon.iam.infra.factory.RetrofitClientFactory;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.mapper.EnterpriseInfoMapper;
import io.choerodon.iam.infra.retrofit.IamClient;
import io.choerodon.iam.infra.utils.ConvertUtils;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:12
 */
@Service
public class EnterpriseInfoServiceImpl implements EnterpriseInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);


    private static final String OPERATION_TENANT_CODE = "operation";
    private static final String ADMIN_LOGIN_NAME = "admin";

    @Value("${choerodon.url: https://api.choerodon.com.cn}")
    private String choerodonUrl;

    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private RetrofitClientFactory retrofitClientFactory;



    @Override
    public Boolean checkEnterpriseInfoComplete() {
        // 只校验admin账户,不是默认的admin账户直接返回true
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (!userDetails.getUsername().equals("admin")) {
            return true;
        }
        // 没有初始化默认组织，则初始化默认组织
        Tenant tenant = tenantMapper.selectByPrimaryKey(TenantConstants.DEFAULT_C7N_TENANT_TD);
        if (tenant == null) {
            return false;
        }

        List<EnterpriseInfoDTO> enterpriseInfoDTOS = enterpriseInfoMapper.selectAll();
        return !CollectionUtils.isEmpty(enterpriseInfoDTOS);
    }

    @Override
    @Transactional
    public void saveEnterpriseInfo(EnterpriseInfoVO enterpriseInfoVO) {
        Tenant tenant = tenantMapper.selectByPrimaryKey(TenantConstants.DEFAULT_C7N_TENANT_TD);

        if (tenant == null) {
            // 默认组织不存在则创建
            Tenant defaultTenant = new Tenant();
            defaultTenant.setTenantName(enterpriseInfoVO.getOrganizationName());
            defaultTenant.setTenantNum(enterpriseInfoVO.getTenantNum());
            createDefaultTenant(defaultTenant);
        } else {
            // 默认组织已经创建则只更新组织名
            Tenant newTenant = new Tenant();
            newTenant.setTenantId(tenant.getTenantId());
            newTenant.setTenantName(tenant.getTenantName());
            if (tenantMapper.updateByPrimaryKeySelective(tenant) != 1) {
                throw new CommonException("error.update.org.name");
            }
        }

        User user = new User();
        user.setLoginName(ADMIN_LOGIN_NAME);

        User admin = userMapper.selectOne(user);
        admin.setRealName(enterpriseInfoVO.getAdminName());
        admin.setEmail(enterpriseInfoVO.getAdminEmail());
        admin.setPhone(enterpriseInfoVO.getAdminPhone());

        if (userMapper.updateByPrimaryKeySelective(admin) != 1) {
            throw new CommonException("error.update.user.info");
        }

        EnterpriseInfoDTO enterpriseInfoDTO = ConvertUtils.convertObject(enterpriseInfoVO, EnterpriseInfoDTO.class);
        if (enterpriseInfoMapper.insert(enterpriseInfoDTO) != 1) {
            throw new CommonException("error.save.enterpriseInfo");
        }

        try {
            IamClient iamClient = (IamClient) retrofitClientFactory.getRetrofitBean(choerodonUrl, IamClient.class);
            Call<ResponseBody> call = iamClient.saveEnterpriseInfo(enterpriseInfoVO);
            call.execute();
        } catch (IOException e) {
            LOGGER.info("::Retrofit::An exception occurred during execution:{}", e);
        }
    }


    private void createDefaultTenant(Tenant defaultTenant) {
        defaultTenant.setEnabledFlag(1);
        initConfig(defaultTenant);
        tenantService.createTenant(defaultTenant);
        timeZoneWorkCalendarService.handleOrganizationInitTimeZone(defaultTenant.getTenantId());
    }

    private void initConfig(Tenant defaultTenant) {
        List<TenantConfig> tenantConfigs = new ArrayList<>();

        TenantConfig userId = new TenantConfig();
        userId.setConfigKey(TenantConfigEnum.USER_ID.value());
        userId.setConfigValue(String.valueOf(1L));
        tenantConfigs.add(userId);

        TenantConfig register = new TenantConfig();
        register.setConfigValue("false");
        register.setConfigKey(TenantConfigEnum.IS_REGISTER.value());
        tenantConfigs.add(register);

        TenantConfig category = new TenantConfig();
        category.setConfigKey(TenantConfigEnum.CATEGORY.value());
        category.setConfigValue(TenantConstants.DEFAULT_CATEGORY);
        tenantConfigs.add(category);

        TenantConfig token = new TenantConfig();
        token.setConfigKey(TenantConfigEnum.REMOTE_TOKEN_ENABLED.value());
        token.setConfigValue("true");
        tenantConfigs.add(token);
        defaultTenant.setTenantConfigs(tenantConfigs);
    }

}
