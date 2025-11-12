package net.risesoft.query.platform;

import java.util.Date;

import lombok.Data;

/**
 * 审计日志查询
 *
 * @author shidaobang
 * @date 2025/08/14
 */
@Data
public class AuditLogQuery {

    /**
     * 对象id
     */
    private String objectId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 用户 ip
     */
    private String userIp;

    /**
     * 租户 id
     */
    private String tenantId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 描述
     */
    private String description;

}
