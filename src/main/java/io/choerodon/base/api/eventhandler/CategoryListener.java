package io.choerodon.base.api.eventhandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.choerodon.base.infra.mapper.ProjectCategoryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.base.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.base.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.core.iam.ResourceLevel;

@Component
public class CategoryListener {
    private final Logger logger = LoggerFactory.getLogger(CategoryListener.class);

    private ProjectMapper projectMapper;
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    private ProjectCategoryMapper projectCategoryMapper;

    public CategoryListener(ProjectMapper projectMapper,
                            ProjectMapCategoryMapper projectMapCategoryMapper,
                            ProjectCategoryMapper projectCategoryMapper) {
        this.projectMapper = projectMapper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.projectCategoryMapper = projectCategoryMapper;
    }

    @TimedTask(name = "ProjectMapCategory新表数据修复", description = "非开源版本创建FD_PROJECT_MAP_CATEGORY表，并同步现有项目的类型",
            oneExecution = true, params = {}, repeatCount = 0, repeatInterval = 0, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.SECONDS)
    @JobTask(maxRetryCount = 2, code = "syncAllCategory", level = ResourceLevel.SITE, description = "非开源版本创建FD_PROJECT_MAP_CATEGORY表，并同步现有项目的类型")
    public void syncAllCategory(Map<String, Object> map) {
        List<ProjectMapCategoryDTO> projectMapCategoryDTOS = projectMapper.selectProjectAndCategoryId();
        if (CollectionUtils.isNotEmpty(projectMapCategoryDTOS)) {
            projectMapCategoryDTOS = projectMapCategoryDTOS.stream().filter(pmc -> pmc.getCategoryId() != null).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(projectMapCategoryMapper.selectAll())) {
            int i = projectMapCategoryMapper.batchInsert(projectMapCategoryDTOS);
            logger.info("Synchronize project category to table FD_PROJECT_MAP_CATEGORY, with " + i + " records in total.");
        }
    }

    @TimedTask(name = "ProjectMapCategory新表数据项目群子项目修复", description = "非开源版本创建FD_PROJECT_MAP_CATEGORY表，并同步现有项目群子项目的类型",
            oneExecution = true, params = {}, repeatCount = 0, repeatInterval = 0, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.SECONDS)
    @JobTask(maxRetryCount = 2, code = "syncAllCategoryProRelation", level = ResourceLevel.SITE, description = "非开源版本创建FD_PROJECT_MAP_CATEGORY表，并同步现有项目群子项目的类型")
    public void syncAllCategoryProRelation(Map<String, Object> map) {
        List<ProjectMapCategoryDTO> projectMapCategoryDTOS = projectMapper.selectProjectAndCategoryIdByRelationship();
        Long agileId = projectCategoryMapper.getIdByCode("AGILE");
        Long projectPramId = projectCategoryMapper.getIdByCode("PROGRAM_PROJECT");
        if (CollectionUtils.isNotEmpty(projectMapCategoryDTOS)) {
            projectMapCategoryDTOS = projectMapCategoryDTOS.stream().filter(pmc -> pmc.getCategoryId().equals(agileId)).collect(Collectors.toList());
        }
        for (ProjectMapCategoryDTO projectMapCategoryDTO : projectMapCategoryDTOS) {
            projectMapCategoryDTO.setCategoryId(projectPramId);
        }
        int i = projectMapCategoryMapper.batchInsert(projectMapCategoryDTOS);
        logger.info("Synchronize project category to table FD_PROJECT_MAP_CATEGORY, with " + i + " records in total.");

    }
}