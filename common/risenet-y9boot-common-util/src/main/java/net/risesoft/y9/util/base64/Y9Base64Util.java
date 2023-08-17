package net.risesoft.y9.util.base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

/**
 * base64 工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9Base64Util {

    public static String decode(String encryptedText) {
        Base64 dec = new Base64(-1);
        byte[] bTemp = dec.decode(encryptedText);
        return new String(bTemp, StandardCharsets.UTF_8);
    }

    public static byte[] decodeAsBytes(String encryptedText) {
        Base64 dec = new Base64(-1);
        return dec.decode(encryptedText);
    }
    
    public static String byteToBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    public static byte[] base64ToByte(String base64Key) throws IOException {
        return Base64.decodeBase64(base64Key);
    }

    public static String encode(byte[] bytes) {
        Base64 encoder = new Base64(-1);
        return encoder.encodeToString(bytes);
    }

    public static String encode(String plainText) {
        Base64 encoder = new Base64(-1);
        return encoder.encodeToString(plainText.getBytes());
    }

    public static void main(String[] args) {
        String str = "111111";
        String enc = Y9Base64Util.encode(str); // MTExMTEx
        String dec = Y9Base64Util.decode(enc);
        System.out.println(enc);
        System.out.println(dec);
    }
}
