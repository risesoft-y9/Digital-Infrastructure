package net.risesoft.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * http处理工具
 *
 */
public class HttpUtil {

    public String get(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseContent;
    }

    public String post(String url, HttpEntity data, ContentType ct) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setEntity(data);// 设置请求体
        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
            if (statusCode == 307) {
                Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD上
                url = header.getValue();
                responseContent = post(url, data, ct);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseContent;
    }

    public String postMap(String url, Map<String, Object> data) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : data.keySet()) {
            params.add(new BasicNameValuePair(key, data.get(key).toString()));
        }
        UrlEncodedFormEntity urlEntity = null;
        try {
            urlEntity = new UrlEncodedFormEntity(params, "UTF-8");
            return post(url, urlEntity, ContentType.APPLICATION_FORM_URLENCODED);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public String postMapContainFile(String url, Map<String, Object> data, Map<String, File> files) {
        return httpPostFormMultipart(url, data, files, null, null);
    }

    public String postParams(String url, List<NameValuePair> params) {
        UrlEncodedFormEntity urlEntity = null;
        try {
            urlEntity = new UrlEncodedFormEntity(params, "UTF-8");
            return post(url, urlEntity, ContentType.APPLICATION_FORM_URLENCODED);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public String postJson(String url, String postData) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");
        StringEntity myEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);// 构造请求数据
        post.setEntity(myEntity);// 设置请求体

        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseContent;
    }

    public String postString(String url, String postData) {
        StringEntity myEntity = new StringEntity(postData, ContentType.APPLICATION_FORM_URLENCODED);// 构造请求数据
        return post(url, myEntity, ContentType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 发送 http post 请求，支持文件上传
     */
    public static String httpPostFormMultipart(String url, Map<String, Object> params, Map<String, File> files, Map<String, String> headers, String encode) {
        String content = null;
        if (encode == null) {
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpost = new HttpPost(url);

        //设置header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mEntityBuilder.setCharset(Charset.forName(encode));

        // 普通参数
        ContentType contentType = ContentType.create("text/plain", Charset.forName(encode));//解决中文乱码
        if (params != null && params.size() > 0) {
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                if (params.get(key) instanceof File)
                    continue;
                mEntityBuilder.addTextBody(key, params.get(key).toString(), contentType);
            }
        }
        //二进制参数
        if (files != null && files.size() > 0) {
            Set<String> keySet = files.keySet();
            for (String key : keySet) {
                File file = files.get(key);
                ContentType contentType1 = ContentType.create("application/octet-stream", Charset.forName(encode));//解决中文乱码
                mEntityBuilder.addBinaryBody(key, file, contentType1, file.getName());
            }
        }
        httpost.setEntity(mEntityBuilder.build());

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpost);
            HttpEntity entity = httpResponse.getEntity();
            content = EntityUtils.toString(entity, encode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
