package net.risesoft.y9.util.mime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;

/**
 * MediaTypeUtils 单元测试
 *
 * @author shidaobang
 * @date 2025/12/17
 */
class MediaTypeUtilTest {

    @Test
    void testGetMediaTypeForValidFileTypes() {
        ServletContext servletContext = new MockServletContext();

        // 测试常见的文件类型
        MediaType mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "test.txt");
        assertEquals(MediaType.TEXT_PLAIN, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "image.jpg");
        assertEquals(MediaType.IMAGE_JPEG, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "document.pdf");
        assertEquals(MediaType.APPLICATION_PDF, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "data.xml");
        assertEquals(MediaType.APPLICATION_XML, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "page.html");
        assertEquals(MediaType.TEXT_HTML, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "script.js");
        assertEquals(MediaType.valueOf("application/javascript"), mediaType);
    }

    @Test
    void testGetMediaTypeForUnknownFileType() {
        ServletContext servletContext = new MockServletContext();

        // 测试未知文件类型，应该返回 APPLICATION_OCTET_STREAM
        MediaType mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "unknown.xyz");
        // 由于ServletContext可能对某些扩展名有预定义，我们测试一个更不可能存在的扩展名
        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "unknown.xyzabc");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);

        // 测试无扩展名文件
        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "README");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);
    }

    @Test
    void testGetMediaTypeForEmptyOrNullFileName() {
        ServletContext servletContext = new MockServletContext();

        // 测试空字符串文件名
        MediaType mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);

        // 测试null文件名
        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, null);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);
    }

    @Test
    void testGetMediaTypeWithSpecialCharactersInFileName() {
        ServletContext servletContext = new MockServletContext();

        // 测试包含特殊字符的文件名
        MediaType mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "file name.pdf");
        assertEquals(MediaType.APPLICATION_PDF, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "file-name.pdf");
        assertEquals(MediaType.APPLICATION_PDF, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "file_name.pdf");
        assertEquals(MediaType.APPLICATION_PDF, mediaType);
    }

    @Test
    void testGetMediaTypeCaseInsensitive() {
        ServletContext servletContext = new MockServletContext();

        // 测试大小写不敏感
        MediaType mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "document.PDF");
        assertEquals(MediaType.APPLICATION_PDF, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "image.JPG");
        assertEquals(MediaType.IMAGE_JPEG, mediaType);

        mediaType = MediaTypeUtil.getMediaTypeForFileName(servletContext, "page.HTML");
        assertEquals(MediaType.TEXT_HTML, mediaType);
    }
}