package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 字典表错误码
 *
 * @author shidaobang
 * @date 2025/08/08
 */
@RequiredArgsConstructor
public enum DictionaryErrorCodeEnum implements ErrorCode {
    DICTIONARY_TYPE_NOT_FOUND(1, "字典表类型[{}]不存在"),
    DICTIONARY_VALUE_NOT_FOUND(2, "字典表值[{}]不存在"),;

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.DICTIONARY_MODULE_CODE;
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
