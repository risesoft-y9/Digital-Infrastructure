package net.risesoft.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.HtmlUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

@Slf4j
public class XSSHttpRequestWrapper extends HttpServletRequestWrapper {

    public static void main(String[] args) {
        try {
            String str = "<script>alert(\"xss\");</script>HELLO WORD！";
            // String str = "<script>alert(\"xss\");</script>HELLO WORD！";
            Policy policy = Policy.getInstance(new ClassPathResource("antisamy-slashdot.xml").getInputStream());
            AntiSamy as = new AntiSamy();
            CleanResults cr = as.scan(str, policy);
            System.out.println(cr.getCleanHTML());
        } catch (PolicyException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (ScanException e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    private static Policy policy;
    
    static {
        try {
            policy = Policy.getInstance(new ClassPathResource("antisamy-y9.xml").getInputStream());
        } catch (PolicyException | IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public XSSHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 清除 xss
     *
     * @param name 参数名
     * @param parameterValue 参数值
     * @return
     */
    private String cleanXss(String name, String parameterValue) {
        if (isParamIgnorable(name) || StringUtils.isBlank(parameterValue)) {
            return parameterValue;
        }

        AntiSamy antiSamy = new AntiSamy();
        CleanResults cleanResults = null;
        try {
            cleanResults = antiSamy.scan(parameterValue, policy);
        } catch (ScanException | PolicyException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        // 先尝试用 antisamy 清除 xss
        String cleanHtml = cleanResults.getCleanHTML();
        // html 转义
        return HtmlUtils.htmlEscape(cleanHtml);
    }

    /**
     * 转义
     *
     * @param name 参数名
     * @param parameterValues 参数值
     * @return
     */
    private String[] cleanXss(String name, String[] parameterValues) {
        if (isParamIgnorable(name) || parameterValues == null) {
            return parameterValues;
        }
        String[] newValues = new String[parameterValues.length];
        for (int i = 0; i < parameterValues.length; i++) {
            newValues[i] = cleanXss(name, parameterValues[i]);
        }
        return newValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXss(name, value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headerValues = super.getHeaders(name);
        return headerValues;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanXss(name, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new HashMap(super.getParameterMap());
        for (String key : parameterMap.keySet()) {
            String[] parameterValues = parameterMap.get(key);
            String[] newValues = cleanXss(key, parameterValues);
            parameterMap.put(key, newValues);
        }
        return parameterMap;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        return cleanXss(name, parameterValues);
    }

    /**
     * 参数名是否在白名单内 在白名单内的参数则不进行处理
     *
     * @param paramName 参数名
     * @return
     */
    private boolean isParamIgnorable(String paramName) {
        List<String> ignoreParamList = Y9Context.getBean(Y9Properties.class).getFeature().getSecurity().getXss().getIgnoreParam();
        return ignoreParamList.stream().anyMatch(paramName::equals);
    }
}
