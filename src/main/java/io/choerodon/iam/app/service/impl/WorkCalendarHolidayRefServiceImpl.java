package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.api.vo.WorkCalendarHolidayRefVO;
import io.choerodon.iam.app.service.WorkCalendarHolidayRefService;
import io.choerodon.iam.app.service.WorkCalendarService;
import io.choerodon.iam.app.service.assemable.WorkCalendarHolidayRefAssembler;
import io.choerodon.iam.infra.config.WorkCalendarHolidayProperties;
import io.choerodon.iam.infra.dto.WorkCalendarHolidayRefDTO;
import io.choerodon.iam.infra.factory.WorkCalendarFactory;
import io.choerodon.iam.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.iam.infra.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@Service
@Transactional(rollbackFor = Exception.class)
@EnableConfigurationProperties(WorkCalendarHolidayProperties.class)
public class WorkCalendarHolidayRefServiceImpl implements WorkCalendarHolidayRefService {

    @Autowired
    private WorkCalendarHolidayProperties workCalendarHolidayProperties;
    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;
    @Autowired
    private WorkCalendarHolidayRefAssembler workCalendarHolidayRefAssembler;
    @Autowired
    private WorkCalendarFactory workCalendarFactory;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkCalendarHolidayRefServiceImpl.class);
    private static final String PARSE_EXCEPTION = "ParseException{}";


    @Override
    public void updateWorkCalendarHolidayRefByYear(Integer year) {
        WorkCalendarService workCalendarService = workCalendarFactory.getWorkCalendarHoliday(workCalendarHolidayProperties.getType());
        if (workCalendarService != null) {
            workCalendarService.updateWorkCalendarHolidayRefByYear(year);
        }
    }

    @Override
    public List<WorkCalendarHolidayRefVO> queryWorkCalendarHolidayRelByYear(Integer year,
                                                                            Date startDate,
                                                                            Date endDate) {

        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        startDate = DateUtil.formatDate(startDate, formatter);
        endDate = DateUtil.formatDate(endDate, formatter);
        return formatAndSortToDTO(workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelWithNextYearByYear(year, startDate, endDate));
    }

    private List<WorkCalendarHolidayRefVO> formatAndSortToDTO(List<WorkCalendarHolidayRefDTO> workCalendarHolidayRefDTOS) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        workCalendarHolidayRefDTOS.forEach(workCalendarHolidayRefDO -> {
            try {
                workCalendarHolidayRefDO.setHoliday(simpleDateFormat.format(simpleDateFormat.parse(workCalendarHolidayRefDO.getHoliday())));
            } catch (ParseException e) {
                LOGGER.warn(PARSE_EXCEPTION, e);
            }
        });
        return workCalendarHolidayRefAssembler.toTargetList(DateUtil.stringDateCompare().
                sortedCopy(workCalendarHolidayRefDTOS), WorkCalendarHolidayRefVO.class);
    }

    @Override
    public List<WorkCalendarHolidayRefVO> queryByYearIncludeLastAndNext(Integer year) {
        return formatAndSortToDTO(workCalendarHolidayRefMapper.queryByYearIncludeLastAndNext(year));
    }
}
