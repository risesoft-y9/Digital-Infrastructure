package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.enums.platform.ManagerLevelEnum;

/**
 * 是否为管理员 <br/>
 * 可用在方法及类上，方法上的注解优先于类上的
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsManager {

    /**
     * 管理员类型
     */
    ManagerLevelEnum[] value() default {};

    /**
     * 必须为全局的管理员
     */
    boolean globalOnly() default false;

}
