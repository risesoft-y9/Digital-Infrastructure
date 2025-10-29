package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 树节点类型
 *
 * @author shidaobang
 * @date 2024/09/02
 */
@AllArgsConstructor
@Getter
public enum TreeNodeType {
    /** 角色 */
    ROLE("role"),
    /** 文件夹 */
    FOLDER("folder"),
    /** 组织机构 */
    ORGANIZATION("Organization"),
    /** 部门 */
    DEPARTMENT("Department"),
    /** 用户组 */
    GROUP("Group"),
    /** 岗位 */
    POSITION("Position"),
    /** 人员 */
    PERSON("Person"),
    /** 三员管理员 */
    MANAGER("Manager"),
    /** 系统 */
    SYSTEM("SYSTEM"),
    /** 应用 */
    APP("APP"),
    /** 菜单 */
    MENU("MENU"),
    /** 操作 */
    OPERATION("OPERATION");
    ;
    
    private final String value;
    
    
}
