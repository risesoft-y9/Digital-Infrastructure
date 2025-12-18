package net.risesoft.y9.util;

import java.util.Calendar;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 日期工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9DayUtil {

    /**
     * 获取指定日期当天的结束时间（23:59:59.999）
     * 
     * @param day 指定日期
     * @return Date 当天的结束时间
     */
    public static Date getEndOfDay(Date day) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(day);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        return endOfDay.getTime();
    }

    /**
     * 获取指定时间毫秒数对应的当天结束时间（23:59:59.999）
     * 
     * @param timeInMillis 时间毫秒数
     * @return Calendar 当天的结束时间
     */
    public static Calendar getEndOfDay(long timeInMillis) {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTimeInMillis(timeInMillis);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        return endOfDay;
    }

    /**
     * 获取指定日期当天的开始时间（00:00:00.000）
     * 
     * @param day 指定日期
     * @return Date 当天的开始时间
     */
    public static Date getStartOfDay(Date day) {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(day);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay.getTime();
    }

    /**
     * 获取指定时间毫秒数对应的当天开始时间（00:00:00.000）
     * 
     * @param timeInMillis 时间毫秒数
     * @return Calendar 当天的开始时间
     */
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