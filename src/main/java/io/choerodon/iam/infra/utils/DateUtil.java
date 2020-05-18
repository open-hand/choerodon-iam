package io.choerodon.iam.infra.utils;

import com.google.common.collect.Ordering;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.dto.WorkCalendarHolidayRefDTO;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String PARSE_EXCEPTION = "ParseException{}";

    private DateUtil() {
    }

    /**
     * 从现有比较器返回一个
     *
     * @return Ordering
     */
    public static Ordering<WorkCalendarHolidayRefDTO> stringDateCompare() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA);
        return Ordering.from((o1, o2) -> {
            int a;
            try {
                a = sdf.parse(o1.getHoliday()).compareTo(sdf.parse(o2.getHoliday()));
            } catch (ParseException e) {
                throw new CommonException(PARSE_EXCEPTION, e);
            }
            return a;
        });
    }
}
