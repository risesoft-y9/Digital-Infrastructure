package net.risesoft.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 针对于http://api.idcode.org.cn接口的调用处理
 * 
 */
@Slf4j
public class IDCodeApiService {

    private boolean containFile = false;

    private Map<String, File> files = new HashMap<>();

    /**
     * 对map集合参数按ASCII由小到大排序
     *
     * @param params
     * @return
     */
    private String sortMapToString(Map<String, Object> params) {
        Map<String, Object> sortMap = new TreeMap<>(params);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> s : sortMap.entrySet()) {
            String key = s.getKey();
            Object value = s.getValue();
            if (value instanceof File) {
                containFile = true;
                files.put(key, (File)value);
                continue;
            }
            stringBuilder.append(key).append("=").append(value).append("&");
        }
        if (!sortMap.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @return
     */
    public String get(String url, Map<String, Object> params) {
        HttpUtil http = new HttpUtil();
        if (!params.isEmpty()) {
            String urlParams = sortMapToString(params);
            if (url.indexOf("?") > -1) {
                url = url + "&";
            } else {
                url = url + "?";
            }
            String hash = MD5Util.MD5Encode(url + urlParams + ConfigReader.API_CODE).toUpperCase();
            urlParams += "&hash=" + hash;
            String resultJson = http.get(ConfigReader.IDCODE_URL + url + urlParams);
            printResult("get json结果：" + resultJson);
            return resultJson;
        } else {
            String hash = MD5Util.MD5Encode(url + ConfigReader.API_CODE).toUpperCase();
            String resultJson = http.get(ConfigReader.IDCODE_URL + url + "&" + hash);
            printResult("get json结果：" + resultJson);
            return resultJson;
        }
    }

    /***
     * post请求
     *
     * @param url
     * @param params
     * @return
     */
    public String post(String url, Map<String, Object> params) {
        String urlParams = sortMapToString(params);
        String encodeUrl = url;
        if (url.indexOf("?") > -1) {
            encodeUrl = url + "&";
        } else {
            encodeUrl = url + "?";
        }
        String hash = MD5Util.MD5Encode(encodeUrl + urlParams + ConfigReader.API_CODE).toUpperCase();
        params.put("hash", hash);
        HttpUtil http = new HttpUtil();
        String resultJson = "";
        if (containFile) {
            resultJson = http.postMapContainFile(ConfigReader.IDCODE_URL + url, params, files);
        } else {
            resultJson = http.postMap(ConfigReader.IDCODE_URL + url, params);
        }
        printResult("post json结果：" + resultJson);
        return resultJson;
    }

    private void printResult(String rs) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(rs);
        }
    }
}
