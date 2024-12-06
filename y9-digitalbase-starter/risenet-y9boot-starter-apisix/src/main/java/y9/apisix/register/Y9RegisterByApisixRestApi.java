package y9.apisix.register;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.apisix.Y9ApisixProperties;

import y9.apisix.util.ApisixUtil;
import y9.apisix.util.EtcdUtil;
import y9.apisix.util.MD5;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;

/**
 * Apisix Rest服务注册
 *
 * @author guoweijun
 *
 */
@Slf4j
public class Y9RegisterByApisixRestApi {

    String contextPath;
    String apiBasePackages;
    // String apiDomain;
    String apiVersion;
    String adminKey;
    String adminAddress;
    String upstreamNodes;
    String upstreamType;
    String etcdAddress;
    String upstreamId;
    boolean consumerEnabled;
    String authenticationType;
    private BuildProperties buildProperties;

    @Autowired
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    private String createUpStream() {
        return ApisixUtil.createUpStream(adminAddress, adminKey, upstreamId, upstreamNodes, upstreamType,
            "api版本：" + apiVersion);
    }

    public List<String> deleteAllRoute() {
        return ApisixUtil.deleteAllRoute(adminAddress, adminKey);
    }

    public List<String> deleteAllUpstream() {
        return ApisixUtil.deleteAllUpstream(adminAddress, adminKey);
    }

    private void deleteUselessApi(List<String> currentRouteIdsList, List<String> resultList) {
        List<String> previousRouteIdList = ApisixUtil.getRouteIdsByUpstreamId(adminAddress, adminKey, upstreamId);
        previousRouteIdList.removeAll(currentRouteIdsList);
        for (String routeId : previousRouteIdList) {
            String result = ApisixUtil.deleteByRouteId(adminAddress, adminKey, routeId);
            resultList.add(result);
        }
    }

