package net.risesoft.id;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 获取WebApplicationContext的一条途径
 */
public class Y9SpringContext implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     *
     * @param name the name of the bean to query
     * @return boolean whether a bean with the given name is present
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
     * @param name the bean name to check for aliases
     * @return the aliases, or an empty array if none
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException – if there is no bean with the given name
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }

    /**
     * 获取类型为requiredType的对象
     * 
     * @param clz ype the bean must match; can be an interface or superclass
     * @return an instance of the single bean matching the required type
     * @param <T> an interface or superclass
     * @throws org.springframework.beans.BeansException -if the bean could not be created
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = applicationContext.getBean(clz);
        return result;
    }

    /**
     * 获取对象
     * 
     * @param name the name of the bean to retrieve
     * @return Object 一个以所给名字注册的bean的实例
     * @param <T> an interface or superclass
     * @throws org.springframework.beans.BeansException -if the bean could not be created
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T)applicationContext.getBean(name);
    }

    public static Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Y9SpringContext.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     * Determine the type of the bean with the given name. More specifically, determine the type of object that getBean
     * would return for the given name.
     * 
     * @param name he name of the bean to query
     * @return Class 注册对象的类型
     *
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException – if there is no bean with the given name
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     *
     * @param name – the name of the bean to query
     * @return boolean whether this bean corresponds to a singleton instance
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException – if there is no bean with the given name
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    public static void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Y9SpringContext.applicationContext = applicationContext;
    }

}