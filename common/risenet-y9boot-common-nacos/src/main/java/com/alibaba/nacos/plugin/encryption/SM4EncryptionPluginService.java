package com.alibaba.nacos.plugin.encryption;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.nacos.plugin.encryption.spi.EncryptionPluginService;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

@Slf4j
public class SM4EncryptionPluginService implements EncryptionPluginService {

    private static final String DEFAULT_SECRET_KEY = "LENGLENGLENGLENG";

    @Override
    public String encrypt(String secretKey, String content) {
        if (StringUtils.isBlank(secretKey)) {
            return content;
        }
        try {
            SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey.getBytes(StandardCharsets.UTF_8));
            return sm4.encryptHex(content);
        } catch (Exception e) {
            LOGGER.error("[SM4EncryptionPluginService] encrypt error", e);
        }
        return content;
    }

    @Override
    public String decrypt(String secretKey, String content) {
        if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(content)) {
            return content;
        }
        try {
            SymmetricCrypto sm4 = new SymmetricCrypto("SM4", secretKey.getBytes(StandardCharsets.UTF_8));
            return sm4.decryptStr(content);
        } catch (Exception e) {
            LOGGER.error("[SM4EncryptionPluginService] decrypt error", e);
        }
        return content;
    }

    @Override
    public String generateSecretKey() {
        // return DEFAULT_SECRET_KEY;
        return RandomUtil.randomString(32);
    }

    @Override
    public String algorithmName() {
        return "sm4";
    }

    @Override
    public String encryptSecretKey(String secretKey) {
        return secretKey;
    }

    @Override
    public String decryptSecretKey(String secretKey) {
        return secretKey;
    }
}
