package net.risesoft.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SQL注入拦截器
 *
 * @author mengjuhua
 *
 */
public class SQLInFilter implements Filter {
    private static final String ERROR_PAGE = "ParameterError.jsp";
    private String[] illegal;
    private String[] skip;

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        String url = httpRequest.getRequestURI();

        if (this.isSkip(url)) {
            // System.out.println("----------skipURI:"+URI);
            chain.doFilter(request, response);
            return;
        }

        @SuppressWarnings("rawtypes")
        Map map = httpRequest.getParameterMap();
        Object[] values = this.getParameterValues(map);
        // System.out.println("----------checkURI:"+URI);
        for (int i = 0; i < values.length; i++) {
            if (!this.isLegal(values[i].toString())) {
                HttpServletResponse httpResponse = (HttpServletResponse)response;
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + ERROR_PAGE);
                return;
            }
        }
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到请求的所有参数
     *
     * @param map
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object[] getParameterValues(Map map) {
        List list = new ArrayList();
        for (Iterator iter = map.values().iterator(); iter.hasNext();) {
            Object value = iter.next();
            if (value instanceof String[]) {
                String[] values = (String[])value;
                for (int i = 0; i < values.length; i++) {
                    list.add(values[i]);
                }
            } else {
                list.add(value);
            }
        }
        return list.toArray();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        illegal = filterConfig.getInitParameter("illegal").split(";");
        skip = filterConfig.getInitParameter("skip").split(";");
    }

    /**
     * 参数是否合法
     *
     * @param value
     * @return
     */
    private boolean isLegal(String value) {
        for (int i = 0; i < this.illegal.length; i++) {
            if (value.indexOf(this.illegal[i]) != -1) {
                // System.out.println("----------URLP:ILLEGAL:" + illegal[i]);
                // System.out.println("----------URLP:ILLvalue:" + value);
                return false;
            }
        }
        return true;
    }

    /**
     * 请求是否跳过参数检查
     *
     * @param value
     * @return
     */
    private boolean isSkip(String value) {
        for (int i = 0; i < this.skip.length; i++) {
            if (value.endsWith(this.skip[i])) {
                return true;
            }
        }
        return false;
    }
}
