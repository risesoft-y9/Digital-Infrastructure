package net.risesoft.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/**
 * 组织节点
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
// Jackson框架下的多态反序列化
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "orgType")
@JsonSubTypes({@JsonSubTypes.Type(value = Department.class, name = "Department"),
    @JsonSubTypes.Type(value = Group.class, name = "Group"),
    @JsonSubTypes.Type(value = Manager.class, name = "Manager"),
    @JsonSubTypes.Type(value = Organization.class, name = "Organization"),
    @JsonSubTypes.Type(value = Person.class, name = "Person"),
    @JsonSubTypes.Type(value = Position.class, name = "Position")})
public class OrgUnit implements Serializable {
    private static final long serialVersionUID = 4473986529965103226L;

    /**
     * 唯一标识
     */
    protected String id;

    /**
     * 父节点ID
     */
    protected String parentId;

    /**
     * 租户Id
     */
    protected String tenantId;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    /**
     * 是否禁用
     */
    protected Boolean disabled = false;

    /**
     * 描述
     */
    protected String description;

    /**
     * 自定义ID
     */
    protected String customId;

    /**
     * 域名称
     */
    protected String dn;

    /**
     * 名称
     */
    protected String name;

    /**
     * 节点类型
     */
    protected String orgType;

    /**
     * 扩展属性（json格式）
     */
    protected String properties;

    /**
     * 序号
     */
    protected Integer tabIndex;

    /**
     * 由ID组成的父子关系列表，之间用逗号分隔
     */
    protected String guidPath;

}
