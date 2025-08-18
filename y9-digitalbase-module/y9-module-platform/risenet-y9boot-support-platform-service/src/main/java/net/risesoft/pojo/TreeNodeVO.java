package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;

@Getter
@Setter
public abstract class TreeNodeVO implements Serializable {

    private static final long serialVersionUID = 5005690888928505845L;

    public String id;

    public String name;

    public String parentId;

    public int tabIndex = 0;

    // 除非确认没有子节点 否则默认返回 true
    public boolean hasChild = true;

    /**
     * 节点类型，包括所有树的节点类型 <br>
     * 组织树的所有节点类型 {@link OrgTypeEnum} <br>
     * 角色树的所有节点类型 {@link net.risesoft.enums.platform.RoleTypeEnum} <br>
     * 资源树的所有节点类型 {@link ResourceTypeEnum} <br>
     * 系统树的所有节点类型
     **/
    public String nodeType;

    public abstract TreeTypeEnum getTreeType();

}
