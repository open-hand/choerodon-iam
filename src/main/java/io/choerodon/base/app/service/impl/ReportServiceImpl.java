package io.choerodon.base.app.service.impl;

import io.choerodon.base.api.dto.ReportDTO;
import io.choerodon.base.app.service.ReportService;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.enums.ProjectCategoryEnum;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.base.infra.mapper.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@Service
public class ReportServiceImpl implements ReportService {
    private static final String AGILE_PROJECT_TYPE = "AGILE";

    private ReportMapper reportMapper;
    private ProjectMapper projectMapper;

    public ReportServiceImpl(ReportMapper reportMapper, ProjectMapper projectMapper) {
        this.reportMapper = reportMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public List<ReportDTO> queryReportList(Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectCategoryByPrimaryKey(projectId);
        List<ReportDTO> reportDTOS = reportMapper.selectAll();
        if(projectDTO.getCategories().stream().anyMatch(categoryDTO -> categoryDTO.getCode().equals(AGILE_PROJECT_TYPE))) {
            return reportDTOS.stream()
                    .filter(reportDTO -> reportDTO.getReportType().equals("敏捷报表")
                            || reportDTO.getReportType().equals("测试报表"))
                    .collect(Collectors.toList());
        }
        if(projectDTO.getCategories().stream().anyMatch(categoryDTO -> ProjectCategoryEnum.OPERATIONS.value().equals(categoryDTO.getCode()))) {
            return reportDTOS.stream()
                    .filter(reportDTO -> reportDTO.getReportType().equals("DevOps 报表"))
                    .collect(Collectors.toList());
        }
        return reportDTOS;
    }
}
