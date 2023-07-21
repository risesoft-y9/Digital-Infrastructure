package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否为系统管理员
 * 只有当前登录用户为系统管理员，方法才能继续调用
 *
 * @author shidaobang
 * @date 2022/11/10
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsSystemManager {

    /**
     * 部门级三员中的系统管理员
     */
    boolean departmental() default false;
    
}
