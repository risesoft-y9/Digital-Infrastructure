package net.risesoft.permission.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.permission.annotation.RepeatSubmit;

/**
 * 防重复提交 Advisor
 *
 * @author shidaobang
 * @date 2025/07/21
 */
@Slf4j
public class RepeatSubmitAdvisor extends StaticMethodMatcherPointcutAdvisor {
    private static final long serialVersionUID = 1790605582584464487L;

    public RepeatSubmitAdvisor() {
        LOGGER.debug("RepeatSubmitAdvisor creating............");
    }

    private boolean isAnnotationPresent(Method method) {
        Annotation a = AnnotationUtils.findAnnotation(method, RepeatSubmit.class);
        if (a != null) {
            LOGGER.trace("findAnnotation RepeatSubmit: " + method.getName());
            return true;
        }
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Method m = method;

        if (isAnnotationPresent(m)) {
            return true;
        }

        // The 'method' parameter could be from an interface that doesn't have the annotation.
        // Check to see if the implementation has it.
        if (targetClass != null) {
            try {
                m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                if (isAnnotationPresent(m)) {
                    return true;
                }
            } catch (NoSuchMethodException ignored) {
                // default return value is false. If we can't find the method, then obviously
                // there is no annotation, so just use the default return value.
            }
        }

        return false;
    }

    @Override
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }

}
