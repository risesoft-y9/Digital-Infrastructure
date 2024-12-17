package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 角色错误码 10-02-xx
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum RoleErrorCodeEnum implements ErrorCode {
    /** role not found */
    ROLE_NOT_FOUND(0, "角色[{}]不存在"),
    /** orgunit role not found */
    ORG_UNIT_ROLE_NOT_FOUND(1, "组织节点和角色的关联[{}]不存在"),
    /** 当前角色的角色成员中已包含要添加的组织节点 */
    ORG_UNIT_INCLUDED(2, "当前角色的角色成员中已包含要添加的组织节点[{}]");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.ROLE_MODULE_CODE;
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
