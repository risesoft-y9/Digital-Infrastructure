package net.risesoft.permission.aop.advisor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.permission.annotation.IsManager;

/**
 *
 * @author shidaobang
 * @date 2023/12/22
 */
@Slf4j
public class IsManagerAdvisor extends StaticMethodMatcherPointcutAdvisor {
    
    private static final long serialVersionUID = 1790605582584464487L;

    public IsManagerAdvisor() {
        LOGGER.debug("IsManagerAdvisor init............");
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        Annotation annotation = AnnotationUtils.findAnnotation(method, IsManager.class);
        if (annotation != null) {
            LOGGER.info("findAnnotation IsManager: " + method.getName());
            return true;
        }
        annotation = AnnotationUtils.findAnnotation(targetClass, IsManager.class);
        if (annotation != null) {
            LOGGER.info("findAnnotation IsManager: " + targetClass.getName());
            return true;
        }

        return false;
    }

    @Override
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }

}
