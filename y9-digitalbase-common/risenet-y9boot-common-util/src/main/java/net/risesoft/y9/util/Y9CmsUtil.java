package net.risesoft.y9.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletRequest;

/**
 * 内容发布工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9CmsUtil {
    public static boolean contains(String str, String[] strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }
        for (String s : strs) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, String> getRequestMap(ServletRequest request, String prefix, boolean nameWithPrefix) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<String> names = request.getParameterNames();
        String name, key, value;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name.startsWith(prefix)) {
                key = nameWithPrefix ? name : name.substring(prefix.length());
                value = Y9Util.join(request.getParameterValues(name), ",");
                map.put(key, value);
            }
        }
        return map;
    }

    private static Map<String, String> getRequestMap(ServletRequest request, String prefix, String[] contains,
        boolean nameWithPrefix) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<String> names = request.getParameterNames();
        String name, key, value;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name.startsWith(prefix)) {
                key = nameWithPrefix ? name : name.substring(prefix.length());
                if (contains(key, contains)) {
                    value = Y9Util.join(request.getParameterValues(name), ",");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    public static Map<String, String> getRequestMapWithPrefix(ServletRequest request, String prefix) {
        return getRequestMap(request, prefix, true);
    }

    public static Map<String, String> getRequestMapWithPrefix(ServletRequest request, String prefix,
        String[] contains) {
        return getRequestMap(request, prefix, contains, false);
    }

}
