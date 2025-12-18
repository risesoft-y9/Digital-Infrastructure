package net.risesoft.y9.util;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9Util {

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
     * 字符串拼接，中间用英文逗号分隔 例：<br/>
     * genCustomStr("a,b", "c,d") -> a,b,c,d <br/>
     * genCustomStr(null, "c,d") -> c,d <br/>
     * genCustomStr("", "c,d") -> c,d <br/>
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
     * 字符串拼接，用指定的分隔符 例：<br/>
     * genCustomStr("a,b", "c,d", "|") -> a,b|c,d <br/>
     * genCustomStr(null, "c,d", "|") -> c,d <br/>
     * genCustomStr("", "c,d", "|") -> c,d <br/>
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

    /**
     * 字符串拼接，用指定的分隔符
     * 
     * @param stringBuffer StringBuffer对象
     * @param addNew 添加的字符串
     * @param delimiter 分隔符
     * @return StringBuffer 拼接后的StringBuffer对象
     */
    public static StringBuffer genCustomStr(StringBuffer stringBuffer, String addNew, String delimiter) {
        if (stringBuffer.length() == 0) {
            stringBuffer.append(addNew);
        } else {
            stringBuffer.append(delimiter).append(addNew);
        }
        return stringBuffer;
    }

    /**
     * 字符串拼接，用指定的分隔符
     * 
     * @param stringBuilder StringBuilder对象
     * @param addNew 添加的字符串
     * @param delimiter 分隔符
     * @return StringBuilder 拼接后的StringBuilder对象
     */
    public static StringBuilder genCustomStr(StringBuilder stringBuilder, String addNew, String delimiter) {
        if (stringBuilder.length() == 0) {
            stringBuilder.append(addNew);
        } else {
            stringBuilder.append(delimiter).append(addNew);
        }
        return stringBuilder;
    }

    /**
     * 获取Bean对象的属性名称数组，排除指定的属性
     * 
     * @param bean Bean对象
     * @param exclude 需要排除的属性名称，多个属性用逗号分隔
     * @return String[] 属性名称数组
     */
    public static String[] getBeanPropertyNames(Object bean, String exclude) {
        return getBeanPropertyNames(bean, exclude.split(","));
    }

    /**
     * 获取Bean对象的属性名称数组，排除指定的属性
     * 
     * @param bean Bean对象
     * @param exclude 需要排除的属性名称数组
     * @return String[] 属性名称数组
     */
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

    /**
     * 使用指定分隔符连接集合中的元素
     * 
     * @param list 字符串集合
     * @param delimiter 分隔符
     * @return String 连接后的字符串
     */
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

    /**
     * 使用指定分隔符连接数组中的元素
     * 
     * @param array 字符串数组
     * @param delimiter 分隔符
     * @return String 连接后的字符串
     */
    public static String join(String[] array, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append(delimiter);
        }

        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - delimiter.length());
        }

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

    /**
     * 发送HTML内容。使用UTF-8编码。
     *
     * @param response 响应信息
     * @param text 发送内容
     */
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

    /**
     * 将字符串数组转换为Set集合
     * 
     * @param array 字符串数组
     * @return Set<String> 字符串Set集合
     */
    public static Set<String> stringArrayToSet(String[] array) {
        Set<String> retSet = new HashSet<String>();
        for (String s : array) {
            if (StringUtils.hasText(s)) {
                retSet.add(s);
            }
        }

        return retSet;
    }

    /**
     * 将字符串按照指定分隔符拆分并转换为集合
     * 
     * @param str 字符串
     * @param delimiter 分隔符
     * @return Collection<String> 字符串集合
     */
    public static Collection<String> stringToCollection(String str, String delimiter) {
        Collection<String> ret = new ArrayList<String>();
        if (null == str || str.isEmpty()) {
            return ret;
        }

        String[] array = str.split(delimiter);
        for (String s : array) {
            if (StringUtils.hasText(s)) {
                ret.add(s);
            }
        }

        return ret;
    }

    /**
     * 将字符串按照指定分隔符拆分并转换为列表
     * 
     * @param str 字符串
     * @param delimiter 分隔符
     * @return List<String> 字符串列表
     */
    public static List<String> stringToList(String str, String delimiter) {
        List<String> ret = new ArrayList<String>();
        if (null == str || str.isEmpty()) {
            return ret;
        }

        String[] array = str.split(delimiter);
        for (String s : array) {
            if (StringUtils.hasText(s)) {
                ret.add(s);
            }
        }

        return ret;
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