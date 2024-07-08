package net.risesoft.util;

import java.security.MessageDigest;

/**
 * 用于MD5的加密处理类
 * 
 * @author qinman
 */
public class MD5Util {
    private final static String[] hexDigits =
        {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 转换字节数组为16进制字串
     * 
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            // 若使用本函数转换则可得到加密结果的16进制表示，即数字字母混合的形式
            resultSb.append(byteToHexString(value));
        }
        return resultSb.toString();
    }

    @SuppressWarnings("unused")
    private static String byteToNumString(byte b) {

        int _b = b;
        if (_b < 0) {
            _b = 256 + _b;
        }

        return String.valueOf(_b);
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String md5Encode(String origin) {
        String resultString = null;

        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");

            resultString = byteArrayToString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {

        }
        return resultString;
    }
}
