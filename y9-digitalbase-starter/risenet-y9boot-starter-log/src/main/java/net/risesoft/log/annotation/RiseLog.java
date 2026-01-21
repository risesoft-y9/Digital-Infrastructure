package net.risesoft.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;

/**
 * 日志注解
 *
 * @author shidaobang
 * @date 2022/09/22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RiseLog {

    /**
     * 是否记录日志
     * 
     * @return boolean 是否记录日志
     */
    boolean enable() default true;

    /**
     * 日志级别 {@link LogLevelEnum}
     * 
     * @return {@code LogLevelEnum} 操作类型 日志级别
     */
    LogLevelEnum logLevel() default LogLevelEnum.RSLOG;

    /**
     * 模块名称
     * 
     * @return String 模块名称
     */
    String moduleName() default "";

    /**
     * 操作名称
     * 
     * @return String 操作名称
     */
    String operationName() default "";

    /**
     * 操作类型
     * 
     * @return {@code OperationTypeEnum} 操作类型
     */
    OperationTypeEnum operationType() default OperationTypeEnum.BROWSE;

    /**
     * 是否保存请求参数
     *
     * @return boolean
     */
    boolean saveParams() default false;
}
