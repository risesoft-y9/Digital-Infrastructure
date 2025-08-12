package net.risesoft.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 审计相关枚举类
 *
 * @author shidaobang
 * @date 2025/08/07
 */
@Getter
@RequiredArgsConstructor
public enum AuditLogEnum {
    // 职位
    JOB_CREATE("JOB_CREATE", "职位 [{}] 创建"),
    JOB_DELETE("JOB_DELETE", "职位 [{}] 删除"),
    JOB_UPDATE("JOB_UPDATE", "职位 [{}] 更新"),

    // 组织机构
    ORGANIZATION_CREATE("ORGANIZATION_CREATE", "组织机构 [{}] 创建"),
    ORGANIZATION_DELETE("ORGANIZATION_DELETE", "组织机构 [{}] 删除"),
    ORGANIZATION_UPDATE("ORGANIZATION_UPDATE", "组织机构 [{}] 更新"),
    ORGANIZATION_UPDATE_DISABLED("ORGANIZATION_UPDATE_DISABLED", "组织机构 [{}] 更新状态为 [{}]"),
    ORGANIZATION_UPDATE_PROPERTIES("ORGANIZATION_UPDATE_PROPERTIES", "组织机构 [{}] 更新扩展属性为 [{}]"),

    // 部门
    DEPARTMENT_CREATE("DEPARTMENT_CREATE", "部门 [{}] 创建"),
    DEPARTMENT_DELETE("DEPARTMENT_DELETE", "部门 [{}] 删除"),
    DEPARTMENT_UPDATE("DEPARTMENT_UPDATE", "部门 [{}] 更新"),
    DEPARTMENT_UPDATE_TABINDEX("DEPARTMENT_UPDATE_TABINDEX", "部门 [{}] 更新排序号为 [{}]"), // todo
    DEPARTMENT_UPDATE_DISABLED("DEPARTMENT_UPDATE_DISABLED", "部门 [{}] 更新状态为 [{}]"),
    DEPARTMENT_UPDATE_PARENTID("DEPARTMENT_UPDATE_PARENTID", "部门 [{}] 更新父节点（移动）为 [{}]"),
    DEPARTMENT_UPDATE_PROPERTIES("DEPARTMENT_UPDATE_PROPERTIES", "部门 [{}] 更新扩展属性为 [{}]"),

    // 人员
    PERSON_CREATE("PERSON_CREATE", "人员 [{}] 创建"),
    PERSON_DELETE("PERSON_DELETE", "人员 [{}] 删除"),
    PERSON_UPDATE("PERSON_UPDATE", "人员 [{}] 更新"),
    PERSON_UPDATE_TABINDEX("PERSON_UPDATE_TABINDEX", "人员 [{}] 更新排序号为 [{}]"), // todo
    PERSON_UPDATE_DISABLED("PERSON_UPDATE_DISABLED", "人员 [{}] 更新状态为 [{}]"),
    PERSON_UPDATE_PARENTID("PERSON_UPDATE_PARENTID", "人员 [{}] 更新父节点（移动）为 [{}]"),
    PERSON_UPDATE_PASSWORD("PERSON_UPDATE_PASSWORD", "人员 [{}] 修改密码"),
    PERSON_UPDATE_PROPERTIES("PERSON_UPDATE_PROPERTIES", "人员 [{}] 更新扩展属性为 [{}]"),
    PERSON_RESET_PASSWORD("PERSON_RESET_PASSWORD", "人员 [{}] 重置密码"),
    PERSON_GROUP_ORDER("GROUP_ORDER", "人员的用户组排序"), // todo
    PERSON_POSITION_ORDER("PERSON_POSITION_ORDER", "人员的岗位排序"), // todo

    // 用户组
    GROUP_CREATE("GROUP_CREATE", "用户组 [{}] 创建"),
    GROUP_DELETE("GROUP_DELETE", "用户组 [{}] 删除"),
    GROUP_UPDATE("GROUP_UPDATE", "用户组 [{}] 更新"),
    GROUP_UPDATE_TABINDEX("GROUP_UPDATE_TABINDEX", "用户组 [{}] 更新排序号为 [{}]"), // todo
    GROUP_UPDATE_DISABLED("GROUP_UPDATE_DISABLED", "用户组 [{}] 更新状态为 [{}]"),
    GROUP_UPDATE_PARENTID("GROUP_UPDATE_PARENTID", "用户组 [{}] 更新父节点（移动）为 [{}]"),
    GROUP_UPDATE_PROPERTIES("GROUP_UPDATE_PROPERTIES", "用户组 [{}] 更新扩展属性为 [{}]"),
    GROUP_ADD_PERSON("GROUP_ADD_PERSON", "用户组 [{}] 添加人员 [{}]"),
    GROUP_REMOVE_PERSON("GROUP_REMOVE_PERSON", "用户组 [{}] 移除人员 [{}]"),
    GROUP_PERSON_ORDER("GROUP_ORDER", "用户组的人员排序"), // todo

