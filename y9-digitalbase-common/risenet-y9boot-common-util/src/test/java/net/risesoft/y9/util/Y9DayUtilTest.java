package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Y9DayUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
public class Y9DayUtilTest {

    private Date testDate;
    private long testTimeInMillis;

    @BeforeEach
    void setUp() {
        // 创建一个测试日期 2022年2月10日 15:30:45.123
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, Calendar.FEBRUARY, 10, 15, 30, 45);
        calendar.set(Calendar.MILLISECOND, 123);
        testDate = calendar.getTime();
        testTimeInMillis = calendar.getTimeInMillis();
    }

    @Test
    void testGetStartOfDayFromDate() {
        Date startOfDay = Y9DayUtil.getStartOfDay(testDate);

        Calendar resultCalendar = Calendar.getInstance();
        resultCalendar.setTime(startOfDay);

        assertEquals(2022, resultCalendar.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, resultCalendar.get(Calendar.MONTH));
        assertEquals(10, resultCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, resultCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, resultCalendar.get(Calendar.MINUTE));
        assertEquals(0, resultCalendar.get(Calendar.SECOND));
        assertEquals(0, resultCalendar.get(Calendar.MILLISECOND));
    }

    @Test
    void testGetEndOfDayFromDate() {
        Date endOfDay = Y9DayUtil.getEndOfDay(testDate);

        Calendar resultCalendar = Calendar.getInstance();
        resultCalendar.setTime(endOfDay);

        assertEquals(2022, resultCalendar.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, resultCalendar.get(Calendar.MONTH));
        assertEquals(10, resultCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(23, resultCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, resultCalendar.get(Calendar.MINUTE));
        assertEquals(59, resultCalendar.get(Calendar.SECOND));
        assertEquals(999, resultCalendar.get(Calendar.MILLISECOND));
    }

    @Test
    void testGetStartOfDayFromLong() {
        Calendar startOfDay = Y9DayUtil.getStartOfDay(testTimeInMillis);

        assertEquals(2022, startOfDay.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, startOfDay.get(Calendar.MONTH));
        assertEquals(10, startOfDay.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, startOfDay.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, startOfDay.get(Calendar.MINUTE));
        assertEquals(0, startOfDay.get(Calendar.SECOND));
        assertEquals(0, startOfDay.get(Calendar.MILLISECOND));
    }

    @Test
    void testGetEndOfDayFromLong() {
        Calendar endOfDay = Y9DayUtil.getEndOfDay(testTimeInMillis);

        assertEquals(2022, endOfDay.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, endOfDay.get(Calendar.MONTH));
        assertEquals(10, endOfDay.get(Calendar.DAY_OF_MONTH));
        assertEquals(23, endOfDay.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, endOfDay.get(Calendar.MINUTE));
        assertEquals(59, endOfDay.get(Calendar.SECOND));
        assertEquals(999, endOfDay.get(Calendar.MILLISECOND));
    }

    @Test
    void testEdgeCases() {
        // 测试一天的开始和结束时间相差应该是24小时减1毫秒
        Date startOfDay = Y9DayUtil.getStartOfDay(testDate);
        Date endOfDay = Y9DayUtil.getEndOfDay(testDate);

        long diff = endOfDay.getTime() - startOfDay.getTime();
        assertEquals(24 * 60 * 60 * 1000 - 1, diff); // 24小时减1毫秒

        // 测试使用Calendar的方法一致性
        Calendar startOfDayCalendar = Y9DayUtil.getStartOfDay(testTimeInMillis);
        Calendar endOfDayCalendar = Y9DayUtil.getEndOfDay(testTimeInMillis);

        assertEquals(startOfDay.getTime(), startOfDayCalendar.getTimeInMillis());
        assertEquals(endOfDay.getTime(), endOfDayCalendar.getTimeInMillis());
    }
}