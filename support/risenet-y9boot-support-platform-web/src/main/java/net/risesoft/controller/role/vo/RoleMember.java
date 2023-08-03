package net.risesoft.controller.role.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色关联组织节点
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Getter
@Setter
public class RoleMember implements Serializable {

    private static final long serialVersionUID = -6419413051796171825L;

    /** id */
    private Integer id;

    /** 是否为负角色关联 */
    private String negative;

    /** 组织节点id */
    private String orgUnitId;

    /** 组织节点dn */
    private String unitDn;

    /** 组织节点名称 */
    private String unitName;

    /** 组织节点类型 */
    private String unitTypeName;

}
