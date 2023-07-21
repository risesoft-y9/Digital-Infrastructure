package y9.apisix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;

/**
 * http工具类
 *
 * @author DJZ
 * @date 2022/12/30
 */
@Slf4j
public class HttpUtil {

    public static Map<String, Object> doDelete(String url, Map<String, String> headers) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete httpDelete = new HttpDelete(url);

        setHeaders(httpDelete, headers);
        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity, Charset.forName("UTF-8"));
                    map.put("success", true);
                    map.put("msg", responseBody);
                }
            } else {
                map.put("msg", "response is null.");
            }
        } catch (IOException e) {
            map.put("msg", e.getMessage());
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    public static <T> T doGet(String url, List<NameValuePair> params, Map<String, String> headers, Class<T> clazz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        GetMethod method = new GetMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    method.setRequestHeader(entry.getKey(), entry.getValue());
                }
            }

            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    T value = Y9JsonUtil.readValue(response, clazz);
                    return value;
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (HttpException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } catch (IOException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        } finally {
            method.releaseConnection();
            ((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return null;
    }

    public static Map<String, Object> doPatch(String url, String bodyJsonStr, Map<String, String> headers) {
        String responseBody = null;
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPatch httpPatch = new HttpPatch(url);

        setHeaders(httpPatch, headers);
        httpPatch.setEntity(new StringEntity(bodyJsonStr, Charset.forName("UTF-8")));
        try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
                map.put("success", true);
                map.put("msg", responseBody);
            }
        } catch (IOException e) {
            map.put("msg", e.getMessage());
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    public static Map<String, Object> doPut(String url, String bodyJsonStr, Map<String, String> headers) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url);

        setHeaders(httpPut, headers);
        httpPut.setEntity(new StringEntity(bodyJsonStr, Charset.forName("UTF-8")));
        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            if (response != null) {
                map.put("msg", response.getStatusLine().getReasonPhrase());
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity);
                    map.put("success", true);
                    map.put("msg", responseBody);
                }
            } else {
                map.put("msg", "response is null.");
            }
        } catch (IOException e) {
            map.put("msg", e.getMessage());
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    private static void setHeaders(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                httpRequest.setHeader(entry.getKey(), value);
            }
        }
    }

    private HttpUtil() {
        throw new IllegalStateException("HttpUtil class");
    }
}
