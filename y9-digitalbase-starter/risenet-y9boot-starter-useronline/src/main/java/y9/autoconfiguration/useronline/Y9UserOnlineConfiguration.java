package y9.autoconfiguration.useronline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import net.risesoft.consts.FilterOrderConsts;
import net.risesoft.filters.Y9UserOnlineFilter;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.useronline.Y9UserOnlineProperties;

/**
 * 用户在线配置类
 *
 * @author shidaobang
 * @date 2025/03/06
 */
@Configuration
@EnableConfigurationProperties({Y9UserOnlineProperties.class, Y9Properties.class})
@ConditionalOnProperty(name = "y9.feature.useronline.enabled", havingValue = "true", matchIfMissing = true)
public class Y9UserOnlineConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "y9UserOnlineFilter")
    public FilterRegistrationBean<Y9UserOnlineFilter> y9UserOnlineFilter(Y9Properties y9Properties,
        Y9UserOnlineProperties y9UserOnlineProperties,
        @Autowired(required = false) KafkaTemplate<String, Object> y9KafkaTemplate) {
        final FilterRegistrationBean<Y9UserOnlineFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9UserOnlineFilter(y9Properties, y9UserOnlineProperties, y9KafkaTemplate));
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/*");
        filterBean.setOrder(FilterOrderConsts.USER_ONLINE_ORDER);

        return filterBean;
    }

}
