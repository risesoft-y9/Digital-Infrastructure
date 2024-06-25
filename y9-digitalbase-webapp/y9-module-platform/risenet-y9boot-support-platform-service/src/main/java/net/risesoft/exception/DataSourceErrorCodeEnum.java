package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 数据源错误码 10-5-xx
 *
 * @author shidaobang
 * @date 2023/06/07
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum DataSourceErrorCodeEnum implements ErrorCode {
    /** 数据源不存在 */
    DATA_SOURCE_NOT_FOUND(0, "数据源[{}]不存在"),
    /** 数据源原密码错误 */
    DATA_SOURCE_OLD_PASSWORD_IS_WRONG(1, "数据源原密码错误"),
    /** JNDI数据源不允许重置密码 */
    JNDI_DATA_SOURCE_RESET_PASSWORD_NOT_ALLOWED(2, "JNDI数据源不允许重置密码"),
    /** 当前数据库暂未完全适配 */
    DATABASE_NOT_FULLY_SUPPORTED(3, "当前数据库暂未完全适配");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.DATA_SOURCE_MODULE_CODE;
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
