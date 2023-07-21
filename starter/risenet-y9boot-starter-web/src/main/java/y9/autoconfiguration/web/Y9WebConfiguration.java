package y9.autoconfiguration.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.web.handler.Y9ControllerAdvice;

/**
 * web 配置
 *
 * @author shidaobang
 * @date 2022/09/27
 */
@Configuration
public class Y9WebConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Y9ControllerAdvice y9ControllerAdvice() {
        return new Y9ControllerAdvice();
    }

}
