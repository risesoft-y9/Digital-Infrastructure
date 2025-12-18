package net.risesoft.y9.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9DayUtil {

    public static Date getEndOfDay(Date day) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(day);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        return endOfDay.getTime();
    }

    public static Calendar getEndOfDay(long timeInMillis) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTimeInMillis(timeInMillis);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        return endOfDay;
    }

    public static Date getStartOfDay(Date day) {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(day);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay.getTime();
    }

    public static Calendar getStartOfDay(long timeInMillis) {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTimeInMillis(timeInMillis);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay;
    }
}
