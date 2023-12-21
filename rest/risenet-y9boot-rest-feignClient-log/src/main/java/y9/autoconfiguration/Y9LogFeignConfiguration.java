package y9.autoconfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import y9.support.Y9ErrorDecoder;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;

/**
 * open feign 自动配置类
 *
 * @author dingzhaojun
 * @date 2022/02/15
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients("y9.client.log")
public class Y9LogFeignConfiguration implements RequestInterceptor {

    @Bean
    @ConditionalOnMissingBean(name = "errorDecoder")
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new Y9ErrorDecoder(objectMapper);
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            HttpSession session = request.getSession(false);
            // 取出 Y9Oauth2ResourceFilter 往 HttpSession 里设置的 access_token
            String accessTokenInSession = session != null ? (String)session.getAttribute("access_token") : null;

            if (accessTokenInSession != null) {
                // 将 access_token 往下游服务传
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenInSession);
            }
        }
    }
}
