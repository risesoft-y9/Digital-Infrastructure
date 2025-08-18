package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.enums.LogicalEnum;
import net.risesoft.enums.platform.org.IdentityTypeEnum;

/**
 * 是否拥有角色 只有拥有相应角色，方法才能继续调用
 *
 * @author shidaobang
 * @date 2022/11/10
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasRoles {

    /**
     * 角色自定义标识 customId 数组
     * 
     * @return {@code String[] } customId数组
     */
    String[] value();

    /**
     * 身份类型
     * 
     * @return {@code IdentityEnum } 身份类型
     */
    IdentityTypeEnum identity() default IdentityTypeEnum.PERSON;

    /**
     * 检查角色的逻辑操作 与 和 或，默认是 与
     * 
     * @return {@code LogicalEnum } 角色的逻辑操作
     */
    LogicalEnum logical() default LogicalEnum.AND;
}
