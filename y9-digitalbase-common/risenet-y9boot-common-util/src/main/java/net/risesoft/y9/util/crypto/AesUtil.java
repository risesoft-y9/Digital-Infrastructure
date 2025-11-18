package net.risesoft.y9.util.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
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
import org.apache.commons.io.IOUtils;

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

    private static final int KEY_BIT_NUMBER = 128;
    private static final int IV_BYTE_NUMBER = 16;

    private AesUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * <p>
     * 解密
     * </p>
     *
     * @param key 解密key
     * @param data 数据
     * @return byte[] 解密后的数据
     * @throws Exception 异常
     */
    public static byte[] decryptByte(String key, byte[] data) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStream outputStream = decryptStream(key, byteArrayOutputStream)) {
            IOUtils.write(data, outputStream);
            return byteArrayOutputStream.toByteArray();
        }
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

            try (FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                OutputStream outputStream = decryptStream(key, fileOutputStream);
                FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
                IOUtils.copy(fileInputStream, outputStream);
            }
        }
    }

    /**
     * <p>
     * 文件解密，密文 = IV + 加密结果
     * </p>
     *
     * @param key 解密key
     * @param out 输出流
     * @return OutputStream 输出流
     */
    public static OutputStream decryptStream(String key, OutputStream out) {
        return new FilterOutputStream(out) {

            private int bytesRead = 0;
            private final byte[] iv = new byte[IV_BYTE_NUMBER];
            private CipherOutputStream cipherOut;
            private boolean initialized = false;

            private void initCipher() throws Exception {
                Key k = toKey(Base64.decodeBase64(key));
                SecretKeySpec secretKeySpec = new SecretKeySpec(k.getEncoded(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

                cipherOut = new CipherOutputStream(out, cipher);
                initialized = true;
            }

            @Override
            public void write(int b) throws IOException {
                try {
                    if (!initialized) {
                        if (bytesRead < IV_BYTE_NUMBER) {
                            iv[bytesRead++] = (byte)b;
                            if (bytesRead == IV_BYTE_NUMBER) {
                                initCipher();
                            }
                            return;
                        }
                    }
                    cipherOut.write(b);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                try {
                    while (len > 0) {
                        if (!initialized) {
                            while (bytesRead < IV_BYTE_NUMBER && len > 0) {
                                iv[bytesRead++] = b[off++];
                                len--;
                            }
                            if (bytesRead == IV_BYTE_NUMBER) {
                                initCipher();
                            }
                            if (len == 0)
                                return;
                        }
                        cipherOut.write(b, off, len);
                        return;
                    }
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }

            @Override
            public void flush() throws IOException {
                if (cipherOut != null)
                    cipherOut.flush();
                super.flush();
            }

            @Override
            public void close() throws IOException {
                if (cipherOut != null)
                    cipherOut.close();
                super.close();
            }
        };
    }

    /**
     * <p>
     * 加密
     * </p>
     *
     * @param key 加密key
     * @param data 数据
     * @return byte[] 加密后数据
     * @throws Exception 异常信息
     */
    public static byte[] encryptByte(String key, byte[] data) throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            InputStream inputStream = encryptStream(key, byteArrayInputStream)) {
            return IOUtils.toByteArray(inputStream);
        }
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

            try (FileInputStream fileInputStream = new FileInputStream(sourceFile);
                InputStream inputStream = encryptStream(key, fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(destFile)) {
                IOUtils.copy(inputStream, fileOutputStream);
            }
        }
    }

    /**
     * <p>
     * 文件加密，密文 = IV + 加密结果
     * </p>
     *
     * @param key AES密钥
     * @param in 输入流
     * @return InputStream 加密后的输入流
     * @throws Exception 异常
     */
    public static InputStream encryptStream(String key, InputStream in) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] iv = new SecureRandom().generateSeed(IV_BYTE_NUMBER);

        SecretKeySpec secretKeySpec = new SecretKeySpec(k.getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CTR_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        ByteArrayInputStream ivByteArrayInputStream = new ByteArrayInputStream(iv);
        CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
        return new SequenceInputStream(ivByteArrayInputStream, cipherInputStream);
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
        if (seed != null && !seed.isEmpty()) {
            secureRandom = new SecureRandom(seed.getBytes());
        } else {
            secureRandom = new SecureRandom();
        }
        keyGenerator.init(KEY_BIT_NUMBER, secureRandom);
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
     */
    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, ALGORITHM);
    }
}