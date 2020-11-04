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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.EnterpriseInfoVO;
import io.choerodon.iam.app.service.EnterpriseInfoService;
import io.choerodon.iam.infra.dto.EnterpriseInfoDTO;
import io.choerodon.iam.infra.factory.RetrofitClientFactory;
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

    @Autowired
    private EnterpriseInfoMapper enterpriseInfoMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RetrofitClientFactory retrofitClientFactory;



    @Override
    public Boolean checkEnterpriseInfoComplete() {
        List<EnterpriseInfoDTO> enterpriseInfoDTOS = enterpriseInfoMapper.selectAll();
        return CollectionUtils.isEmpty(enterpriseInfoDTOS);
    }

    @Override
    @Transactional
    public void saveEnterpriseInfo(EnterpriseInfoVO enterpriseInfoVO) {
        Tenant record = new Tenant();
        record.setTenantNum(OPERATION_TENANT_CODE);
        Tenant tenant = tenantMapper.selectOne(record);

        tenant.setTenantName(enterpriseInfoVO.getOrganizationName());
        if (tenantMapper.updateByPrimaryKeySelective(tenant) != 1) {
            throw new CommonException("error.update.org.name");
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
            IamClient iamClient = (IamClient) retrofitClientFactory.getRetrofitBean("https://api.choerodon.com.cn", IamClient.class);
            Call<ResponseBody> call = iamClient.saveEnterpriseInfo(enterpriseInfoVO);
            call.execute();
        } catch (IOException e) {
            LOGGER.info("::Retrofit::An exception occurred during execution:{}", e);
        }
    }

}
