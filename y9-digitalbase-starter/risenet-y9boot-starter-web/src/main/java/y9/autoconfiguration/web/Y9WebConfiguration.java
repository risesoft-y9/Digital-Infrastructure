package y9.autoconfiguration.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.web.Y9LaxStringToEnumConverterFactory;
import net.risesoft.web.handler.Y9ControllerAdvice;

/**
 * web 配置
 *
 * @author shidaobang
 * @date 2022/09/27
 */
@Configuration
public class Y9WebConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public Y9ControllerAdvice y9ControllerAdvice() {
        return new Y9ControllerAdvice();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new Y9LaxStringToEnumConverterFactory());
        registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    }
}
