package y9.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

public class Y9MessageDigest {
    public static String byte2hex(byte[] b) {
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

    /**
     * SHA1加密
     * 
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String SHA1(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hex(sha1Bytes);
    }

    public static String SHA256(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hex(sha1Bytes);
    }

    public static String SHA512(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hex(sha1Bytes);
    }

    public static String hashpw(String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashed;
    }

    public static boolean checkpw(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }
}
