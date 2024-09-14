package net.risesoft.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 校验手机号是否合法
 *
 * @author shidaobang
 * @date 2022/08/04
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = MobileValidator.class)
public @interface Mobile {
    // 分组
    Class<?>[] groups() default {};

    // 默认校验错误提示
    String message() default "手机号不合法";

    // 负载
    Class<? extends Payload>[] payload() default {};
}
