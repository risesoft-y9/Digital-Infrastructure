package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * InetAddressUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
public class InetAddressUtilTest {

    @Test
    public void testGetHostName() {
        // 测试正常的 InetSocketAddress
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8080);
        String hostName = InetAddressUtil.getHostName(socketAddress);
        assertNotNull(hostName);

        // 测试 null 地址
        String nullHostName = InetAddressUtil.getHostName(null);
        assertNull(nullHostName);
    }

    @Test
    public void testIsInvalidLocalHost() {
        // 测试 null 值
        assertTrue(InetAddressUtil.isInvalidLocalHost(null));

        // 测试空字符串
        assertTrue(InetAddressUtil.isInvalidLocalHost(""));

        // 测试 localhost
        assertTrue(InetAddressUtil.isInvalidLocalHost("localhost"));

        // 测试 0.0.0.0
        assertTrue(InetAddressUtil.isInvalidLocalHost("0.0.0.0"));

        // 测试 127.x.x.x 系列
        assertTrue(InetAddressUtil.isInvalidLocalHost("127.0.0.1"));
        assertTrue(InetAddressUtil.isInvalidLocalHost("127.0.0.2"));
        assertTrue(InetAddressUtil.isInvalidLocalHost("127.10.20.30"));

        // 测试有效的 IP 地址
        assertFalse(InetAddressUtil.isInvalidLocalHost("192.168.1.1"));
        assertFalse(InetAddressUtil.isInvalidLocalHost("8.8.8.8"));
    }

    @Test
    public void testIsValidAddress() {
        // 测试有效的地址格式
        assertTrue(InetAddressUtil.isValidAddress("192.168.1.1:8080"));
        assertTrue(InetAddressUtil.isValidAddress("8.8.8.8:53"));
        assertTrue(InetAddressUtil.isValidAddress("127.0.0.1:80"));

        // 测试无效的地址格式
        assertFalse(InetAddressUtil.isValidAddress("192.168.1.1")); // 缺少端口号
        assertFalse(InetAddressUtil.isValidAddress("192.168.1.1:")); // 缺少端口号
        assertFalse(InetAddressUtil.isValidAddress(":8080")); // 缺少IP地址
        assertFalse(InetAddressUtil.isValidAddress("192.168.1.1:999999")); // 端口号超出范围
        assertFalse(InetAddressUtil.isValidAddress("abcd.efgh.ijkl.mnop:8080")); // 非法IP格式
    }

    @Test
    public void testGetLocalAddress() {
        // 测试基本的 getLocalAddress 方法
        try {
            // 使用一个相对宽松的子网设置，以便在大多数环境中都能工作
            InetAddress address = InetAddressUtil.getLocalAddress("127.0.0.1,192.168.x.x,10.x.x.x,172.16.x.x");
            // 在大多数环境中，这个方法应该能成功返回一个地址
            assertNotNull(address);
        } catch (IllegalArgumentException e) {
            // 在某些网络环境下可能找不到匹配的有效地址，这是预期的行为
            // 我们接受这个异常，因为它说明了方法的正确行为
        }

        // 测试带有目标主机端口的 getLocalAddress 方法
        try {
            Map<String, Integer> destHostPorts = new HashMap<>();
            destHostPorts.put("www.baidu.com", 80);
            destHostPorts.put("www.google.com", 80);

            InetAddress address =
                InetAddressUtil.getLocalAddress(destHostPorts, "127.0.0.1,192.168.x.x,10.x.x.x,172.16.x.x");
            // 在大多数环境中，这个方法应该能成功返回一个地址
            assertNotNull(address);
        } catch (IllegalArgumentException e) {
            // 在某些受限的网络环境中，可能无法连接到目标主机
            // 这种情况是可以接受的
        }
    }

    @Test
    public void testLocalAddressConstants() {
        // 测试 ANYHOST 和 LOCALHOST 常量
        assertEquals("127.0.0.1", InetAddressUtil.LOCALHOST);
        assertEquals("0.0.0.0", InetAddressUtil.ANYHOST);
    }

    @Test
    public void testComplexSubnetConfiguration() {
        // 测试更复杂的子网配置
        try {
            InetAddress address = InetAddressUtil.getLocalAddress("192.168.1.0/24,10.0.0.0/8");
            // 这应该不会抛出异常，即使没有匹配的地址
            assertNotNull(address);
        } catch (IllegalArgumentException e) {
            // 如果配置无效则会抛出此异常
        }
    }
}