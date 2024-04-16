package net.risesoft.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.OncePerRequestFilter;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;
import net.risesoft.y9.configuration.feature.security.cors.Y9CorsProperties;

/**
 * cors过滤器
 *
 * @author shidaobang
 * @date 2024/04/12
 * @see org.springframework.web.filter.CorsFilter
 */
public class CorsFilter extends OncePerRequestFilter {

    private final CorsProcessor processor = new DefaultCorsProcessor();
    private final CorsConfiguration corsConfiguration = new CorsConfiguration();

    private Y9CorsProperties y9CorsProperties = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        Y9CorsProperties corsProperties = Y9Context.getBean(Y9SecurityProperties.class).getCors();
        if (y9CorsProperties != corsProperties) {
            y9CorsProperties = corsProperties;
            applyLatestCorsProperties(corsConfiguration, corsProperties);
        }
        
        boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
        if (!isValid || CorsUtils.isPreFlightRequest(request)) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void applyLatestCorsProperties(CorsConfiguration corsConfiguration, Y9CorsProperties corsProperties) {
        corsConfiguration.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
        corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethods());
        corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        corsConfiguration.setAllowCredentials(corsProperties.isAllowCredentials());
        corsConfiguration.setMaxAge(corsProperties.getMaxAge());
    }

}
