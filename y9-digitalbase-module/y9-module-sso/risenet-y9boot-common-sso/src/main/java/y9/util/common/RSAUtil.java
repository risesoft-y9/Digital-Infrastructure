package y9.util.common;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import y9.util.Y9Base64;

public class RSAUtil {

    public static final String RSA = "RSA";

    public static final String RSA_OAEP_SHA256_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final OAEPParameterSpec OAEP_PARAMETER_SPEC =
        new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);

    private static final int KEY_SIZE = 1024;

    /**
     * 获取RSA公私钥匙对
     */
    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(KEY_SIZE);
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
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     */
    private static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = Y9Base64.base64ToByte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String content, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, string2PublicKey(publicKey), OAEP_PARAMETER_SPEC);
        byte[] byteEncrypt = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Y9Base64.byteToBase64(byteEncrypt);
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String contentBase64, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, string2PrivateKey(privateKey), OAEP_PARAMETER_SPEC);
        byte[] bytesDecrypt = cipher.doFinal(Y9Base64.base64ToByte(contentBase64));
        return new String(bytesDecrypt, StandardCharsets.UTF_8);
    }

    public static String toPem(PrivateKey privateKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PRIVATE KEY-----\n");

        String base64 = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(privateKey.getEncoded());
        sb.append(base64);
        sb.append("\n-----END PRIVATE KEY-----");
        return sb.toString();
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