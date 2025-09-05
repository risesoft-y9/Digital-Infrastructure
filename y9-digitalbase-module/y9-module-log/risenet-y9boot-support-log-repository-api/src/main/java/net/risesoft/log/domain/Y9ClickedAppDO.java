package net.risesoft.log.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用点击信息表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9ClickedAppDO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 144334145599572308L;

    /** 主键，唯一标识 */
    private String id;

    /** 用户ID */
    private String personId;

    /** 租户ID */
    private String tenantId;

    /** 应用ID */
    private String appId;

    /** 应用名称 */
    private String appName;

    /** 保存日期 */
    private Date saveDate;
}
