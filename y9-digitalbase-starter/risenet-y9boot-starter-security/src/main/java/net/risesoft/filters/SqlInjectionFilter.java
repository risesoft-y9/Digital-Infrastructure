package net.risesoft.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * SQL注入拦截器
 *
 * @author mengjuhua
 *
 */
@Slf4j
public class SqlInjectionFilter implements Filter {
    private static final String SQL_REGX =
        ".*(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|drop|execute)\\b).*";
    private String[] skip;

    // 获取request请求body中参数
    public static String getBodyString(BufferedReader br) {
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return str;
    }

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

        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        SqlInjectionRequestWrapper requestWrapper = new SqlInjectionRequestWrapper(httpRequest);

        // 获取请求参数
        Map<String, Object> paramsMaps = new HashMap<>();
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String body = requestWrapper.getBody();
            paramsMaps = Y9JsonUtil.readValue(body, HashMap.class);
        } else {
            Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
            Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
            for (Map.Entry<String, String[]> next : entries) {
                paramsMaps.put(next.getKey(), next.getValue()[0]);
            }
        }

        // 校验SQL注入
        for (Object o : paramsMaps.entrySet()) {
            Map.Entry entry = (Map.Entry)o;
            Object paramName = entry.getKey();
            if (isParamIgnorable(paramName.toString())) {
                continue;
            }
            Object value = entry.getValue();
            if (value != null) {
                boolean isValid = checkSqlInject(value.toString(), response);
                if (!isValid) {
                    return;
                }
            }
        }

        try {
            chain.doFilter(requestWrapper, response);
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
                Collections.addAll(list, values);
            } else {
                list.add(value);
            }
        }
        return list.toArray();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        skip = filterConfig.getInitParameter("skip").split(",");
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

    /**
     * 检查SQL注入
     *
     * @param value 参数值
     * @param servletResponse 相应实例
     * @throws IOException IO异常
     */
    private boolean checkSqlInject(String value, ServletResponse servletResponse) throws IOException {

        if (null != value && value.matches(SQL_REGX)) {
            LOGGER.error("您输入的参数有非法字符，请输入正确的参数");
            HttpServletResponse response = (HttpServletResponse)servletResponse;

            Map<String, String> rsp = new HashMap<>();
            rsp.put("code", HttpStatus.BAD_REQUEST.value() + "");
            rsp.put("message", "您输入的参数有非法字符，请输入正确的参数！");

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Y9JsonUtil.writeValueAsString(rsp));
            response.getWriter().flush();
            response.getWriter().close();
            return false;
        }
        return true;
    }

    /**
     * 参数名是否在白名单内 在白名单内的参数则不进行处理
     *
     * @param paramName 参数名
     * @return
     */
    private boolean isParamIgnorable(String paramName) {
        List<String> ignoreParamList = Y9Context.getBean(Y9SecurityProperties.class).getSqlIn().getIgnoreParam();
        return ignoreParamList.stream().anyMatch(paramName::equals);
    }

}
