package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

/**
 * Y9Util 单元测试
 *
 * @author YourName
 * @date 2025/12/18
 */
class Y9UtilTest {

    @Test
    void testEscape() {
        // 测试正常转义
        String input = "hello+world";
        String result = Y9Util.escape(input);
        assertEquals("hello\\+world", result);

        // 测试特殊字符转义
        input = "test:query(term)";
        result = Y9Util.escape(input);
        assertEquals("test\\:query\\(term\\)", result);

        // 测试空字符串
        input = "";
        result = Y9Util.escape(input);
        assertEquals("", result);

        // 测试无特殊字符
        input = "normal text";
        result = Y9Util.escape(input);
        assertEquals("normal text", result);
    }

    @Test
    void testGenCustomStrTwoParams() {
        // 测试两个参数的方法，正常情况
        String result = Y9Util.genCustomStr("a,b", "c,d");
        assertEquals("a,b,c,d", result);

        // 测试第一个参数为null
        result = Y9Util.genCustomStr((String)null, "c,d");
        assertEquals("c,d", result);

        // 测试第一个参数为空字符串
        result = Y9Util.genCustomStr("", "c,d");
        assertEquals("c,d", result);
    }

    @Test
    void testGenCustomStrThreeParams() {
        // 测试三个参数的方法，正常情况
        String result = Y9Util.genCustomStr("a,b", "c,d", "|");
        assertEquals("a,b|c,d", result);

        // 测试第一个参数为null
        result = Y9Util.genCustomStr((String)null, "c,d", "|");
        assertEquals("c,d", result);

        // 测试第一个参数为空字符串
        result = Y9Util.genCustomStr("", "c,d", "|");
        assertEquals("c,d", result);
    }

    @Test
    void testGenCustomStrStringBuffer() {
        // 测试StringBuffer版本
        StringBuffer sb = new StringBuffer();
        StringBuffer result = Y9Util.genCustomStr(sb, "first", ",");
        assertSame(sb, result);
        assertEquals("first", result.toString());

        // 添加第二个元素
        result = Y9Util.genCustomStr(sb, "second", ",");
        assertEquals("first,second", result.toString());
    }

    @Test
    void testGenCustomStrStringBuilder() {
        // 测试StringBuilder版本
        StringBuilder sb = new StringBuilder();
        StringBuilder result = Y9Util.genCustomStr(sb, "first", ",");
        assertSame(sb, result);
        assertEquals("first", result.toString());

        // 添加第二个元素
        result = Y9Util.genCustomStr(sb, "second", ",");
        assertEquals("first,second", result.toString());
    }

    @Test
    void testJoinCollection() {
        // 测试集合join方法
        List<String> list = Arrays.asList("a", "b", "c");
        String result = Y9Util.join(list, ",");
        assertEquals("a,b,c", result);

        // 测试空集合
        List<String> emptyList = new ArrayList<>();
        result = Y9Util.join(emptyList, ",");
        assertEquals("", result);

        // 测试不同分隔符
        result = Y9Util.join(list, "|");
        assertEquals("a|b|c", result);
    }

    @Test
    void testJoinArray() {
        // 测试数组join方法
        String[] array = {"a", "b", "c"};
        String result = Y9Util.join(array, ",");
        assertEquals("a,b,c", result);

        // 测试空数组
        String[] emptyArray = {};
        result = Y9Util.join(emptyArray, ",");
        assertEquals("", result);

        // 测试不同分隔符
        result = Y9Util.join(array, "|");
        assertEquals("a|b|c", result);
    }

    @Test
    void testRender() throws IOException {
        // 测试render方法
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);

        Y9Util.render(response, "text/plain", "test content");

        verify(response).setContentType("text/plain");
        verify(response).setHeader("Pragma", "No-cache");
        verify(response).setHeader("Cache-Control", "no-cache");
        verify(response).setDateHeader("Expires", 0);
        verify(writer).write("test content");
    }

    @Test
    void testRenderHtml() throws IOException {
        // 测试renderHtml方法
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);

        Y9Util.renderHtml(response, "test html");

        verify(response).setContentType("text/html;charset=UTF-8");
        verify(writer).write("test html");
    }

    @Test
    void testRenderJson() throws IOException {
        // 测试renderJson方法
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);

        Y9Util.renderJson(response, "{\"key\":\"value\"}");

        verify(response).setContentType("application/json;charset=UTF-8");
        verify(writer).write("{\"key\":\"value\"}");
    }

    @Test
    void testRenderXml() throws IOException {
        // 测试renderXml方法
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);

        Y9Util.renderXml(response, "<root>test</root>");

        verify(response).setContentType("text/xml;charset=UTF-8");
        verify(writer).write("<root>test</root>");
    }

    @Test
    void testStringArrayToSet() {
        // 测试字符串数组转Set
        String[] array = {"a", "b", "c", "a"};
        java.util.Set<String> result = Y9Util.stringArrayToSet(array);
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));

        // 测试包含空字符串的数组
        array = new String[] {"a", "", "b", null, "c"};
        result = Y9Util.stringArrayToSet(array);
        assertEquals(3, result.size()); // 空字符串和null应该被过滤掉
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    void testStringToCollection() {
        // 测试字符串转Collection
        String str = "a,b,c";
        java.util.Collection<String> result = Y9Util.stringToCollection(str, ",");
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));

        // 测试空字符串
        str = "";
        result = Y9Util.stringToCollection(str, ",");
        assertEquals(0, result.size());

        // 测试null字符串
        result = Y9Util.stringToCollection(null, ",");
        assertEquals(0, result.size());
    }

    @Test
    void testStringToList() {
        // 测试字符串转List
        String str = "a,b,c";
        List<String> result = Y9Util.stringToList(str, ",");
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));

        // 测试空字符串
        str = "";
        result = Y9Util.stringToList(str, ",");
        assertEquals(0, result.size());

        // 测试null字符串
        result = Y9Util.stringToList(null, ",");
        assertEquals(0, result.size());
    }

    @Test
    void testStrToList() {
        // 测试strToList方法
        String source = "a,b,c";
        List<String> result = Y9Util.strToList(source);
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));

        // 测试空字符串
        source = "";
        result = Y9Util.strToList(source);
        assertEquals(0, result.size());

        // 测试null字符串
        result = Y9Util.strToList(null);
        assertEquals(0, result.size());
    }
}