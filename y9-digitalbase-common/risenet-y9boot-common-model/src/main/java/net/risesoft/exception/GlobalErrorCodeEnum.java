package net.risesoft.exception;

import static net.risesoft.exception.GlobalErrorCodeConsts.AUTH_MODULE_CODE;
import static net.risesoft.exception.GlobalErrorCodeConsts.NONE_MODULE_CODE;
import static net.risesoft.exception.GlobalErrorCodeConsts.PERMISSION_MODULE_CODE;

import lombok.AllArgsConstructor;

/**
 * 全局的错误代码
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@AllArgsConstructor
public enum GlobalErrorCodeEnum implements ErrorCode {

    /** 操作成功 */
    SUCCESS(NONE_MODULE_CODE, 0, "操作成功"),
    /** 服务器内部错误 */
    FAILURE(NONE_MODULE_CODE, 1, "服务器内部错误，请联系开发人员"),
    /** 参数校验失败 */
    INVALID_ARGUMENT(NONE_MODULE_CODE, 2, "参数校验失败"),
    /** 实体类不存在 */
    ENTITY_NOT_FOUND(NONE_MODULE_CODE, 3, "对象[{}]不存在"),

    /** 令牌未传入 */
    ACCESS_TOKEN_NOT_FOUND(AUTH_MODULE_CODE, 0, "令牌未传入"),
    /** 令牌已失效 */
    ACCESS_TOKEN_EXPIRED(AUTH_MODULE_CODE, 1, "令牌已失效"),
    /** 校验令牌出问题了 */
    ACCESS_TOKEN_VERIFICATION_FAILED(AUTH_MODULE_CODE, 2, "校验令牌出问题了"),

    /** 权限不足拒绝访问 */
    PERMISSION_DENIED(PERMISSION_MODULE_CODE, 0, "权限不足拒绝访问"),
    /** IP 不在允许访问的白名单中 */
    IP_NOT_IN_WHITE_LIST(PERMISSION_MODULE_CODE, 1, "请求的 IP [{}]不在白名单中"),
    /** IP 在不允许访问的黑名单中 */
    IP_NOT_IN_BLACK_LIST(PERMISSION_MODULE_CODE, 2, "请求的 IP [{}]在黑名单中"),
    /** 访问控制记录不存在 */
    ACCESS_CONTROL_NOT_FOUND(PERMISSION_MODULE_CODE, 3, "访问控制记录[{}]不存在"),
    /** 当前接口需验证签名，请求头不完整 */
    API_SIGN_HEADERS_INCOMPLETE(PERMISSION_MODULE_CODE, 4, "当前接口需验证签名，请求头不完整"),
    /** 当前接口签名不正确 */
    API_SIGN_INCORRECT(PERMISSION_MODULE_CODE, 5, "接口签名不正确"),
    /** 请求已失效 */
    API_SIGN_TIMESTAMP_INVALID(PERMISSION_MODULE_CODE, 6, "请求已失效"),
    /** 当前用户不是全局系统管理员 */
    NOT_GLOBAL_SYSTEM_MANAGER(PERMISSION_MODULE_CODE, 11, "当前用户不是全局系统管理员"),
    /** 当前用户不是全局安全保密员 */
    NOT_GLOBAL_SECURITY_MANAGER(PERMISSION_MODULE_CODE, 12, "当前用户不是全局安全保密员"),
    /** 当前用户不是全局安全审计员 */
    NOT_GLOBAL_AUDIT_MANAGER(PERMISSION_MODULE_CODE, 13, "当前用户不是全局安全审计员"),
    /** 当前用户不是部门系统管理员 */
    NOT_DEPT_SYSTEM_MANAGER(PERMISSION_MODULE_CODE, 14, "当前用户不是部门系统管理员"),
    /** 当前用户不是部门安全保密员 */
    NOT_DEPT_SECURITY_MANAGER(PERMISSION_MODULE_CODE, 15, "当前用户不是部门安全保密员"),
    /** 当前用户不是部门安全审计员 */
    NOT_DEPT_AUDIT_MANAGER(PERMISSION_MODULE_CODE, 16, "当前用户不是部门安全审计员"),
    /** 当前用户没有拥有角色[{}] */
    PERSON_NOT_HAS_ROLE(PERMISSION_MODULE_CODE, 20, "当前用户没有拥有角色[{}]"),
    /** 当前岗位没有拥有角色[{}] */
    POSITION_NOT_HAS_ROLE(PERMISSION_MODULE_CODE, 21, "当前岗位没有拥有角色[{}]"),
    /** 当前岗位没有拥有角色[{}] */
    NOT_MANAGER(PERMISSION_MODULE_CODE, 22, "当前用户不是[{}]"),
    /** 当前用户没有被授权资源[{}] */
    PERSON_UNAUTHORIZED_RESOURCE(PERMISSION_MODULE_CODE, 30, "当前用户没有被授权资源[{}]"),
    /** 当前岗位没有被授权资源[{}] */
    POSITION_UNAUTHORIZED_RESOURCE(PERMISSION_MODULE_CODE, 31, "当前岗位没有被授权资源[{}]"),

    ;

    private final int moduleCode;
    private final int moduleErrorCode;
    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int moduleCode() {
        return this.moduleCode;
    }

    @Override
    public int moduleErrorCode() {
        return this.moduleErrorCode;
    }

    @Override
    public int systemCode() {
        return GlobalErrorCodeConsts.SYSTEM_CODE;
    }
}
