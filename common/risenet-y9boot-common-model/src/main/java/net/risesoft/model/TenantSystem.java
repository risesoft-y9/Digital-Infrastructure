package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 租户系统
 * 
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
@Data
public class TenantSystem implements Serializable {

    private static final long serialVersionUID = -8865793827133987927L;

    /** 主键id */
    private String id;

    /** 租户id */
    private String tenantId;

    /** 系统id */
    private String systemId;

    /** 数据源id */
    private String tenantDataSource;

    /** 租户数据已经初始化 */
    private Boolean initialized;

}
