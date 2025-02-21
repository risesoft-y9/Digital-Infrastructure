package org.apereo.cas.web.y9.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Y9MessageDigest {
    private static String byte2hexSplitWithColon(byte[] b) {
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
    public static String sha1(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hexSplitWithColon(sha1Bytes);
    }

    public static String sha256(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hexSplitWithColon(sha1Bytes);
    }

    public static String sha512(String str) throws NoSuchAlgorithmException {
        if (str == null || str.length() == 0) {
            return "";
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(str.getBytes());
        byte[] sha1Bytes = messageDigest.digest();
        return byte2hexSplitWithColon(sha1Bytes);
    }

    public static String bcrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean bcryptMatch(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }
}
