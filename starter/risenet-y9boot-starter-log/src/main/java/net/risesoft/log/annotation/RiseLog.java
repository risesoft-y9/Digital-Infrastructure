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
     * @return
     */
    boolean enable() default true;

    /**
     * 日志级别
     * {@link LogLevelEnum}
     * 
     * @return
     */
    LogLevelEnum logLevel() default LogLevelEnum.MANAGERLOG;

    /**
     * 模块名称
     * 
     * @return
     */
    String moduleName() default "";

    /**
     * 操作名称
     * 
     * @return
     */
    String operationName() default "";

    /**
     * 操作类型
     * 
     * @return
     */
    OperationTypeEnum operationType() default OperationTypeEnum.BROWSE;
    
}
