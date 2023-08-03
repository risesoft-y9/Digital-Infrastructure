package net.risesoft.y9.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9DateFormat;

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

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Y9DateFormat sdf = new Y9DateFormat();
        objectMapper.setDateFormat(sdf);
    }

    public static <T> T getCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
        GetMethod method = new GetMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    T value = objectMapper.readValue(response, clz);
                    return value;
                }
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
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
        GetMethod method = new GetMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    List<T> value = objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
                    return value;
                }
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

    public static <T> T getCallRemoteServiceWhithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
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
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    T value = objectMapper.readValue(response, clz);
                    return value;
                }
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
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
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
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    List<T> value = objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
                    return value;
                }
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

    public static <T> T postCallRemoteService(String url, List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
        PostMethod method = new PostMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    T value = objectMapper.readValue(response, clz);
                    return value;
                }
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
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
        PostMethod method = new PostMethod(url);
        try {
            if (params != null && !params.isEmpty()) {
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
            }

            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    List<T> value = objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
                    return value;
                }
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

    public static <T> T postCallRemoteServiceWhithHeader(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
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
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    T value = objectMapper.readValue(response, clz);
                    return value;
                }
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

    public static <T> List<T> postCallRemoteServiceWhithHeaderToList(String url, List<NameValuePair> headerParams,
        List<NameValuePair> params, Class<T> clz) {
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, StandardCharsets.UTF_8);
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
            if (code == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (null != inputStream) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuffer stringBuffer = new StringBuffer();
                    String b = "";
                    while ((b = br.readLine()) != null) {
                        stringBuffer.append(b);
                    }
                    String response = stringBuffer.toString();
                    List<T> value = objectMapper.readValue(response,
                        objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clz));
                    return value;
                }
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
}
