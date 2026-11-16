package net.risesoft.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.enums.LogicalEnum;

/**
 * 检查当前用户是否拥有指定岗位 <br>
 * 可用在方法及类上，方法上的注解优先于类上的
 * 
 * @author shidaobang
 * @date 2022/11/10
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPositions {

    /**
     * 岗位 id 数组
     * 
     * @return {@code String[] } 岗位Id数组
     */
    String[] value();

    /**
     * 检查岗位的逻辑操作 与 和 或，默认是 与
     * 
     * @return {@code LogicalEnum } 岗位的逻辑操作
     */
    LogicalEnum logical() default LogicalEnum.AND;
}
