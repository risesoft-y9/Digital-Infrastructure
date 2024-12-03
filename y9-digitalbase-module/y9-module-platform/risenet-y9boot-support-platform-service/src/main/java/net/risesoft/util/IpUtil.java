package net.risesoft.util;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

/**
 * ip util
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
public class IpUtil {

    public static IPAddress parse(String ip) throws AddressStringException {
        return new IPAddressString(ip).toAddress();
    }

    public static boolean isSubnetContains(String subnet, String ip) {
        return new IPAddressString(subnet).contains(new IPAddressString(ip));
    }

    public static void main(String[] args) {
        IPAddressString str = new IPAddressString("*");
        System.out.println(str.contains(new IPAddressString("192.168.21.100")));
    }
}
