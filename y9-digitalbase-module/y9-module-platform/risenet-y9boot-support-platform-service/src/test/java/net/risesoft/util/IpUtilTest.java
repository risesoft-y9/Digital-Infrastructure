package net.risesoft.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;

/**
 * IpUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class IpUtilTest {

    @Test
    void testParseValidIpAddress() throws AddressStringException {
        // 测试 IPv4 地址
        IPAddress ipv4Address = IpUtil.parse("192.168.1.1");
        assertNotNull(ipv4Address);
        assertEquals("192.168.1.1", ipv4Address.toString());

        // 测试 IPv6 地址
        IPAddress ipv6Address = IpUtil.parse("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        assertNotNull(ipv6Address);
        assertEquals("2001:db8:85a3::8a2e:370:7334", ipv6Address.toCanonicalString());
    }

    @Test
    void testParseInvalidIpAddress() {
        // 根据实际行为，IPAddressString不会对无效地址抛出异常，而是在调用toAddress()时才可能抛出
        assertThrows(AddressStringException.class, () -> IpUtil.parse("a.b.c.d"));
        assertThrows(AddressStringException.class, () -> IpUtil.parse("999.999.999.999"));
    }

    @Test
    void testIsSubnetContainsValidIp() {
        // 测试 IPv4 子网包含特定 IP
        assertTrue(IpUtil.isSubnetContains("192.168.1.0/24", "192.168.1.100"));
        assertTrue(IpUtil.isSubnetContains("10.0.0.0/8", "10.0.0.1"));
        assertTrue(IpUtil.isSubnetContains("10.0.0.0/8", "10.255.255.255"));

        // 测试 IPv4 子网不包含特定 IP
        assertFalse(IpUtil.isSubnetContains("192.168.1.0/24", "192.168.2.1"));
        assertFalse(IpUtil.isSubnetContains("172.16.0.0/12", "172.32.0.1"));

        // 测试通配符匹配
        assertTrue(IpUtil.isSubnetContains("*", "192.168.1.1"));
    }

    @Test
    void testEdgeCases() {
        // 测试精确匹配
        assertTrue(IpUtil.isSubnetContains("192.168.1.1", "192.168.1.1"));

        // 测试网络地址和广播地址
        assertTrue(IpUtil.isSubnetContains("192.168.1.0/24", "192.168.1.0")); // 网络地址
        assertTrue(IpUtil.isSubnetContains("192.168.1.0/24", "192.168.1.255")); // 广播地址
    }
}