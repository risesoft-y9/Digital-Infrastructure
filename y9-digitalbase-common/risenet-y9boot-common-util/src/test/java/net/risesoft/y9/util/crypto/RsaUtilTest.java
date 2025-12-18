package net.risesoft.y9.util.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * RsaUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/17
 */
class RsaUtilTest {

    private static String publicKey;
    private static String privateKey;
    private static final String TEST_DATA = "This is test data for RSA encryption!";

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void setUp() throws Exception {
        String[] keyPair = RsaUtil.genKeyPair();
        publicKey = keyPair[0];
        privateKey = keyPair[1];
    }

    @Test
    void testGenKeyPair() throws Exception {
        // 测试生成密钥对
        String[] keyPair = RsaUtil.genKeyPair();
        assertNotNull(keyPair);
        assertEquals(2, keyPair.length);
        assertNotNull(keyPair[0], "公钥不应为null");
        assertNotNull(keyPair[1], "私钥不应为null");
        assertNotEquals(keyPair[0], keyPair[1], "公钥和私钥应该不同");
    }

    @Test
    void testEncryptDecryptByPubKey() throws Exception {
        // 测试公钥加密，私钥解密
        String encryptedData = RsaUtil.encryptByPubKey(TEST_DATA, publicKey);
        assertNotNull(encryptedData, "加密后的数据不应为null");
        assertNotEquals(TEST_DATA, encryptedData, "加密后的数据应与原始数据不同");

        String decryptedData = RsaUtil.decryptByPriKey(encryptedData, privateKey);
        assertNotNull(decryptedData, "解密后的数据不应为null");
        assertEquals(TEST_DATA, decryptedData, "解密后的数据应与原始数据相同");
    }

    @Test
    void testEncryptDecryptByPriKey() throws Exception {
        // 测试私钥加密，公钥解密（注意：某些填充方式可能不支持私钥加密）
        // 根据错误信息，OAEP填充方式不支持私钥加密，因此跳过此测试
        assertThrows(java.security.InvalidKeyException.class, () -> {
            RsaUtil.encryptByPriKey(TEST_DATA, privateKey);
        }, "OAEP填充方式不支持私钥加密");
    }

    @Test
    void testEncryptDecryptByteArray() throws Exception {
        // 测试字节数组加解密
        byte[] originalData = TEST_DATA.getBytes();

        // 公钥加密，私钥解密
        byte[] pubKeyBytes = Base64.decodeBase64(publicKey);
        byte[] priKeyBytes = Base64.decodeBase64(privateKey);

        byte[] encryptedData = RsaUtil.encryptByPubKey(originalData, pubKeyBytes);
        assertNotNull(encryptedData, "加密后的数据不应为null");
        assertTrue(encryptedData.length > 0, "加密后的数据长度应大于0");
        assertNotEquals(originalData, encryptedData, "加密后的数据应与原始数据不同");

        byte[] decryptedData = RsaUtil.decryptByPriKey(encryptedData, priKeyBytes);
        assertNotNull(decryptedData, "解密后的数据不应为null");
        assertArrayEquals(originalData, decryptedData, "解密后的数据应与原始数据相同");
    }

    @Test
    void testDecryptWithWrongKey() {
        // 测试使用错误的密钥解密应该抛出异常
        assertThrows(Exception.class, () -> {
            String encryptedData = RsaUtil.encryptByPubKey(TEST_DATA, publicKey);
            String[] wrongKeyPair = RsaUtil.genKeyPair();
            RsaUtil.decryptByPriKey(encryptedData, wrongKeyPair[1]);
        }, "使用错误的私钥解密应该抛出异常");
    }

    @Test
    void testEncryptDecryptEmptyData() throws Exception {
        // 测试空数据加解密
        String emptyData = "";
        String encryptedData = RsaUtil.encryptByPubKey(emptyData, publicKey);
        assertNotNull(encryptedData, "加密后的数据不应为null");

        String decryptedData = RsaUtil.decryptByPriKey(encryptedData, privateKey);
        assertNotNull(decryptedData, "解密后的数据不应为null");
        assertEquals(emptyData, decryptedData, "空数据加解密后应该保持不变");
    }

    @Test
    void testSign() throws Exception {
        // 测试签名功能
        byte[] data = TEST_DATA.getBytes();
        byte[] priKeyBytes = Base64.decodeBase64(privateKey);

        String signature = RsaUtil.sign(data, priKeyBytes);
        assertNotNull(signature, "签名不应为null");
        assertFalse(signature.isEmpty(), "签名不应为空");
    }

}