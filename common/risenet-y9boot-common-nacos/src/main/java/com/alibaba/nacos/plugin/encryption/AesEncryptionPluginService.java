package com.alibaba.nacos.plugin.encryption;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.nacos.common.codec.Base64;
import com.alibaba.nacos.plugin.encryption.spi.EncryptionPluginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesEncryptionPluginService implements EncryptionPluginService {
    
    public static final String AES_NAME = "aes";

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";

    private static final String IV_PARAMETER = "fa6fa5207b3286b2";

    private static final String DEFAULT_SECRET_KEY = "nacos6b31e19f931a7603ae5473250b4";

    private static final int IV_LENGTH = 16;

    @Override
    public String encrypt(String secretKey, String content) {
        if (StringUtils.isBlank(secretKey)) {
            return content;
        }
        try {
            secretKey = new String(Base64.decodeBase64(secretKey.getBytes(StandardCharsets.UTF_8)));
            Key key = new SecretKeySpec(Hex.decodeHex(secretKey.toCharArray()), AES_NAME);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key, generateIv(secretKey));
            byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            LOGGER.error("[AesEncryptionPluginService] encrypt error", e);
        }
        return content;
    }

    @Override
    public String decrypt(String secretKey, String content) {
        if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(content)) {
            return content;
        }
        try {
            secretKey = new String(Base64.decodeBase64(secretKey.getBytes(StandardCharsets.UTF_8)));
            Key key = new SecretKeySpec(Hex.decodeHex(secretKey.toCharArray()), AES_NAME);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, key, generateIv(secretKey));
            byte[] result = cipher.doFinal(Hex.decodeHex(content.toCharArray()));
            return new String(result);
        } catch (Exception e) {
            LOGGER.error("[AesEncryptionPluginService] decrypt error", e);
        }
        return content;
    }

    @Override
    public String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_NAME);
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            String key = Hex.encodeHexString(byteKey);
            return new String(Base64.encodeBase64(key.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.error("[AesEncryptionPluginService] generate key error", e);
        }
        return DEFAULT_SECRET_KEY;
    }

    /**
     * IV initial vector size is 16 bytes, take the first 16 bytes of secret Key.
     *
     * @param secretKey secretKey
     * @return IvParameterSpec
     */
    private IvParameterSpec generateIv(String secretKey) {
        if (StringUtils.isBlank(secretKey) || secretKey.length() < IV_LENGTH) {
            new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
        }
        String iv = secretKey.substring(0, IV_LENGTH);
        return new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String algorithmName() {
        return AES_NAME;
    }

    @Override
    public String encryptSecretKey(String secretKey) {
        // todo
        return secretKey;
    }

    @Override
    public String decryptSecretKey(String secretKey) {
        // todo
        return secretKey;
    }
}
