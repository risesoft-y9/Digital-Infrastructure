package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织身份事件
 *
 * @author mengjuhua
 * @date 2022/09/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageOrg implements Serializable {

    private static final long serialVersionUID = -5015205405901752553L;

    /**
     * 事件对象信息
     */
    private Serializable orgObj;

    /** 事件类型 */
    private String eventType;

    /** 租户id */
    private String tenantId;

}
