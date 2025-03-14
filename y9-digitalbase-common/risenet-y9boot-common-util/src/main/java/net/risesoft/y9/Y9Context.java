package net.risesoft.y9;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.util.InetAddressUtil;

/**
 * 获取WebApplicationContext的一条途径
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9Context implements ApplicationContextAware, EnvironmentAware, ServletContextAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;
    private static ServletContext servletContext;

    private static String systemName;
    private static String hostName;
    private static String hostIp;

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     *
     * @param name bean名字
     * @return boolean 是否匹配
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static ApplicationContext getAc() {
        return applicationContext;
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     *
     * @param name bean名字
     * @return String[] bean定义中别名
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean with the specified
     *             name
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     *
     * @param clz 类型
     * @return T an instance of the single bean matching the required type
     * @param <T> type the bean must match; can be an interface or superclass
     * @throws org.springframework.beans.BeansException if the bean could not be obtained
     * @see BeansException
     */

    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = applicationContext.getBean(clz);
        return result;
    }

    /**
     * 获取对象
     * 
     * @param name bean名字
     * @return T 一个以所给名字注册的bean的实例
     * @param <T> 返回类型
     * @throws org.springframework.beans.BeansException if the bean could not be obtained
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T)applicationContext.getBean(name);
    }

    /**
     * 获取对应类型的bean
     * 
     * @param clz the class or interface to match, or null for all concrete beans
     * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as
     *         values
     * @param <T> 返回类型
     */
    public static <T> Map<String, T> getBeans(Class<T> clz) {
        return applicationContext.getBeansOfType(clz);
    }

    public static String getContextPath() {
        return servletContext.getContextPath();
    }

    public static Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Y9Context.environment = environment;
    }

    public static String getHostIp() {
        if (Y9Context.hostIp == null) {
            Y9Context.hostIp = InetAddressUtil.getLocalAddress().getHostAddress();
        }
        return Y9Context.hostIp;
    }

    public static String getHostName() {
        if (Y9Context.hostName == null) {
            try {
                Y9Context.hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return Y9Context.hostName;
    }

    /**
     * 获取访问者IP
     *
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     *
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)， 如果还不存在则调用Request.getRemoteAddr()。
     *
     * @param request 请求信息
     * @return String 访问者IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String addr = null;

        String[] addrHeader = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : addrHeader) {
            if ((!StringUtils.hasText(addr)) || "unknown".equalsIgnoreCase(addr)) {
                addr = request.getHeader(header);
            } else {
                break;
            }
        }

        if ((!StringUtils.hasText(addr)) || "unknown".equalsIgnoreCase(addr)) {
            addr = request.getRemoteAddr();
        } else {
            int i = addr.indexOf(",");
            if (i > 0) {
                addr = addr.substring(0, i);
            }
        }
        return addr;
    }

    public static String getLogoutUrl() {
        String path = environment.getProperty("y9.feature.sso.logoutUrl");
        String logoutUrl = path + servletContext.getContextPath();
        return logoutUrl;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public static String getRealPath(String path) {
        return servletContext.getRealPath(path);
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        Y9Context.servletContext = servletContext;
    }

    public static ServletContext getServletContext(String uripath) {
        return servletContext.getContext(uripath);
    }

    public static String getSystemName() {
        if (Y9Context.systemName == null) {
            Y9Context.systemName = Y9Context.environment.getProperty("y9.systemName");
        }

        return Y9Context.systemName;
    }

    public static void setSystemName(String systemName) {
        Y9Context.systemName = systemName;
    }

    /**
     * @param name bean名字
     * @return Class the name of the bean to query
     *
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean with the specified
     *             name
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    public static String getWebRootRealPath() {
        return servletContext.getRealPath("/");
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     *
     * @param name the name of the bean to query
     * @return boolean whether this bean corresponds to a singleton instance
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean with the specified
     *             name
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    public static void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Y9Context.applicationContext = applicationContext;
    }

}