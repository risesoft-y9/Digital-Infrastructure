package net.risesoft.y9.util.signing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

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
public class Y9MessageDigest {

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

    public static void main(String[] args) {
        try {
            String str = "111111";
            String enc = sha1(str);
            System.out.println(enc);// 3D:4F:2B:F0:7D:C1:BE:38:B2:0C:D6:E4:69:49:A1:07:1F:9D:0E:3D
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * MD5加密-32位小写 @
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
     */
    public static String md5(String encryptStr) {
        return md5(encryptStr.getBytes());
    }

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
     * @param str
     * @return str
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

    public static String sha256(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return sha256(str.getBytes());
    }

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

    public static String sha512(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return sha512(str.getBytes());
    }

    public static String bcrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean bcryptMatch(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

}
