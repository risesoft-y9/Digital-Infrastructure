package y9.autoconfiguration.web;

import java.util.Date;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.web.Y9LaxStringToEnumConverterFactory;
import net.risesoft.web.handler.Y9ControllerAdvice;

import cn.hutool.core.date.DateUtil;

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
        registry.addConverter(new MultiFormatDateConverter());
        registry.addConverterFactory(new Y9LaxStringToEnumConverterFactory());
    }

    /**
     * 支持多种格式的日期转换器
     */
    static class MultiFormatDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            if (StringUtils.hasText(source)) {
                return DateUtil.parse(source.trim());
            }
            return null;
        }
    }
}
