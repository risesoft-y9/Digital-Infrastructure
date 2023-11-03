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

    /** 令牌未传入 */
    ACCESS_TOKEN_NOT_FOUND(AUTH_MODULE_CODE, 0, "令牌未传入"),
    /** 令牌已失效 */
    ACCESS_TOKEN_EXPIRED(AUTH_MODULE_CODE, 1, "令牌已失效"),
    /** 校验令牌出问题了 */
    ACCESS_TOKEN_VERIFICATION_FAILED(AUTH_MODULE_CODE, 2, "校验令牌出问题了"),

    /** 权限不足拒绝访问 */
    PERMISSION_DENIED(PERMISSION_MODULE_CODE, 0, "权限不足拒绝访问"),
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
    /** 当前用户没有被授权资源[{}] */
    PERSON_UNAUTHORIZED_RESOURCE(PERMISSION_MODULE_CODE, 30, "当前用户没有被授权资源[{}]"),
    /** 当前岗位没有被授权资源[{}] */
    POSITION_UNAUTHORIZED_RESOURCE(PERMISSION_MODULE_CODE, 31, "当前岗位没有被授权资源[{}]"),;

    private final int moduleCode;
    private final int moduleErrorCode;
    private final String description;

    @Override
    public int systemCode() {
        return GlobalErrorCodeConsts.SYSTEM_CODE;
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
    public String getDescription() {
        return this.description;
    }
}
