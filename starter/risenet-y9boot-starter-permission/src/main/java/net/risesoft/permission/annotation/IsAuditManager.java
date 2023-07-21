package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否为审计管理员
 * 只有当前登录用户为审计管理员，方法才能继续调用
 * 
 * @author shidaobang
 * @date 2022/11/11
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAuditManager {

    /**
     * 部门级三员中的审计管理员
     */
    boolean departmental() default false;
    
}
