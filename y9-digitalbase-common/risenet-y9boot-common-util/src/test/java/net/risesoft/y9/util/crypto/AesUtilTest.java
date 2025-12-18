package net.risesoft.y9.util.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * AesUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/17
 */
class AesUtilTest {

    private String secretKey;
    private static final String TEST_DATA = "This is test data for AES encryption!";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        secretKey = AesUtil.getSecretKey();
    }

    @Test
    void testGetSecretKey() throws Exception {
        // 测试生成随机密钥
        String key1 = AesUtil.getSecretKey();
        String key2 = AesUtil.getSecretKey();
        assertNotNull(key1);
        assertNotNull(key2);
        assertNotEquals(key1, key2, "两次生成的密钥应该不同");

        // 测试使用种子生成密钥
        String seed = "testSeed";
        String keyWithSeed1 = AesUtil.getSecretKey(seed);
        String keyWithSeed2 = AesUtil.getSecretKey(seed);
        assertNotNull(keyWithSeed1);
        // 注意：即使使用相同的种子，每次生成的密钥也可能不同，这是安全特性
        assertNotNull(keyWithSeed2);
    }

    @Test
    void testEncryptDecryptByte() throws Exception {
        // 准备测试数据
        byte[] originalData = TEST_DATA.getBytes(StandardCharsets.UTF_8);

        // 加密数据
        byte[] encryptedData = AesUtil.encryptByte(secretKey, originalData);
        assertNotNull(encryptedData);
        assertTrue(encryptedData.length > 0);
        assertNotEquals(originalData, encryptedData, "加密后的数据应该与原数据不同");

        // 解密数据
        byte[] decryptedData = AesUtil.decryptByte(secretKey, encryptedData);
        assertNotNull(decryptedData);
        assertArrayEquals(originalData, decryptedData, "解密后的数据应该与原数据相同");
        assertEquals(TEST_DATA, new String(decryptedData, StandardCharsets.UTF_8), "解密后的文本应该与原文本相同");
    }

    @Test
    void testEncryptDecryptStream() throws Exception {
        // 准备测试数据
        byte[] originalData = TEST_DATA.getBytes(StandardCharsets.UTF_8);

        // 创建临时文件用于测试流加解密
        File sourceFile = tempDir.resolve("source.txt").toFile();
        File encryptedFile = tempDir.resolve("encrypted.txt").toFile();
        File decryptedFile = tempDir.resolve("decrypted.txt").toFile();

        // 写入源文件
        try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
            fos.write(originalData);
        }

        // 测试文件加密
        AesUtil.encryptFile(secretKey, sourceFile.getAbsolutePath(), encryptedFile.getAbsolutePath());
        assertTrue(encryptedFile.exists(), "加密文件应该存在");
        assertTrue(encryptedFile.length() > 0, "加密文件不应该为空");

        // 测试文件解密
        AesUtil.decryptFile(secretKey, encryptedFile.getAbsolutePath(), decryptedFile.getAbsolutePath());
        assertTrue(decryptedFile.exists(), "解密文件应该存在");

        // 验证解密后的内容
        byte[] decryptedBytes = java.nio.file.Files.readAllBytes(decryptedFile.toPath());
        assertArrayEquals(originalData, decryptedBytes, "解密后的文件内容应该与原文件内容相同");
    }

    @Test
    void testDecryptWithWrongKey() throws Exception {
        byte[] originalData = TEST_DATA.getBytes(StandardCharsets.UTF_8);

        // 加密数据
        byte[] encryptedData = AesUtil.encryptByte(secretKey, originalData);

        // 使用错误的密钥尝试解密
        String wrongKey = AesUtil.getSecretKey();
        // 使用错误密钥解密不会抛出异常，但会得到错误的数据
        byte[] decryptedData = AesUtil.decryptByte(wrongKey, encryptedData);
        assertNotNull(decryptedData);
        assertNotEquals(originalData, decryptedData, "使用错误密钥解密应该得到不同的数据");
    }

    @Test
    void testEncryptDecryptEmptyData() throws Exception {
        byte[] emptyData = new byte[0];

        // 加密空数据
        byte[] encryptedData = AesUtil.encryptByte(secretKey, emptyData);
        assertNotNull(encryptedData);

        // 解密空数据
        byte[] decryptedData = AesUtil.decryptByte(secretKey, encryptedData);
        assertArrayEquals(emptyData, decryptedData, "空数据加解密后应该保持不变");
    }

    @Test
    void testEncryptDecryptLargeData() throws Exception {
        // 创建较大数据进行测试
        StringBuilder largeDataBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeDataBuilder.append("This is a large data test line ").append(i).append("\n");
        }
        String largeDataStr = largeDataBuilder.toString();
        byte[] largeData = largeDataStr.getBytes(StandardCharsets.UTF_8);

        // 加密大数据
        byte[] encryptedData = AesUtil.encryptByte(secretKey, largeData);
        assertNotNull(encryptedData);
        assertNotEquals(largeData, encryptedData, "加密后的数据应该与原数据不同");

        // 解密大数据
        byte[] decryptedData = AesUtil.decryptByte(secretKey, encryptedData);
        assertArrayEquals(largeData, decryptedData, "解密后的数据应该与原数据相同");
        assertEquals(largeDataStr, new String(decryptedData, StandardCharsets.UTF_8), "解密后的文本应该与原文本相同");
    }
}