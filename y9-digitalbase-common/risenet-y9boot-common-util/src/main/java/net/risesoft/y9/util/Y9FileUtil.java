package net.risesoft.y9.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;

/**
 * 文件操作工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9FileUtil {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    /**
     * 写出XML文件在本地，自定义存储路径
     * 
     * @param uploadDir 上传路径
     * @param list 值列表
     * @param names 名称列表
     * @param fileType 文件类型
     * @return String 存储路径
     */
    public static String exportFile(String uploadDir, List<Map<String, Object>> list, List<String> names,
        String fileType) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + getRandomNum(3) + fileType;
        String filePath = uploadDir + File.separator + fileName;
        File file = new File(filePath);

        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            StringBuffer str = new StringBuffer();
            str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            str.append("<root>\n");
            for (int i = 0; i < list.size(); i++) {
                str.append("\t<row id=\"" + (i + 1) + "\">\n");
                Map<String, Object> map = new HashMap<String, Object>();
                map = list.get(i);
                for (int j = 0; j < names.size(); j++) {

                    String name = names.get(j);
                    String value = (String)map.get(name);
                    if (value != null && value.length() > 0) {
                        str.append("\t\t<" + name + ">" + value + "</" + name + ">\n");
                    } else {
                        str.append("\t\t<" + name + "/>\n");
                    }
                }
                str.append("\t</row>\n");
            }
            str.append("</root>");
            bw.write(str.toString());// 写出到文件
            bw.flush(); // 刷新输出流
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return filePath;
    }

    /**
     * 读取文件内容转为String
     * 
     * @param filePath 文件路径
     * @return String 文件内容
     */
    public static String getContent(String filePath) {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            LOGGER.warn("file too big...");
            return null;
        }

        String content = "";
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return content;
    }

    /**
     * 获取自定义个数的随机数字组成的字符串
     * 
     * @param length 长度
     * @return String 随机数字组成的字符串
     */
    public static String getRandomNum(int length) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= length; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * <p>
     * Iterates over a base name and returns the first non-existent file.<br />
     * This method extracts a file's base name, iterates over it until the first non-existent appearance with
     * <code>basename(n).ext</code>. Where n is a positive integer starting from one.
     * </p>
     * 
     * @param file base file
     * @return first non-existent file
     */
    public static File getUniqueFile(final File file) {
        if (!file.exists()) {
            return file;
        }

        File tmpFile = new File(file.getAbsolutePath());
        File parentDir = tmpFile.getParentFile();
        int count = 1;
        String extension = FilenameUtils.getExtension(tmpFile.getName());
        String baseName = FilenameUtils.getBaseName(tmpFile.getName());
        do {
            tmpFile = new File(parentDir, baseName + "(" + count++ + ")." + extension);
        } while (tmpFile.exists());
        return tmpFile;
    }

    /**
     * 根据传进来的文件路径，判断文件是否存在
     * 
     * @param filePath 文件路径
     * @return boolean 判断结果
     */
    public static boolean isExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 读取 SQL 文件，获取 SQL 语句
     *
     * @param sqlFile SQL 脚本文件
     * @return {@code List<sql>} 返回所有 SQL 语句的 List
     * @throws Exception 异常
     */
    public static List<String> loadSql(String sqlFile) throws Exception {
        List<String> sqlList = new ArrayList<>();
        // Windows 下换行是 \r\n, Linux 下是 \n
        String[] sqlArray = sqlFile.split(";");
        for (int i = 0; i < sqlArray.length; i++) {
            String sql = sqlArray[i].replaceAll("--.*", "").replaceAll("/\\*(\\s|.|\\r|\\n)*?\\*/", "").trim();
            if (!"".equals(sql)) {
                sqlList.add(sql);
                LOGGER.info("清理之后的sql：{}", sql);
            }
        }
        return sqlList;
    }

    /**
     * 导入xml文件
     * 
     * @param file 文件
     * @param proNames 属性数组
     * @param tagName 标记名称
     * @return
     */
    public static List<Map<String, Object>> readXml(File file, String[] proNames, String tagName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            // Document 接口表示整个 HTML 或 XML 文档。
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            // 这是一种便捷属性，该属性允许直接访问文档的文档元素的子节点
            Element element = doc.getDocumentElement();
            // 以文档顺序返回具有给定标记名称的所有后代 Elements 的 NodeList。
            NodeList nl = element.getElementsByTagName(tagName);
            // 列表中的节点数。
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (String name : proNames) {
                    Element ele = (Element)element.getElementsByTagName(tagName).item(i);
                    String value = ele.getElementsByTagName(name).item(0).getTextContent();
                    map.put(name, value);
                }
                list.add(map);
            }
        } catch (SAXException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 写文件到本地(自定义存储路径、自动生成文件名)
     * 
     * @param in 输入流
     * @param fileType 文件类型
     * @param filePath 文件路径
     * @return File 文件
     */
    public static File writeFile(InputStream in, String fileType, String filePath) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String prefix = System.currentTimeMillis() + "_" + getRandomNum(3);
        String path = filePath + File.separator + prefix + fileType;
        File saveFile = new File(path);
        try (FileOutputStream fs = new FileOutputStream(saveFile)) {
            byte[] buffer = new byte[1024 * 1024];
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
        } catch (IOException e) {
            String msg = "写文件时异常！file:" + path;
            LOGGER.error(msg);
        }
        return saveFile;
    }

    /**
     * 写文件到本地(自定义存储路径、文件名)
     *
     * @param in 输入流
     * @param fileName 文件名称
     * @param filePath 文件路径
     */
    public static File writeFile2(InputStream in, String fileName, String filePath) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String path = filePath + File.separator + fileName;
        File saveFile = new File(path);
        try (FileOutputStream fs = new FileOutputStream(saveFile)) {
            byte[] buffer = new byte[1024 * 1024];
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
                fs.flush();
            }
        } catch (IOException e) {
            String msg = "写文件时异常！file:" + path;
            LOGGER.error(msg);
        }
        return saveFile;
    }

    /**
     * 根据传入字符串内容写入到log格式数据文件中
     * 
     * @param prefix 文件名称
     * @param message 字符串内容
     */
    public static void writerLogByStr(String prefix, String message) {
        String fileName = Y9Context.getRealPath("/file/temp/") + prefix + ".log";
        try (FileWriterWithEncoding fw = new FileWriterWithEncoding(fileName, "utf-8", true);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(message);
            bw.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 导出xml文件
     * 
     * @param fileName 文件名称
     * @param list 对象列表
     * @param names 名称列表
     */
    public static void writerXml(String fileName, List<Map<String, Object>> list, List<String> names) {

        try (FileWriter fw = new FileWriter(fileName); // 用来写入字符文件的便捷类
            BufferedWriter bw = new BufferedWriter(fw)) {
            StringBuffer str = new StringBuffer(); // 用来存储xml字符串的
            str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            str.append("<root>\n");
            for (int i = 0; i < list.size(); i++) {
                str.append("\t<row id=\"" + (i + 1) + "\">\n");
                Map<String, Object> map = new HashMap<String, Object>();
                map = list.get(i);
                for (int j = 0; j < names.size(); j++) {
                    String name = names.get(j);
                    String value = (String)map.get(name);
                    if (value != null && value.length() > 0) {
                        str.append("\t\t<" + name + ">" + value + "</" + name + ">\n");
                    } else {
                        str.append("\t\t<" + name + "/>\n");
                    }
                }
                str.append("\t</row>\n");
            }
            str.append("</root>");
            bw.write(str.toString());// 写出到文件
            bw.flush(); // 刷新输出流
        } catch (IOException e) {

            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 根据传入字符串内容导出XML格式数据文件
     * 
     * @param uploadDir 上传路径
     * @param dataStr 字符串内容
     * @param fileType 文件列表
     * @return File 文件
     */
    public static File writerXmlByStr(String uploadDir, String dataStr, String fileType) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + getRandomNum(3) + fileType;
        String filePath = uploadDir + File.separator + fileName;
        File file = new File(filePath);

        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(dataStr);
            bw.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 文件字节数转换为可读性较好的格式（B KB MB GB 保留小数点后两位）
     * 
     * @param fileSize 文件字节数
     * @return String 转换过的文件字节数
     */
    public static String getDisplayFileSize(long fileSize) {
        if (fileSize < 0) {
            throw new IllegalArgumentException("文件字节数不应为负数");
        }

        DecimalFormat df = new DecimalFormat("#.00");
        String displayFileSize;

        if (fileSize < KB) {
            displayFileSize = df.format((double)fileSize) + "B";
        } else if (fileSize < MB) {
            displayFileSize = df.format((double)fileSize / KB) + "KB";
        } else if (fileSize < GB) {
            displayFileSize = df.format((double)fileSize / MB) + "MB";
        } else {
            displayFileSize = df.format((double)fileSize / GB) + "GB";
        }

        return displayFileSize;
    }
}
