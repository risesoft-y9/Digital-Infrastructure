package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.IdentityEnum;
import net.risesoft.enums.LogicalEnum;

/**
 * 是否拥有相应的资源权限
 * 只有拥有相应权限，方法才能继续调用
 *
 * @author shidaobang
 * @date 2022/11/10
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasAuthorities {
    
    /**
     * 资源自定义标识 customId 数组
     */
    String[] value();

    /**
     * 权限类型
     */
    AuthorityEnum authority() default AuthorityEnum.BROWSE;

    /**
     * 身份类型
     */
    IdentityEnum identity() default IdentityEnum.PERSON;
    
    /**
     * 检查资源权限的逻辑操作 与 和 或，默认是 与
     */
    LogicalEnum logical() default LogicalEnum.AND;
}
