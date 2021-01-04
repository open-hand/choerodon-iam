package io.choerodon.iam.infra.valitador;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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

    @Autowired
    private DevopsFeignClient devopsFeignClient;

    public ProjectValidator(ProjectCategoryMapper projectCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
    }

    public void validateProjectCategory(List<ProjectCategoryDTO> projectCategoryDTOS) {
        List<String> codes = projectCategoryDTOS.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(codes)) {
            throw new CommonException("error.project.category.empty");
        }
        validateProjectCategoryCode(projectCategoryDTOS);
        //如果创建devops模块，必须要求当前用户的gitlab同步用户同步成功
        if (codes.contains(ProjectCategoryEnum.N_DEVOPS.value())) {
            UserAttrVO userAttrVO = devopsFeignClient.queryByUserId(BaseConstants.DEFAULT_TENANT_ID, DetailsHelper.getUserDetails().getUserId()).getBody();
            if (userAttrVO == null) {
                throw new CommonException("error.user.gitlab.not.exist", DetailsHelper.getUserDetails().getUserId());
            }
        }

    }

    public void validateProjectCategoryCode(List<ProjectCategoryDTO> projectCategoryDTOS) {
        projectCategoryDTOS.forEach(projectCategoryDTO -> {
            if (!ProjectCategoryEnum.contains(projectCategoryDTO.getCode())) {
                throw new CommonException("error.params.invalid", projectCategoryDTO.getCode());
            }
            ProjectCategoryDTO categoryDTO = new ProjectCategoryDTO();
            categoryDTO.setCode(projectCategoryDTO.getCode());
            categoryDTO = projectCategoryMapper.selectOne(projectCategoryDTO);
            if (ObjectUtils.isEmpty(projectCategoryDTO)) {
                throw new CommonException("error.project.category.not.existed", projectCategoryDTO.getCode());
            }
            projectCategoryDTO.setId(categoryDTO.getId());
        });
    }
}
