package net.risesoft.model.platform;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

import net.risesoft.enums.platform.RoleTypeEnum;

/**
 * 角色
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = -7783526250811707188L;

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 继承关系
     */
    private String dn;

    /**
     * 类型
     */
    private RoleTypeEnum type;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 父节点
     */
    private String parentId;

    /**
     * 承继节点
     */
    private String guidPath;

    /**
     * 系统标识
     */
    private String systemName;

    /**
     * 租户自定义
     */
    private Boolean tenantCustom = false;

    /**
     * tenantCustom=true时的租户id
     */
    private String tenantId;

    /**
     * 动态角色
     */
    private Boolean dynamic = false;

    /**
     * 扩展属性键值对
     */
    private Map<String, String> values;
}
