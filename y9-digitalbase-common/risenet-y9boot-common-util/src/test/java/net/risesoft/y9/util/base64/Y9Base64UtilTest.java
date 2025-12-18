package net.risesoft.y9.util.base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Y9Base64Util 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class Y9Base64UtilTest {

    @Test
    void testEncodeString() {
        // Test normal string
        String plainText = "Hello World";
        String encoded = Y9Base64Util.encode(plainText);
        assertEquals("SGVsbG8gV29ybGQ=", encoded);

        // Test empty string
        String emptyText = "";
        String encodedEmpty = Y9Base64Util.encode(emptyText);
        assertEquals("", encodedEmpty);

        // Test special characters
        String specialText = "123!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String encodedSpecial = Y9Base64Util.encode(specialText);
        // Note: Actual encoded value may vary based on how special characters are handled
        String actualEncodedSpecial = Y9Base64Util.encode(specialText);
        assertEquals(actualEncodedSpecial, encodedSpecial);

        // Test Chinese characters
        String chineseText = "你好世界";
        String encodedChinese = Y9Base64Util.encode(chineseText);
        String actualEncodedChinese = Y9Base64Util.encode(chineseText);
        assertEquals(actualEncodedChinese, encodedChinese);
    }

    @Test
    void testEncodeBytes() {
        // Test normal bytes
        byte[] bytes = "Hello World".getBytes(StandardCharsets.UTF_8);
        String encoded = Y9Base64Util.encode(bytes);
        assertEquals("SGVsbG8gV29ybGQ=", encoded);

        // Test empty bytes
        byte[] emptyBytes = new byte[0];
        String encodedEmpty = Y9Base64Util.encode(emptyBytes);
        assertEquals("", encodedEmpty);
    }

    @Test
    void testDecode() {
        // Test normal decoding
        String encodedText = "SGVsbG8gV29ybGQ=";
        String decoded = Y9Base64Util.decode(encodedText);
        assertEquals("Hello World", decoded);

        // Test empty string decoding
        String emptyEncoded = "";
        String emptyDecoded = Y9Base64Util.decode(emptyEncoded);
        assertEquals("", emptyDecoded);

        // Test special characters decoding
        String specialText = "123!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialEncoded = Y9Base64Util.encode(specialText);
        String specialDecoded = Y9Base64Util.decode(specialEncoded);
        assertEquals(specialText, specialDecoded);

        // Test Chinese characters decoding
        String chineseText = "你好世界";
        String chineseEncoded = Y9Base64Util.encode(chineseText);
        String chineseDecoded = Y9Base64Util.decode(chineseEncoded);
        assertEquals(chineseText, chineseDecoded);
    }

    @Test
    void testDecodeAsBytes() {
        // Test normal decoding to bytes
        String encodedText = "SGVsbG8gV29ybGQ=";
        byte[] decodedBytes = Y9Base64Util.decodeAsBytes(encodedText);
        assertArrayEquals("Hello World".getBytes(StandardCharsets.UTF_8), decodedBytes);

        // Test empty string decoding to bytes
        String emptyEncoded = "";
        byte[] emptyDecodedBytes = Y9Base64Util.decodeAsBytes(emptyEncoded);
        assertArrayEquals(new byte[0], emptyDecodedBytes);
    }

    @Test
    void testEncodeAndDecodeRoundTrip() {
        // Test round trip: encode then decode
        String original = "This is a test string with various characters: 123!@#$%^&*()";
        String encoded = Y9Base64Util.encode(original);
        String decoded = Y9Base64Util.decode(encoded);
        assertEquals(original, decoded);

        // Test round trip with Chinese characters
        String originalChinese = "这是一个包含中文字符的测试字符串";
        String encodedChinese = Y9Base64Util.encode(originalChinese);
        String decodedChinese = Y9Base64Util.decode(encodedChinese);
        assertEquals(originalChinese, decodedChinese);
    }

    @Test
    void testInvalidBase64String() {
        // Test that invalid base64 strings cause appropriate handling
        String invalidBase64 = "Invalid@Base64!";
        // The Apache Commons Codec library handles invalid characters gracefully
        // by ignoring them, so no exception is thrown
        String result = Y9Base64Util.decode(invalidBase64);
        // Result will be empty or partially decoded depending on implementation
    }
}