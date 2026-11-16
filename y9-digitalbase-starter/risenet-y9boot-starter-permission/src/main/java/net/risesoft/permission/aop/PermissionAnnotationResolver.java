package net.risesoft.permission.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 查找权限注解，方法上的注解优先于目标类上的注解。
 */
public final class PermissionAnnotationResolver {

    private PermissionAnnotationResolver() {}

    public static <A extends Annotation> A findAnnotationForInvocation(Method method, Object target,
        Class<A> annotationType) {
        Class<?> targetClass = target != null ? AopUtils.getTargetClass(target) : null;
        return findAnnotation(method, targetClass, annotationType);
    }

    public static <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationType) {
        A annotation = AnnotationUtils.findAnnotation(method, annotationType);
        if (annotation != null) {
            return annotation;
        }

        Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        if (!targetMethod.equals(method)) {
            annotation = AnnotationUtils.findAnnotation(targetMethod, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        Class<?> annotationClass = targetClass != null ? targetClass : method.getDeclaringClass();
        return AnnotationUtils.findAnnotation(annotationClass, annotationType);
    }
}
