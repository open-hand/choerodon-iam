package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.api.vo.UserGuideVO;
import io.choerodon.iam.app.service.UserGuideService;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.UserGuideMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:11
 */
@Service
public class UserGuideServiceImpl implements UserGuideService {

    @Autowired
    private UserGuideMapper userGuideMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public List<UserGuideVO> listUserGuideByMenuId(Long menuId, Long projectId, Long organizationId) {
        List<UserGuideVO> userGuideVOS = userGuideMapper.listUserGuideByMenuId(menuId);
        userGuideVOS.forEach(userGuideVO -> {
            calculatePageUrl(projectId, organizationId, userGuideVO);
        });

        return userGuideVOS;
    }

    private void calculatePageUrl(Long projectId, Long organizationId, UserGuideVO userGuideVO) {
        String pageUrl = userGuideVO.getPageUrl();
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
        userGuideVO.setPageUrl(pageUrl);

    }
}
