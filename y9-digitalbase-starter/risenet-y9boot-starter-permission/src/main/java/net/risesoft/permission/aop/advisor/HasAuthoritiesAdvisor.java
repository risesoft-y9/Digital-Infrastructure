package net.risesoft.permission.aop.advisor;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.permission.annotation.HasAuthorities;
import net.risesoft.permission.aop.PermissionAnnotationResolver;

/**
 *
 * @author shidaobang
 * @date 2022/11/11
 */
@Slf4j
public class HasAuthoritiesAdvisor extends StaticMethodMatcherPointcutAdvisor {
    private static final long serialVersionUID = 1790605582584464487L;

    public HasAuthoritiesAdvisor() {
        LOGGER.debug("HasAuthoritiesAdvisor init............");
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (PermissionAnnotationResolver.findAnnotation(method, targetClass, HasAuthorities.class) != null) {
            LOGGER.trace("findAnnotation HasAuthorities: {}", method.getName());
            return true;
        }
        return false;
    }

    @Override
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }

}
