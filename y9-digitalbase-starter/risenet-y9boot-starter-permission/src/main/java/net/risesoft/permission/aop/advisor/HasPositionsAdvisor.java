package net.risesoft.permission.aop.advisor;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.permission.annotation.HasPositions;
import net.risesoft.permission.aop.PermissionAnnotationResolver;

/**
 *
 * @author shidaobang
 * @date 2022/11/11
 */
@Slf4j
public class HasPositionsAdvisor extends StaticMethodMatcherPointcutAdvisor {
    private static final long serialVersionUID = 1790605582584464487L;

    public HasPositionsAdvisor() {
        LOGGER.debug("HasPositionsAdvisor init............");
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (PermissionAnnotationResolver.findAnnotation(method, targetClass, HasPositions.class) != null) {
            LOGGER.trace("findAnnotation HasPositions: {}", method.getName());
            return true;
        }
        return false;
    }

    @Override
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }

}
