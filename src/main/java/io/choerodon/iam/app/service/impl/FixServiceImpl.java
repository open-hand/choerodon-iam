package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.app.service.FixService;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.iam.infra.enums.ProjectCategoryEnum;
import io.choerodon.iam.infra.mapper.ProjectCategoryMapper;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FixServiceImpl implements FixService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    @Autowired
    private ProjectCategoryMapper projectCategoryMapper;

    @Override
    @Async
    public void fixProjectCateGory() {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> start fix project category <<<<<<<<<<<<<<<<<<<<<<");
        projectMapper.selectAll().forEach(t -> {
            List<String> oldListCategory = projectMapper.listCategoryByProjectId(t.getId());
            if (!CollectionUtils.isEmpty(oldListCategory)) {
                Set<String> newListCateGory = new HashSet<>();
                oldListCategory.forEach(category -> {
                    switch (ProjectCategoryEnum.forValue(category)) {
                        case GENERAL:
                            newListCateGory.add(ProjectCategoryEnum.N_AGILE.value());
                            newListCateGory.add(ProjectCategoryEnum.N_REQUIREMENT.value());
                            newListCateGory.add(ProjectCategoryEnum.N_DEVOPS.value());
                            newListCateGory.add(ProjectCategoryEnum.N_TEST.value());
                            break;
                        case AGILE:
                            newListCateGory.add(ProjectCategoryEnum.N_AGILE.value());
                            newListCateGory.add(ProjectCategoryEnum.N_REQUIREMENT.value());
                            break;
                        case OPERATIONS:
                            newListCateGory.add(ProjectCategoryEnum.N_DEVOPS.value());
                            newListCateGory.add(ProjectCategoryEnum.N_OPERATIONS.value());
                            break;
                    }
                });
                newListCateGory.removeAll(oldListCategory);
                if (!CollectionUtils.isEmpty(newListCateGory)) {
                    List<Long> categoryIds = projectCategoryMapper.ListIdByCodes(newListCateGory);
                    if (!CollectionUtils.isEmpty(categoryIds)) {
                        List<ProjectMapCategoryDTO> mapCategoryDTOS = categoryIds.stream().map(newCategoryId -> {
                            ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
                            projectMapCategoryDTO.setCategoryId(newCategoryId);
                            projectMapCategoryDTO.setProjectId(t.getId());
                            return projectMapCategoryDTO;
                        }).collect(Collectors.toList());
                        projectMapCategoryMapper.batchInsert(mapCategoryDTOS);
                    }
                }
            }
        });
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> end fix project category <<<<<<<<<<<<<<<<<<<<<<");
    }
}
