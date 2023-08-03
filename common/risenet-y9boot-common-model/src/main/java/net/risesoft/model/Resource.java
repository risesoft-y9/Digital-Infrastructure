package net.risesoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import net.risesoft.enums.ResourceTypeEnum;

/**
 * 资源节点
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
// Jackson框架下的多态反序列化
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "resourceType")
@JsonSubTypes({@JsonSubTypes.Type(value = App.class, name = "0"), @JsonSubTypes.Type(value = Menu.class, name = "1"),
    @JsonSubTypes.Type(value = Operation.class, name = "2")})
public class Resource implements Serializable {

    private static final long serialVersionUID = 1680635528797868917L;

    /**
     * 唯一标识
     */
    protected String id;

    /**
     * 名称
     */
    protected String name;

    /**
     * 描述
     */
    protected String description;

    /**
     * 启用状态
     */
    protected Boolean enabled;

    /**
     * 是否隐藏
     */
    protected Boolean hidden;

    /**
     * 图标
     */
    protected String iconUrl;

    /**
     * 链接地址
     */
    protected String url;

    /**
     * 链接地址2
     */
    protected String url2;

    /**
     * 父节点ID
     */
    protected String parentId;

    /**
     * 资源类型
     *
     * {@link ResourceTypeEnum}
     */
    protected Integer resourceType;

    /**
     * 是否为继承上级节点的权限
     */
    protected Boolean inherit;

    /**
     * 排序
     */
    protected Integer tabIndex;

    /**
     * customID
     */
    protected String customId;

}
