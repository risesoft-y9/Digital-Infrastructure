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
    /** tenant not found */
    TENANT_NOT_FOUND(0, "租户[{}]不存在"),
    /** tenant system exists */
    TENANT_SYSTEM_EXISTS(1, "租户[{}]已租用系统[{}]"),
    /** tenant app not found */
    TENANT_APP_NOT_FOUND(2, "租户应用[{}]不存在"),
    /** 不能将租户移动到本身或子租户中 */
    MOVE_TO_SUB_TENANT_NOT_PERMITTED(3, "不能将租户移动到本身或子租户中");

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
    public String getDescription() {
        return this.description;
    }

}
