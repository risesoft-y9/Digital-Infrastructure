package net.risesoft.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class StringUtil {

    /**
     * 获取自定义个数的随机数字组成的字符串
     *
     * @param length 长度
     * @return String
     */
    public static String getRandomNum(int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 将相应数据转换成XML格式字符串(包含两个集合)
     * 
     * @param tagName1 标记名称1
     * @param list1 数据集1
     * @param proNames1 属性名称2
     * @param tagName2 标记名称2
     * @param list2 数据集2
     * @param proNames2 属性名称2
     * @return String
     */
    public static String parseXmlStr(String tagName1, List<Map<String, Object>> list1, String[] proNames1,
        String tagName2, List<Map<String, Object>> list2, String[] proNames2) {
        StringBuilder str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        str.append("<root>\n");
        for (int i = 0; i < list1.size(); i++) {
            str.append("\t<" + tagName1 + " id=\"" + tagName1 + "_" + (i + 1) + "\">\n");
            Map<String, Object> map = list1.get(i);
            for (String name : proNames1) {
                String value = (String)map.get(name);
                if (value != null && value.length() > 0) {
                    str.append("\t\t<" + name + ">" + value + "</" + name + ">\n");
                } else {
                    str.append("\t\t<" + name + "/>\n");
                }
            }
            str.append("\t</" + tagName1 + ">\n");
        }

        for (int i = 0; i < list2.size(); i++) {
            str.append("\t<" + tagName2 + " id=\"" + tagName2 + "_" + (i + 1) + "\">\n");
            Map<String, Object> map = list2.get(i);
            for (String name : proNames2) {
                String value = (String)map.get(name);
                if (value != null && value.length() > 0) {
                    str.append("\t\t<" + name + ">" + value + "</" + name + ">\n");
                } else {
                    str.append("\t\t<" + name + "/>\n");
                }
            }
            str.append("\t</" + tagName2 + ">\n");
        }
        str.append("</root>");
        return str.toString();
    }

    /**
     * 实现替换字符串中所有要替换字段的功能
     *
     * @param str 字符串
     * @param target 目标字符串
     * @param replacement 替换字符串
     * @return String
     */
    public static String replaceAll(String str, String target, String replacement) {
        while (str.indexOf(target) >= 0) {
            str = str.replace(target, replacement);
        }
        return str;
    }

    /**
     * 实现将xml的String字符串转化标准格式的String字符串
     *
     * @param str xml字节流
     * @return String
     */
    public static String strChangeToXml(byte[] str) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new ByteArrayInputStream(str));
        } catch (DocumentException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        StringWriter writer = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlwriter = new XMLWriter(writer, format);
        try {
            xmlwriter.write(document);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return writer.toString();
    }
}
