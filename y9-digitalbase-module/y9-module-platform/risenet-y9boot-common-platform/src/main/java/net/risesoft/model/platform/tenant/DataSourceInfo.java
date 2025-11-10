package net.risesoft.model.platform.tenant;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.model.BaseModel;

/**
 * 数据源信息
 *
 * @author shidaobang
 * @date 2025/11/06
 */
@Data
public class DataSourceInfo extends BaseModel {

    private static final long serialVersionUID = 4824010195634081642L;

    /** 主键 */
    private String id;

    /** 数据源类型1=jndi; 2=druid */
    private DataSourceTypeEnum type;

    /** 数据源名称 */
    @NotBlank
    private String jndiName;

    /** 驱动 */
    private String driver;

    /** 路径 */
    private String url;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 数据库初始化大小 */
    private Integer initialSize;

    /** 参数maxActive */
    private Integer maxActive;

    /** 参数minIdle */
    private Integer minIdle;

}
