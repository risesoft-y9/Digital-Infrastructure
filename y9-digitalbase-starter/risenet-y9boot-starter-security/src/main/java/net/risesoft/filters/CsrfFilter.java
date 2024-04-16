package net.risesoft.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;

public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;

        if (!isRefererValid(httpRequest)) {
            httpResponse.setStatus(403);
            httpResponse.getOutputStream().print("For security reasons, your request could not be processed");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isAcceptedReferer(String referer) {
        Y9SecurityProperties y9SecurityProperties = Y9Context.getBean(Y9SecurityProperties.class);
        List<String> acceptedRefererList = y9SecurityProperties.getCsrf().getAcceptedReferer();
        return acceptedRefererList.stream().anyMatch(referer::contains);
    }

    /**
     * referer是否有效 避免crsf远程站点攻击
     */
    public boolean isRefererValid(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referer) && !referer.contains(request.getContextPath())
            && !isAcceptedReferer(referer)) {
            return false;
        }
        return true;
    }
}
