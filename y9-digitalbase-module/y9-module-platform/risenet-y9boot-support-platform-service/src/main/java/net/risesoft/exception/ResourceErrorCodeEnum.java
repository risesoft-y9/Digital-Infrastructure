package net.risesoft.exception;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.ErrorCodeConsts;

/**
 * 资源错误码 10-01-xx
 *
 * @author shidaobang
 * @date 2023/06/07
 * @since 9.6.2
 */
@RequiredArgsConstructor
public enum ResourceErrorCodeEnum implements ErrorCode {
    /** menu not found */
    MENU_NOT_FOUND(0, "菜单[{}]不存在"),
    /** operation not found */
    OPERATION_NOT_FOUND(1, "按钮[{}]不存在"),
    /** app not found */
    APP_NOT_FOUND(2, "应用[{}]不存在"),
    /** app icon not found */
    APP_ICON_NOT_FOUND(3, "应用图标[{}]不存在"),
    /** 数据目录不存在 */
    DATA_CATALOG_NOT_FOUND(4, "数据目录[{}]不存在"),
    /** IP 地址格式不合法 */
    IP_NOT_VALID(5, "IP 地址[{}]格式不合法"),
    /** 资源父节点不存在 */
    RESOURCE_PARENT_NOT_FOUND(6, "资源父节点[{}]不存在"),
    /** 资源节点不存在 */
    RESOURCE_NOT_FOUND(7, "资源节点[{}]不存在");

    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return ErrorCodeConsts.SYSTEM_CODE;
    }

    @Override
    public int moduleCode() {
        return ErrorCodeConsts.RESOURCE_MODULE_CODE;
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
