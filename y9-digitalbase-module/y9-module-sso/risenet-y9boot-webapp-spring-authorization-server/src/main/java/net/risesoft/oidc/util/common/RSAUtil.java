package y9.util.common;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import net.risesoft.oidc.util.Y9Base64;

public class RSAUtil {

    /**
     * 获取RSA公私钥匙对
     */
    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
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
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     */
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = Y9Base64.base64ToByte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String content, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, RSAUtil.string2PublicKey(publicKey));
        byte[] byteEncrypt = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Y9Base64.byteToBase64(byteEncrypt);
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String contentBase64, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, RSAUtil.string2PrivateKey(privateKey));
        byte[] bytesDecrypt = cipher.doFinal(Y9Base64.base64ToByte(contentBase64));
        return new String(bytesDecrypt, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            String[] arr = genKeyPair();
            System.out.println("publicKey:" + arr[0]);
            System.out.println("privateKey:" + arr[1]);

            String encryptString = publicEncrypt("Risesoft@2023", arr[0]);
            System.out.println("公钥加密后字符串:" + encryptString);
            String decryptString = privateDecrypt(encryptString, arr[1]);
            System.out.println("私钥解密后字符串:" + decryptString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}