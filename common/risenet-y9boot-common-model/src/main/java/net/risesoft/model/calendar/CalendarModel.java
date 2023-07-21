package net.risesoft.model.calendar;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.model.calendar.enums.ActivityType;
import net.risesoft.model.calendar.enums.CalendarType;

/**
 * 日程实体类
 *
 * @author shidaobang
 */
@Data
public class CalendarModel implements Serializable {

    private static final long serialVersionUID = -7454620978289052570L;

    /**
     * 日程id主键
     */
    private String id;

    /**
     * 创建时间
     *
     * @ignore
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 日程人员id
     */
    private String personId;

    /**
     * 日程人员姓名
     */
    private String personName;

    /**
     * 日程标题
     */
    private String title;

    /**
     * 地点
     */
    private String address;

    /**
     * 日程内容
     */
    private String content;

    /**
     * 日程开始时间 ,包含时分秒
     */
    private String startDate;

    /**
     * 日程结束时间 ,包含时分秒
     */
    private String endDate;

    /**
     * 活动类型
     */
    private ActivityType activityType;

    /**
     * 是否成功发送到微信
     */
    private boolean weChatSent;

    /**
     * 日程分享id
     */
    private String calendarShareId;

    /**
     * 分享人姓名
     */
    private String sharePersonName;

    /**
     * 日程类型 0.自己创建 1.他人分享 2.待确认 3.请假
     */
    private CalendarType type;

    /**
     * 反馈理由
     */
    private String feedbackReason;

    /**
     * 是否为重复日程
     */
    private boolean repeat;

    /**
     * 日程颜色
     */
    private String color;

    /**
     * 日程标题
     */
    private String windowTitle;

    public String getColor() {
        CalendarType calendartype = getType();
        if (calendartype == null) {
            return null;
        }
        if (calendartype.equals(CalendarType.SHARED)) {
            return CalendarType.SHARED.getColor();
        } else if (calendartype.equals(CalendarType.WAIT_FOR_CONFIRM)) {
            return CalendarType.WAIT_FOR_CONFIRM.getColor();
        } else if (calendartype.equals(CalendarType.LEAVE)) {
            return CalendarType.LEAVE.getColor();
        } else {
            return CalendarType.OWN.getColor();
        }
    }

    public String getWindowTitle() {
        CalendarType calendartype = getType();
        if (calendartype == null) {
            return null;
        }
        if (calendartype.equals(CalendarType.SHARED)) {
            return "来自分享的日程";
        } else if (calendartype.equals(CalendarType.WAIT_FOR_CONFIRM)) {
            return "待确认的日程";
        } else if (calendartype.equals(CalendarType.LEAVE)) {
            return "已请假的日程";
        } else {
            return "我的日程";
        }
    }

}
