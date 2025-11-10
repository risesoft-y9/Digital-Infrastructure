package net.risesoft.model.platform;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 事件信息
 *
 * @author shidaobang
 * @date 2025/10/30
 */
@Data
public class PublishedEvent extends BaseModel {

    private static final long serialVersionUID = -6056135340786126721L;

    /** 主键 */
    private String id;

    /** 租户id */
    private String tenantId;

    /** 事件类型 */
    private String eventType;

    /** 事件类型名称 */
    private String eventName;

    /** 事件处理对象id */
    private String objId;

    /** 事件操作者 */
    private String operator;

    /** 操作者的客户端ip */
    private String clientIp;

    /** 具体事件描述 */
    private String eventDescription;

    /** 事件处理对象实体类信息 */
    private String entityJson;

}
