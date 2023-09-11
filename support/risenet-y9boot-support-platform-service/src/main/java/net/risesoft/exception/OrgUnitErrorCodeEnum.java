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
    /** org unit as parent not found */
    ORG_UNIT_AS_PARENT_NOT_FOUND(0, "父节点[{}]不存在"),
    /** org unit parent not found */
    ORG_UNIT_PARENT_NOT_FOUND(1, "[{}]的父节点不存在"),
    /** org unit bureau not found */
    ORG_UNIT_BUREAU_NOT_FOUND(2, "[{}]的委办局不存在"),
    /** org unit not found */
    ORG_UNIT_NOT_FOUND(3, "组织节点[{}]不存在"),
    /** move to sub department not permitted */
    MOVE_TO_SUB_DEPARTMENT_NOT_PERMITTED(4, "禁止移动到子部门"),
    /** department not found */
    DEPARTMENT_NOT_FOUND(5, "部门[{}]不存在"),
    /** group not found */
    GROUP_NOT_FOUND(6, "用户组[{}]不存在"),
    /** job exists */
    JOB_EXISTS(7, "职位[{}]已存在"),
    /** related position exists */
    RELATED_POSITION_EXISTS(8, "职位存在关联的岗位，禁止删除"),
    /** job not found */
    JOB_NOT_FOUND(9, "职位[{}]不存在"),
    /** manager not found */
    MANAGER_NOT_FOUND(10, "管理员[{}]不存在"),
    /** person not found */
    PERSON_NOT_FOUND(11, "人员[{}]不存在"),
    /** person ext not found */
    PERSON_EXT_NOT_FOUND(12, "人员扩展信息[{}]不存在"),
    /** position is full */
    POSITION_IS_FULL(13, "岗位[{}]已满员，不能减小岗位容量"),
    /** position not found */
    POSITION_NOT_FOUND(14, "岗位[{}]不存在"),
    /** organization not found */
    ORGANIZATION_NOT_FOUND(15, "组织[{}]不存在"),
    /** custom group not found */
    CUSTOM_GROUP_NOT_FOUND(16, "自定义用户组[{}]不存在");

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
    public String getDescription() {
        return this.description;
    }
}
