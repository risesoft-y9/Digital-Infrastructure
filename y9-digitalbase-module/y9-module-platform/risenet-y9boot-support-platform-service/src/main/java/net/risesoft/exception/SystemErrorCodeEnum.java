package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 系统错误码 10-04-xx
 *
 * @author shidaobang
 * @date 2023/06/07
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum SystemErrorCodeEnum implements ErrorCode {
    SYSTEM_NOT_FOUND(0, "系统[{}]不存在"),
    SYSTEM_WITH_SPECIFIC_NAME_EXISTS(1, "已存在名称为[{}]的系统"),
    SYSTEM_HAS_APPS(2, "存在关联的应用，系统不能删除"),
    SYSTEM_REGISTERED_BY_TENANT(3, "系统已有租户租用，不能删除"),;

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.SYSTEM_MODULE_CODE;
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
