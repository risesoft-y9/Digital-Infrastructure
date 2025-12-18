package net.risesoft.y9.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 对象转换工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Y9ModelConvertUtil {

    /**
     * 将源对象列表转换为目标类型的对象列表
     * 
     * @param sourceList 源对象列表
     * @param targetClass 目标对象的Class
     * @return List<T> 转换后的目标对象列表
     */
    public static <T> List<T> convert(List<?> sourceList, Class<T> targetClass) {
        return convert(sourceList, targetClass, (String[])null);
    }

    /**
     * 将源对象列表转换为目标类型的对象列表，可忽略部分属性
     ** 
     * @param sourceList 源对象列表
     * @param targetClass 目标对象的Class
     * @param ignoreProperties 忽略的属性数组
     * @return List<T> 转换后的目标对象列表
     */
    public static <T> List<T> convert(List<?> sourceList, Class<T> targetClass, String... ignoreProperties) {
        List<T> modelList = new ArrayList<>();
        if (sourceList != null) {
            for (Object entity : sourceList) {
                modelList.add(convert(entity, targetClass, ignoreProperties));
            }
        }
        return modelList;
    }

    /**
     * 将源对象转换为目标类型的对象
     * 
     * @param source 源对象
     * @param modelClass 目标对象的Class
     * @return T 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> modelClass) {
        return convert(source, modelClass, (String[])null);
    }

    /**
     * 将源对象转换为目标类型的对象，可忽略部分属性
     * 
     * @param source 源对象
     * @param modelClass 目标对象的Class
     * @param ignoreProperties 忽略的属性数组
     * @return T 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> modelClass, String... ignoreProperties) {
        try {
            T t = null;
            if (source != null) {
                t = modelClass.getDeclaredConstructor().newInstance();
                Y9BeanUtil.copyProperties(source, t, ignoreProperties);
            }
            return t;
        } catch (InstantiationException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

}