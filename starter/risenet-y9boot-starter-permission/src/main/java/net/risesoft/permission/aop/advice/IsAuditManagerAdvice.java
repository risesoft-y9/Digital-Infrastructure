package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.IsAuditManager;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9PermissionException;

/**
 * 是否为安全审计员的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class IsAuditManagerAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        IsAuditManager isAuditManager = AnnotationUtils.findAnnotation(method, IsAuditManager.class);
        if (isAuditManager != null) {
            if (isAuditManager.departmental()) {
                checkDeptAuditManager();
            } else {
                checkGlobalAuditManager();
            }
        }
    }

    private static void checkGlobalAuditManager() {
        if (!(Y9LoginUserHolder.getUserInfo().isGlobalManager()
            && ManagerLevelEnum.AUDIT_MANAGER.getValue().equals(Y9LoginUserHolder.getUserInfo().getManagerLevel()))) {
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_GLOBAL_AUDIT_MANAGER.getCode(),
                GlobalErrorCodeEnum.NOT_GLOBAL_AUDIT_MANAGER.getDescription());
        }
    }

    private static void checkDeptAuditManager() {
        if (!(!Y9LoginUserHolder.getUserInfo().isGlobalManager()
            && ManagerLevelEnum.AUDIT_MANAGER.getValue().equals(Y9LoginUserHolder.getUserInfo().getManagerLevel()))) {
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_DEPT_AUDIT_MANAGER.getCode(),
                GlobalErrorCodeEnum.NOT_DEPT_AUDIT_MANAGER.getDescription());
        }
    }

}
