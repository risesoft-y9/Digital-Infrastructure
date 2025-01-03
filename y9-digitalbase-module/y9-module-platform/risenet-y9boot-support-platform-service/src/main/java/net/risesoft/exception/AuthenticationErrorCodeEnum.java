package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

@RequiredArgsConstructor
public enum AuthenticationErrorCodeEnum implements ErrorCode {
    LOGINNAME_PASSWORD_INCORRECT(0, "用户名或密码不正确");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.AUTHENTICATION_MODULE_CODE;
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
