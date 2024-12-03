package y9.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.FilterOrderConsts;
import net.risesoft.filters.ApiBlackListFilter;
import net.risesoft.filters.ApiSignFilter;
import net.risesoft.filters.ApiTokenFilter;
import net.risesoft.filters.ApiWhiteListFilter;
import net.risesoft.y9.configuration.feature.apiacl.Y9ApiAccessControlProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.Y9ApiAccessControlService;

/**
 * api访问控制配置类
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(Y9ApiAccessControlProperties.class)
public class Y9ApiAccessControlConfiguration {

    @Bean
    @ConditionalOnProperty(name = "y9.feature.api-access-control.white-list.enabled", havingValue = "true")
    public FilterRegistrationBean<ApiWhiteListFilter> apiWhiteListFilter(
        Y9ApiAccessControlService y9ApiAccessControlService,
        Y9ApiAccessControlProperties y9ApiAccessControlProperties) {
        LOGGER.info("ApiWhiteListFilter init.");

        FilterRegistrationBean<ApiWhiteListFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiWhiteListFilter(y9ApiAccessControlService));
        filterBean.setOrder(FilterOrderConsts.API_WHITE_LIST_ORDER);
        filterBean.setUrlPatterns(y9ApiAccessControlProperties.getUrlPatterns());
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.api-access-control.black-list.enabled", havingValue = "true")
    public FilterRegistrationBean<ApiBlackListFilter> apiBlackListFilter(
        Y9ApiAccessControlService y9ApiAccessControlService,
        Y9ApiAccessControlProperties y9ApiAccessControlProperties) {
        LOGGER.info("ApiBlackListFilter init.");

        FilterRegistrationBean<ApiBlackListFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiBlackListFilter(y9ApiAccessControlService));
        filterBean.setOrder(FilterOrderConsts.API_BLACK_LIST_ORDER);
        filterBean.setUrlPatterns(y9ApiAccessControlProperties.getUrlPatterns());
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.api-access-control.token.enabled", havingValue = "true")
    public FilterRegistrationBean<ApiTokenFilter>
        apiTokenFilter(Y9ApiAccessControlProperties y9ApiAccessControlProperties) {
        LOGGER.info("ApiTokenFilter init. Configuration:{}",
            Y9JsonUtil.writeValueAsString(y9ApiAccessControlProperties.getToken()));

        FilterRegistrationBean<ApiTokenFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiTokenFilter(y9ApiAccessControlProperties.getToken()));
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(FilterOrderConsts.API_TOKEN_ORDER);
        filterBean.setUrlPatterns(y9ApiAccessControlProperties.getUrlPatterns());
        return filterBean;
    }

    @Bean
    @ConditionalOnProperty(name = "y9.feature.api-access-control.sign.enabled", havingValue = "true")
    public FilterRegistrationBean<ApiSignFilter> apiSignFilter(Y9ApiAccessControlService y9ApiAccessControlService,
        Y9ApiAccessControlProperties y9ApiAccessControlProperties) {
        LOGGER.info("ApiSignFilter init:{}", y9ApiAccessControlProperties);

        FilterRegistrationBean<ApiSignFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ApiSignFilter(y9ApiAccessControlService));
        filterBean.setOrder(FilterOrderConsts.API_SIGN_ORDER);
        filterBean.setUrlPatterns(y9ApiAccessControlProperties.getUrlPatterns());
        return filterBean;
    }
}
