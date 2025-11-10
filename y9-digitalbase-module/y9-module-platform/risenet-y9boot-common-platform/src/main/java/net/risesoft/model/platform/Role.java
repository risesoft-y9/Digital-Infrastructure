package net.risesoft.model.platform;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.BaseModel;

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
public class Role extends BaseModel implements Serializable {

    private static final long serialVersionUID = -7783526250811707188L;

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 应用id
     */
    private String appId;

    /** 系统id 公共角色时为空 */
    private String systemId;

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 描述
     */
    private String description;

    /** 自定义id */
    private String customId;

    /**
     * 继承关系
     */
    private String dn;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    private String guidPath;

    /**
     * 扩展属性
     */
    private String properties;

    /**
     * 类型
     */
    private RoleTypeEnum type;

    /**
     * tenantCustom=true时的租户id
     */
    private String tenantId;

    /**
     * 动态角色
     */
    private Boolean dynamic;

    /**
     * 父节点
     */
    private String parentId;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 扩展属性键值对
     */
    // private Map<String, String> values;
}
