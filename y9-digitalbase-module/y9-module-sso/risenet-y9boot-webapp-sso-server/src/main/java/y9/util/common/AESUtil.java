package y9.util.common;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtil {

    public static final String IV = "1234567890123456";

    public static final String KEY = "weKnAwrp23EghP5c";

    /*******************************************************************
     * AES加密算法 加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    public static String encrypt(String sSrc) throws Exception {
        byte[] raw = KEY.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());

        return Base64.encodeBase64String(encrypted);// 此处使用BAES64做转码功能，同时能起到2次
    }

    // 解密
    public static String decrypt(String sSrc) {
        byte[] encrypted1 = Base64.decodeBase64(sSrc);// 先用bAES64解密

        byte[] raw = KEY.getBytes(StandardCharsets.US_ASCII);

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {
        }

        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(encrypt("11111111-1111-1111-1111-111111111117"));
    }
}