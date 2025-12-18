package net.risesoft.y9.util.mime;

import javax.servlet.ServletContext;

import org.springframework.http.MediaType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 媒体类型工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaTypeUtil {

    /**
     * 根据文件名获取媒体类型
     * 
     * @param servletContext Servlet上下文
     * @param fileName 文件名
     * @return MediaType 媒体类型
     */
    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
        String mineType = servletContext.getMimeType(fileName);
        try {
            return MediaType.parseMediaType(mineType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}