    private void deleteUselessApiFromEtcd(List<String> currentRouteIdsList, List<String> resultList) {
        List<String> previousRouteIdList = new ArrayList<>();
        String keyPrefix = "/apisix/routes/" + upstreamId;
        try {
            previousRouteIdList = EtcdUtil.getKeyPrefix(keyPrefix, etcdAddress);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        previousRouteIdList.removeAll(currentRouteIdsList);
        for (String routeId : previousRouteIdList) {
            String result = ApisixUtil.deleteByRouteId(adminAddress, adminKey, routeId);
            resultList.add(result);
        }
    }

    @PostConstruct
    public void init() {
        Y9ApisixProperties y9ApisixProperties = Y9Context.getBean(Y9ApisixProperties.class);

        contextPath = Y9Context.getProperty("server.servlet.contextPath");
        if (contextPath == null) {
            contextPath = Y9Context.getProperty("spring.application.name");
            if (contextPath == null) {
                contextPath = Y9Context.getProperty("contextPath");
                if (contextPath == null) {
                    contextPath = "testApp";
                }
            }
        }

        apiVersion = y9ApisixProperties.getApiVersion();
        if (!StringUtils.hasText(apiVersion)) {
            if (buildProperties != null) {
                apiVersion = buildProperties.getVersion();
            }
        }
        if (apiVersion == null) {
            apiVersion = "v9_5_0";
        }
        apiVersion = apiVersion.replace(".", "_");

        upstreamId = (contextPath + "_api").replace(".", "_");

        adminKey = y9ApisixProperties.getAdminKey();
        adminAddress = y9ApisixProperties.getAdminAddress();
        upstreamNodes = y9ApisixProperties.getUpstreamNodes();
        apiBasePackages = y9ApisixProperties.getApiBasePackages();

        upstreamType = y9ApisixProperties.getUpstreamType();
        etcdAddress = y9ApisixProperties.getEtcdAddress();

        consumerEnabled = y9ApisixProperties.isConsumerEnabled();
        authenticationType = y9ApisixProperties.getAuthenticationType();
    }

    public List<String> registerApiToApisix() {
        List<String> resultList = new ArrayList<>();
        List<String> routeIdsList = new ArrayList<>();

        String result = this.createUpStream();
        if (result.contains("create Upsteam failed")) {
            resultList.add(result);
        } else {
            ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(apiBasePackages.split(",")).scan();
            restApiRegister(routeIdsList, resultList, scanResult);
            deleteUselessApi(routeIdsList, resultList);
            // deleteUselessApiFromEtcd(routeIdsList, resultList);
        }
        return resultList;
    }

    private void restApiRegister(List<String> routeIdsList, List<String> resultList, ScanResult scanResult) {
        try {
            ClassInfoList classInfoList =
                scanResult.getClassesWithAnnotation("org.springframework.web.bind.annotation.RequestMapping");
            for (ClassInfo classInfo : classInfoList) {
                AnnotationInfo classAnnotationInfo = classInfo.getAnnotationInfo("y9.apisix.annotation.NoApiClass");
                if (null != classAnnotationInfo) {
                    continue;
                }

                AnnotationInfo classRequestMappingAnnotationInfo =
                    classInfo.getAnnotationInfo("org.springframework.web.bind.annotation.RequestMapping");
                Map<String, AnnotationParameterValue> requestMappingAnnotationValueMap =
                    classRequestMappingAnnotationInfo.getParameterValues().asMap();
                String[] classRequestMappingAnnotationValues =
                    (String[])requestMappingAnnotationValueMap.get("value").getValue();
                MethodInfoList methodInfoList = classInfo.getMethodInfo();

                for (String classRequestMappingAnnotationValue : classRequestMappingAnnotationValues) {
                    for (MethodInfo methodInfo : methodInfoList) {
                        String methodDescription = methodInfo.toStringWithSimpleNames();
                        int i = methodDescription.indexOf(" public ");
                        if (i > -1) {
                            // methodDescription不能长于255个字符，否则bindUpstreamToRoute出错。
                            methodDescription = methodDescription.substring(i + 1);
                            methodDescription =
                                methodDescription.substring(0, Math.min(methodDescription.length(), 255));
                        }

                        AnnotationInfo methodAnnotationInfo =
                            methodInfo.getAnnotationInfo("y9.apisix.annotation.NoApiMethod");
                        if (null != methodAnnotationInfo) {
                            continue;
                        }

                        AnnotationInfo getMappingAnnotationInfo =
                            methodInfo.getAnnotationInfo("org.springframework.web.bind.annotation.GetMapping");
                        if (null != getMappingAnnotationInfo) {
                            String[] getMappingAnnotationValues =
                                (String[])getMappingAnnotationInfo.getParameterValues().asMap().get("value").getValue();

                            for (String getMappingAnnotationValue : getMappingAnnotationValues) {
                                getMappingAnnotationValue = getMappingAnnotationValue.contains("{")
                                    ? getMappingAnnotationValue.substring(0, getMappingAnnotationValue.indexOf("{"))
                                        + "*"
                                    : getMappingAnnotationValue;

                                String uri =
                                    "/" + contextPath + classRequestMappingAnnotationValue + getMappingAnnotationValue;
                                String routeId = upstreamId + uri.replaceAll("/", "_").replaceAll("\\*", ".");

                                if (routeId.length() > 64) {
                                    routeId = MD5.hash(routeId, StandardCharsets.UTF_8);
                                }

                                routeIdsList.add(routeId);
                                String result = ApisixUtil.bindRouteToUpstream(adminAddress, adminKey, uri, upstreamId,
                                    routeId, methodDescription, consumerEnabled, authenticationType);
                                resultList.add(result);
                            }
                        }

                        AnnotationInfo postMappingAnnotationInfo =
                            methodInfo.getAnnotationInfo("org.springframework.web.bind.annotation.PostMapping");
                        if (null != postMappingAnnotationInfo) {
                            String[] postMappingAnnotationValues =
                                (String[])postMappingAnnotationInfo.getParameterValues().get("value").getValue();

                            for (String postMappingAnnotationValue : postMappingAnnotationValues) {
                                postMappingAnnotationValue = postMappingAnnotationValue.contains("{")
                                    ? postMappingAnnotationValue.subSequence(0, postMappingAnnotationValue.indexOf("{"))
                                        + "*"
                                    : postMappingAnnotationValue;

                                String uri =
                                    "/" + contextPath + classRequestMappingAnnotationValue + postMappingAnnotationValue;
                                String routeId = upstreamId + uri.replaceAll("/", "_").replaceAll("\\*", ".");

                                if (routeId.length() > 64) {
                                    routeId = MD5.hash(routeId, StandardCharsets.UTF_8);
                                }

                                routeIdsList.add(routeId);
                                String result = ApisixUtil.bindRouteToUpstream(adminAddress, adminKey, uri, upstreamId,
                                    routeId, methodDescription, consumerEnabled, authenticationType);
                                resultList.add(result);
                            }
                        }

                        AnnotationInfo methodRequestMappingAnnotationInfo =
                            methodInfo.getAnnotationInfo("org.springframework.web.bind.annotation.RequestMapping");
                        if (null != methodRequestMappingAnnotationInfo) {
                            String[] methodRequestMappingAnnotationValues = (String[])methodRequestMappingAnnotationInfo
                                .getParameterValues().asMap().get("value").getValue();

                            for (String methodRequestMappingAnnotationValue : methodRequestMappingAnnotationValues) {
                                methodRequestMappingAnnotationValue = methodRequestMappingAnnotationValue.contains("{")
                                    ? methodRequestMappingAnnotationValue.subSequence(0,
                                        methodRequestMappingAnnotationValue.indexOf("{")) + "*"
                                    : methodRequestMappingAnnotationValue;
                                String uri = "/" + contextPath + classRequestMappingAnnotationValue
                                    + methodRequestMappingAnnotationValue;
                                String routeId = upstreamId + uri.replaceAll("/", "_").replaceAll("\\*", ".");

                                if (routeId.length() > 64) {
                                    routeId = MD5.hash(routeId, StandardCharsets.UTF_8);
                                }

                                routeIdsList.add(routeId);

                                String result = ApisixUtil.bindRouteToUpstream(adminAddress, adminKey, uri, upstreamId,
                                    routeId, methodDescription, consumerEnabled, authenticationType);
                                resultList.add(result);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            resultList.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
    }
}
