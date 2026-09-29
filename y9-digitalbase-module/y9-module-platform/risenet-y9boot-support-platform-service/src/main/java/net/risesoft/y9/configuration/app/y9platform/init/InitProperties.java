package net.risesoft.y9.configuration.app.y9platform.init;

import lombok.Getter;
import lombok.Setter;

/**
 * 初始化属性
 *
 * @author shidaobang
 * @date 2026/07/20
 */
@Getter
@Setter
public class InitProperties {

    /**
     * 初始化租户名称
     */
    private String tenantName = "default";

    /**
     * 是否创建租户数据库、schema
     */
    private boolean createSchemaEnabled = true;

    /**
     * 初始化租户数据库、schema 名称
     */
    private String tenantSchemaName = "y9_default";

    /**
     * 新建的表空间存储目录 不指定一般会存储在数据库的默认路径下 例子：/u01/app/oracle/oradata/orcl/
     */
    private String newTableSpacePath = "";

}
