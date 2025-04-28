package net.risesoft.y9.util.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * AES加密解密工具包
 * </p>
 *
 * @author IceWee
 * @version 1.0
 * @date 2012-5-18
 */
@Slf4j
public class AesUtil {
    private static final String ALGORITHM = "AES";

    // 不使用有填充的模式，填充模式有可能会导致解密后数据跟加密前数据不一致
    private static final String AES_CTR_NOPADDING = "AES/CTR/NoPadding";
    // 128位 初始化向量
    private static final String IV = "3f7e2c9b8d4e6a1f432b5c7d8e9a0b1c";

    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;

    private AesUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * <p>
     * 解密
     * </p>
     *
     * @param data 数据
     * @param key 解密key
     * @return byte[] 解密后的数据
     * @throws Exception 异常
     */
    public static byte[] decryptByte(byte[] data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(Hex.decodeHex(IV.toCharArray())));
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key 解密key
     * @param sourceFilePath 目标文件路径
     * @param destFilePath 完成后的文件路径
     * @throws Exception 异常
     */
    public static void decryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            CipherOutputStream cout = null;
            try (FileInputStream in = new FileInputStream(sourceFile);
                FileOutputStream out = new FileOutputStream(destFile)) {

                Key k = toKey(Base64.decodeBase64(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (cout != null) {
                    try {
                        cout.close();
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }

                }
            }
        }
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key 解密key
     * @param out 输出流
     * @return OutputStream 输出流
     * @throws Exception 异常
     */
    public static OutputStream decryptStream(String key, OutputStream out) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(Hex.decodeHex(IV.toCharArray())));
        return new CipherOutputStream(out, cipher);
    }

    /**
     * <p>
     * 加密
     * </p>
     *
     * @param data 数据
     * @param key 加密key
     * @return byte[] 加密后数据
     * @throws Exception 异常信息
     */
    public static byte[] encryptByte(byte[] data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key 加密key
     * @param sourceFilePath 目标文件路径
     * @param destFilePath 加密后文件路径
     * @throws Exception 异常
     */
    public static void encryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();

            CipherInputStream cin = null;
            try (InputStream in = new FileInputStream(sourceFile); OutputStream out = new FileOutputStream(destFile)) {

                Key k = toKey(Base64.decodeBase64(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                cin = new CipherInputStream(in, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = cin.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }

            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                if (cin != null) {
                    try {
                        cin.close();
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }

                }
            }

        }
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key AES密钥
     * @param in 输入流
     * @return InputStream 加密后的输入流
     * @throws Exception 异常
     */
    public static InputStream encryptStream(String key, InputStream in) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(Hex.decodeHex(IV.toCharArray())));
        return new CipherInputStream(in, cipher);
    }

    /**
     * <p>
     * 生成随机密钥
     * </p>
     *
     * @return String 随机密钥
     * @throws Exception 异常
     */
    public static String getSecretKey() throws Exception {
        return getSecretKey(null);
    }

    /**
     * <p>
     * 生成密钥
     * </p>
     *
     * @param seed 密钥种子
     * @return String 密钥
     * @throws Exception 异常
     */
    public static String getSecretKey(String seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom;
        if (seed != null && !"".equals(seed)) {
            secureRandom = new SecureRandom(seed.getBytes());
        } else {
            secureRandom = new SecureRandom();
        }
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.encodeBase64String(secretKey.getEncoded());
    }

    /**
     * <p>
     * 转换密钥
     * </p>
     *
     * @param key 加密key
     * @return String 密钥
     * @throws Exception 异常
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

}