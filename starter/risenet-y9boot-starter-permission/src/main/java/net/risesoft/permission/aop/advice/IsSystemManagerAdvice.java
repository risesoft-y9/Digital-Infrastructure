package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.IsSystemManager;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9PermissionException;

/**
 * 是否为系统管理员的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class IsSystemManagerAdvice implements MethodBeforeAdvice {

    private static void checkGlobalSystemManager() {
        if (!(Y9LoginUserHolder.getUserInfo().isGlobalManager() && ManagerLevelEnum.SYSTEM_MANAGER.getValue()
            .equals(Y9LoginUserHolder.getUserInfo().getManagerLevel().getValue()))) {
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_GLOBAL_SYSTEM_MANAGER.getCode(),
                GlobalErrorCodeEnum.NOT_GLOBAL_SYSTEM_MANAGER.getDescription());
        }
    }

    private static void checkDepartmentSystemManager() {
        if (!(!Y9LoginUserHolder.getUserInfo().isGlobalManager() && ManagerLevelEnum.SYSTEM_MANAGER.getValue()
            .equals(Y9LoginUserHolder.getUserInfo().getManagerLevel().getValue()))) {
            throw new Y9PermissionException(GlobalErrorCodeEnum.NOT_DEPT_SYSTEM_MANAGER.getCode(),
                GlobalErrorCodeEnum.NOT_DEPT_SYSTEM_MANAGER.getDescription());
        }
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        IsSystemManager isSystemManager = AnnotationUtils.findAnnotation(method, IsSystemManager.class);
        if (isSystemManager != null) {
            if (isSystemManager.departmental()) {
                checkDepartmentSystemManager();
            } else {
                checkGlobalSystemManager();
            }
        }
    }

}
