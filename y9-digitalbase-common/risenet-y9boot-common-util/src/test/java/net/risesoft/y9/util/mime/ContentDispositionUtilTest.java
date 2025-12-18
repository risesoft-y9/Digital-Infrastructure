package net.risesoft.y9.util.mime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * ContentDispositionUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/17
 */
class ContentDispositionUtilTest {

    @Test
    void testStandardizeAttachment() {
        // 测试普通英文文件名
        String fileName = "test.txt";
        String result = ContentDispositionUtil.standardizeAttachment(fileName);
        assertEquals("attachment; filename=test.txt;filename*=utf-8'zh_cn'test.txt", result);

        // 测试包含空格的文件名
        fileName = "my document.pdf";
        result = ContentDispositionUtil.standardizeAttachment(fileName);
        assertEquals("attachment; filename=my%20document.pdf;filename*=utf-8'zh_cn'my%20document.pdf", result);

        // 测试中文文件名
        fileName = "测试文件.docx";
        result = ContentDispositionUtil.standardizeAttachment(fileName);
        assertEquals(
            "attachment; filename=%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6.docx;filename*=utf-8'zh_cn'%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6.docx",
            result);

        // 测试特殊字符文件名
        fileName = "file-with_special*chars%.txt";
        result = ContentDispositionUtil.standardizeAttachment(fileName);
        assertEquals(
            "attachment; filename=file-with_special*chars%25.txt;filename*=utf-8'zh_cn'file-with_special*chars%25.txt",
            result);

        // 测试空文件名
        fileName = "";
        result = ContentDispositionUtil.standardizeAttachment(fileName);
        assertEquals("attachment; filename=;filename*=utf-8'zh_cn'", result);
    }

    @Test
    void testStandardizeInline() {
        // 测试普通英文文件名
        String fileName = "image.png";
        String result = ContentDispositionUtil.standardizeInline(fileName);
        assertEquals("inline; filename=image.png;filename*=utf-8'zh_cn'image.png", result);

        // 测试包含空格的文件名
        fileName = "my image.jpg";
        result = ContentDispositionUtil.standardizeInline(fileName);
        assertEquals("inline; filename=my%20image.jpg;filename*=utf-8'zh_cn'my%20image.jpg", result);

        // 测试中文文件名
        fileName = "图片.jpg";
        result = ContentDispositionUtil.standardizeInline(fileName);
        assertEquals("inline; filename=%E5%9B%BE%E7%89%87.jpg;filename*=utf-8'zh_cn'%E5%9B%BE%E7%89%87.jpg", result);

        // 测试特殊字符文件名
        fileName = "special!@#$%^&*().gif";
        result = ContentDispositionUtil.standardizeInline(fileName);
        assertEquals(
            "inline; filename=special%21%40%23%24%25%5E%26*%28%29.gif;filename*=utf-8'zh_cn'special%21%40%23%24%25%5E%26*%28%29.gif",
            result);

        // 测试空文件名
        fileName = "";
        result = ContentDispositionUtil.standardizeInline(fileName);
        assertEquals("inline; filename=;filename*=utf-8'zh_cn'", result);
    }

    @Test
    void testStandardizeDifferentFileNames() {
        // 测试各种类型的文件名
        String[] testFileNames =
            {"simple.txt", "文件.docx", "file with spaces.pdf", "文件 名称.xlsx", "special-chars_2022!.pdf",
                "long_filename_with_many_characters_for_testing_purposes.txt", "混合chars_and_中文.pdf"};

        for (String fileName : testFileNames) {
            String attachmentResult = ContentDispositionUtil.standardizeAttachment(fileName);
            String inlineResult = ContentDispositionUtil.standardizeInline(fileName);

            // 验证两种方法的主要区别在于前缀
            assertTrue(attachmentResult.startsWith("attachment;"));
            assertTrue(inlineResult.startsWith("inline;"));

            // 验证其余部分相同
            String attachmentSuffix = attachmentResult.substring("attachment".length());
            String inlineSuffix = inlineResult.substring("inline".length());
            assertEquals(attachmentSuffix, inlineSuffix);

            // 验证结果包含原始文件名的编码形式
            String encodedFileName = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8);
            encodedFileName = encodedFileName.replace("+", "%20");
            assertTrue(attachmentResult.contains(encodedFileName));
        }
    }
}