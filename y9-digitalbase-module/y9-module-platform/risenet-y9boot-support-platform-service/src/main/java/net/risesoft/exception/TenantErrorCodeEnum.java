package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 租户错误码 10-06-xx
 *
 * @author shidaobang
 * @date 2023/06/07
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum TenantErrorCodeEnum implements ErrorCode {
    TENANT_NOT_FOUND(0, "租户[{}]不存在"),
    TENANT_SYSTEM_EXISTS(1, "租户[{}]已租用系统[{}]"),
    TENANT_APP_NOT_FOUND(2, "租户应用[{}]不存在"),
    MOVE_TO_SUB_TENANT_NOT_PERMITTED(3, "不能将租户移动到本身或子租户中"),
    NAME_HAS_BEEN_USED(4, "租户名[{}]已经被使用"),
    SHORT_NAME_HAS_BEEN_USED(5, "租户英文名[{}]已被使用"),
    TENANT_SYSTEM_NOT_EXISTS(6, "租户系统[{}]不存在"),;

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.TENANT_MODULE_CODE;
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