    // 岗位
    POSITION_CREATE("POSITION_CREATE", "岗位 [{}] 创建"),
    POSITION_DELETE("POSITION_DELETE", "岗位 [{}] 删除"),
    POSITION_UPDATE("POSITION_UPDATE", "岗位 [{}] 更新"),
    POSITION_UPDATE_TABINDEX("POSITION_UPDATE_TABINDEX", "岗位 [{}] 更新排序号为 [{}]"), // todo
    POSITION_UPDATE_DISABLED("POSITION_UPDATE_DISABLED", "岗位 [{}] 更新状态为 [{}]"),
    POSITION_UPDATE_PARENTID("POSITION_UPDATE_PARENTID", "岗位 [{}] 更新父节点（移动）为 [{}]"),
    POSITION_UPDATE_PROPERTIES("POSITION_UPDATE_PROPERTIES", "岗位 [{}] 更新扩展属性为 [{}]"),
    POSITION_ADD_PERSON("POSITION_ADD_PERSON", "岗位 [{}] 添加人员 [{}]"),
    POSITION_REMOVE_PERSON("POSITION_REMOVE_PERSON", "岗位 [{}] 移除人员 [{}]"),
    POSITION_PERSON_ORDER("POSITION_PERSON_ORDER", "岗位的人员排序"), // todo

    // 三员管理员
    MANAGER_CREATE("MANAGER_CREATE", "{} [{}] 创建"),
    MANAGER_DELETE("MANAGER_DELETE", "{} [{}] 删除"),
    MANAGER_UPDATE("MANAGER_UPDATE", "{} [{}] 更新"),
    MANAGER_UPDATE_DISABLED("MANAGER_UPDATE_DISABLED", "{} [{}] 更新状态为 [{}]"),
    MANAGER_UPDATE_PASSWORD("MANAGER_UPDATE_PASSWORD", "{} [{}] 修改密码"),
    MANAGER_RESET_PASSWORD("MANAGER_RESET_PASSWORD", "{} [{}] 重置密码"),

    // 系统
    SYSTEM_CREATE("SYSTEM_CREATE", "系统 [{}] 创建"),
    SYSTEM_DELETE("SYSTEM_DELETE", "系统 [{}] 删除"),
    SYSTEM_UPDATE("SYSTEM_UPDATE", "系统 [{}] 更新"),
    SYSTEM_UPDATE_ENABLE("SYSTEM_UPDATE_ENABLE", "系统 [{}] 更新状态为 [{}]"),

    // 应用
    APP_CREATE("APP_CREATE", "应用 [{}] 创建"),
    APP_DELETE("APP_DELETE", "应用 [{}] 删除"),
    APP_UPDATE("APP_UPDATE", "应用 [{}] 更新"),
    APP_UPDATE_ENABLE("APP_UPDATE_ENABLE", "应用 [{}] 更新状态为 [{}]"),

    // 菜单
    MENU_CREATE("MENU_CREATE", "菜单 [{}] 创建"),
    MENU_DELETE("MENU_DELETE", "菜单 [{}] 删除"),
    MENU_UPDATE("MENU_UPDATE", "菜单 [{}] 更新"),
    MENU_UPDATE_ENABLE("MENU_UPDATE_ENABLE", "菜单 [{}] 更新状态为 [{}]"),

    // 按钮
    OPERATION_CREATE("OPERATION_CREATE", "按钮 [{}] 创建"),
    OPERATION_DELETE("OPERATION_DELETE", "按钮 [{}] 删除"),
    OPERATION_UPDATE("OPERATION_UPDATE", "按钮 [{}] 更新"),
    OPERATION_UPDATE_ENABLE("OPERATION_UPDATE_ENABLE", "按钮 [{}] 更新状态为 [{}]"),

    // 权限
    AUTHORIZATION_CREATE("AUTHORIZATION_CREATE", "授权 [{}] {} [{}] 创建"),
    AUTHORIZATION_DELETE("AUTHORIZATION_DELETE", "授权 [{}] {} [{}] 删除"),

    // 角色
    ROLE_CREATE("ROLE_CREATE", "角色 [{}] 创建"),
    ROLE_DELETE("ROLE_DELETE", "角色 [{}] 删除"),
    ROLE_UPDATE("ROLE_UPDATE", "角色 [{}] 更新"),
    ROLE_UPDATE_PARENTID("ROLE_UPDATE_PARENTID", "角色 [{}] 更新父节点（移动）为 [{}]"),
    ROLE_UPDATE_PROPERTIES("ROLE_UPDATE_PROPERTIES", "角色 [{}] 更新扩展属性为 [{}]"), // remove?
    ROLE_ADD_MEMBER("ROLE_ADD_MEMBER", "角色 [{}] 添加成员 [{}]"),
    ROLE_REMOVE_REMEMBER("ROLE_REMOVE_REMEMBER", "角色 [{}] 移除成员 [{}]"),

    // 数据字典
    DICTIONARY_TYPE_CREATE("DICTIONARY_TYPE_CREATE", "字典表类型 [{}] 创建"),
    DICTIONARY_TYPE_DELETE("DICTIONARY_TYPE_DELETE", "字典表类型 [{}] 删除"),
    DICTIONARY_VALUE_CREATE("DICTIONARY_VALUE_CREATE", "字典表值 [{}] 创建"),
    DICTIONARY_VALUE_DELETE("DICTIONARY_VALUE_DELETE", "字典表值 [{}] 删除"),
    DICTIONARY_VALUE_UPDATE("DICTIONARY_VALUE_UPDATE", "字典表值 [{}] 更新"),

    // 租户
    TENANT_CREATE("TENANT_CREATE", "租户 [{}] 创建"),
    TENANT_UPDATE("TENANT_UPDATE", "租户 [{}] 更新"),

    // 租户设置
    TENANT_SETTING_UPDATE("TENANT_SETTING_UPDATE", ""),; // todo

    private final String action;
    private final String description;

}
