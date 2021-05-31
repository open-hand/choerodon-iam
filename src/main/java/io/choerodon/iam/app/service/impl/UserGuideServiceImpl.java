package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.choerodon.iam.api.vo.UserGuideStepVO;
import io.choerodon.iam.api.vo.UserGuideVO;
import io.choerodon.iam.app.service.UserGuideService;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.UserGuideMapper;
import io.choerodon.iam.infra.mapper.UserGuideStepMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:11
 */
@Service
public class UserGuideServiceImpl implements UserGuideService {

    @Value("${services.front.url:http://app.example.com}")
    private String frontUrl;
    @Autowired
    private UserGuideMapper userGuideMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private UserGuideStepMapper userGuideStepMapper;

    @Override
    public UserGuideVO listUserGuideByMenuId(Long menuId, String guideCode, Long projectId, Long organizationId) {
        UserGuideVO userGuideVO;

        if (menuId != null) {
            userGuideVO = userGuideMapper.queryUserGuideByMenuId(menuId);
        } else if (StringUtils.isNoneBlank(guideCode)){
            userGuideVO = userGuideMapper.queryUserGuideByCode(guideCode);
        } else {
            return null;
        }
        if (userGuideVO == null) {
            return null;
        }

        List<UserGuideStepVO> userGuideStepVOList = userGuideStepMapper.listStepByGuideId(userGuideVO.getId());

        userGuideStepVOList.forEach(userGuideStepVO -> calculatePageUrl(projectId, organizationId, userGuideStepVO));

        userGuideVO.setUserGuideStepVOList(userGuideStepVOList);
        return userGuideVO;
    }

    private void calculatePageUrl(Long projectId, Long organizationId, UserGuideStepVO userGuideStepVO) {
        String pageUrl = userGuideStepVO.getPageUrl();
        if (projectId != null) {
            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
            pageUrl = pageUrl.replace("${projectId}", projectId.toString());
            if (projectDTO != null) {
                pageUrl = pageUrl.replace("${projectName}", projectDTO.getName());
            }
        }
        if (organizationId != null) {
            Tenant tenant = tenantMapper.selectByPrimaryKey(organizationId);
            pageUrl = pageUrl.replace("${organizationId}", organizationId.toString());
            if (tenant != null) {
                pageUrl = pageUrl.replace("${organizationName}", tenant.getTenantName());
            }
        }
        userGuideStepVO.setPageUrl(pageUrl);

    }
}
