package net.risesoft.y9.util.mime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * Content-Disposition 响应头下载文件名工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class ContentDispositionUtil {

    public static String standardizeAttachment(String fileName) {
        return standardize(fileName, "attachment");
    }

    public static String standardizeInline(String fileName) {
        return standardize(fileName, "inline");
    }

    private static String standardize(String fileName, String value) {
        StringBuilder sb = new StringBuilder();
        String name = "";
        try {
            name = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        name = name.replace("+", "%20");
        sb.append(value).append("; filename=").append(name).append(";filename*=utf-8'zh_cn'").append(name);
        return sb.toString();
    }

    private ContentDispositionUtil() {
        throw new IllegalStateException("ContentDispositionUtil Utility class");
    }

}
