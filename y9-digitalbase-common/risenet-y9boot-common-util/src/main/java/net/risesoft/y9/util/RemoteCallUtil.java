package net.risesoft.y9.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9DateFormat;
import net.risesoft.y9.json.Y9JsonUtil;

import cn.hutool.json.JSONObject;

import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 * 远程调用工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class RemoteCallUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();

    private static final Service service = new Service();

    /**
     * 双杨推送接口
     * @param endpointURL
     * @param methodName
     * @param para
     * @return
     * @throws Exception
     */
    public static Object invoke(String endpointURL, String methodName, Object[] para) throws Exception {
        Call call = (Call) service.createCall();
        URL url = new URL(endpointURL);
        call.setTargetEndpointAddress(url);
        call.setTimeout(Integer.valueOf(5000));
        call.setOperationName(new QName(endpointURL, methodName));
        Object result = call.invoke(para);
        return result;
    }

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Y9DateFormat sdf = new Y9DateFormat();
        objectMapper.setDateFormat(sdf);
    }

    public static <T> T delete(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        DeleteMethod method = new DeleteMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }
            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> Y9Result<T> get(String url, List<NameValuePair> params, Class<T> clz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Y9Result.class, clz);
        return sendRequest(MethodType.GET, url, params, null, javaType);
    }

    public static <T> T getCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        GetMethod method = new GetMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> List<T> getCallRemoteServiceByList(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        GetMethod method = new GetMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> List<T> getCallRemoteServiceWhithHeaderToList(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        GetMethod method = new GetMethod(url);
        try {
            if (headerParams != null && !headerParams.isEmpty()) {
                for (NameValuePair p : headerParams) {
                    method.addRequestHeader(p.getName(), p.getValue());
                }
            }

            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> T getCallRemoteServiceWithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        GetMethod method = new GetMethod(url);
        try {
            if (headerParams != null && !headerParams.isEmpty()) {
                for (NameValuePair p : headerParams) {
                    method.addRequestHeader(p.getName(), p.getValue());
                }
            }
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> Y9Result<List<T>> getList(String url, List<NameValuePair> params, Class<T> clz) {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz);
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Y9Result.class, collectionType);
        return sendRequest(MethodType.GET, url, params, null, javaType);
    }

    public static <T> Y9Page<T> getPage(String url, List<NameValuePair> params, Class<T> clz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Y9Page.class, clz);
        return sendRequest(MethodType.GET, url, params, null, javaType);
    }

    public static List<NameValuePair> objectToNameValuePairList(Object object) {
        List<NameValuePair> requestBody = new ArrayList<>();
        Map<String, String> keyValueMap =
            Y9JsonUtil.readValue(Y9JsonUtil.writeValueAsString(object), new TypeReference<Map<String, String>>() {});
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            requestBody.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        return requestBody;
    }

    public static <T> Y9Result<T> post(String url, List<NameValuePair> params, Class<T> clz) {
        return post(url, params, List.of(), clz);
    }

    public static <T> String postXml(String url, String xmlData, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(xmlData, "application/xml", "UTF-8");
            method.setRequestEntity(requestEntity);
            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
//                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Y9Result.class, clz);
//                return objectMapper.readValue(response, javaType);
                System.out.println(response);
                return response;
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> Y9Result<T> post(String url, List<NameValuePair> params, List<NameValuePair> bodyParams,
        Class<T> clz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Y9Result.class, clz);
        return sendRequest(MethodType.POST, url, params, bodyParams, javaType);
    }

    public static <T> T post(String url, List<NameValuePair> params, String requestBodyJson, Class<T> clz) {
        return sendPostRequest(url, params, requestBodyJson, clz);
    }

    public static <T> T put(String url, List<NameValuePair> params, String requestBodyJson, Class<T> clz) {
        return sendPutRequest(url, params, requestBodyJson, clz);
    }

    public static <T> T postCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> List<T> postCallRemoteServiceByList(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> T postCallRemoteServiceWithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        if (headerParams != null && !headerParams.isEmpty()) {
            for (NameValuePair p : headerParams) {
                method.addRequestHeader(p.getName(), p.getValue());
            }
        }
        if (params != null && !params.isEmpty()) {
            method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        }
        try {
            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static <T> List<T> postCallRemoteServiceWithHeaderToList(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        if (headerParams != null && !headerParams.isEmpty()) {
            for (NameValuePair p : headerParams) {
                method.addRequestHeader(p.getName(), p.getValue());
            }
        }
        if (params != null && !params.isEmpty()) {
            method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        }
        try {
            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response,
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    private static <T> T sendRequest(MethodType methodType, String url, List<NameValuePair> params,
        List<NameValuePair> bodyParams, JavaType javaType) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        HttpMethod method = null;
        try {
            if (MethodType.GET.equals(methodType)) {
                method = new GetMethod(url);
            }
            if (MethodType.POST.equals(methodType) || (bodyParams != null && !bodyParams.isEmpty())) {
                method = new PostMethod(url);
                JSONObject jsonObject = new JSONObject();
                for (NameValuePair pair : bodyParams) {
                    jsonObject.putOpt(pair.getName(), pair.getValue());
                }
                RequestEntity request = new StringRequestEntity(jsonObject.toString(), MediaType.APPLICATION_JSON_VALUE,
                    StandardCharsets.UTF_8.name());
                ((PostMethod)method).setRequestEntity(request);
            }
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b = "";
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, javaType);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    private static <T> T sendPostRequest(String url, List<NameValuePair> params, String requestBodyJson,
        Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PostMethod method = new PostMethod(url);
        try {
            if (StringUtils.isNotBlank(requestBodyJson)) {
                RequestEntity request = new StringRequestEntity(requestBodyJson, MediaType.APPLICATION_JSON_VALUE,
                    StandardCharsets.UTF_8.name());
                method.setRequestEntity(request);
            }
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b;
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    private static <T> T sendPutRequest(String url, List<NameValuePair> params, String requestBodyJson, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8.name());
        PutMethod method = new PutMethod(url);
        try {
            if (StringUtils.isNotBlank(requestBodyJson)) {
                RequestEntity request = new StringRequestEntity(requestBodyJson, MediaType.APPLICATION_JSON_VALUE,
                    StandardCharsets.UTF_8.name());
                method.setRequestEntity(request);
            }
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            String response = null;
            InputStream inputStream = method.getResponseBodyAsStream();
            if (null != inputStream) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuffer = new StringBuilder();
                String b;
                while ((b = br.readLine()) != null) {
                    stringBuffer.append(b);
                }
                response = stringBuffer.toString();
            }
            if (code == HttpStatus.SC_OK) {
                return objectMapper.readValue(response, clz);
            } else {
                LOGGER.info("http status code: {}, response: {}", code, response);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOGGER.warn(ioe.getMessage(), ioe);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    enum MethodType {
        GET, POST
    }
}
