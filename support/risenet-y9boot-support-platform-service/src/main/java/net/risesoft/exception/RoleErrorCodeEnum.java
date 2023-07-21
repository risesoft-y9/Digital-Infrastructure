package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 角色错误码 10-15-xx
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum RoleErrorCodeEnum implements ErrorCode {
    ROLE_NOT_FOUND(0, "角色[{}]不存在");

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
