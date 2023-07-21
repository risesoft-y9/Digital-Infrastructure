/**
 * @Project Name:r7-infoissue-web
 * @File Name: Base64.java
 * @Package Name: net.risesoft.util
 * @Company:北京有生博大软件有限公司（深圳分公司）
 * @Copyright (c) 2016,RiseSoft All Rights Reserved.
 * @date 2016年3月22日 下午3:13:59
 */
package org.apereo.cas.web.y9.util.common;

/**
 * @ClassName: Base64.java
 * @Description: 与base64.js形成配套加密解密，可避免传输过程中乱码问题
 *
 * @author abel
 * @date 2016年3月22日 下午3:13:59
 * @version
 * @since JDK 1.6
 */
public class Base64Util {
    /**
     * 定义编码规则
     */
    private static final byte[] ENCODING_TABLE = {(byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G', (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N', (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', (byte)'V',
        (byte)'W', (byte)'X', (byte)'Y', (byte)'Z', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g', (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n', (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', (byte)'v',
        (byte)'w', (byte)'x', (byte)'y', (byte)'z', (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/'};
    private static final byte[] DECODING_TABLE;

    static {
        DECODING_TABLE = new byte[128];
        for (int i = 0; i < 128; i++) {
            DECODING_TABLE[i] = (byte)-1;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            DECODING_TABLE[i] = (byte)(i - 'A');
        }
        for (int i = 'a'; i <= 'z'; i++) {
            DECODING_TABLE[i] = (byte)(i - 'a' + 26);
        }
        for (int i = '0'; i <= '9'; i++) {
            DECODING_TABLE[i] = (byte)(i - '0' + 52);
        }
        DECODING_TABLE['+'] = 62;
        DECODING_TABLE['/'] = 63;
    }

    public static byte[] decode(byte[] data) {
        byte[] bytes;
        byte b1;
        byte b2;
        byte b3;
        byte b4;
        data = discardNonBase64Bytes(data);
        if (data[data.length - 2] == '=') {
            bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
        } else if (data[data.length - 1] == '=') {
            bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
        } else {
            bytes = new byte[((data.length / 4) * 3)];
        }
        for (int i = 0, j = 0; i < (data.length - 4); i += 4, j += 3) {
            b1 = DECODING_TABLE[data[i]];
            b2 = DECODING_TABLE[data[i + 1]];
            b3 = DECODING_TABLE[data[i + 2]];
            b4 = DECODING_TABLE[data[i + 3]];
            bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
            bytes[j + 2] = (byte)((b3 << 6) | b4);
        }
        if (data[data.length - 2] == '=') {
            b1 = DECODING_TABLE[data[data.length - 4]];
            b2 = DECODING_TABLE[data[data.length - 3]];
            bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
        } else if (data[data.length - 1] == '=') {
            b1 = DECODING_TABLE[data[data.length - 4]];
            b2 = DECODING_TABLE[data[data.length - 3]];
            b3 = DECODING_TABLE[data[data.length - 2]];
            bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
        } else {
            b1 = DECODING_TABLE[data[data.length - 4]];
            b2 = DECODING_TABLE[data[data.length - 3]];
            b3 = DECODING_TABLE[data[data.length - 2]];
            b4 = DECODING_TABLE[data[data.length - 1]];
            bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
            bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
        }
        return bytes;
    }

    /**
     * @MethodName: decode
     * @Description: TODO 执行解码
     * @param data 需要解码的数据
     * @return
     *
     * @Author abel
     * @date 2016年3月23日 下午6:35:33
     */
    public static byte[] decode(String data) {
        byte[] bytes;
        byte b1;
        byte b2;
        byte b3;
        byte b4;
        data = discardNonBase64Chars(data);
        if (data.charAt(data.length() - 2) == '=') {
            bytes = new byte[(((data.length() / 4) - 1) * 3) + 1];
        } else if (data.charAt(data.length() - 1) == '=') {
            bytes = new byte[(((data.length() / 4) - 1) * 3) + 2];
        } else {
            bytes = new byte[((data.length() / 4) * 3)];
        }
        for (int i = 0, j = 0; i < (data.length() - 4); i += 4, j += 3) {
            b1 = DECODING_TABLE[data.charAt(i)];
            b2 = DECODING_TABLE[data.charAt(i + 1)];
            b3 = DECODING_TABLE[data.charAt(i + 2)];
            b4 = DECODING_TABLE[data.charAt(i + 3)];
            bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
            bytes[j + 2] = (byte)((b3 << 6) | b4);
        }
        if (data.charAt(data.length() - 2) == '=') {
            b1 = DECODING_TABLE[data.charAt(data.length() - 4)];
            b2 = DECODING_TABLE[data.charAt(data.length() - 3)];
            bytes[bytes.length - 1] = (byte)((b1 << 2) | (b2 >> 4));
        } else if (data.charAt(data.length() - 1) == '=') {
            b1 = DECODING_TABLE[data.charAt(data.length() - 4)];
            b2 = DECODING_TABLE[data.charAt(data.length() - 3)];
            b3 = DECODING_TABLE[data.charAt(data.length() - 2)];
            bytes[bytes.length - 2] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[bytes.length - 1] = (byte)((b2 << 4) | (b3 >> 2));
        } else {
            b1 = DECODING_TABLE[data.charAt(data.length() - 4)];
            b2 = DECODING_TABLE[data.charAt(data.length() - 3)];
            b3 = DECODING_TABLE[data.charAt(data.length() - 2)];
            b4 = DECODING_TABLE[data.charAt(data.length() - 1)];
            bytes[bytes.length - 3] = (byte)((b1 << 2) | (b2 >> 4));
            bytes[bytes.length - 2] = (byte)((b2 << 4) | (b3 >> 2));
            bytes[bytes.length - 1] = (byte)((b3 << 6) | b4);
        }
        return bytes;
    }

    public static String decode(String data, String charset) throws Exception {
        if (data == null || data.length() == 0) {
            return data;
        }
        return new String(Base64Util.decode(data), charset);
    }

    private static byte[] discardNonBase64Bytes(byte[] data) {
        byte[] temp = new byte[data.length];
        int bytesCopied = 0;
        for (int i = 0; i < data.length; i++) {
            if (isValidBase64Byte(data[i])) {
                temp[bytesCopied++] = data[i];
            }
        }
        byte[] newData = new byte[bytesCopied];
        System.arraycopy(temp, 0, newData, 0, bytesCopied);
        return newData;
    }

    private static String discardNonBase64Chars(String data) {
        StringBuffer sb = new StringBuffer();
        int length = data.length();
        for (int i = 0; i < length; i++) {
            if (isValidBase64Byte((byte)(data.charAt(i)))) {
                sb.append(data.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String encode(byte[] data) {
        byte[] result = encode(data, 0);
        StringBuffer sb = new StringBuffer(result.length);
        for (int i = 0; i < result.length; i++) {
            sb.append((char)result[i]);
        }
        return sb.toString();
    }

    public static byte[] encode(byte[] data, int offset) {
        byte[] bytes;
        int realCount = data.length - offset;
        int modulus = realCount % 3;
        if (modulus == 0) {
            bytes = new byte[(4 * realCount) / 3];
        } else {
            bytes = new byte[4 * ((realCount / 3) + 1)];
        }
        int dataLength = (data.length - modulus);
        int a1;
        int a2;
        int a3;
        for (int i = offset, j = 0; i < dataLength; i += 3, j += 4) {
            a1 = data[i] & 0xff;
            a2 = data[i + 1] & 0xff;
            a3 = data[i + 2] & 0xff;
            bytes[j] = ENCODING_TABLE[(a1 >>> 2) & 0x3f];
            bytes[j + 1] = ENCODING_TABLE[((a1 << 4) | (a2 >>> 4)) & 0x3f];
            bytes[j + 2] = ENCODING_TABLE[((a2 << 2) | (a3 >>> 6)) & 0x3f];
            bytes[j + 3] = ENCODING_TABLE[a3 & 0x3f];
        }
        int b1;
        int b2;
        int b3;
        int d1;
        int d2;
        switch (modulus) {
            /* nothing left to do */
            case 0:
                break;
            case 1:
                d1 = data[data.length - 1] & 0xff;
                b1 = (d1 >>> 2) & 0x3f;
                b2 = (d1 << 4) & 0x3f;
                bytes[bytes.length - 4] = ENCODING_TABLE[b1];
                bytes[bytes.length - 3] = ENCODING_TABLE[b2];
                bytes[bytes.length - 2] = (byte)'=';
                bytes[bytes.length - 1] = (byte)'=';
                break;
            case 2:
                d1 = data[data.length - 2] & 0xff;
                d2 = data[data.length - 1] & 0xff;
                b1 = (d1 >>> 2) & 0x3f;
                b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
                b3 = (d2 << 2) & 0x3f;
                bytes[bytes.length - 4] = ENCODING_TABLE[b1];
                bytes[bytes.length - 3] = ENCODING_TABLE[b2];
                bytes[bytes.length - 2] = ENCODING_TABLE[b3];
                bytes[bytes.length - 1] = (byte)'=';
                break;
            default:
                break;
        }
        return bytes;
    }

    public static String encode(String data, String charset) throws Exception {
        // byte[] result = (data.getBytes("Unicode"));
        if (data == null || data.length() == 0) {
            return data;
        }
        int offset = 0;
        // getBytes("unicode")转完后会在前头加上两字节”FE“
        byte[] result = encode(data.getBytes(charset), offset);
        StringBuffer sb = new StringBuffer(result.length);
        for (int i = 0; i < result.length; i++) {
            sb.append((char)result[i]);
        }
        return sb.toString();
    }

    private static boolean isValidBase64Byte(byte b) {
        if (b == '=') {
            return true;
        } else if ((b < 0) || (b >= 128)) {
            return false;
        } else if (DECODING_TABLE[b] == -1) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        String data = "/v9OAVFGT8o=";
        String data1 = encode(data, "Unicode");
        String data2 = decode(data, "Unicode");
        System.out.println(data);
        System.out.println(data1);
        System.out.println(data2);
    }
}
