package y9.util.common;

import y9.util.Y9Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    /**
     * 获取RSA公私钥匙对
     */
    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 获取公钥(base64编码)
     */
    private static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Y9Base64.byteToBase64(bytes);
    }

    /**
     * 获取私钥(Base64编码)
     */
    private static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Y9Base64.byteToBase64(bytes);
    }

    public static String[] genKeyPair() throws Exception {
        KeyPair keyPair = getKeyPair();
        String[] keyPairArr = new String[2];
        keyPairArr[0] = getPublicKey(keyPair);
        keyPairArr[1] = getPrivateKey(keyPair);
        return keyPairArr;
    }

    /**
     * 将Base64编码后的公钥转换成PublicKey对象
     */
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = Y9Base64.base64ToByte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     */
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = Y9Base64.base64ToByte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String content, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, RSAUtil.string2PublicKey(publicKey));
        byte[] byteEncrypt = cipher.doFinal(content.getBytes("utf-8"));
        String msg = Y9Base64.byteToBase64(byteEncrypt);
        return msg;
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String contentBase64, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, RSAUtil.string2PrivateKey(privateKey));
        byte[] bytesDecrypt = cipher.doFinal(Y9Base64.base64ToByte(contentBase64));
        String msg = new String(bytesDecrypt, "utf-8");
        return msg;
    }

    public static void main(String[] args) {
        try {
            //String[] arr = genKeyPair();
            String[] arr = {"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6DvO5m7p1Of/IkPwMRdQK7tx4wZKrbmVj2aqhlX5z+McbxeiP1XgwpiFINxigtXqU49rtCb1eyUafD4lIYZ399Z5TAZeD3kH+ggdP6xRr0G7+u1UE9qgoLXPOugZsABwSOq5w9v6qGUAY8U3doLDkjCueMbEeGG96FjqaaMLckwIDAQAB","MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALoO87mbunU5/8iQ/AxF1Aru3HjBkqtuZWPZqqGVfnP4xxvF6I/VeDCmIUg3GKC1epTj2u0JvV7JRp8PiUhhnf31nlMBl4PeQf6CB0/rFGvQbv67VQT2qCgtc866BmwAHBI6rnD2/qoZQBjxTd2gsOSMK54xsR4Yb3oWOppowtyTAgMBAAECgYACb5OtSGamhj3dCFjkaD2gbwQf6Jjc+bMGHaFoQCbJqeYhbPPgfjD2ohYpdd8yq22RaXJCTDBYf1YXWwK3GeARn19tYnJodqspAya3jyfSFY+IRDQ2kQ/59w1VmBiOtJsUJG4va2Bz9SRfLZuXwOyznjKk7BE89Anb3ici6u6HYQJBAONiagv+Kc8dkxoip1NWYVFjCWNHTejWTgLLgVDZi1jep/8Xx5d5E1aewcvPCJRz8A7hYEY5Uc8Mt9PB6NhLedkCQQDReSXdMdg+/lLYwdX7lRfPH7PIGNK4UwmZQ0LW7fsoy8/TD9gMnQ9sAVHILKxQuhubDHGfonVhA08WA2nvgzpLAkEA0hMmbpYPAm8MbOT/OhtgJdUd4z8JV8hGooZpnsyd1SlAhIjvuZ3+o9Rgr29DOgQzEUxfqgFi96uEWnuYJ9zzYQJBAK0SL5qmiEjj0IeO/8Gx2c4kH2KAP/pTgWbEEAGM9ysp2jhEqvkg+5D9Nhjx8B1y50Qf7/E5RUo2da12fJyMDCkCQDjBuq+dssaPGs36oijHBxzJmeaFR6vmKBUaiwYJEHg2ayi3r3/dHRrqYxXC2wblxOhkytUl/R1DErHztHHm878="};
            System.out.println("publicKey:" + arr[0]);
            System.out.println("privateKey:" + arr[1]);

            String encryptString = publicEncrypt("Risesoft@2025", arr[0]);
            System.out.println("公钥加密后字符串:" + encryptString);
            String decryptString = privateDecrypt(encryptString, arr[1]);
            System.out.println("私钥解密后字符串:" + decryptString);

            encryptString = publicEncrypt("安全审计员", arr[0]);
            System.out.println("公钥加密后字符串:" + encryptString);
            decryptString = privateDecrypt(encryptString, arr[1]);
            System.out.println("私钥解密后字符串:" + decryptString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}