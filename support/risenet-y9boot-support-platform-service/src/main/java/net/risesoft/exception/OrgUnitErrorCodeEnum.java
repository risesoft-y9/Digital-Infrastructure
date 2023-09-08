package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 组织节点错误码 10-00-xx
 *
 * @author shidaobang
 * @date 2023/09/08
 * @since 9.6.3
 */
@RequiredArgsConstructor
public enum OrgUnitErrorCodeEnum implements ErrorCode {
    ORG_UNIT_AS_PARENT_NOT_FOUND(0, "父节点[{}]不存在"), ORG_UNIT_PARENT_NOT_FOUND(1, "[{}]的父节点不存在"),
    ORG_UNIT_BUREAU_NOT_FOUND(2, "[{}]的委办局不存在"), ORG_UNIT_NOT_FOUND(3, "组织节点[{}]不存在");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.ORGANIZATION_MODULE_CODE;
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
