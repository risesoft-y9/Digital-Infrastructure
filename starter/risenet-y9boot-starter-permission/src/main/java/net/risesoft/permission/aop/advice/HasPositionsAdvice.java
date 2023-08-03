package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.permission.annotation.HasPositions;

/**
 * 是否拥有岗位的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class HasPositionsAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        HasPositions hasPositions = AnnotationUtils.findAnnotation(method, HasPositions.class);
        if (hasPositions != null) {

        }
    }

}
