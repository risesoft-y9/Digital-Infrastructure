package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 授权错误码 10-03-xx
 *
 * @author shidaobang
 * @date 2023/06/07
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum AuthorizationErrorCodeEnum implements ErrorCode {
    /** authorization not found */
    AUTHORIZATION_NOT_FOUND(0, "授权[{}]不存在"),
    /** 当前资源的授权列表已包含要添加的组织节点 */
    ORG_UNIT_INCLUDED(1, "当前资源的授权列表已包含要添加的组织节点[{}]");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.AUTHORIZATION_MODULE_CODE;
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
