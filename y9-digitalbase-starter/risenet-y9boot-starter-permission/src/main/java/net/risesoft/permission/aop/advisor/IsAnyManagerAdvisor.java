package net.risesoft.permission.aop.advisor;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.permission.aop.PermissionAnnotationResolver;

/**
 *
 * @author shidaobang
 * @date 2023/12/22
 */
@Slf4j
public class IsAnyManagerAdvisor extends StaticMethodMatcherPointcutAdvisor {

    private static final long serialVersionUID = 1790605582584464487L;

    public IsAnyManagerAdvisor() {
        LOGGER.debug("IsManagerAdvisor init............");
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (PermissionAnnotationResolver.findAnnotation(method, targetClass, IsAnyManager.class) != null) {
            LOGGER.trace("findAnnotation IsAnyManager: {}", method.getName());
            return true;
        }

        return false;
    }

    @Override
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }

}
