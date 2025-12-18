package net.risesoft.y9.util.base64;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * base64 工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9Base64Util {

    /**
     * 解密Base64编码的字符串
     * 
     * @param encryptedText 加密的文本
     * @return String 解密后的字符串
     */
    public static String decode(String encryptedText) {
        Base64 dec = new Base64(-1);
        byte[] bTemp = dec.decode(encryptedText);
        return new String(bTemp, StandardCharsets.UTF_8);
    }

    /**
     * 解密Base64编码的字符串为字节数组
     * 
     * @param encryptedText 加密的文本
     * @return byte[] 解密后的字节数组
     */
    public static byte[] decodeAsBytes(String encryptedText) {
        Base64 dec = new Base64(-1);
        return dec.decode(encryptedText);
    }

    /**
     * 将字节数组进行Base64编码
     * 
     * @param bytes 字节数组
     * @return String Base64编码后的字符串
     */
    public static String encode(byte[] bytes) {
        Base64 encoder = new Base64(-1);
        return encoder.encodeToString(bytes);
    }

    /**
     * 将字符串进行Base64编码
     * 
     * @param plainText 明文字符串
     * @return String Base64编码后的字符串
     */
    public static String encode(String plainText) {
        Base64 encoder = new Base64(-1);
        return encoder.encodeToString(plainText.getBytes());
    }

}