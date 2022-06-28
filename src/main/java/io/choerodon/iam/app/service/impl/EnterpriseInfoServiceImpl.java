package io.choerodon.iam.app.service.impl;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;
import retrofit2.Retrofit;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.EnterpriseInfoVO;
import io.choerodon.iam.app.service.EnterpriseInfoService;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.infra.constant.TenantConstants;
import io.choerodon.iam.infra.dto.EnterpriseInfoDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.mapper.EnterpriseInfoMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.retrofit.IamClient;
import io.choerodon.iam.infra.retrofit.RetrofitHandler;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseInfoServiceImpl.class);


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
    private TenantC7nService tenantC7nService;
    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public Boolean checkEnterpriseInfoComplete() {
        // 只校验admin账户,不是默认的admin账户直接返回true
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (!userDetails.getUsername().equals(ADMIN_LOGIN_NAME)) {
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
            tenantC7nService.createDefaultTenant(enterpriseInfoVO.getOrganizationName(), enterpriseInfoVO.getTenantNum());
        } else {
            // 默认组织已经创建则只更新组织名
            Tenant newTenant = new Tenant();
            newTenant.setTenantId(tenant.getTenantId());
            newTenant.setTenantName(enterpriseInfoVO.getOrganizationName());
            newTenant.setObjectVersionNumber(tenant.getObjectVersionNumber());
            if (Boolean.TRUE.equals(checkEnableUpdateTenantNum())) {
                newTenant.setTenantNum(enterpriseInfoVO.getTenantNum());
            }
            if (tenantMapper.updateByPrimaryKeySelective(newTenant) != 1) {
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
            Retrofit retrofit = RetrofitHandler.initRetrofit(choerodonUrl);
            IamClient iamClient = retrofit.create(IamClient.class);

            Call<ResponseBody> call = iamClient.saveEnterpriseInfo(enterpriseInfoVO);
            call.execute();
        } catch (IOException e) {
            LOGGER.info("::Retrofit::An exception occurred during execution:{}", e);
        }
    }

    @Override
    public Boolean checkEnableUpdateTenantNum() {
        ProjectDTO record = new ProjectDTO();
        record.setOrganizationId(TenantConstants.DEFAULT_C7N_TENANT_TD);
        return projectMapper.selectCount(record) == 0;
    }

}
