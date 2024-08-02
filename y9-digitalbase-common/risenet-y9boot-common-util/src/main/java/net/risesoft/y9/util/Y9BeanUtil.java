package net.risesoft.y9.util;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

/**
 * 在更新实体类的时候用来复制实体属性到之前的实体类中
 * 
 * @see org.springframework.beans.BeanUtils
 * 
 * @author 安一伟
 * @date 2022/2/10
 */
public class Y9BeanUtil {
    /**
     * 将更新过字段的实体类拷贝到老的数据库查出的实体类中： <br>
     * <b>for example:</b>
     * <p>
     * If we want to change moblie values for Tom: <br>
     * source:{id:123,name:Tom,moblie:120,sex:null,address:null}<br>
     * target:{id:123,name:Tom,moblie:110,sex:male,address:LuoHu} <br>
     * <br>
     * <b>after method run:</b> <br>
     * target:{id:123,name:Tom,moblie:120,sex:male,address:LuoHu}
     * </p>
     * 
     * @param source 更新后的实体类
     * @param target 以前的老实体类
     * @throws BeansException Beans异常
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, null, (String[])null);
    }

    /**
     * 将更新过字段的实体类拷贝到老的数据库查出的实体类中： <br>
     * 查看 {@link #copyProperties(Object, Object)}
     * 
     * @param source 更新后的实体类
     * @param target 以前的老实体类
     * @param editable 限制属性设置为的类（或接口）
     * @throws BeansException Beans异常
     */
    public static void copyProperties(Object source, Object target, Class<?> editable) {
        copyProperties(source, target, editable, (String[])null);
    }

    /**
     * 将更新过字段的实体类拷贝到老的数据库查出的实体类中： <br>
     * 查看 {@link #copyProperties(Object, Object)}
     *
     * @param source 更新后的实体类
     * @param target 以前的老实体类
     * @param ignoreProperties 忽略的属性名
     * @throws BeansException Beans异常
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties);
    }

    private static void copyProperties(Object source, Object target, Class<?> editable, String... ignoreProperties)
        throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName()
                    + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        Set<String> ignoredProps = (ignoreProperties != null ? new HashSet<>(Arrays.asList(ignoreProperties)) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoredProps == null || !ignoredProps.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source);
                        // 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
                        if (value != null) {
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }
}
