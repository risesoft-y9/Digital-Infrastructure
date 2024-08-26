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

    public static String byte2hexSplitWithColon(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + ":";
            }
        }
        return hs.toUpperCase();
    }

    public static String byte2hex(byte[] content) {
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
     * MD5加密-32位小写
     * 
     * @param content 内容
     * @return String MD5加密后的字符串
     */
    public static String md5(byte[] content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(content);
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
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            return byte2hex(md5Bytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return encryptStr;
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
     * @param str 需加密的字符串
     * @return str SHA1加密字符串
     */
    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
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
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] shaBytes = messageDigest.digest(str.getBytes());
            return byte2hex(shaBytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return str;
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
        if (str == null || str.length() == 0) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] shaBytes = messageDigest.digest(str.getBytes());
            return byte2hex(shaBytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return str;
    }

    public static String bcrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean bcryptMatch(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

}
