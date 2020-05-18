package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.iam.infra.mapper.TimeZoneWorkCalendarMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TimeZoneWorkCalendarServiceImpl implements TimeZoneWorkCalendarService {

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    @Override
    public TimeZoneWorkCalendarDTO queryTimeZoneDetailByOrganizationId(Long organizationId) {
        return timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(organizationId);
    }
}
