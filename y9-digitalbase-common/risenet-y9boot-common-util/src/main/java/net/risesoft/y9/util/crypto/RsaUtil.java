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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

/**
 * RSA加密工具类
 *
 * @author saysky
 * @date 2020/2/29 2:56 下午
 */
@Slf4j
public class RsaUtil {

    /**
     * 数字签名，密钥算法
     */
    private static final String RSA_KEY_ALGORITHM = "RSA";

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
    public static byte[] decodeBase64(String key) {
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
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
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
        byte[] priKey = RsaUtil.decodeBase64(privateKey);
        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
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
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
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
        byte[] pubKey = RsaUtil.decodeBase64(publicKey);
        byte[] design = decryptByPubKey(Base64.decodeBase64(data), pubKey);
        return new String(design);
    }

    /**
     * 密钥转成字符串
     *
     * @param key 密钥
     * @return String 密钥转成的字符串
     */
    public static String encodeBase64StringBase64String(byte[] key) {
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
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
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
        byte[] priKey = RsaUtil.decodeBase64(privateKey);
        byte[] enSign = encryptByPriKey(data.getBytes(), priKey);
        return Base64.encodeBase64String(enSign);
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
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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
        byte[] pubKey = RsaUtil.decodeBase64(publicKey);
        byte[] enSign = encryptByPubKey(data.getBytes(), pubKey);
        return Base64.encodeBase64String(enSign);
    }

    /**
     * 生成密钥对
     * 
     * @return 密钥对
     * @throws NoSuchAlgorithmException – if no Provider supports a KeyPairGeneratorSpi implementation for the specified
     *             algorithm
     * @throws NullPointerException – if algorithm is null
     */
    public static Map<String, String> initKey() throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        /**
         * 初始化密钥生成器
         */
        keygen.initialize(KEY_SIZE);
        KeyPair keys = keygen.genKeyPair();

        byte[] pubKey = keys.getPublic().getEncoded();
        String publicKeyString = Base64.encodeBase64String(pubKey);

        byte[] priKey = keys.getPrivate().getEncoded();
        String privateKeyString = Base64.encodeBase64String(priKey);

        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKeyString", publicKeyString);
        keyPairMap.put("privateKeyString", privateKeyString);

        return keyPairMap;
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
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 初始化Signature
        signature.initSign(privateKey);
        // 更新
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
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
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
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

    // /**
    // * 示例
    // * @param args
    // */
    // public static void main(String[] args) {
    // // 使用私钥加密的内容，只能通过公钥来解密
    // // 使用公钥加密的内容，只能通过私钥来解密
    // try {
    // Map<String, String> keyMap = initKey();
    // String publicKeyString = keyMap.get("publicKeyString");
    // String privateKeyString = keyMap.get("privateKeyString");
    // saveKeyForFile("/Users/liuyanzhao/code/FileSystem/Server/src/main/resources/public.key", publicKeyString);
    // saveKeyForFile("/Users/liuyanzhao/code/FileSystem/Server/src/main/resources/private.key", privateKeyString);
    // System.out.println("公钥:" + publicKeyString);
    // System.out.println("私钥:" + privateKeyString);
    //
    // // 待加密数据
    // String data = "admin123";
    // // 私钥加密
    // String encrypt = RSAUtil.encryptByPriKey(data, privateKeyString);
    // // 公钥解密
    // String decrypt = RSAUtil.decryptByPubKey(encrypt, publicKeyString);
    //
    // System.out.println("加密前:" + data);
    // System.out.println("加密后:" + encrypt);
    // System.out.println("解密后:" + decrypt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
