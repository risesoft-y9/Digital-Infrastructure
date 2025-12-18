package net.risesoft.y9.util.signing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息摘要、加密工具类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9MessageDigestUtil {

    public static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[abxy]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    /**
     * 将字节数组转换为十六进制字符串，以冒号分隔
     * 
     * @param bytes 字节数组
     * @return String 十六进制字符串
     */
    private static String byte2hexSplitWithColon(byte[] bytes) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = (Integer.toHexString(bytes[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < bytes.length - 1) {
                hs = hs + ":";
            }
        }
        return hs.toUpperCase();
    }

    /**
     * 将字节数组转换为十六进制字符串
     * 
     * @param content 字节数组
     * @return String 十六进制字符串
     */
    private static String byte2hex(byte[] content) {
        StringBuilder sb = new StringBuilder(2 * content.length);
        for (byte b : content) {
            int val = (b) & 0xff;
            if (val < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }

    /**
     * MD5加密-32位小写
     * 
     * @param content 内容
     * @return String MD5加密后的字符串
     */
    public static String md5(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = messageDigest.digest(content);
            return byte2hex(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return byte2hex(content);
    }

    /**
     * MD5加密-32位小写
     * 
     * @param encryptStr 加密字符串
     * @return String MD5加密后的字符串
     */
    public static String md5(String encryptStr) {
        return md5(encryptStr.getBytes());
    }

    /**
     * SHA1加密
     * 
     * @param content 内容字节数组
     * @return String SHA1加密后的字符串
     */
    public static String sha1(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(content);
            byte[] sha1Bytes = messageDigest.digest(content);
            return byte2hex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return byte2hex(content);
    }

    /**
     * SHA1加密
     * 
     * @param str 需加密的字符串
     * @return str SHA1加密字符串
     */
    public static String sha1(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] shaBytes = messageDigest.digest(str.getBytes());
            return byte2hexSplitWithColon(shaBytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return str;
    }

    /**
     * SHA256加密
     * 
     * @param content 内容字节数组
     * @return String SHA256加密后的字符串
     */
    public static String sha256(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] sha1Bytes = messageDigest.digest(content);
            return byte2hex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return byte2hex(content);
    }

    /**
     * SHA256加密
     * 
     * @param str 需加密的字符串
     * @return String SHA256加密后的字符串
     */
    public static String sha256(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return sha256(str.getBytes());
    }

    /**
     * SHA512加密
     * 
     * @param content 内容字节数组
     * @return String SHA512加密后的字符串
     */
    public static String sha512(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] sha1Bytes = messageDigest.digest(content);
            return byte2hex(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return byte2hex(content);
    }

    /**
     * SHA512加密
     * 
     * @param str 需加密的字符串
     * @return String SHA512加密后的字符串
     */
    public static String sha512(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return sha512(str.getBytes());
    }

    /**
     * BCrypt加密
     * 
     * @param password 密码
     * @return String 加密后的密码
     */
    public static String bcrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * BCrypt密码匹配验证
     * 
     * @param plaintext 明文
     * @param hashed 加密后的密文
     * @return boolean 是否匹配
     */
    public static boolean bcryptMatch(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

}