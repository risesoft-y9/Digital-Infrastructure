package y9.autoconfiguration.id.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author DZJ
 * @date 2022/03/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(IdGeneratorConfiguration.class)
@Inherited
public @interface EnableIdGenerator {}
