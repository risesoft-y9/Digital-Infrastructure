package net.risesoft.config;

import net.risesoft.y9.Y9LoginUserHolder;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class CheckUserLoginFilterSkipSSO implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    @Override
    public void destroy() {}

}
