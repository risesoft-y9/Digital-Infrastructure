package net.risesoft.vo.role;

import lombok.Data;

import net.risesoft.enums.platform.RoleTypeEnum;

/**
 * 角色
 *
 * @author shidaobang
 * @date 2022/3/16
 */
@Data
public class RoleVO {

    /** 主键 */
    private String id;

    /** 角色名称 */
    private String name;

    /** 名称组成的父子关系列表，之间用逗号分隔 */
    private String dn;

    /**
     * 节点类型
     *
     * {@link RoleTypeEnum}
     */
    private RoleTypeEnum type;

    /** 父节点ID */
    private String parentId;

    /** 系统名称 */
    private String systemName;

    /** 系统中文名称 */
    private String systemCnName;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    private String guidPath;

    /** 有子节点 */
    private Boolean hasChild = Boolean.FALSE;

}
