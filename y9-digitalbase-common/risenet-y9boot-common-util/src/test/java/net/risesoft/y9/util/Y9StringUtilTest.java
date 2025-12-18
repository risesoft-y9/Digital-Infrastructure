package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Y9StringUtil 测试类
 *
 * @author YourName
 * @date 2025/12/18
 */
class Y9StringUtilTest {

    @Test
    void testFormatWithSinglePlaceholder() {
        String template = "Hello, {}!";
        String result = Y9StringUtil.format(template, "World");
        assertEquals("Hello, World!", result);
    }

    @Test
    void testFormatWithMultiplePlaceholders() {
        String template = "My name is {}, I am {} years old.";
        String result = Y9StringUtil.format(template, "Alice", 18);
        assertEquals("My name is Alice, I am 18 years old.", result);
    }

    @Test
    void testFormatWithNoPlaceholder() {
        String template = "Hello World!";
        String result = Y9StringUtil.format(template);
        assertEquals("Hello World!", result);
    }

    @Test
    void testFormatWithEmptyTemplate() {
        String template = "";
        String result = Y9StringUtil.format(template, "test");
        assertEquals("", result);
    }

    @Test
    void testFormatWithNullTemplate() {
        String result = Y9StringUtil.format(null, "test");
        assertEquals("null", result);
    }

    @Test
    void testFormatWithNullParams() {
        String template = "Hello, {}!";
        String result = Y9StringUtil.format(template, (Object[])null);
        assertEquals("Hello, {}!", result);
    }

    @Test
    void testFormatWithSpecialCharacters() {
        String template = "Price: {}$, Message: {}";
        String result = Y9StringUtil.format(template, 99.9, "It's a test!");
        assertEquals("Price: 99.9$, Message: It's a test!", result);
    }

    @Test
    void testFormatWithNumberAndBooleanParams() {
        String template = "Status: {}, Count: {}, Active: {}";
        String result = Y9StringUtil.format(template, "OK", 100, true);
        assertEquals("Status: OK, Count: 100, Active: true", result);
    }
}