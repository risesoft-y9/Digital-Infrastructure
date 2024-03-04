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
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

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

    private static final String AES_ECB_PADDING = "AES/ECB/PKCS5Padding";

    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * 解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByte(byte[] data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
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
                FileOutputStream out = new FileOutputStream(destFile);) {

                Key k = toKey(Base64.decodeBase64(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
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
     * @param key
     * @throws Exception
     */
    public static OutputStream decryptStream(String key, OutputStream out) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        CipherOutputStream cout = new CipherOutputStream(out, cipher);
        cout.close();
        out.close();
        return cout;
    }

    /**
     * <p>
     * 加密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByte(byte[] data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
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
            try (InputStream in = new FileInputStream(sourceFile); OutputStream out = new FileOutputStream(destFile);) {

                Key k = toKey(Base64.decodeBase64(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
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
     * @throws Exception
     */
    public static InputStream encryptStream(String key, InputStream in) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        CipherInputStream cin = new CipherInputStream(in, cipher);
        cin.close();
        in.close();
        return cin;
    }

    /**
     * <p>
     * 生成随机密钥
     * </p>
     *
     * @return
     * @throws Exception
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
     * @return
     * @throws Exception
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
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

    private AesUtil() {
        throw new IllegalStateException("Utility class");
    }

}