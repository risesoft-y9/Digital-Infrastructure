package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.enums.ManagerLevelEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.IsSecurityManager;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9PermissionException;

/**
 * 是否为安全管理员的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class IsSecurityManagerAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        IsSecurityManager isSecurityManager = AnnotationUtils.findAnnotation(method, IsSecurityManager.class);
        if (isSecurityManager != null) {
            if (isSecurityManager.departmental()) {
                checkDeptSecurityManager();
            } else {
                checkGlobalSecurityManager();
            }
        }
    }

    private static void checkGlobalSecurityManager() {
        if (!(Y9LoginUserHolder.getUserInfo().isGlobalManager()
                && ManagerLevelEnum.SECURITY_MANAGER.getValue().equals(Y9LoginUserHolder.getUserInfo().getManagerLevel()))) {
            // 如果不是全局的安全管理员则抛出异常
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_GLOBAL_SECURITY_MANAGER.getCode(), GlobalErrorCodeEnum.NOT_GLOBAL_SECURITY_MANAGER.getDescription());
        }
    }

    private static void checkDeptSecurityManager() {
        if (!(!Y9LoginUserHolder.getUserInfo().isGlobalManager()
                && ManagerLevelEnum.SECURITY_MANAGER.getValue().equals(Y9LoginUserHolder.getUserInfo().getManagerLevel()))) {
            // 如果不是部门的安全管理员则抛出异常
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_DEPT_SECURITY_MANAGER.getCode(), GlobalErrorCodeEnum.NOT_DEPT_SECURITY_MANAGER.getDescription());
        }
    }

}
