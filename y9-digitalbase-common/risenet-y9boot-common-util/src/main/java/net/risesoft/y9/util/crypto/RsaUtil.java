package net.risesoft.y9.util.crypto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.apache.commons.codec.binary.Base64;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RSA加密工具类
 *
 * @author saysky
 * @date 2020/2/29 2:56 下午
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RsaUtil {

    /**
     * 数字签名，密钥算法
     */
    private static final String RSA = "RSA";

    public static final String RSA_OAEP_SHA256_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final OAEPParameterSpec OAEP_PARAMETER_SPEC =
        new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);

    /**
     * 数字签名签名/验证算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 密钥转成byte[]
     *
     * @param key 密钥
     * @return byte[] 密钥
     */
    private static byte[] decodeBase64(String key) {
        return Base64.decodeBase64(key);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密的数据
     * @param priKey 私钥
     * @return byte[] 解密的数据
     * @throws Exception 异常
     */
    public static byte[] decryptByPriKey(byte[] data, byte[] priKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, byteArrayToPrivateKey(priKey), OAEP_PARAMETER_SPEC);
        return cipher.doFinal(data);
    }

    private static PrivateKey byteArrayToPrivateKey(byte[] priKey)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 私钥解密
     *
     * @param data 解密前的字符串
     * @param privateKey 私钥
     * @return String 解密后的字符串
     * @throws Exception 异常
     */
    public static String decryptByPriKey(String data, String privateKey) throws Exception {
        byte[] priKey = decodeBase64(privateKey);
        byte[] design = decryptByPriKey(decodeBase64(data), priKey);
        return new String(design);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密的数据
     * @param pubKey 公钥
     * @return String 解密后的数据
     * @throws Exception 异常
     */
    public static byte[] decryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, byteArrayToPublicKey(pubKey), OAEP_PARAMETER_SPEC);
        return cipher.doFinal(data);
    }

    private static PublicKey byteArrayToPublicKey(byte[] pubKey)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 公钥解密
     *
     * @param data 解密前的字符串
     * @param publicKey 公钥
     * @return String 解密后的字符串
     * @throws Exception 异常
     */
    public static String decryptByPubKey(String data, String publicKey) throws Exception {
        byte[] pubKey = decodeBase64(publicKey);
        byte[] design = decryptByPubKey(decodeBase64(data), pubKey);
        return new String(design);
    }

    /**
     * 密钥转成字符串
     *
     * @param key 密钥
     * @return String 密钥转成的字符串
     */
    private static String encodeBase64String(byte[] key) {
        return Base64.encodeBase64String(key);
    }

    /**
     * 私钥加密
     *
     * @param data 待加密的数据
     * @param priKey 私钥
     * @return byte[] 加密后的数据
     * @throws Exception 异常
     */
    public static byte[] encryptByPriKey(byte[] data, byte[] priKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, byteArrayToPrivateKey(priKey), OAEP_PARAMETER_SPEC);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data 加密前的字符串
     * @param privateKey 私钥
     * @return String 加密后的字符串
     * @throws Exception 异常
     */
    public static String encryptByPriKey(String data, String privateKey) throws Exception {
        byte[] priKey = decodeBase64(privateKey);
        byte[] enSign = encryptByPriKey(data.getBytes(), priKey);
        return encodeBase64String(enSign);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param pubKey 公钥
     * @return byte[] 加密数据
     * @throws Exception 异常
     */
    public static byte[] encryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_SHA256_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, byteArrayToPublicKey(pubKey), OAEP_PARAMETER_SPEC);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data 加密前的字符串
     * @param publicKey 公钥
     * @return String 加密后的字符串
     * @throws Exception 异常
     */
    public static String encryptByPubKey(String data, String publicKey) throws Exception {
        byte[] pubKey = decodeBase64(publicKey);
        byte[] enSign = encryptByPubKey(data.getBytes(), pubKey);
        return encodeBase64String(enSign);
    }

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
        return encodeBase64String(bytes);
    }

    /**
     * 获取私钥(Base64编码)
     */
    private static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return encodeBase64String(bytes);
    }

    public static String[] genKeyPair() throws Exception {
        KeyPair keyPair = getKeyPair();
        String[] keyPairArr = new String[2];
        keyPairArr[0] = getPublicKey(keyPair);
        keyPairArr[1] = getPrivateKey(keyPair);
        return keyPairArr;
    }

    /**
     * 生成文件保存秘钥
     *
     * @param filePath 文件路径
     * @param keyStr 秘钥
     */
    public static void saveKeyForFile(String filePath, String keyStr) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(keyStr);
            bw.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * RSA签名
     *
     * @param data 待签名数据
     * @param priKey 私钥
     * @return String 签名
     * @throws Exception 异常
     */
    public static String sign(byte[] data, byte[] priKey) throws Exception {
        // 取得私钥
        PrivateKey privateKey = byteArrayToPrivateKey(priKey);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initSign(privateKey);
        // 更新
        signature.update(data);
        return encodeBase64String(signature.sign());
    }

    /**
     * RSA校验数字签名
     *
     * @param data 待校验数据
     * @param sign 数字签名
     * @param pubKey 公钥
     * @return boolean 校验成功返回true，失败返回false
     * @exception Exception error
     */
    public boolean verify(byte[] data, byte[] sign, byte[] pubKey) throws Exception {
        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        // 初始化公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        // 产生公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initVerify(publicKey);
        // 更新
        signature.update(data);
        // 验证
        return signature.verify(sign);
    }

}
