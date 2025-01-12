package org.apereo.cas.web.y9.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apereo.cas.web.y9.service.Y9KeyValueService;
import org.apereo.cas.web.y9.util.Y9Base64;
import org.apereo.cas.web.y9.util.Y9QRCode;
import org.apereo.cas.web.y9.util.Y9Result;
import org.apereo.cas.web.y9.util.common.Y9Util;
import org.apereo.cas.web.y9.util.json.Y9JacksonUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class QRCodeController {

    private final Y9KeyValueService y9KeyValueService;

    @Value("${cas.server.name}")
    private String name;

    @ResponseBody
    @RequestMapping(value = "/getQRCode", method = RequestMethod.POST)
    public Map<String, Object> getQRCode() {
        Map<String, Object> map = new HashMap<>();
        map.put("img", "");
        map.put("uuid", "");
        map.put("success", false);
        try {
            name = name.endsWith("/") ? name : name + "/";
            String uuid = UUID.randomUUID().toString();
            String url = name + "sso/login?url=" + name + "sso/api/saveScanResult&uuid=" + uuid;
            InputStream imgis =
                this.getClass().getClassLoader().getResourceAsStream("static/y9static/y9new/img/qrCodeLogo.png");
            String img = Y9QRCode.encode(url, 512, 512, imgis);
            y9KeyValueService.put("QRCode:" + uuid, "2", 2);
            map.put("img", img);
            map.put("uuid", uuid);
            map.put("success", true);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/getScanResult", method = RequestMethod.POST)
    public Map<String, Object> getScanResult(String uuid) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("userId", "");
        map.put("msg", "获取扫描结果失败");
        try {
            String value = y9KeyValueService.get("QRCode:" + uuid);
            if (null != value) {
                if (value.contains("$")) {
                    String[] valArr = value.split("\\$");
                    map.put("scanResult", valArr[0]);
                    map.put("userId", valArr[1]);
                    map.put("success", true);
                } else {
                    map.put("success", true);
                    map.put("scanResult", value);
                }
                map.put("msg", "获取扫描结果成功");
            } else {
                map.put("scanResult", 0);
                map.put("msg", "uuid对应的二维码已过期");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/saveScanResult")
    public void saveScanResult(String uuid, String userId, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(userId)) {
                Y9Util.renderJson(response,
                    Y9JacksonUtil.writeValueAsString(Y9Result.failure(400, "请求参数有问题：uuid或userId不能为空")));
                return;
            }
            userId = Y9Base64.decode(userId);
            String obj = y9KeyValueService.get("QRCode:" + uuid);
            if (null != obj) {
                if (obj.contains("1$")) {
                    Y9Util.renderJson(response,
                        Y9JacksonUtil.writeValueAsString(Y9Result.failure(419, "二维码已过期：已被扫描。")));
                } else {
                    y9KeyValueService.put("QRCode:" + uuid, 1 + "$" + userId, 5);
                    Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(Y9Result.successMsg("扫码成功")));
                }
            } else {
                Y9Util.renderJson(response,
                    Y9JacksonUtil.writeValueAsString(Y9Result.failure(419, "二维码已过期：uuid已过期自动清除。")));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }
}