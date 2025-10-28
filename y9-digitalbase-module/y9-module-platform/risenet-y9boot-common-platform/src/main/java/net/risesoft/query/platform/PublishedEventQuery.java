package net.risesoft.query.platform;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 事件信息查询
 * 
 * @author mengjuhua
 * @date 2025/10/24
 */
@Data
@Builder
public class PublishedEventQuery {

    /** 租户id */
    private String tenantId;
    /** 事件名 */
    private String eventName;

    /** 事件描述 */
    private String eventDescription;

    /** 开始时间 格式 yyyy-MM-dd HH:mm:ss */
    private Date startTime;

    /** 结束时间 格式 yyyy-MM-dd HH:mm:ss */
    private Date endTime;

}
