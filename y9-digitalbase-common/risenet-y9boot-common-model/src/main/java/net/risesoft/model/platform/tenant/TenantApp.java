package net.risesoft.model.platform.tenant;

import java.io.Serializable;

import lombok.Data;

/**
 * 租户应用
 * 
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
@Data
public class TenantApp implements Serializable {

    private static final long serialVersionUID = -8865793827133987927L;

    /** 主键id */
    private String id;

    /** 租户id */
    private String tenantId;

    /** 系统id */
    private String systemId;

    /** 应用 ID */
    private String appId;

}
