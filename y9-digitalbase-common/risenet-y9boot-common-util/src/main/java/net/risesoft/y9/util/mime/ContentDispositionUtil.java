package net.risesoft.y9.util.mime;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Content-Disposition 响应头工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentDispositionUtil {

    /**
     * 标准化附件下载文件名
     * 
     * @param fileName 文件名
     * @return String 标准化的Content-Disposition头部值
     */
    public static String standardizeAttachment(String fileName) {
        return standardize(fileName, "attachment");
    }

    /**
     * 标准化内联显示文件名
     * 
     * @param fileName 文件名
     * @return String 标准化的Content-Disposition头部值
     */
    public static String standardizeInline(String fileName) {
        return standardize(fileName, "inline");
    }

    private static String standardize(String fileName, String value) {
        StringBuilder sb = new StringBuilder();
        String name = "";
        name = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        name = name.replace("+", "%20");
        sb.append(value).append("; filename=").append(name).append(";filename*=utf-8'zh_cn'").append(name);
        return sb.toString();
    }

}