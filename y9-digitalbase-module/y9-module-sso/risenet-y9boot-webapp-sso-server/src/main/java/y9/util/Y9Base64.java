package y9.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9Base64 {

    public static String decode(String encryptedText) {
        Base64 dec = new Base64(-1);
        byte[] bTemp = dec.decode(encryptedText);
        return new String(bTemp, StandardCharsets.UTF_8);
    }

    public static String encode(String plainText) {
        Base64 encoder = new Base64(-1);
        String str = encoder.encodeToString(plainText.getBytes());
        return str;
    }

    public static String byteToBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    public static byte[] base64ToByte(String base64Key) throws IOException {
        return Base64.decodeBase64(base64Key);
    }

    public static void main(String[] args) {
        try {
            String str = "111111";
            String enc = Y9Base64.encode(str); // MTExMTEx
            String dec = Y9Base64.decode(enc);
            System.out.println(enc);
            System.out.println(dec);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
