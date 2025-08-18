package net.risesoft.model.platform.tenant;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 租户
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Tenant implements Serializable {
    private static final long serialVersionUID = 9202505254222513538L;

    /** 租户中的主键id */
    private String id;

    /** 父节点id */
    private String parentId;

    /** 租户顺序号，自增字段 */
    private Integer serial;

    /** 租户英文名称 */
    private String shortName;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    private String guidPath;

    /** 由shortName组成的父子关系列表，之间用逗号分隔 */
    private String namePath;

    /** 租户名称 */
    private String name;

    /** 租户描述 */
    private String description;

    /** 是否启用 */
    private boolean enabled;

    /** 排序号 */
    private Integer tabIndex;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

    /** 租户logo */
    private String logoIcon;

    /** 门户页尾显示信息 */
    private String footer;

    /** 默认的租户数据源id，对应 DataSourceDefine 表的id字段 */
    private String defaultDataSourceId;

}
