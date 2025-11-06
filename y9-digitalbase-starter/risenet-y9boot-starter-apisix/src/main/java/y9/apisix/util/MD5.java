package y9.apisix.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5 {

    private static final MD5 INSTANCE = new MD5();

    private static final char[] DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private MD5() {

    }

    public static byte[] digest(byte[] data) {
        return getMessageDigest().digest(data);
    }

    public static byte[] digest(File file) throws IOException {
        MessageDigest digest = getMessageDigest();
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, numRead);
            }
            return digest.digest();
        } catch (IOException e) {
            throw e;
        }
    }

    public static String encode(byte[] data) {
        char[] charArray = encodeAsChars(data);
        return new String(charArray);
    }

    public static char[] encodeAsChars(byte[] data) {
        int l = data.length;

        char[] out = new char[l << 1];

        int i = 0;
        for (int j = 0; i < l; i++) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }

        return out;
    }

    public static final MD5 getInstance() {
        return INSTANCE;
    }

    static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(byte[] bytes) {
        byte[] encode = digest(bytes);
        return MD5.encode(encode);
    }

    public static String hash(File file) throws IOException {
        byte[] encode = digest(file);
        return MD5.encode(encode);
    }

    public static String hash(String message) {
        return hash(message.getBytes());
    }

    public static String hash(String message, Charset charset) {
        return hash(message.getBytes(charset));
    }

    public byte[] hashDigest(byte[] source) {
        return digest(source);
    }

}