package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author zhongchongjie
 *
 */
@Data
public class CalendarConfigModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5625830014785041762L;

    /**
     * 主键
     */
    private String id;

    private String workingDay2Holiday;

    /**
     * 周末补班日期
     */
    private String weekend2WorkingDay;

    /**
     * 全年节假日期，包括工作日休假，排除周末补班日期，存储多年的节假日
     */
    private String everyYearHoliday;

    /**
     * 年份
     */
    private String year;
}
