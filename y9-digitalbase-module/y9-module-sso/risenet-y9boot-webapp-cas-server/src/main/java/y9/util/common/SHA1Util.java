package y9.util.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Util {
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

    /*
     * public static void main(String[] args) throws Exception { System.out.println(SHA1("111111")); //3D:4F:2B:F0:7D:C1:BE:38:B2:0C:D6:E4:69:49:A1:07:1F:9D:0E:3D //db.getCollection('y9User').updateMany({}, {$set: {password: "3D:4F:2B:F0:7D:C1:BE:38:B2:0C:D6:E4:69:49:A1:07:1F:9D:0E:3D"}}) }
     */
}
