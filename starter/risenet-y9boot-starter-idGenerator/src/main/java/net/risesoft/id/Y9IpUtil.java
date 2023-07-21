 package net.risesoft.id;

 import java.net.Inet6Address;
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.net.SocketException;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.List;

 import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9IpUtil {
     public static String getIp() {
         String ip = "";
         try {
             List<String> ipList = getHostAddress(null);
             // default the first
             ip = (!ipList.isEmpty()) ? ipList.get(0) : "";
         } catch (Exception ex) {
             LOGGER.warn("exception in getIp", ex);
         }
         return ip;
     }

     /**
      * 获取已激活网卡的IP地址
      *
      * @param interfaceName 可指定网卡名称,null则获取全部
      * @return List<String>
      */
     public static List<String> getHostAddress(String interfaceName) throws SocketException {
         List<String> ipList = new ArrayList<String>(5);
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
         while (interfaces.hasMoreElements()) {
             NetworkInterface ni = interfaces.nextElement();
             Enumeration<InetAddress> allAddress = ni.getInetAddresses();
             while (allAddress.hasMoreElements()) {
                 InetAddress address = allAddress.nextElement();
                 if (address.isLoopbackAddress()) {
                     // skip the loopback addr
                     continue;
                 }
                 if (address instanceof Inet6Address) {
                     // skip the IPv6 addr
                     continue;
                 }
                 String hostAddress = address.getHostAddress();
                 if (null == interfaceName) {
                     ipList.add(hostAddress);
                 } else if (interfaceName.equals(ni.getDisplayName())) {
                     ipList.add(hostAddress);
                 }
             }
         }
         return ipList;
     }

}
