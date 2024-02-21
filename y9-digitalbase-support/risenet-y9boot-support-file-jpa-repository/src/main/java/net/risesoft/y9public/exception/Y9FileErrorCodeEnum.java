package net.risesoft.y9public.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.exception.ErrorCode;
import net.risesoft.exception.GlobalErrorCodeConsts;

/**
 * 文件服务错误码
 *
 * @author shidaobang
 * @date 2023/06/06
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum Y9FileErrorCodeEnum implements ErrorCode {
    FILE_NOT_FOUND(0, "文件[{}]不存在");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return GlobalErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return GlobalErrorCodeConsts.FILE_MODULE_CODE;
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
