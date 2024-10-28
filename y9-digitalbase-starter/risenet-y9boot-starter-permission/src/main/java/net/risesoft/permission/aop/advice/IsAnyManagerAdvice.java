package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 是否为管理员的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2023/12/21
 */
public class IsAnyManagerAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        IsAnyManager isAnyManager = AnnotationUtils.findAnnotation(method, IsAnyManager.class);
        if (isAnyManager != null) {
            checkAnyManager(isAnyManager.value(), isAnyManager.globalOnly());
            return;
        }
        isAnyManager = AnnotationUtils.findAnnotation(method.getDeclaringClass(), IsAnyManager.class);
        if (isAnyManager != null) {
            checkAnyManager(isAnyManager.value(), isAnyManager.globalOnly());
            return;
        }
    }

    private void checkAnyManager(ManagerLevelEnum[] managerLevelEnums, boolean globalOnly) {
        if (managerLevelEnums != null) {
            for (ManagerLevelEnum managerLevelEnum : managerLevelEnums) {
                if (managerLevelEnum.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())
                    && (Y9LoginUserHolder.getUserInfo().isGlobalManager()
                        || Y9LoginUserHolder.getUserInfo().isGlobalManager() == globalOnly)) {
                    return;
                }
            }
            // 所有都不满足才抛出异常
            String managerNames =
                Arrays.stream(managerLevelEnums).map(ManagerLevelEnum::getName).collect(Collectors.joining(","));
            throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.NOT_MANAGER, managerNames);
        }
    }

}
