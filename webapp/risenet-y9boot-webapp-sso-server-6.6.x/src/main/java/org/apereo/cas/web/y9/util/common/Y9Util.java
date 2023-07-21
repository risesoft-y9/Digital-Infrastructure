package org.apereo.cas.web.y9.util.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9Util {
    /*
     * 生成自定义的字符串，例如a,b,c 其中str是要生成的字符串 addNew是添加的字符串
     */
    public static String genCustomStr(String str, String addNew) {
        if (str == null) {
            str = "";
        }
        if ("".equals(str)) {
            str = addNew;
        } else {
            str += "," + addNew;
        }
        return str;
    }

    public static String[] getBeanPropertyNames(Object bean, String exclude) {
        return getBeanPropertyNames(bean, exclude.split(","));
    }

    public static String[] getBeanPropertyNames(Object bean, String[] exclude) {
        List<String> list = new ArrayList<String>();
        try {
            Map<String, Object> beanMap = PropertyUtils.describe(bean);
            Set<String> set = beanMap.keySet();
            for (String s : set) {
                if (!s.equals("class")) {
                    list.add(s);
                }
            }

            if (exclude.length > 0) {
                for (String name : exclude) {
                    list.remove(name);
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return list.toArray(new String[0]);
    }

    public static String join(Collection<String> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(delimiter);
        }

        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - delimiter.length());
        }

        return sb.toString();
    }

    public static String ListToSQLIN(List<String> list) {
        StringBuilder sb = new StringBuilder().append("(");
        for (String s : list) {
            if (StringUtils.hasText(s)) {
                sb.append("'").append(s.trim()).append("',");
            }
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    /**
     * 发送内容。使用UTF-8编码。
     * 
     * @param response
     * @param contentType
     * @param text
     */
    public static void render(HttpServletResponse response, String contentType, String text) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 发送json。使用UTF-8编码。
     * 
     * @param response HttpServletResponse
     * @param text 发送的字符串
     */
    public static void renderJson(HttpServletResponse response, String text) {
        render(response, "application/json;charset=UTF-8", text);
    }

    /**
     * 发送xml。使用UTF-8编码。
     * 
     * @param response HttpServletResponse
     * @param text 发送的字符串
     */
    public static void renderXml(HttpServletResponse response, String text) {
        render(response, "text/xml;charset=UTF-8", text);
    }

    public static Set<String> StringArrayToSet(String[] arry) {
        Set<String> retSet = new HashSet<String>();
        for (String s : arry) {
            if (StringUtils.hasText(s)) {
                retSet.add(s);
            }
        }

        return retSet;
    }

    public static Collection<String> StringToCollection(String str, String delimiter) {
        Collection<String> ret = new ArrayList<String>();
        if (null == str || str.isEmpty()) {
            return ret;
        }

        String[] arry = str.split(delimiter);
        for (String s : arry) {
            if (StringUtils.hasText(s)) {
                ret.add(s);
            }
        }

        return ret;
    }

    public static List<String> StringToList(String str, String delimiter) {
        List<String> ret = new ArrayList<String>();
        if (null == str || str.isEmpty()) {
            return ret;
        }

        String[] arry = str.split(delimiter);
        for (String s : arry) {
            if (StringUtils.hasText(s)) {
                ret.add(s);
            }
        }

        return ret;
    }

    public static String StringToSQLIN(String str, String delimiter) {
        List<String> list = StringToList(str, delimiter);
        StringBuilder sb = new StringBuilder().append("(");
        if (null == str || str.isEmpty()) {
            sb.append(")");
            return sb.toString();
        }
        for (String s : list) {
            if (StringUtils.hasText(s)) {
                sb.append("'").append(s.trim()).append("',");
            }
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    /*
     * source是a,b,c，按照,拆分，将每个值放入List中
     */
    public static List<String> strToList(String source) {
        List<String> resultList = new ArrayList<String>();
        if (null == source || source.isEmpty()) {
            return resultList;
        }
        String[] temp = source.split(",");
        for (int i = 0; i < temp.length; i++) {
            resultList.add(temp[i]);
        }
        return resultList;
    }

}
