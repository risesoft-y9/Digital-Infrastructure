package net.risesoft.controller.resource.vo;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.ResourceTypeEnum;

/**
 * 资源
 *
 * @author shidaobang
 * @date 2022/3/10
 */
@Getter
@Setter
public class ResourceBaseVO {

    /** 主键 */
    private String id;

    /** 名称 */
    private String name;

    /**
     * 资源类型：0=应用，1=菜单，2=操作
     *
     * {@link ResourceTypeEnum}
     */
    private Integer resourceType;

    /** 父节点ID */
    private String parentId;

    /** 系统id */
    private String systemId;

    /** 应用id */
    private String appId;

    /** 是否继承上级节点的权限 */
    private Boolean inherit;

    public String getParentId() {
        if (resourceType.equals(ResourceTypeEnum.APP.getValue())) {
            return systemId;
        }
        return parentId;
    }

    public String getAppId() {
        if (resourceType.equals(ResourceTypeEnum.APP.getValue())) {
            return id;
        }
        return appId;
    }
}
