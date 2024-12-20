package net.risesoft.y9.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9Util {

    /**
     * Closes the object quietly, catching rather than throwing IOException. Intended for use from finally blocks.
     *
     * @param closeable the object to close, may be {@code null}
     * @since 3.0
     */
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                // Ignored
            }
        }
    }

    /**
     * Returns a String where those characters that QueryParser expects to be escaped are escaped by a preceding
     * <code>\</code>. 拷贝org.apache.lucene.queryparser.classic.QueryParserBase
     *
     * @param s 字符串
     * @return String characters that QueryParser expects to be escaped are escaped by a preceding
     */
    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^'
                || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 生成自定义的字符串，例如a,b,c 其中str是要生成的字符串 addNew是添加的字符串
     * 
     * @param str 字符串
     * @param addNew 添加的字符串
     * @return String 自定义的字符串
     */
    public static String genCustomStr(String str, String addNew) {
        if (str == null) {
            str = "";
        }
        if (str.isEmpty()) {
            str = addNew;
        } else {
            str += "," + addNew;
        }
        return str;
    }

    /**
     * 生成自定义的字符串,用指定的分隔符
     *
     * @param str 字符串
     * @param addNew 添加的字符串
     * @param delimiter 分隔符
     * @return String 自定义的字符串
     */
    public static String genCustomStr(String str, String addNew, String delimiter) {
        if (str == null) {
            str = "";
        }
        if (str.isEmpty()) {
            str = addNew;
        } else {
            str += delimiter + addNew;
        }
        return str;
    }

    public static StringBuffer genCustomStr(StringBuffer stringBuffer, String addNew, String delimiter) {
        if (stringBuffer.length() == 0) {
            stringBuffer.append(addNew);
        } else {
            stringBuffer.append(delimiter).append(addNew);
        }
        return stringBuffer;
    }

    public static StringBuilder genCustomStr(StringBuilder stringBuffer, String addNew, String delimiter) {
        if (stringBuffer.length() == 0) {
            stringBuffer.append(addNew);
        } else {
            stringBuffer.append(delimiter).append(addNew);
        }
        return stringBuffer;
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
                if (!"class".equals(s)) {
                    list.add(s);
                }
            }

            for (String name : exclude) {
                if (StringUtils.hasText(name)) {
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

    public static String join(String[] aray, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : aray) {
            sb.append(s).append(delimiter);
        }

        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - delimiter.length());
        }

        return sb.toString();
    }

    public static String listToSqlIn(List<String> list) {
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
     * @param response 响应信息
     * @param contentType 内容格式
     * @param text 发送内容
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

    public static void renderHtml(HttpServletResponse response, String text) {
        render(response, "text/html;charset=UTF-8", text);
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

    public static Set<String> stringArrayToSet(String[] arry) {
        Set<String> retSet = new HashSet<String>();
        for (String s : arry) {
            if (StringUtils.hasText(s)) {
                retSet.add(s);
            }
        }

        return retSet;
    }

    public static Collection<String> stringToCollection(String str, String delimiter) {
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

    public static List<String> stringToList(String str, String delimiter) {
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

    public static String stringToSqlIn(String str, String delimiter) {
        List<String> list = stringToList(str, delimiter);
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

    /**
     * source是a,b,c，按照,拆分，将每个值放入List中
     * 
     * @param source 字符串
     * @return {@code List<String>} 拆分后的字符列表
     */
    public static List<String> strToList(String source) {
        List<String> resultList = new ArrayList<String>();
        if (null == source || source.isEmpty()) {
            return resultList;
        }
        String[] temp = source.split(",");
        Collections.addAll(resultList, temp);
        return resultList;
    }

}
