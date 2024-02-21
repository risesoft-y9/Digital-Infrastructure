package net.risesoft.log.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.annotation.RiseLog;

@Slf4j
public class RiseLogAdvisor extends StaticMethodMatcherPointcutAdvisor {
    private static final long serialVersionUID = -3373169665682250208L;

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] ANNOTATION_CLASSES =
        new Class[] {RiseLog.class, RequestMapping.class};

    public RiseLogAdvisor() {
        LOGGER.debug("RiseLogAdvisor init...............");
    }

    private boolean isAnnotationPresent(Method method) {
        for (Class<? extends Annotation> annClass : ANNOTATION_CLASSES) {
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
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
