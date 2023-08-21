package net.risesoft.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

public class CSRFFilter implements Filter {

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
        Y9Properties properties = Y9Context.getBean(Y9Properties.class);
        List<String> acceptedRefererList = properties.getFeature().getSecurity().getCsrf().getAcceptedReferer();
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
