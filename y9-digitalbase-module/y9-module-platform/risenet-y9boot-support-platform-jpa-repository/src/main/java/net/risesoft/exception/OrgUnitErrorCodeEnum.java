package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 组织节点错误码 10-00-xx
 *
 * @author shidaobang
 * @date 2023/09/08
 * @since 9.6.3
 */
@RequiredArgsConstructor
public enum OrgUnitErrorCodeEnum implements ErrorCode {
    ORG_UNIT_AS_PARENT_NOT_FOUND(0, "父节点[{}]不存在"),
    ORG_UNIT_PARENT_NOT_FOUND(1, "[{}]的父节点不存在"),
    ORG_UNIT_BUREAU_NOT_FOUND(2, "[{}]的委办局不存在"),
    ORG_UNIT_NOT_FOUND(3, "组织节点[{}]不存在"),
    MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED(4, "禁止移动到子部门"),
    DEPARTMENT_NOT_FOUND(5, "部门[{}]不存在"),
    GROUP_NOT_FOUND(6, "用户组[{}]不存在"),
    JOB_EXISTS(7, "职位[{}]已存在"),
    RELATED_POSITION_EXISTS(8, "职位存在关联的岗位，禁止删除"),
    JOB_NOT_FOUND(9, "职位[{}]不存在"),
    MANAGER_NOT_FOUND(10, "管理员[{}]不存在"),
    PERSON_NOT_FOUND(11, "人员[{}]不存在"),
    PERSON_EXT_NOT_FOUND(12, "人员扩展信息[{}]不存在"),
    POSITION_IS_FULL(13, "岗位[{}]已满员，增加岗位容量或减少岗位关联人员"),
    POSITION_NOT_FOUND(14, "岗位[{}]不存在"),
    ORGANIZATION_NOT_FOUND(15, "组织[{}]不存在"),
    CUSTOM_GROUP_NOT_FOUND(16, "自定义用户组[{}]不存在"),
    OLD_PASSWORD_IS_INCORRECT(17, "旧密码不正确"),
    NOT_ALL_PERSONS_DISABLED(18, "存在未禁用的人员，禁止禁用当前节点"),
    NOT_ALL_POSITIONS_DISABLED(19, "存在未禁用的岗位，禁止禁用当前节点"),
    NOT_ALL_GROUPS_DISABLED(20, "存在未禁用的用户组，禁止禁用当前节点"),
    NOT_ALL_DEPARTMENTS_DISABLED(21, "存在未禁用的部门，禁止禁用当前节点"),
    NOT_ALL_DESCENDENTS_DISABLED(23, "存在未禁用的后代组织节点，不能禁用当前节点"),
    PERSON_OR_POSITION_NOT_FOUND(22, "人员或岗位[{}]不存在"),;

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.ORG_UNIT_MODULE_CODE;
    }

    @Override
    public int moduleErrorCode() {
        return this.moduleErrorCode;
    }

    @Override
    public int getCode() {
        return ErrorCode.super.formatCode();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
