package io.choerodon.iam.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.ReportC7nService;
import io.choerodon.base.infra.dto.ReportDTO;
import io.choerodon.base.infra.mapper.ReportMapper;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class ReportC7nServiceImpl implements ReportC7nService {
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
        return reportDTOS;
    }
}
