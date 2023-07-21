package net.risesoft.y9.util.mime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * 下载文件名工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class DownloadFileNameUtil {

    public static String standardize(String fileName) {
        StringBuilder sb = new StringBuilder();
        String name = "";
        try {
            name = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        name = name.replace("+", "%20");
        sb.append("attachment; filename=").append(name).append(";filename*=utf-8'zh_cn'").append(name);
        return sb.toString();
    }

    public static String standardize(String fileName, String inline) {
        StringBuilder sb = new StringBuilder();
        String name = "";
        try {
            name = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        name = name.replace("+", "%20");
        sb.append("inline; filename=").append(name).append(";filename*=utf-8'zh_cn'").append(name);
        return sb.toString();
    }

    private DownloadFileNameUtil() {
        throw new IllegalStateException("DownloadFileNameUtil Utility class");
    }

}
