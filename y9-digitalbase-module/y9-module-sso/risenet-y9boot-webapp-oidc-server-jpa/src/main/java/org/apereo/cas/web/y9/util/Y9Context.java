package org.apereo.cas.web.y9.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ServletContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取WebApplicationContext的一条途径
 */
@Slf4j
public class Y9Context implements ApplicationContextAware, EnvironmentAware, ServletContextAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;
    private static ServletContext servletContext;

    private static String hostName;
    private static String hostIp;

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     * 
     * 
     * @param name
     * @return boolean
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
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }

    /**
     * 获取类型为requiredType的对象
     * 
     * 
     * @param clz
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }

    /**
     * 获取对象
     * 
     * 
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * 
     * @throws BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T)applicationContext.getBean(name);
    }

    public static String getContextPath() {
        return servletContext.getContextPath();
    }

    public static Environment getEnvironment() {
        return environment;
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
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)， 如果还不存在则调用Request .getRemoteAddr()。
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String addr = null;

        String[] ADDR_HEADER = {"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String header : ADDR_HEADER) {
            if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
                addr = request.getHeader(header);
            } else {
                break;
            }
        }

        if (StringUtils.isEmpty(addr) || "unknown".equalsIgnoreCase(addr)) {
            addr = request.getRemoteAddr();
        } else {
            int i = addr.indexOf(",");
            if (i > 0) {
                addr = addr.substring(0, i);
            }
        }
        return addr;
    }

    public static String getLogoutUrl(String path) {
        String logoutUrl = path + servletContext.getContextPath();
        return logoutUrl;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getRealPath(String path) {
        return servletContext.getRealPath(path);
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static ServletContext getServletContext(String uripath) {
        return servletContext.getContext(uripath);
    }

    public static String getSystemName() {
        return getProperty("systemName");
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * 
     * @throws NoSuchBeanDefinitionException
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
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
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

    @Override
    public void setEnvironment(Environment environment) {
        Y9Context.environment = environment;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        Y9Context.servletContext = servletContext;
    }

}