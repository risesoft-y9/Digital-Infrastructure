package y9.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InetAddressUtil {

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static volatile InetAddress LOCAL_ADDRESS = null;

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    /*
     * A类网络 ip地址范围:10.0.0.0~10.255.255.255     ip地址数量:16777216    网络数:1个A类网络
     * B类网络 ip地址范围:172.16.0.0~172.31.255.255   ip地址数量:1048576     网络数:16个B类网络
     * C类网络 ip地址范围:192.168.0.0~192.168.255.255 ip地址数量:65536       网络数:256个C类网络
     */

    // return ip to avoid lookup dns
    public static String getHostName(SocketAddress socketAddress) {
        if (socketAddress == null) {
            return null;
        }

        if (socketAddress instanceof InetSocketAddress) {
            InetAddress addr = ((InetSocketAddress)socketAddress).getAddress();
            if (addr != null) {
                return addr.getHostAddress();
            }
        }

        return null;
    }

    /**
     * 获取服务器地址
     *
     * @param validSubnets 有效的子网段，多个可用英文逗号,分隔
     * @return InetAddress IP地址
     */
    public static InetAddress getLocalAddress(String validSubnets) {
        return getLocalAddress(null, validSubnets);
    }

    /**
     * <pre>
     * 查找策略：首先看是否已经查到ip --> hostname对应的ip --> 根据连接目标端口得到的本地ip --> 轮询网卡
     * </pre>
     * 
     * @return loca ip
     */
    public static InetAddress getLocalAddress(Map<String, Integer> destHostPorts, String validSubnets) {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }

        InetAddress localAddress = getLocalAddressByHostname();
        if (isValidAddress(localAddress, validSubnets)) {
            return localAddress;
        }

        localAddress = getLocalAddressBySocket(destHostPorts);
        if (isValidAddress(localAddress, validSubnets)) {
            return localAddress;
        }

        localAddress = getLocalAddressByNetworkInterface(validSubnets);
        if (isValidAddress(localAddress, validSubnets)) {
            LOCAL_ADDRESS = localAddress;
            return localAddress;
        }

        throw new IllegalArgumentException("invalid subnets, check your configuration");
    }

    private static InetAddress getLocalAddressByHostname() {
        try {
            return InetAddress.getLocalHost();
        } catch (Throwable e) {
            LOGGER.warn("Failed to retrieve local address by hostname:" + e);
        }
        return null;
    }

    private static InetAddress getLocalAddressByNetworkInterface(String validSubnets) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address, validSubnets)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                LOGGER.warn("Failed to retrieve ip address", e);
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.warn("Failed to retrieve ip address", e);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("Failed to retrieve ip address", e);
        }
        return null;
    }

    private static InetAddress getLocalAddressBySocket(Map<String, Integer> destHostPorts) {
        if (destHostPorts == null || destHostPorts.size() == 0) {
            return null;
        }

        for (Map.Entry<String, Integer> entry : destHostPorts.entrySet()) {
            String host = entry.getKey();
            int port = entry.getValue();
            try (Socket socket = new Socket()) {
                SocketAddress addr = new InetSocketAddress(host, port);
                socket.connect(addr, 1000);
                return socket.getLocalAddress();
            } catch (Exception e) {
                LOGGER.warn(String.format(
                    "Failed to retrieve local address by connecting to dest host:port(%s:%s) false", host, port), e);
            }
        }
        return null;
    }

    public static boolean isInvalidLocalHost(String host) {
        return host == null || host.length() == 0 || "localhost".equalsIgnoreCase(host) || "0.0.0.0".equals(host)
            || (LOCAL_IP_PATTERN.matcher(host).matches());
    }

    public static boolean isValidAddress(InetAddress address, String validSubnets) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String ipAddress = address.getHostAddress();
        boolean valid = ipAddress != null && !ANYHOST.equals(ipAddress) && !LOCALHOST.equals(ipAddress)
            && IP_PATTERN.matcher(ipAddress).matches();
        if (valid == false) {
            return false;
        }

        if (StringUtils.hasText(validSubnets)) {
            valid = false;
            String[] arry = validSubnets.toLowerCase().split(",");
            for (String ip : arry) {
                String ipSegment = ip;
                int i = ip.trim().indexOf(".x");
                if (i > -1) {
                    ipSegment = ip.substring(0, i);
                }
                if (ipAddress.contains(ipSegment)) {
                    valid = true;
                    break;
                }
            }
            return valid;
        }
        return true;
    }

    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

}
