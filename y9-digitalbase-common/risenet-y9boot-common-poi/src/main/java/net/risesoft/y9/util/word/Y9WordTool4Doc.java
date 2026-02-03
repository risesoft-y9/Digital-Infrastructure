package net.risesoft.y9.util.word;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.Range;

import lombok.extern.slf4j.Slf4j;

/**
 * 使用POI,进行Word相关的操作
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9WordTool4Doc {

    public static List<String> getBookmarkNameList(InputStream is) {
        HWPFDocument document = null;
        List<String> bookmarkList = new ArrayList<>();
        try {
            document = new HWPFDocument(is);
            Bookmarks bookmarks = document.getBookmarks();
            for (int i = 0; i < bookmarks.getBookmarksCount(); i++) {
                bookmarkList.add(bookmarks.getBookmark(i).getName());
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return bookmarkList;
    }

    /**
     * 获取文档内容
     *
     * @param is 输入流
     */
    public static String getText(InputStream is) {
        String content = "";
        try {
            HWPFDocument document = new HWPFDocument(is);
            WordExtractor extractor = new WordExtractor(document);
            // 提取文档内容
            content = extractor.getText();
            // 关闭资源
            extractor.close();
            document.close();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return content;
    }

    /**
     * 进行标签替换的例子,传入的Map中，key表示标签名称，value是替换的信息
     *
     * @param is 输入流
     * @param response 响应
     * @param map 标签替换Map，key表示标签名称，value是替换的信息
     */
    public static void replaceBookMark(InputStream is, HttpServletResponse response, Map<String, Object> map) {
        try (OutputStream out = response.getOutputStream()) {
            HWPFDocument document = new HWPFDocument(is);
            Bookmarks bookmarks = document.getBookmarks();
            for (int i = 0; i < bookmarks.getBookmarksCount(); i++) {
                Bookmark bookmark = bookmarks.getBookmark(i);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (bookmark.getName().equals(entry.getKey())) {
                        Range range = new Range(bookmark.getStart(), bookmark.getEnd(), document);
                        range.replaceText((String)entry.getValue(), false);
                    }
                }
            }
            document.write(out);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public static byte[] replaceBookMark(InputStream is, Map<String, Object> map) {
        ByteArrayOutputStream out = null;
        try {
            HWPFDocument document = new HWPFDocument(is);
            Bookmarks bookmarks = document.getBookmarks();
            for (int i = 0; i < bookmarks.getBookmarksCount(); i++) {
                Bookmark bookmark = bookmarks.getBookmark(i);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (bookmark.getName().equals(entry.getKey())) {
                        Range range = new Range(bookmark.getStart(), bookmark.getEnd(), document);
                        range.replaceText((String)entry.getValue(), false);
                    }
                }
            }
            out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return null;
    }

}
