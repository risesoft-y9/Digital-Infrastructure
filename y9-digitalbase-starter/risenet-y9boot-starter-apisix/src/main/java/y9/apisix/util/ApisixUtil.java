package y9.apisix.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * Apisix工具类
 *
 * @author guoweijun
 *
 */
@Slf4j
public class ApisixUtil {

    private ApisixUtil() {
        throw new IllegalStateException("ApisixUtil class");
    }

    public static String bindRouteToUpstream(String adminAddress, String adminKey, String uri, String upstreamId,
        String routeId, String methodDescription, boolean consumerEnabled, String authenticationType) {
        String result = "";
        String url = adminAddress + "routes/" + routeId;

        Map<String, Object> routeParameterMap = new HashMap<>(16);
        // name
        routeParameterMap.put("name", routeId);

        // desc
        routeParameterMap.put("desc", methodDescription);

        // vars
        // routeParameterMap.put("vars", List.of(List.of("arg_apiDomain", "==", apiDomain)));

        // uri
        routeParameterMap.put("uri", uri);
        // upstream_id
        routeParameterMap.put("upstream_id", upstreamId);

        // plugins
        if (consumerEnabled) {
            routeParameterMap.put("plugins", Map.of(authenticationType, Map.of()));
        }
        String routeParameterJsonStr = Y9JsonUtil.writeValueAsString(routeParameterMap);

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> responseMap = HttpUtil.doPut(url, routeParameterJsonStr, headerMap);
        boolean success = (boolean)responseMap.get("success");
        if (success) {
            result = "create route success, routeId: " + routeId;
        } else {
            result =
                "----create route failed, routeId: " + routeId + ", response: " + responseMap.get("msg").toString();
        }
        LOGGER.info(result);

        return result;
    }

    public static String createService(String adminAddress, String adminKey, String serviceId, String upstreamNodes,
        String upstreamType, String desc) {
        String result = "";
        String url = adminAddress + "services/" + serviceId;

        Map<String, Object> serviceParameterMap = new HashMap<>(2);
        serviceParameterMap.put("name", serviceId);
        serviceParameterMap.put("desc", desc);

        // upstreamNodes
        Map<String, Integer> upstreamNodeMap = new HashMap<>(16);
        if (StringUtils.hasText(upstreamNodes)) {
            String[] upstreamAddrs = upstreamNodes.split(",");
            for (int i = 0; i < upstreamAddrs.length; i++) {
                upstreamNodeMap.put(upstreamAddrs[i], 1);
            }
        } else {
            String host = Y9Context.getHostIp();
            String port = Y9Context.getProperty("server.port", "8080");
            Map<String, Integer> nodeMap = getUpstreamNodeMapByService(adminAddress, adminKey, serviceId);
            upstreamNodeMap.putAll(nodeMap);
            if (!nodeMap.containsKey(host + ":" + port)) {
                upstreamNodeMap.put(host + ":" + port, 1);
            }
        }
        serviceParameterMap.put("nodes", upstreamNodeMap);

        // type
        serviceParameterMap.put("type", upstreamType);
        String serviceParameterJsonStr = Y9JsonUtil.writeValueAsString(serviceParameterMap);

        // headers
        Map<String, String> headerMap = new HashMap<String, String>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> responseMap = HttpUtil.doPut(url, serviceParameterJsonStr, headerMap);
        boolean success = (boolean)responseMap.get("success");
        if (success) {
            result = "create Service success, serviceId:" + serviceId;
        } else {
            result =
                "----create Service failed, serviceId:" + serviceId + ", response:" + responseMap.get("msg").toString();
        }
        LOGGER.info(result);

        return result;
    }

