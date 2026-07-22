package net.risesoft.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {

    private static final int DEFAULT_TIMEOUT = 30000;

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(DEFAULT_TIMEOUT)
            .setSocketTimeout(DEFAULT_TIMEOUT).setConnectionRequestTimeout(DEFAULT_TIMEOUT).build())
        .setRetryHandler(new DefaultHttpRequestRetryHandler(2, true)).build();

    public static String get(String url) {
        if (url == null || url.isEmpty())
            return null;
        HttpGet get = new HttpGet(url);
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(get)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (statusCode == 200 && entity != null) {
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } else {
                EntityUtils.consume(entity);
                LOGGER.warn("HTTP GET 请求返回非200状态码，URL: {}, 状态码: {}", url, statusCode);
            }
        } catch (IOException e) {
            LOGGER.error("HTTP GET 请求发生网络异常，URL: {}", url, e);
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    public static String post(String url, HttpEntity data, ContentType ct) {
        if (url == null || url.isEmpty())
            return null;
        HttpPost post = new HttpPost(url);
        if (ct != null) {
            post.setHeader("Content-Type", ct.toString());
        }
        post.setEntity(data);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            if (statusCode == 307) {
                Header header = response.getFirstHeader("location");
                if (header != null && header.getValue() != null && !header.getValue().isEmpty()) {
                    return post(header.getValue(), data, ct);
                }
            } else {
                EntityUtils.consume(response.getEntity());
                LOGGER.warn("HTTP POST 请求返回非200状态码，URL: {}, 状态码: {}", url, statusCode);
            }
        } catch (IOException e) {
            LOGGER.error("HTTP POST 请求发生网络异常，URL: {}", url, e);
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    public static String postMap(String url, Map<String, Object> data) {
        if (url == null || url.isEmpty() || data == null || data.isEmpty())
            return null;
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        try {
            return post(url, new UrlEncodedFormEntity(params, StandardCharsets.UTF_8),
                ContentType.APPLICATION_FORM_URLENCODED);
        } catch (Exception e) {
            LOGGER.error("HTTP POST Map 构建参数异常，URL: {}", url, e);
            return null;
        }
    }

    public static String postMapContainFile(String url, Map<String, Object> data, Map<String, File> files) {
        return httpPostFormMultipart(url, data, files, null, null);
    }

    public static String postParams(String url, List<NameValuePair> params) {
        if (url == null || url.isEmpty() || params == null || params.isEmpty())
            return null;
        try {
            return post(url, new UrlEncodedFormEntity(params, StandardCharsets.UTF_8),
                ContentType.APPLICATION_FORM_URLENCODED);
        } catch (Exception e) {
            LOGGER.error("HTTP POST Params 构建参数异常，URL: {}", url, e);
            return null;
        }
    }

    public static String postJson(String url, String postData) {
        if (url == null || url.isEmpty() || postData == null)
            return null;
        HttpEntity entity = new StringEntity(postData, ContentType.APPLICATION_JSON);
        return post(url, entity, ContentType.APPLICATION_JSON);
    }

    public static String postString(String url, String postData) {
        if (url == null || url.isEmpty() || postData == null)
            return null;
        return post(url, new StringEntity(postData, ContentType.APPLICATION_FORM_URLENCODED),
            ContentType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 发送 http post 请求，支持文件上传
     */
    public static String httpPostFormMultipart(String url, Map<String, Object> params, Map<String, File> files,
        Map<String, String> headers, String encode) {
        if (url == null || url.isEmpty())
            return null;
        String charset = (encode != null) ? encode : StandardCharsets.UTF_8.name();
        HttpPost httpost = new HttpPost(url);
        try {
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
            mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mEntityBuilder.setCharset(Charset.forName(charset));

            ContentType contentType = ContentType.create("text/plain", Charset.forName(charset));
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (!(entry.getValue() instanceof File) && entry.getValue() != null) {
                        mEntityBuilder.addTextBody(entry.getKey(), entry.getValue().toString(), contentType);
                    }
                }
            }
            if (files != null && !files.isEmpty()) {
                ContentType binaryContentType =
                    ContentType.create("application/octet-stream", Charset.forName(charset));
                for (Map.Entry<String, File> entry : files.entrySet()) {
                    File file = entry.getValue();
                    if (file != null && file.exists()) {
                        mEntityBuilder.addBinaryBody(entry.getKey(), file, binaryContentType, file.getName());
                    }
                }
            }
            httpost.setEntity(mEntityBuilder.build());

            try (CloseableHttpResponse httpResponse = HTTP_CLIENT.execute(httpost)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                HttpEntity entity = httpResponse.getEntity();
                if (statusCode == 200 && entity != null) {
                    return EntityUtils.toString(entity, charset);
                } else {
                    EntityUtils.consume(entity);
                    LOGGER.warn("HTTP POST Multipart 请求返回非200状态码，URL: {}, 状态码: {}", url, statusCode);
                    return null;
                }
            }
        } catch (IOException e) {
            LOGGER.error("HTTP POST Multipart 文件上传异常，URL: {}", url, e);
        } finally {
            httpost.releaseConnection();
        }
        return null;
    }
}