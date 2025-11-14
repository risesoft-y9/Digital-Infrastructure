package net.risesoft.model.platform.resource;

import java.io.Serializable;
import java.util.Comparator;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import net.risesoft.enums.platform.resource.ResourceTypeEnum;

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
public abstract class Resource implements Serializable, Comparable<Resource> {

    private static final long serialVersionUID = 1680635528797868917L;

    /**
     * 唯一标识
     */
    protected String id;

    /**
     * 自定义 id
     */
    protected String customId;

    /**
     * 系统 id
     */
    protected String systemId;

    /**
     * 名称
     */
    @NotBlank
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
     * 资源类型
     *
     * {@link ResourceTypeEnum}
     */
    protected ResourceTypeEnum resourceType;

    /**
     * 是否为继承上级节点的权限
     */
    protected Boolean inherit;

    /**
     * 排序
     */
    protected Integer tabIndex;

    /** 由ID组成的父子关系列表(正序)，之间用逗号分隔 */
    protected String guidPath;

    @Override
    public int compareTo(Resource resource) {
        // 排序时能保证同系统中同一层级（parentId 相同）的资源能按 tabIndex 升序排列
        return Comparator.comparing(Resource::getSystemId)
            .thenComparing(Resource::getParentId, Comparator.nullsFirst(String::compareTo))
            .thenComparing(Resource::getTabIndex)
            .compare(this, resource);
    }

    public abstract String getAppId();

    public abstract String getParentId();
}
