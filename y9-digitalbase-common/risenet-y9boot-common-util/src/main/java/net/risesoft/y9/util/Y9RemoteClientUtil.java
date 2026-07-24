package net.risesoft.y9.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9DateFormat;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 远程调用工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.11
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9RemoteClientUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final CloseableHttpClient HTTP_CLIENT;

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setDateFormat(new Y9DateFormat());

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        connectionManager.setValidateAfterInactivity(30 * 1000);

        RequestConfig defaultConfig = RequestConfig.custom()
            .setConnectTimeout(30000)
            .setSocketTimeout(30000)
            .setConnectionRequestTimeout(10000)
            .build();

        HTTP_CLIENT =
            HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(defaultConfig).build();
    }

    /**
     * 拼接URL参数，失败返回false
     */
    private static void appendUrlParams(HttpRequestBase httpMethod, List<NameValuePair> urlParams) {
        if (urlParams != null && !urlParams.isEmpty()) {
            try {
                URIBuilder uriBuilder = new URIBuilder(httpMethod.getURI());
                for (NameValuePair p : urlParams) {
                    String encodedValue = URLEncoder.encode(p.getValue(), StandardCharsets.UTF_8);
                    uriBuilder.addParameter(p.getName(), encodedValue);
                }
                httpMethod.setURI(uriBuilder.build());

            } catch (Exception e) {
                LOGGER.error("拼接URL参数失败", e);
            }
        }
    }

    /**
     * 处理RequestBody参数
     * 
     * @param httpMethod
     * @param requestBody
     * @param contentType
     */
    private static void setRequestBody(HttpRequestBase httpMethod, String requestBody, String contentType) {
        if (StringUtils.isNotBlank(requestBody) && (httpMethod instanceof HttpPost || httpMethod instanceof HttpPut)) {
            StringEntity entity = new StringEntity(requestBody, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(contentType)) {
                entity.setContentType(contentType);
            } else {
                entity.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }
            if (httpMethod instanceof HttpPost) {
                ((HttpPost)httpMethod).setEntity(entity);
            } else if (httpMethod instanceof HttpPut) {
                ((HttpPut)httpMethod).setEntity(entity);
            } else {
                // 兜底逻辑，抛出明确的不支持异常，避免无意义强转
                throw new IllegalArgumentException("当前请求类型不支持设置请求体");
            }
        }
    }

    private static <T> T doExecuteRequest(HttpRequestBase httpMethod, List<NameValuePair> headerParams,
        List<NameValuePair> urlParams, String requestBody, String contentType, JavaType javaType) {

        Assert.notNull(httpMethod, "httpMethod cannot be null");
        Assert.notNull(javaType, "javaType cannot be null");

        if (headerParams != null && !headerParams.isEmpty()) {
            headerParams.forEach(p -> httpMethod.addHeader(p.getName(), p.getValue()));
        }
        appendUrlParams(httpMethod, urlParams);
        setRequestBody(httpMethod, requestBody, contentType);
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpMethod)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String respStr = null;
            if (entity != null) {
                respStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }

            if (statusCode == HttpStatus.SC_OK) {
                if (respStr == null) {
                    return null;
                }
                return OBJECT_MAPPER.readValue(respStr, javaType);
            } else {
                LOGGER.info("http status code: {}, response: {}", statusCode, respStr);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    private static <T> T executeWithoutBody(HttpRequestBase httpMethod, List<NameValuePair> urlParams, Class<T> clz) {
        return doExecuteRequest(httpMethod, null, urlParams, null, null, OBJECT_MAPPER.constructType(clz));
    }

    public static <T> T delete(String url, List<NameValuePair> params, Class<T> clz) {
        return executeWithoutBody(new HttpDelete(url), params, clz);
    }

    public static <T> Y9Result<T> get(String url, List<NameValuePair> params, Class<T> clz) {
        JavaType resultType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Y9Result.class, clz);
        return doExecuteRequest(new HttpGet(url), null, params, null, null, resultType);
    }

    public static <T> T getCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        return executeWithoutBody(new HttpGet(url), params, clz);
    }

    public static <T> List<T> getCallRemoteServiceByList(String url, List<NameValuePair> params, Class<T> clz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        return doExecuteRequest(new HttpGet(url), null, params, null, null, listType);
    }

    public static <T> List<T> getCallRemoteServiceWithHeaderToList(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        return doExecuteRequest(new HttpGet(url), headerParams, params, null, null, listType);
    }

    public static <T> T getCallRemoteServiceWithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        return doExecuteRequest(new HttpGet(url), headerParams, params, null, null, OBJECT_MAPPER.constructType(clz));
    }

    public static <T> Y9Result<List<T>> getList(String url, List<NameValuePair> params, Class<T> clz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        JavaType resultType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Y9Result.class, listType);
        return doExecuteRequest(new HttpGet(url), null, params, null, null, resultType);
    }

    public static <T> Y9Page<T> getPage(String url, List<NameValuePair> params, Class<T> clz) {
        JavaType pageType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Y9Page.class, clz);
        return doExecuteRequest(new HttpGet(url), null, params, null, null, pageType);
    }

    public static <T> Y9Result<T> post(String url, List<NameValuePair> params, Class<T> clz) {
        return post(url, params, List.of(), clz);
    }

    public static <T> Y9Result<T> post(String url, List<NameValuePair> params, List<NameValuePair> bodyParams,
        Class<T> clz) {
        JavaType resultType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Y9Result.class, clz);
        HttpPost httpPost = new HttpPost(url);

        if (bodyParams != null && !bodyParams.isEmpty()) {
            Map<String, String> bodyMap = new java.util.HashMap<>(bodyParams.size());
            bodyParams.forEach(p -> {
                if (p.getValue() != null) {
                    bodyMap.put(p.getName(), p.getValue());
                }
            });
            String jsonBody = Y9JsonUtil.writeValueAsString(bodyMap);
            return doExecuteRequest(httpPost, null, params, jsonBody, null, resultType);
        }
        return doExecuteRequest(httpPost, null, params, null, null, resultType);
    }

    public static <T> T post(String url, List<NameValuePair> params, String requestBodyJson, Class<T> clz) {
        return doExecuteRequest(new HttpPost(url), null, params, requestBodyJson, null,
            OBJECT_MAPPER.constructType(clz));
    }

    public static <T> T postCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        return executeWithoutBody(new HttpPost(url), params, clz);
    }

    public static <T> List<T> postCallRemoteServiceByList(String url, List<NameValuePair> params, Class<T> clz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        return doExecuteRequest(new HttpPost(url), null, params, null, null, listType);
    }

    public static <T> T postCallRemoteServiceWithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        return doExecuteRequest(new HttpPost(url), headerParams, params, null, null, OBJECT_MAPPER.constructType(clz));
    }

    public static <T> List<T> postCallRemoteServiceWithHeaderToList(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        return doExecuteRequest(new HttpPost(url), headerParams, params, null, null, listType);
    }

    public static String postXml(String url, String xmlData) {
        return postXml(url, null, xmlData);
    }

    public static String postXml(String url, List<NameValuePair> urlParams, String xmlData) {
        HttpPost httpPost = new HttpPost(url);
        appendUrlParams(httpPost, urlParams);
        setRequestBody(httpPost, xmlData, MediaType.APPLICATION_XML_VALUE);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(httpPost)) {
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseBody = null;
            if (entity != null) {
                responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
            if (code == HttpStatus.SC_OK) {
                return responseBody;
            } else {
                LOGGER.info("http status code: {}, response: {}", code, responseBody);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T put(String url, List<NameValuePair> params, String requestBodyJson, Class<T> clz) {
        return doExecuteRequest(new HttpPut(url), null, params, requestBodyJson, null,
            OBJECT_MAPPER.constructType(clz));
    }

    public static List<NameValuePair> objectToNameValuePairList(Object object) {
        List<NameValuePair> requestBody = new ArrayList<>();
        Map<String, String> keyValueMap =
            Y9JsonUtil.readValue(Y9JsonUtil.writeValueAsString(object), new TypeReference<Map<String, String>>() {});
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            requestBody.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return requestBody;
    }

}