    public static String createUpStream(String adminAddress, String adminKey, String upstreamId, String upstreamNodes,
        String upstreamType, String desc) {
        String result = "";
        String url = adminAddress + "upstreams/" + upstreamId;

        Map<String, Object> upstreamParameterMap = new HashMap<>(2);
        upstreamParameterMap.put("name", upstreamId);
        upstreamParameterMap.put("desc", desc);

        // nodes,采用hash table方式
        Map<String, Integer> upstreamNodeMap = new HashMap<String, Integer>(16);
        if (StringUtils.hasText(upstreamNodes)) {
            String[] upstreamAddrs = upstreamNodes.split(",");
            for (int i = 0; i < upstreamAddrs.length; i++) {
                upstreamNodeMap.put(upstreamAddrs[i], 1);
            }
        } else {
            String host = Y9Context.getHostIp();
            String port = Y9Context.getProperty("server.port", "8080");
            Map<String, Integer> nodeMap = getUpstreamNodeMap(adminAddress, adminKey, upstreamId);
            upstreamNodeMap.putAll(nodeMap);
            if (!nodeMap.containsKey(host + ":" + port)) {
                upstreamNodeMap.put(host + ":" + port, 1);
            }
        }
        upstreamParameterMap.put("nodes", upstreamNodeMap);

        // nodes,采用数组方式
        /**
         * List<Map<String, Object>> nodeList = new ArrayList<>(); if (StringUtils.hasText(upstreamNodes)) { String[]
         * upstreamAddrs = upstreamNodes.split(","); for (int i = 0; i < upstreamAddrs.length; i++) { String[] hostPort
         * = upstreamAddrs[i].split(":"); nodeList.add(Map.of("host", hostPort[0], "port", hostPort[1], "weight", 1)); }
         * } else { String host = InetAddressUtil.getLocalAddress().getHostAddress(); String port =
         * Y9Context.getProperty("server.port", "8080"); boolean exist = false; nodeList =
         * getUpstreamNodeList(adminAddress, adminKey, upstreamId); for (Map<String, Object> nodeMap : nodeList) { if
         * (nodeMap.get("host").equals(host) && nodeMap.get("port").equals(port)) { exist = true; break; } } if (!exist)
         * { nodeList.add(Map.of("host", host, "port", port, "weight", 1)); } } upstreamParameterMap.put("nodes",
         * nodeList);
         **/

        // type
        upstreamParameterMap.put("type", upstreamType);
        String upstreamParameterJsonStr = Y9JsonUtil.writeValueAsString(upstreamParameterMap);

        // headers
        Map<String, String> headerMap = new HashMap<String, String>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> responseMap = HttpUtil.doPut(url, upstreamParameterJsonStr, headerMap);
        boolean success = (boolean)responseMap.get("success");
        if (success) {
            result = "create Upsteam success, upstreamId:" + upstreamId;
        } else {
            result = "----create Upsteam failed, upstreamId:" + upstreamId + ", response:"
                + responseMap.get("msg").toString();
        }
        LOGGER.info(result);

        return result;
    }

    public static List<String> deleteAllRoute(String adminAddress, String adminKey) {
        List<String> list = new ArrayList<>();
        List<String> routeIds = getAllRouteIds(adminAddress, adminKey);
        for (String routeId : routeIds) {
            String result = deleteByRouteId(adminAddress, adminKey, routeId);
            list.add(result);
        }
        return list;
    }

    public static List<String> deleteAllUpstream(String adminAddress, String adminKey) {
        List<String> list = new ArrayList<>();
        List<String> upstreamIds = getAllUpstreamIds(adminAddress, adminKey);
        for (String upstreamId : upstreamIds) {
            String result = deleteByUpstreamId(adminAddress, adminKey, upstreamId);
            list.add(result);
        }
        return list;
    }

    public static String deleteByRouteId(String adminAddress, String adminKey, String routeId) {
        String result = "";
        String url = adminAddress + "routes/" + routeId;

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doDelete(url, headerMap);
        boolean success = (boolean)response.get("success");
        if (success) {
            result = "delete route 成功, routeId: " + routeId;
        } else {
            result = "delete route 失败！, routeId: " + routeId + ", response: " + response.get("msg").toString();
        }
        LOGGER.info(result);

        return result;
    }

    public static String deleteByUpstreamId(String adminAddress, String adminKey, String upstreamId) {
        String result = "";
        String url = adminAddress + "upstreams/" + upstreamId;

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doDelete(url, headerMap);
        boolean success = (boolean)response.get("success");
        if (success) {
            result = "delete upstream 成功, upstreamId: " + upstreamId;
        } else {
            result = "delete upstream 失败！, upstreamId: " + upstreamId + ", response: " + response.get("msg").toString();
        }
        LOGGER.info(result);

        return result;
    }

