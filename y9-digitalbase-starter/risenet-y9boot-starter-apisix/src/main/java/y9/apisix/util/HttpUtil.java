package y9.apisix.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;

/**
 * http工具类
 *
 * @author DJZ
 * @date 2022/12/30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {

    public static Map<String, Object> doDelete(String url, Map<String, String> headers) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "");

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpDelete httpDelete = new HttpDelete(url);
            setHeaders(httpDelete, headers);
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String responseBody = readResponse(response.getEntity());
                    map.put("success", true);
                    map.put("msg", responseBody);
                }
            }
        } catch (IOException e) {
            map.put("msg", e.getMessage());
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    public static <T> T doGet(String url, List<BasicNameValuePair> params, Map<String, String> headers,
        Class<T> clazz) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null && !params.isEmpty()) {
                for (BasicNameValuePair p : params) {
                    uriBuilder.addParameter(p.getName(), p.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            setHeaders(httpGet, headers);

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String responseBody = readResponse(response.getEntity());
                    return Y9JsonUtil.readValue(responseBody, clazz);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> doPatch(String url, String bodyJsonStr, Map<String, String> headers) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPatch httpPatch = new HttpPatch(url);
            setHeaders(httpPatch, headers);
            httpPatch.setEntity(new StringEntity(bodyJsonStr, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_OK) {
                    String responseBody = readResponse(response.getEntity());
                    map.put("success", true);
                    map.put("msg", responseBody);
                }
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

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPut httpPut = new HttpPut(url);
            setHeaders(httpPut, headers);
            httpPut.setEntity(new StringEntity(bodyJsonStr, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                map.put("msg", response.getStatusLine().getReasonPhrase());
                if (statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_OK) {
                    String responseBody = readResponse(response.getEntity());
                    map.put("success", true);
                    map.put("msg", responseBody);
                }
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
                httpRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private static String readResponse(HttpEntity entity) throws IOException {
        if (entity == null) {
            return null;
        }

        long contentLength = entity.getContentLength();
        if (contentLength > 10 * 1024 * 1024) {
            LOGGER.warn("Response content too large: " + contentLength + " bytes");
        }

        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }
}