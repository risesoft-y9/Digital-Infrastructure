package net.risesoft.y9.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
public class Y9ModelConvertUtil {

    public static <T> List<T> convert(List<?> sourceList, Class<T> targetClass) {
        return convert(sourceList, targetClass, (String[])null);
    }

    public static <T> List<T> convert(List<?> sourceList, Class<T> targetClass, String... ignoreProperties) {
        List<T> modelList = new ArrayList<>();
        if (sourceList != null) {
            for (Object entity : sourceList) {
                modelList.add(convert(entity, targetClass, ignoreProperties));
            }
        }
        return modelList;
    }

    public static <T> T convert(Object source, Class<T> modelClass) {
        return convert(source, modelClass, (String[])null);
    }

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

    public Y9ModelConvertUtil() {}

}
