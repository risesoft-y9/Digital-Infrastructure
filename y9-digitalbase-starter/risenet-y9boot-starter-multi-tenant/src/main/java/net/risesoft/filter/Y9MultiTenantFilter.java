package net.risesoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.y9.Y9TenantHolder;

/**
 * 多租户过滤器
 *
 * @author shidaobang
 * @date 2026/07/17
 */
public class Y9MultiTenantFilter implements Filter {

    private static final String TENANT_ID_PARAMETER = "tenantId";

    private static final String AUTH_TENANT_ID_HEADER = "auth-tenantId";

    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            String tenantId = resolveTenantId(httpRequest);

            if (StringUtils.isNotBlank(tenantId)) {
                Y9TenantHolder.setCurrentTenantId(tenantId);
            }

            chain.doFilter(request, response);
        } finally {
            Y9TenantHolder.clearCurrentTenantId();
        }
    }

    private String resolveTenantId(HttpServletRequest request) {
        String tenantId = request.getParameter(TENANT_ID_PARAMETER);
        if (StringUtils.isBlank(tenantId)) {
            tenantId = request.getHeader(AUTH_TENANT_ID_HEADER);
        }
        if (StringUtils.isBlank(tenantId)) {
            tenantId = request.getHeader(TENANT_ID_HEADER);
        }
        return tenantId;
    }
}
