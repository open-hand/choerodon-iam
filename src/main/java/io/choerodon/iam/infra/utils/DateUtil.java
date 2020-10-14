package io.choerodon.iam.infra.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.Ordering;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.dto.WorkCalendarHolidayRefDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String PARSE_EXCEPTION = "ParseException{}";
    private static final Integer DEFAULT_DAY = 7;

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

    /**
     * 判断两个时间之差是否超过最大天数
     *
     * @param min 较小时间
     * @param max 较大时间
     * @return
     */
    public static boolean isExceedDay(Date min, Date max) {
        return isExceedDay(min, max, DEFAULT_DAY);
    }

    /**
     * 判断两个时间之差是否超过最大天数
     *
     * @param min 较小时间
     * @param max 较大时间
     * @return
     */
    public static boolean isExceedDay(Date min, Date max, Integer maxDay) {
        int days = (int) ((max.getTime() - min.getTime()) / (1000 * 3600 * 24));
        return days > maxDay;
    }
}
