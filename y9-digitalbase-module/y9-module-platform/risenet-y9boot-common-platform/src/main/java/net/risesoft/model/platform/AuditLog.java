package net.risesoft.model.platform;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 审计日志
 *
 * @author shidaobang
 * @date 2025/10/29
 */
@Data
public class AuditLog extends BaseModel {

    private static final long serialVersionUID = 655407520989314224L;

    /**
     * 主键
     */
    private String id;

    /**
     * 租户 id
     */
    private String tenantId;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 用户浏览器或客户端信息
     */
    private String userAgent;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作简要描述
     */
    private String description;

    /**
     * 操作对象 id
     */
    private String objectId;

    /**
     * 旧对象 JSON
     */
    private String oldObjectJson;

    /**
     * 当前对象 JSON
     */
    private String currentObjectJson;
}
