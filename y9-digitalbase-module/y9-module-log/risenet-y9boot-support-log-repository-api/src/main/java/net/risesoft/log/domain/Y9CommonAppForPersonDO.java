package net.risesoft.log.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人常用应用信息表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9CommonAppForPersonDO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6201670055765212286L;

    /** 主键，唯一标识 */
    private String id;

    /** 用户ID */
    private String personId;

    /** 租户ID */
    private String tenantId;

    /** 应用IDS */
    private String appIds;
}
