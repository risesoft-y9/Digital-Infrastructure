package net.risesoft.example.config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class CheckLoginFilter implements Filter {

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;


        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

}