    public static List<String> getAllRouteIds(String adminAddress, String adminKey) {
        List<String> routeIdList = new ArrayList<>();
        String url = adminAddress + "routes";

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                List<Map<String, Object>> nodeList = (List<Map<String, Object>>)nodeMap.get("nodes");
                if (null != nodeList && !nodeList.isEmpty()) {
                    for (Map<String, Object> map : nodeList) {
                        Map<String, Object> valueMap = (Map<String, Object>)map.get("value");
                        if (null != valueMap) {
                            String id = (String)valueMap.get("id");
                            routeIdList.add(id);
                        }
                    }
                }
            }
        }
        return routeIdList;
    }

    public static List<String> getAllUpstreamIds(String adminAddress, String adminKey) {
        List<String> idList = new ArrayList<>();
        String url = adminAddress + "upstreams";

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                List<Map<String, Object>> nodeList = (List<Map<String, Object>>)nodeMap.get("nodes");
                if (null != nodeList && !nodeList.isEmpty()) {
                    for (Map<String, Object> map : nodeList) {
                        Map<String, Object> valueMap = (Map<String, Object>)map.get("value");
                        if (null != valueMap) {
                            String id = (String)valueMap.get("id");
                            idList.add(id);
                        }
                    }
                }
            }
        }
        return idList;
    }

    public static List<String> getRouteIdsByUpstreamId(String adminAddress, String adminKey, String upstreamId) {
        List<String> routeIdList = new ArrayList<>();
        String url = adminAddress + "routes";

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                List<Map<String, Object>> nodeList = (List<Map<String, Object>>)nodeMap.get("nodes");
                if (null != nodeList && !nodeList.isEmpty()) {
                    for (Map<String, Object> map : nodeList) {
                        Map<String, Object> valueMap = (Map<String, Object>)map.get("value");
                        if (null != valueMap) {
                            String newUpstreamId = (String)valueMap.get("upstream_id");
                            if (null != newUpstreamId && newUpstreamId.equals(upstreamId)) {
                                routeIdList.add((String)valueMap.get("id"));
                            }
                        }
                    }
                }
            }
        }
        return routeIdList;
    }

    public static List<Map<String, Object>> getUpstreamNodeList(String adminAddress, String adminKey,
        String upstreamId) {
        List<Map<String, Object>> returnNodeList = new ArrayList<>();
        String url = adminAddress + "upstreams/" + upstreamId;

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                Map<String, Object> valueMap = (Map<String, Object>)nodeMap.get("value");
                if (valueMap != null) {
                    try {
                        returnNodeList = (List<Map<String, Object>>)valueMap.get("nodes");
                    } catch (Exception e) {
                        returnNodeList = new ArrayList<>();
                        LinkedHashMap<String, Object> nodesMap = (LinkedHashMap<String, Object>)valueMap.get("nodes");
                        if (nodesMap != null && !nodesMap.isEmpty()) {
                            if (nodesMap.get("host") != null) {
                                returnNodeList.add(nodesMap);
                            } else {
                                for (Map.Entry<String, Object> entry : nodesMap.entrySet()) {
                                    String[] hostPort = entry.getKey().split(":");
                                    Map<String, Object> hostPortWeight = new HashMap<>(3);
                                    hostPortWeight.put("host", hostPort[0]);
                                    hostPortWeight.put("port", hostPort[1]);
                                    hostPortWeight.put("weight", entry.getValue());
                                    returnNodeList.add(hostPortWeight);
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnNodeList;
    }

    public static Map<String, Integer> getUpstreamNodeMap(String adminAddress, String adminKey, String upstreamId) {
        Map<String, Integer> returnMap = new HashMap<>(16);
        String url = adminAddress + "upstreams/" + upstreamId;

        // headers
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                Map<String, Object> valueMap = (Map<String, Object>)nodeMap.get("value");
                if (valueMap != null) {
                    try {
                        // 通过 apisix dashboard 添加的Upstream , nodes是数组
                        List<Map<String, Object>> nodeList = (List<Map<String, Object>>)valueMap.get("nodes");
                        if (null != nodeList && !nodeList.isEmpty()) {
                            for (Map<String, Object> map : nodeList) {
                                returnMap.put(map.get("host") + ":" + map.get("port"), (Integer)map.get("weight"));
                            }
                        }
                    } catch (Exception e) {
                        // 通过 apisix Admin API添加的Upstream , nodes是对象
                        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)valueMap.get("nodes");
                        if (map != null && map.size() > 0) {
                            if (map.get("host") != null) {
                                returnMap.put(map.get("host") + ":" + map.get("port"), (Integer)map.get("weight"));
                            } else {
                                map.forEach((key, value) -> {
                                    returnMap.put(key, (Integer)value);
                                });
                            }
                        }
                    }
                }
            }
        }
        return returnMap;
    }

    public static Map<String, Integer> getUpstreamNodeMapByService(String adminAddress, String adminKey,
        String serviceId) {
        Map<String, Integer> returnMap = new HashMap<>(16);
        String url = adminAddress + "services/" + serviceId;

        // header
        Map<String, String> headerMap = new HashMap<>(2);
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("X-API-KEY", adminKey);

        Map<String, Object> response = HttpUtil.doGet(url, null, headerMap, Map.class);
        if (response != null) {
            Map<String, Object> nodeMap = (Map<String, Object>)response.get("node");
            if (nodeMap != null) {
                Map<String, Object> valueMap = (Map<String, Object>)nodeMap.get("value");
                if (valueMap != null) {
                    Map<String, Object> upstreamMap = (Map<String, Object>)valueMap.get("upstream");
                    if (upstreamMap != null) {
                        try {
                            // 通过 apisix dashboard 添加的Service,nodes是数组
                            List<Map<String, Object>> nodeList = (List<Map<String, Object>>)upstreamMap.get("nodes");
                            if (null != nodeList && !nodeList.isEmpty()) {
                                for (Map<String, Object> map : nodeList) {
                                    returnMap.put(map.get("host") + ":" + map.get("port"), (Integer)map.get("weight"));
                                }
                            }
                        } catch (Exception e) {
                            // 通过 apisix Admin API添加的Service,nodes是对象
                            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)upstreamMap.get("nodes");
                            if (map != null && map.size() > 0) {
                                map.forEach((key, value) -> {
                                    returnMap.put(key, (Integer)value);
                                });
                            }
                        }
                    }
                }
            }
        }
        return returnMap;
    }

}
