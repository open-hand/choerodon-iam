package io.choerodon.iam.infra.valitador;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.enums.ProjectCategoryEnum;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.ProjectCategoryMapper;

@Component
public class ProjectValidator {
    private ProjectCategoryMapper projectCategoryMapper;

    private DevopsFeignClient devopsFeignClient;

    public ProjectValidator(ProjectCategoryMapper projectCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
    }

    public ProjectCategoryDTO validateProjectCategory(String category) {
        validateProjectCategoryCode(category);
        if (ObjectUtils.isEmpty(category)) {
            throw new CommonException("error.project.category.empty");
        }
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setCode(category);
        projectCategoryDTO = projectCategoryMapper.selectOne(projectCategoryDTO);
        if (ObjectUtils.isEmpty(projectCategoryDTO)) {
            throw new CommonException("error.project.category.not.existed", category);
        }
        //如果创建devops模块，必须要求当前用户的gitlab同步用户同步成功
        if (StringUtils.equalsIgnoreCase(ProjectCategoryEnum.DEVOPS.value(), category.trim())) {
            UserAttrVO userAttrVO = devopsFeignClient.queryByUserId(BaseConstants.DEFAULT_TENANT_ID, DetailsHelper.getUserDetails().getUserId()).getBody();
            if (userAttrVO == null) {
                throw new CommonException("error.user.gitlab.not.exist", category);
            }
        }

        return projectCategoryDTO;
    }

    public void validateProjectCategoryCode(String code) {
        if (!ProjectCategoryEnum.contains(code)) {
            throw new CommonException("error.params.invalid");
        }

    }
}
